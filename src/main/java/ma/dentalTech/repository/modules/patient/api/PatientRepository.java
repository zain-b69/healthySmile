package ma.dentalTech.repository.modules.patient.api;

import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.common.CrudRepository;

import java.sql.SQLException;

public interface PatientRepository extends CrudRepository<Patient, Long>

{

    Patient save(Patient patient) throws SQLException, SQLException;
}
