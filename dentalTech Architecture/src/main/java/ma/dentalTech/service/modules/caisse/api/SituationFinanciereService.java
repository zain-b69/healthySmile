package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.entities.dossierMedical.SituationFinanciere;

import java.sql.SQLException;

/**
 * Cas d'usage : Gérer les situations financières.
 *
 * NB : La DB contient un statut (EN_ATTENTE/EN_COURS/TERMINE/ANNULE) mais
 * l'énum Java de l'entité est (EN_REGLE/EN_CREDIT/EN_PROMO).
 */
public interface SituationFinanciereService {

    SituationFinanciere consulterParDossier(long idDm);

    SituationFinanciere getOuCreerPourDossier(long idDm, boolean enPromo, String creePar) throws SQLException;

    /**
     * Recalcule totale_des_actes (via interventions) et totale_paye (via factures), puis met à jour credit + statut.
     */
    SituationFinanciere recalculerEtMettreAJour(long idDm);

    /**
     * Active une promotion et applique une remise sur le total des actes.
     */
    SituationFinanciere appliquerPromotion(long idSf, double pourcentageRemise);

    /**
     * Paiement manuel : augmente totale_paye et met à jour credit + statut.
     * (Les paiements liés aux factures sont recalculés via {@link #recalculerEtMettreAJour(long)}.)
     */
    SituationFinanciere enregistrerPaiementManuel(long idSf, double montant);
}
