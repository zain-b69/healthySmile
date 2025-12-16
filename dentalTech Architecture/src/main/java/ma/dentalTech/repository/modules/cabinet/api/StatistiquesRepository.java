package ma.dentalTech.repository.modules.cabinet.api;

import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.enums.CategorieStatistique;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface StatistiquesRepository extends CrudRepository<Statistiques, Long> {

    List<Statistiques> findByCabinet(Long cabinetId);

    List<Statistiques> findByCategorie(CategorieStatistique categorie);

    List<Statistiques> findByCabinetAndCategorie(Long cabinetId, CategorieStatistique categorie);

    List<Statistiques> findByDate(LocalDate dateCalcul);

    boolean existsById(Long id);

    long count();

    List<Statistiques> findPage(int limit, int offset);
}
