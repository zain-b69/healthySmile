package ma.dentalTech.service.modules.patient.api;

import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PatientService {

    // ---- CRUD ----
    List<Patient> findAll();
    Patient findById(Long id);

    void create(Patient patient) throws SQLException;
    void update(Patient patient);
    void delete(Patient patient);
    void deleteById(Long id);

    // ---- extra ----
    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByTelephone(String telephone);
    List<Patient> searchByNomPrenom(String keyword);
    boolean existsById(Long id);
    long count();
    List<Patient> findPage(int limit, int offset);

    // ---- Many-to-Many : patient_antecedent ----
    void addAntecedentToPatient(Long patientId, Long antecedentId);
    void removeAntecedentFromPatient(Long patientId, Long antecedentId);
    void removeAllAntecedentsFromPatient(Long patientId);
    List<Antecedent> getAntecedentsOfPatient(Long patientId);
    List<Patient> getPatientsByAntecedent(Long antecedentId);
}
