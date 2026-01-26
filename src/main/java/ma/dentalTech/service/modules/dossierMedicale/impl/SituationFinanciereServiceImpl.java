package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.SituationFinanciere;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;
import ma.dentalTech.repository.modules.dossierMedical.api.SituationFinanciereRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.SituationFinanciereService;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class SituationFinanciereServiceImpl implements SituationFinanciereService {

    private final SituationFinanciereRepo situationFinanciereRepo;

    public SituationFinanciereServiceImpl(SituationFinanciereRepo situationFinanciereRepo) {
        this.situationFinanciereRepo = Objects.requireNonNull(situationFinanciereRepo);
    }

    // =========================
    // Accès minimal
    // =========================

    @Override
    public SituationFinanciere getById(Long idSf) {
        return requireSf(idSf);
    }

    @Override
    public void deleteById(Long idSf) {
        if (idSf == null) return;
        situationFinanciereRepo.deleteById(idSf);
    }

    // =========================
    // Finders
    // =========================

    @Override
    public SituationFinanciere findByDossierMedical(Long idDm) {
        if (idDm == null) throw new IllegalArgumentException("idDm ne peut pas être null");
        SituationFinanciere sf = situationFinanciereRepo.findByDossierMedical(idDm);
        if (sf != null) normalize(sf);
        return sf;
    }

    @Override
    public List<SituationFinanciere> findByStatut(StatutSituationFinanciere statut) {
        if (statut == null) throw new IllegalArgumentException("statut ne peut pas être null");
        return situationFinanciereRepo.findByStatut(statut);
    }

    @Override
    public List<SituationFinanciere> findPage(int limit, int offset) {
        if (limit <= 0) limit = 10;
        if (offset < 0) offset = 0;
        return situationFinanciereRepo.findPage(limit, offset);
    }

    // =========================
    // Métier
    // =========================

    @Override
    public double calculerResteAPayer(Long idSf) {
        SituationFinanciere sf = requireSf(idSf);
        double total = nz(sf.getTotaleDesActes());
        double paye = nz(sf.getTotalePaye());
        return Math.max(0.0, total - paye);
    }

    @Override
    public void appliquerPromo(Long idSf, boolean enPromo) {
        SituationFinanciere sf = requireSf(idSf);

        if (enPromo && !sf.getEnPromo()) {
            // Appliquer la réduction de 25%
            double ancienTotal = sf.getTotaleDesActes();
            double nouveauTotal = ancienTotal * 0.75; // -25%
            sf.setTotaleDesActes(nouveauTotal);
        } else if (!enPromo && sf.getEnPromo()) {
            // Retirer la promotion : revenir au prix original
            double totalAvecPromo = sf.getTotaleDesActes();
            double totalOriginal = totalAvecPromo / 0.75; // Annuler la réduction
            sf.setTotaleDesActes(totalOriginal);
        }

        sf.setEnPromo(enPromo);
        // on garde les montants, on met juste le statut cohérent avec le flag promo
        sf.setStatut(calcStatut(sf));

        situationFinanciereRepo.update(sf);
    }

    @Override
    public void enregistrerPaiement(Long idSf, double montant) {
        if (montant <= 0) throw new IllegalArgumentException("Le montant doit être > 0");

        SituationFinanciere sf = requireSf(idSf);

        double total = nz(sf.getTotaleDesActes());
        double paye = nz(sf.getTotalePaye()) + montant;

        // si surpaiement => surplus va au credit, paye est plafonné au total
        if (paye > total) {
            double surplus = paye - total;
            sf.setTotalePaye(total);
            sf.setCredit(nz(sf.getCredit()) + surplus);
        } else {
            sf.setTotalePaye(paye);
        }

        sf.setStatut(calcStatut(sf));
        validateOrThrow(sf);
        situationFinanciereRepo.update(sf);
    }

    @Override
    public void recalculerCreditEtStatut(Long idSf) {
        SituationFinanciere sf = requireSf(idSf);

        double total = nz(sf.getTotaleDesActes());
        double paye = nz(sf.getTotalePaye());

        // Si payé > total : mettre surplus en credit et plafonner paye
        if (paye > total) {
            sf.setCredit(paye - total);
            sf.setTotalePaye(total);
        } else {
            // pas de surplus => credit ne doit pas être négatif
            sf.setCredit(Math.max(0.0, nz(sf.getCredit())));
        }

        sf.setStatut(calcStatut(sf));
        validateOrThrow(sf);
        situationFinanciereRepo.update(sf);
    }

    // =========================
    // Validation
    // =========================

    @Override
    public void validateOrThrow(SituationFinanciere sf) {
        if (sf == null) throw new IllegalArgumentException("SituationFinanciere ne peut pas être null");

        if (sf.getIdDm() == null) {
            throw new IllegalArgumentException("SituationFinanciere.idDm est obligatoire (liaison dossier médical)");
        }

        // montants non négatifs
        if (nz(sf.getTotaleDesActes()) < 0) throw new IllegalArgumentException("totaleDesActes ne peut pas être négatif");
        if (nz(sf.getTotalePaye()) < 0) throw new IllegalArgumentException("totalePaye ne peut pas être négatif");
        if (nz(sf.getCredit()) < 0) throw new IllegalArgumentException("credit ne peut pas être négatif");

        if (sf.getStatut() == null) {
            throw new IllegalArgumentException("statut ne peut pas être null");
        }
        if (sf.getEnPromo() == null) {
            throw new IllegalArgumentException("enPromo ne peut pas être null");
        }
    }

    @Override
    public Long createForDossierMedical(Long idDm) {
        if (idDm == null) throw new IllegalArgumentException("idDm ne peut pas être null");

        SituationFinanciere sf = new SituationFinanciere();
        sf.setIdDm(idDm);
        sf.setTotaleDesActes(0.0);
        sf.setTotalePaye(0.0);
        sf.setCredit(0.0);
        sf.setEnPromo(false);
        sf.setStatut(StatutSituationFinanciere.EN_CREDIT);

        validateOrThrow(sf);

        // il faut que ton repo ait un create(...) qui retourne l'id généré
        return situationFinanciereRepo.createAndReturnId(sf);
    }

    // =========================
    // Helpers
    // =========================

    private SituationFinanciere requireSf(Long idSf) {
        if (idSf == null) throw new IllegalArgumentException("idSf ne peut pas être null");

        SituationFinanciere sf = situationFinanciereRepo.findById(idSf);
        if (sf == null) throw new IllegalArgumentException("Situation financière introuvable: id=" + idSf);

        normalize(sf);

        // statut recalculé “à la volée” (ça évite les incohérences si data ancienne)
        sf.setStatut(calcStatut(sf));
        return sf;
    }

    private void normalize(SituationFinanciere sf) {
        if (sf.getTotaleDesActes() == null) sf.setTotaleDesActes(0.0);
        if (sf.getTotalePaye() == null) sf.setTotalePaye(0.0);
        if (sf.getCredit() == null) sf.setCredit(0.0);
        if (sf.getEnPromo() == null) sf.setEnPromo(false);

        // statut peut être null dans la DB si ancien enregistrement
        if (sf.getStatut() == null) sf.setStatut(StatutSituationFinanciere.EN_CREDIT);
    }

    private double nz(Double v) {
        return v == null ? 0.0 : v;
    }

    private StatutSituationFinanciere calcStatut(SituationFinanciere sf) {
        if (Boolean.TRUE.equals(sf.getEnPromo())) {
            return StatutSituationFinanciere.EN_PROMO;
        }

        double total = nz(sf.getTotaleDesActes());
        double paye = nz(sf.getTotalePaye());
        double credit = nz(sf.getCredit());

        // EN_CREDIT : reste à payer OU surplus stocké en credit
        if (paye < total || credit > 0.0) {
            return StatutSituationFinanciere.EN_CREDIT;
        }

        // sinon : réglé
        return StatutSituationFinanciere.EN_REGLE;
    }
}
