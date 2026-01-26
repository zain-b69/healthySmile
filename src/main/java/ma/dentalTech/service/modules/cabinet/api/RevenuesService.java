package ma.dentalTech.service.modules.cabinet.api;

import ma.dentalTech.entities.cabinet.Revenues;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des revenus d'un cabinet médical.
 */
public interface RevenuesService {

    /**
     * Enregistre un nouveau revenu pour le cabinet.
     */
    Revenues enregistrerRevenue(Revenues revenue) throws SQLException;

    /**
     * Modifie un revenu existant.
     */
    Revenues modifierRevenue(Revenues revenue);

    /**
     * Supprime un revenu erroné.
     */
    void supprimerRevenue(long idRevenu);

    /**
     * Consulte les revenus d'un cabinet sur une période.
     */
    List<Revenues> consulterRevenuesCabinet(
            long idCabinet,
            LocalDateTime debut,
            LocalDateTime fin
    );

    /**
     * Calcule le total des revenus pour un cabinet sur une période.
     */
    double calculerTotalRevenues(
            long idCabinet,
            LocalDateTime debut,
            LocalDateTime fin
    );

    /**
     * Consulte un revenu spécifique.
     */
    Optional<Revenues> consulterRevenue(long idRevenu);

    /**
     * Consulte tous les revenus d'un cabinet.
     */
    List<Revenues> consulterTousRevenuesCabinet(long idCabinet);

    /**
     * Calcule le total de tous les revenus d'un cabinet.
     */
    double calculerTotalTousRevenuesCabinet(long idCabinet);

    /**
     * Consulte les revenus sur une période (tous cabinets).
     */
    List<Revenues> consulterRevenuesParPeriode(
            LocalDateTime debut,
            LocalDateTime fin
    );

    /**
     * Consulte les N derniers revenus d'un cabinet.
     */
    List<Revenues> consulterDerniersRevenues(long idCabinet, int nombre);

    /**
     * Recherche des revenus par mot-clé (titre ou description).
     */
    List<Revenues> rechercherRevenuesParMotCle(long idCabinet, String motCle);

    /**
     * Calcule la moyenne mensuelle des revenus.
     */
    double calculerMoyenneMensuelleRevenues(long idCabinet, int nombreMois);

    /**
     * Retourne le revenu le plus élevé sur une période.
     */
    Revenues getPlusGrosRevenu(
            long idCabinet,
            LocalDateTime debut,
            LocalDateTime fin
    );

    /**
     * Retourne le nombre total de revenus d'un cabinet.
     */
    long nombreTotalRevenuesCabinet(long idCabinet);
}
