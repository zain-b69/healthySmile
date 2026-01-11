package ma.dentalTech.service.modules.cabinet.api;

import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.enums.CategorieStatistique;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface StatistiquesService {

    /**
     * Génère des statistiques pour une période.
     */
    Statistiques genererStatistiques(Statistiques statistique) throws SQLException;

    /**
     * Met à jour des statistiques existantes.
     */
    Statistiques mettreAJourStatistiques(Statistiques statistique);

    /**
     * Supprime des statistiques erronées.
     */
    void supprimerStatistiques(long idStatistique);

    /**
     * Consulte les statistiques d'un cabinet.
     */
    List<Statistiques> consulterStatistiquesCabinet(long idCabinet, LocalDate debut, LocalDate fin);

    /**
     * Analyse les statistiques par catégorie.
     */
    List<Statistiques> analyserStatistiquesParCategorie(long idCabinet, CategorieStatistique categorie);

    /**
     * Génère un rapport de performance.
     */
    String genererRapportPerformance(long idCabinet, LocalDate debut, LocalDate fin);

    /**
     * Consulte des statistiques par leur identifiant.
     */
    Statistiques consulterStatistiquesParId(long idStatistique);

    /**
     * Vérifie si des statistiques existent.
     */
    boolean existeStatistiques(long idStatistique);

    /**
     * Récupère le nombre total de statistiques pour un cabinet.
     */
    long nombreTotalStatistiquesCabinet(long idCabinet);

    /**
     * Consulte les N dernières statistiques d'un cabinet.
     */
    List<Statistiques> consulterDernieresStatistiques(long idCabinet, int nombre);

    /**
     * Calcule le taux de croissance des revenus entre deux périodes.
     */
    double calculerTauxCroissanceRevenus(long idCabinet,
                                         LocalDate periode1Debut, LocalDate periode1Fin,
                                         LocalDate periode2Debut, LocalDate periode2Fin);

    /**
     * Génère un rapport comparatif entre deux périodes.
     */
    String genererRapportComparatif(long idCabinet,
                                    LocalDate periodeCouranteDebut, LocalDate periodeCouranteFin,
                                    LocalDate periodePrecedenteDebut, LocalDate periodePrecedenteFin);
}