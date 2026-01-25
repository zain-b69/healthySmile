package ma.dentalTech.mvc.controllers.modules.patient.api;

import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PatientController {

    List<Patient> findAll();
    Patient findById(Long id);

    void create(Patient patient) throws SQLException;
    void update(Patient patient);
    void deleteById(Long id);

    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByTelephone(String telephone);
    List<Patient> searchByNomPrenom(String keyword);
    void addAntecedentToPatient(Long patientId, Long antecedentId);
    void removeAntecedentFromPatient(Long patientId, Long antecedentId);
    List<Antecedent> getAntecedentsOfPatient(Long patientId);
    List<Patient> getPatientsByAntecedent(Long antecedentId);

}
