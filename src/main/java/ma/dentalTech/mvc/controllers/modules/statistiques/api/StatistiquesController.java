package ma.dentalTech.mvc.controllers.modules.statistiques.api;

import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.enums.CategorieStatistique;

import java.time.LocalDate;
import java.util.List;

public interface StatistiquesController {

    // CRUD snapshot (table statistiques)
    List<Statistiques> findAll();
    Statistiques findById(Long id);

    Statistiques create(Statistiques s);
    Statistiques update(Statistiques s);
    void deleteById(Long id);

    // Finders
    List<Statistiques> findByCabinet(Long idCabinet);
    List<Statistiques> findByCategorie(CategorieStatistique categorie);
    List<Statistiques> findByCabinetAndCategorie(Long idCabinet, CategorieStatistique categorie);
    List<Statistiques> findByDate(LocalDate dateFin);

    // Métier / rapports
    Statistiques generer(Statistiques s); // appelle genererStatistiques()
    String rapportPerformance(Long idCabinet, LocalDate debut, LocalDate fin);
    String rapportComparatif(Long idCabinet,
                             LocalDate debutCourant, LocalDate finCourant,
                             LocalDate debutPrec, LocalDate finPrec);
}
