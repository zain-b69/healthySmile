package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.ConsultationService;

import java.time.LocalDate;
import java.util.List;

public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepo consultationRepo;

    public ConsultationServiceImpl(ConsultationRepo consultationRepo) {
        if (consultationRepo == null) throw new IllegalArgumentException("ConsultationRepo requis");
        this.consultationRepo = consultationRepo;
    }

    // -------------------------
    // Use-case 1 : ouvrir depuis RDV
    // -------------------------

    @Override
    public Consultation ouvrirDepuisRdv(Long idRdv, Long idMedecin, Long idDm, LocalDate date) {
        if (idRdv == null) throw new IllegalArgumentException("idRdv requis");
        if (idMedecin == null) throw new IllegalArgumentException("idMedecin requis");
        if (idDm == null) throw new IllegalArgumentException("idDm requis");
        if (date == null) throw new IllegalArgumentException("date requise");

        // règle métier : 1 RDV -> 1 consultation
        if (consultationRepo.findByIdRdv(idRdv).isPresent()) {
            throw new IllegalStateException("Consultation déjà créée pour le RDV: " + idRdv);
        }

        Consultation c = new Consultation();
        c.setIdConsultation(null);
        c.setIdRdv(idRdv);
        c.setIdMedecin(idMedecin);
        c.setIdDm(idDm);
        c.setDate(date);

        // statut initial (si ton enum contient autre chose, adapte ici)
        c.setStatut(defaultStatutInitial());

        // observation vide -> null
        c.setObservationMedecin(null);

        return consultationRepo.save(c);
    }

    // -------------------------
    // Use-case 2 : changement de statut (workflow)
    // -------------------------

    @Override
    public Consultation changerStatut(Long idConsultation, StatutConsultation nouveauStatut) {
        if (idConsultation == null) throw new IllegalArgumentException("idConsultation requis");
        if (nouveauStatut == null) throw new IllegalArgumentException("nouveauStatut requis");

        Consultation c = getById(idConsultation);

        // règle métier : on évite des transitions incohérentes
        if (!transitionAutorisee(c.getStatut(), nouveauStatut)) {
            throw new IllegalStateException(
                    "Transition statut invalide: " + c.getStatut() + " -> " + nouveauStatut
            );
        }

        c.setStatut(nouveauStatut);
        return consultationRepo.update(c);
    }

    // -------------------------
    // Use-case 3 : observation médecin
    // -------------------------

    @Override
    public Consultation ajouterOuRemplacerObservation(Long idConsultation, String observation) {
        if (idConsultation == null) throw new IllegalArgumentException("idConsultation requis");

        Consultation c = getById(idConsultation);

        String obs = normalizeOptionalText(observation);
        c.setObservationMedecin(obs);

        return consultationRepo.update(c);
    }

    // -------------------------
    // Use-case 4 : replanifier
    // -------------------------

    @Override
    public Consultation replanifier(Long idConsultation, LocalDate nouvelleDate, Long nouveauIdMedecin) {
        if (idConsultation == null) throw new IllegalArgumentException("idConsultation requis");
        if (nouvelleDate == null) throw new IllegalArgumentException("nouvelleDate requise");
        if (nouveauIdMedecin == null) throw new IllegalArgumentException("nouveauIdMedecin requis");

        Consultation c = getById(idConsultation);

        // règle métier simple : si consultation terminée, on ne replanifie pas
        if (isCloturee(c.getStatut())) {
            throw new IllegalStateException("Impossible de replanifier une consultation clôturée");
        }

        c.setDate(nouvelleDate);
        c.setIdMedecin(nouveauIdMedecin);

        return consultationRepo.update(c);
    }

    // -------------------------
    // Use-case 5 : historiques / recherche
    // -------------------------

    @Override
    public List<Consultation> historiqueDossier(Long idDm) {
        if (idDm == null) throw new IllegalArgumentException("idDm requis");
        return consultationRepo.findByIdDm(idDm);
    }

    @Override
    public List<Consultation> historiqueMedecin(Long idMedecin) {
        if (idMedecin == null) throw new IllegalArgumentException("idMedecin requis");
        return consultationRepo.findByIdMedecin(idMedecin);
    }

    @Override
    public List<Consultation> consultationsDansPeriode(LocalDate start, LocalDate end) {
        if (start == null || end == null) throw new IllegalArgumentException("Dates start/end requises");
        if (end.isBefore(start)) throw new IllegalArgumentException("Intervalle invalide: end < start");
        return consultationRepo.findByDateBetween(start, end);
    }

    @Override
    public List<Consultation> consultationsParStatut(StatutConsultation statut) {
        if (statut == null) throw new IllegalArgumentException("statut requis");
        return consultationRepo.findByStatut(statut);
    }

    // -------------------------
    // Accès ciblés
    // -------------------------

    @Override
    public Consultation getByRdv(Long idRdv) {
        if (idRdv == null) throw new IllegalArgumentException("idRdv requis");
        return consultationRepo.findByIdRdv(idRdv)
                .orElseThrow(() -> new RuntimeException("Consultation introuvable pour RDV: " + idRdv));
    }

    @Override
    public Consultation getById(Long idConsultation) {
        if (idConsultation == null) throw new IllegalArgumentException("idConsultation requis");
        return consultationRepo.findById(idConsultation)
                .orElseThrow(() -> new RuntimeException("Consultation introuvable: " + idConsultation));
    }

    // -------------------------
    // Helpers métier
    // -------------------------

    private String normalizeOptionalText(String text) {
        if (text == null) return null;
        String t = text.trim();
        return t.isBlank() ? null : t;
    }

    private StatutConsultation defaultStatutInitial() {
        // Hypothèse: ton enum contient PLANIFIEE.
        // Si ton enum est différent, remplace ici la valeur initiale.
        try {
            return StatutConsultation.valueOf("PLANIFIEE");
        } catch (Exception e) {
            // fallback: null si enum ne contient pas PLANIFIEE
            return null;
        }
    }

    private boolean isCloturee(StatutConsultation st) {
        if (st == null) return false;
        // Hypothèse: si l'enum contient TERMINEE ou ANNULEE -> clôture
        String name = st.name();
        return name.equalsIgnoreCase("TERMINEE") || name.equalsIgnoreCase("ANNULEE");
    }

    private boolean transitionAutorisee(StatutConsultation from, StatutConsultation to) {
        // Si pas de statut encore, tout est autorisé sauf null (déjà géré)
        if (from == null) return true;

        // Règle générique minimaliste "prof" (sans inventer toutes les valeurs):
        // - si consultation clôturée, pas de changement
        if (isCloturee(from)) return false;

        // sinon : autoriser (prof simple) sauf retour en arrière évident
        // (si tu veux un workflow strict, on le fera quand tu me donnes l'enum complet)
        return true;
    }

    // Dans ConsultationServiceImpl.java, AJOUTER cette méthode :

    @Override
    public Consultation creerConsultationManuelle(Long idDm, Long idMedecin,
                                                  LocalDate date, String observations) {

        // Validation
        if (idDm == null) throw new IllegalArgumentException("Dossier médical requis");
        if (idMedecin == null) throw new IllegalArgumentException("Médecin requis");
        if (date == null) throw new IllegalArgumentException("Date requise");

        // Vérifier si le dossier médical existe (optionnel)
        // Vous pourriez avoir besoin d'un service DossierMedicalService

        Consultation c = new Consultation();
        c.setIdConsultation(null);
        c.setIdDm(idDm);
        c.setIdMedecin(idMedecin);
        c.setDate(date);
        c.setStatut(StatutConsultation.PLANIFIEE);
        c.setObservationMedecin(observations);
        c.setIdRdv(null); // IMPORTANT : pas de RDV associé

        return consultationRepo.save(c);
    }
    @Override
    public boolean deleteById(Long idConsultation) {
        if (idConsultation == null) throw new IllegalArgumentException("idConsultation requis");

        // Récupérer la consultation pour vérifier son statut
        Consultation c = getById(idConsultation);

        // Règle métier : on ne peut pas supprimer une consultation terminée sans confirmation spéciale
        // (optionnel, ajuste selon tes besoins)
        if (isCloturee(c.getStatut())) {
            // Tu peux lancer une exception ou demander confirmation dans l'UI
            // Pour l'instant, on permet la suppression
        }

        return consultationRepo.deleteById(idConsultation);
    }
}
