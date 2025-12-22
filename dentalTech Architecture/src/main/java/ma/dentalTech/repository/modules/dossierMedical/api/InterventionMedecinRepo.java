package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.InterventionMedecin;

import java.util.List;
import java.util.Optional;

public interface InterventionMedecinRepo {

    InterventionMedecin save(InterventionMedecin im);
    InterventionMedecin update(InterventionMedecin im);

    Optional<InterventionMedecin> findById(Long idIm);
    List<InterventionMedecin> findAll();
    boolean deleteById(Long idIm);

    List<InterventionMedecin> findByIdConsultation(Long idConsultation);
    List<InterventionMedecin> findByIdActe(Long idActe);

    List<InterventionMedecin> findByIdConsultationAndNumDent(Long idConsultation, Integer numDent);
}
