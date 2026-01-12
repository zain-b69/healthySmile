package ma.dentalTech.service.modules.cabinet.api;

import ma.dentalTech.entities.cabinet.Charges;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service métier pour la gestion des charges d'un cabinet médical.
 */
public interface ChargesService {

    /**
     * Enregistre une nouvelle charge pour le cabinet.
     */
    Charges enregistrerCharge(Charges charge) throws SQLException;

    /**
     * Modifie une charge existante.
     */
    Charges modifierCharge(Charges charge);

    /**
     * Supprime une charge erronée.
     */
    void supprimerCharge(long idCharge);

    /**
     * Consulte les charges d'un cabinet sur une période.
     */
    List<Charges> consulterChargesCabinet(long idCabinet, LocalDateTime debut, LocalDateTime fin);

    /**
     * Calcule le total des charges pour un cabinet sur une période.
     */
    double calculerTotalCharges(long idCabinet, LocalDateTime debut, LocalDateTime fin);

    /**
     * Consulte toutes les charges d'un cabinet.
     */
    List<Charges> consulterToutesChargesCabinet(long idCabinet);

    /**
     * Calcule le total de toutes les charges d'un cabinet.
     */
    double calculerTotalToutesCharges(long idCabinet);

    /**
     * Consulte une charge par son identifiant.
     */
    Charges consulterChargeParId(long idCharge);

    /**
     * Vérifie si une charge existe.
     */
    boolean existeCharge(long idCharge);

    /**
     * Consulte les N dernières charges d'un cabinet.
     */
    List<Charges> consulterDernieresCharges(long idCabinet, int nombre);
}