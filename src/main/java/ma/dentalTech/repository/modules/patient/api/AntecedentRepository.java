package ma.dentalTech.repository.modules.patient.api;

import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AntecedentRepository extends CrudRepository<Antecedent, Long> {

    Optional<Antecedent> findByNom(String nom);
    List<Antecedent> findByCategorie(CategorieAntecedent categorie);
    List<Antecedent> findByNiveauRisque(NiveauRisque niveau);
    boolean existsById(Long id);
    long count();
    List<Antecedent> findPage(int limit, int offset);

    // ---- Navigation inverse ----
    List<Patient> getPatientsHavingAntecedent(Long antecedentId);
}
