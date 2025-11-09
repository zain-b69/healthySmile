package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.DossierMedical;

import java.sql.SQLException;
import java.util.List;

public interface DossierMedicalRepository {
    List<DossierMedical> findByPatientId(Long idPatient) throws SQLException;
}
