package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.DossierMedical;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DossierMedicalRepo {

    DossierMedical save(DossierMedical dossierMedical);
    DossierMedical update(DossierMedical dossierMedical);

    Optional<DossierMedical> findById(Long idDm);
    List<DossierMedical> findAll();
    boolean deleteById(Long idDm);

    Optional<DossierMedical> findByIdPatient(Long idPatient); // souvent unique (1 dossier / patient)
    List<DossierMedical> findByDateCreationBetween(LocalDate start, LocalDate end);
}
