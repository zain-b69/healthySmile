package ma.dentalTech.mvc.controllers.modules.patient.api;

import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface AntecedentController {

    List<Antecedent> findAll();
    Antecedent findById(Long id);

    void create(Antecedent a) throws SQLException;
    void update(Antecedent a);
    void deleteById(Long id);

    Optional<Antecedent> findByNom(String nom);
    List<Antecedent> findByCategorie(CategorieAntecedent categorie);
    List<Antecedent> findByNiveauRisque(NiveauRisque niveau);

    List<Patient> getPatientsHavingAntecedent(Long antecedentId);
}
