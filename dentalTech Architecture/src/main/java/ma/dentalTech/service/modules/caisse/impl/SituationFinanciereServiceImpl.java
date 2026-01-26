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
//        return repo.findByDossierMedicalId(idDm).orElse(null);
        return null;
    }

    @Override
    public SituationFinanciere getOuCreerPourDossier(long idDm, boolean enPromo, String creePar) throws SQLException {
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

        repo.create(sf);
        return repo.findById(sf.getIdSf());
    }

    @Override
    public SituationFinanciere recalculerEtMettreAJour(long idDm) {
//
        return null;
    }

    @Override
    public SituationFinanciere appliquerPromotion(long idSf, double pourcentageRemise) {
//
        return null;
    }

    @Override
    public SituationFinanciere enregistrerPaiementManuel(long idSf, double montant) {
//
        return null;
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
