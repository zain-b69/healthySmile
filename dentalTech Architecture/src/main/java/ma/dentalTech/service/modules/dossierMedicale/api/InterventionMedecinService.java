package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.InterventionMedecin;

import java.util.List;

public interface InterventionMedecinService {
    InterventionMedecin create(InterventionMedecin im);
    InterventionMedecin update(InterventionMedecin im);
    InterventionMedecin getById(Long idIm);
    List<InterventionMedecin> getAll();
    boolean delete(Long idIm);

    List<InterventionMedecin> getByConsultation(Long idConsultation);
    List<InterventionMedecin> getByActe(Long idActe);
    List<InterventionMedecin> getByConsultationAndNumDent(Long idConsultation, Integer numDent);
}
