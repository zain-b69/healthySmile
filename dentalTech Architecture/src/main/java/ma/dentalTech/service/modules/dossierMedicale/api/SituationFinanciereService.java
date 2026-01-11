package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.SituationFinanciere;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;

import java.util.List;

public interface SituationFinanciereService {

    // ---- accès minimal utile
    SituationFinanciere getById(Long idSf);
    void deleteById(Long idSf);

    // ---- finders
    SituationFinanciere findByDossierMedical(Long idDm);
    List<SituationFinanciere> findByStatut(StatutSituationFinanciere statut);
    List<SituationFinanciere> findPage(int limit, int offset);

    // ---- métier
    double calculerResteAPayer(Long idSf);
    void appliquerPromo(Long idSf, boolean enPromo);
    void enregistrerPaiement(Long idSf, double montant);
    void recalculerCreditEtStatut(Long idSf);

    // ---- validation (utile pour réutiliser ailleurs)
    void validateOrThrow(SituationFinanciere sf);
}
