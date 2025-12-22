package ma.dentalTech.service.modules.caisse.impl;

import ma.dentalTech.entities.dossierMedical.SituationFinanciere;
import ma.dentalTech.repository.modules.dossierMedical.api.SituationFinanciereRepo;
import ma.dentalTech.repository.modules.dossierMedical.impl.SituationFinanciereRepoImpl;
import ma.dentalTech.service.modules.caisse.api.SituationFinanciereService;

import java.sql.SQLException;

/**
 * Implémentation "métier" de la situation financière.
 */
public class SituationFinanciereServiceImpl implements SituationFinanciereService {

    private final SituationFinanciereRepo repo;

    public SituationFinanciereServiceImpl() {
        this(new SituationFinanciereRepoImpl());
    }

    public SituationFinanciereServiceImpl(SituationFinanciereRepo repo) {
        this.repo = repo;
    }

    @Override
    public SituationFinanciere consulterParDossier(long idDm) {
        return repo.findByDossierMedicalId(idDm).orElse(null);
    }

    @Override
    public SituationFinanciere getOuCreerPourDossier(long idDm, boolean enPromo, String creePar) {
        SituationFinanciere existing = consulterParDossier(idDm);
        if (existing != null) return existing;

        SituationFinanciere sf = new SituationFinanciere();
        sf.setIdDm(idDm);
        sf.setEnPromo(enPromo);
        sf.setTotaleDesActes(0.0);
        sf.setTotalePaye(0.0);
        sf.setCredit(0.0);
        sf.setCreePar(creePar);
        sf.setModifiePar(creePar);

        try {
            repo.create(sf);
            return repo.findById(sf.getIdSf());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SituationFinanciere recalculerEtMettreAJour(long idDm) {
        SituationFinanciere sf = getOuCreerPourDossier(idDm, false, "system");

        double totalActes = repo.computeTotalActesPourDossier(idDm);
        double totalPaye = repo.computeTotalPayePourSituation(sf.getIdSf());

        boolean promo = sf.getEnPromo() != null && sf.getEnPromo();
        double credit = calculerCredit(totalActes, totalPaye);

        String statutDb = calcStatutDb(totalActes, totalPaye, credit);
        String enPromoDb = promo ? "OUI" : "NON";

        repo.updateTotauxEtStatut(sf.getIdSf(), totalActes, totalPaye, credit, statutDb, enPromoDb);
        return repo.findById(sf.getIdSf());
    }

    @Override
    public SituationFinanciere appliquerPromotion(long idSf, double pourcentageRemise) {
        SituationFinanciere sf = repo.findById(idSf);
        if (sf == null) return null;

        double remise = Math.max(0.0, Math.min(100.0, pourcentageRemise));

        // Recalc base totals
        double totalActes = repo.computeTotalActesPourDossier(sf.getIdDm());
        if (remise > 0.0) {
            totalActes = totalActes * (1.0 - (remise / 100.0));
        }
        double totalPaye = repo.computeTotalPayePourSituation(idSf);
        double credit = calculerCredit(totalActes, totalPaye);

        repo.updateTotauxEtStatut(idSf, totalActes, totalPaye, credit, calcStatutDb(totalActes, totalPaye, credit), "OUI");
        return repo.findById(idSf);
    }

    @Override
    public SituationFinanciere enregistrerPaiementManuel(long idSf, double montant) {
        SituationFinanciere sf = repo.findById(idSf);
        if (sf == null) return null;

        double paiement = Math.max(0.0, montant);
        double totalActes = sf.getTotaleDesActes() != null ? sf.getTotaleDesActes() : 0.0;
        double totalPaye = (sf.getTotalePaye() != null ? sf.getTotalePaye() : 0.0) + paiement;
        double credit = calculerCredit(totalActes, totalPaye);

        boolean promo = sf.getEnPromo() != null && sf.getEnPromo();
        repo.updateTotauxEtStatut(idSf, totalActes, totalPaye, credit, calcStatutDb(totalActes, totalPaye, credit), promo ? "OUI" : "NON");
        return repo.findById(idSf);
    }

    // ------------------- helpers -------------------

    private static double calculerCredit(double totalActes, double totalPaye) {
        double c = totalActes - totalPaye;
        return c > 0.0 ? c : 0.0;
    }

    private static String calcStatutDb(double totalActes, double totalPaye, double credit) {
        if (totalActes <= 0.0) return "EN_ATTENTE";
        if (totalPaye <= 0.0) return "EN_ATTENTE";
        if (credit > 0.000001) return "EN_COURS";
        return "TERMINE";
    }
}
