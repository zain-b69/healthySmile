package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.DossierMedical;

import java.time.LocalDate;
import java.util.List;

public interface DossierMedicalService {
    DossierMedical create(DossierMedical dossierMedical);
    DossierMedical update(DossierMedical dossierMedical);
    DossierMedical getById(Long idDm);
    List<DossierMedical> getAll();
    boolean delete(Long idDm);

    DossierMedical getByPatient(Long idPatient);
    List<DossierMedical> getByDateCreationBetween(LocalDate start, LocalDate end);
}
