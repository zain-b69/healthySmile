package ma.dentalTech.repository.modules.patient.api;

import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.common.CrudRepository;
import ma.dentalTech.entities.patient.Antecedent;

import java.util.List;
import java.util.Optional;

/**
 * @author : Haitham
 */
public interface PatientRepository extends CrudRepository<Patient, Long> {

    Optional<Patient> findByEmail(String email);
    Optional<Patient> findByTelephone(String telephone);
    List<Patient> searchByNomPrenom(String keyword); // LIKE %keyword%
    boolean existsById(Long id);
    long count();
    List<Patient> findPage(int limit, int offset);

    // ---- Liaison Many-to-Many ----
    void addAntecedentToPatient(Long patientId, Long antecedentId);
    void removeAntecedentFromPatient(Long patientId, Long antecedentId);
    void removeAllAntecedentsFromPatient(Long patientId);
    List<Antecedent> getAntecedentsOfPatient(Long patientId);
    List<Patient> getPatientsByAntecedent(Long antecedentId);

}
