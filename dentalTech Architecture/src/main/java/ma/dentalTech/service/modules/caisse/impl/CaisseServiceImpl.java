package ma.dentalTech.service.modules.caisse.impl;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.dossierMedical.Facture;
import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo;
import ma.dentalTech.repository.modules.facturation.api.FactureRepo;
import ma.dentalTech.service.modules.caisse.api.CaisseService;
import ma.dentalTech.service.modules.dossierMedicale.api.InterventionMedecinService;

import java.time.LocalDateTime;
import java.util.List;

public class CaisseServiceImpl implements CaisseService {

    private final FactureRepo factureRepo;
    private final ConsultationRepo consultationRepo;
    private final InterventionMedecinService interventionMedecinService;

    public CaisseServiceImpl(FactureRepo factureRepo, ConsultationRepo consultationRepo, InterventionMedecinService interventionMedecinService /* + autres deps */) {
        this.factureRepo = factureRepo;
        this.consultationRepo = consultationRepo;
        this.interventionMedecinService = interventionMedecinService;
    }
    // -------------------------
    // Use-case 1 : créer facture
    // -------------------------

    @Override
    public Facture creerFacture(Long idConsultation, Long idSf, Double total) {
        if (idConsultation == null) throw new IllegalArgumentException("idConsultation requis");
        if (idSf == null) throw new IllegalArgumentException("idSf requis");
        double t = validatePositive(total, "total requis/invalide");

        Facture f = new Facture();
        f.setIdFacture(null);
        f.setIdConsultation(idConsultation);
        f.setIdSf(idSf);

        f.setTotaleFacture(t);
        f.setReste(t);

        f.setStatut(StatutFacture.EN_ATTENTE);
        f.setDateFacture(LocalDateTime.now());

        // règle: 1 consultation -> 1 facture (si tu veux l’imposer)
        factureRepo.findByIdConsultation(idConsultation).ifPresent(existing -> {
            throw new IllegalStateException("Une facture existe déjà pour cette consultation: " + idConsultation);
        });

        return factureRepo.save(f);
    }

    // -------------------------
    // Use-case 2 : encaisser
    // -------------------------

    @Override
    public Facture encaisser(Long idFacture, Double montantEncaisse) {
        if (idFacture == null) throw new IllegalArgumentException("idFacture requis");
        double m = validatePositive(montantEncaisse, "montantEncaisse invalide");

        Facture f = getById(idFacture);

        if (f.getStatut() == StatutFacture.ANNULEE) {
            throw new IllegalStateException("Impossible d'encaisser: facture annulée");
        }
        if (f.getStatut() == StatutFacture.PAYEE) {
            throw new IllegalStateException("Impossible d'encaisser: facture déjà payée");
        }

        double reste = nvl(f.getReste(), 0.0);
        if (reste <= 0) {
            // si reste 0 mais statut pas PAYEE -> on corrige
            factureRepo.updateStatut(idFacture, StatutFacture.PAYEE);
            f.setStatut(StatutFacture.PAYEE);
            f.setReste(0.0);
            return f;
        }

        if (m > reste) {
            throw new IllegalArgumentException("Montant (" + m + ") > reste à payer (" + reste + ")");
        }

        double newReste = round2(reste - m);
        if (newReste < 0) newReste = 0;

        StatutFacture newStatut = computeStatut(f.getTotaleFacture(), newReste);

        boolean ok = factureRepo.updateResteAndStatut(idFacture, newReste, newStatut);
        if (!ok) throw new RuntimeException("Echec encaissement (update) pour facture: " + idFacture);

        // refresh objet en mémoire
        f.setReste(newReste);
        f.setStatut(newStatut);
        return f;
    }

    // -------------------------
    // Use-case 3 : annuler facture
    // -------------------------

    @Override
    public Facture annuler(Long idFacture) {
        if (idFacture == null) throw new IllegalArgumentException("idFacture requis");

        Facture f = getById(idFacture);

        if (f.getStatut() == StatutFacture.PAYEE) {
            throw new IllegalStateException("Impossible d'annuler une facture PAYEE");
        }
        if (f.getStatut() == StatutFacture.ANNULEE) {
            return f; // déjà annulée
        }

        boolean ok = factureRepo.updateStatut(idFacture, StatutFacture.ANNULEE);
        if (!ok) throw new RuntimeException("Echec annulation facture: " + idFacture);

        f.setStatut(StatutFacture.ANNULEE);
        return f;
    }

    // -------------------------
    // Getters / Finders
    // -------------------------

    @Override
    public Facture getById(Long idFacture) {
        if (idFacture == null) throw new IllegalArgumentException("idFacture requis");
        return factureRepo.findById(idFacture)
                .orElseThrow(() -> new RuntimeException("Facture introuvable: " + idFacture));
    }

    @Override
    public List<Facture> findAll() {
        return factureRepo.findAll();
    }

    @Override
    public Facture findByConsultation(Long idConsultation) {
        if (idConsultation == null) throw new IllegalArgumentException("idConsultation requis");
        return factureRepo.findByIdConsultation(idConsultation)
                .orElseThrow(() -> new RuntimeException("Facture introuvable pour consultation: " + idConsultation));
    }

    @Override
    public List<Facture> findBySf(Long idSf) {
        if (idSf == null) throw new IllegalArgumentException("idSf requis");
        return factureRepo.findByIdSf(idSf);
    }

    @Override
    public List<Facture> findByStatut(StatutFacture statut) {
        if (statut == null) throw new IllegalArgumentException("statut requis");
        return factureRepo.findByStatut(statut);
    }

    @Override
    public List<Facture> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) throw new IllegalArgumentException("start/end requis");
        if (end.isBefore(start)) throw new IllegalArgumentException("Intervalle invalide (end < start)");
        return factureRepo.findByDateFactureBetween(start, end);
    }

    @Override
    public Facture creerFactureDepuisConsultation(Long idConsultation, Long idSituationFinanciere) {
        if (idConsultation == null) throw new IllegalArgumentException("idConsultation obligatoire");
        if (idSituationFinanciere == null) throw new IllegalArgumentException("idSituationFinanciere obligatoire");

        Consultation c = consultationRepo.findById(idConsultation)
                .orElseThrow(() -> new IllegalArgumentException("Consultation inexistante"));

        double total = interventionMedecinService.calculerTotalConsultation(idConsultation);
        double reste = total;

        Long idFacture =factureRepo.create(idConsultation, idSituationFinanciere, total, reste);

        Facture f = new Facture();
        f.setIdFacture(idFacture);
        f.setIdConsultation(idConsultation);
        f.setIdSf(idSituationFinanciere);
        f.setTotaleFacture(total);
        f.setReste(reste);
        f.setStatut(StatutFacture.EN_ATTENTE);
        f.setDateFacture(java.time.LocalDateTime.now());
        return f;
    }


    // -------------------------
    // Helpers métier
    // -------------------------

    private StatutFacture computeStatut(Double total, double reste) {
        double t = nvl(total, 0.0);

        if (reste <= 0) return StatutFacture.PAYEE;

        // si le total est inconnu ou 0, on se base sur reste>0
        if (t <= 0) return StatutFacture.IMPAYEE;

        if (reste < t) return StatutFacture.PARTIELLEMENT_PAYEE;

        return StatutFacture.EN_ATTENTE;
    }

    private double validatePositive(Double v, String msg) {
        if (v == null) throw new IllegalArgumentException(msg);
        if (v <= 0) throw new IllegalArgumentException(msg);
        return v;
    }

    private double nvl(Double v, double def) {
        return v == null ? def : v;
    }

    private double round2(double x) {
        return Math.round(x * 100.0) / 100.0;
    }
}
