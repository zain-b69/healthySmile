package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.enums.StatutConsultation;

import java.time.LocalDate;
import java.util.List;

public interface ConsultationService {
    Consultation create(Consultation consultation);
    Consultation update(Consultation consultation);
    Consultation getById(Long idConsultation);
    List<Consultation> getAll();
    boolean delete(Long idConsultation);

    List<Consultation> getByDossierMedical(Long idDm);
    List<Consultation> getByMedecin(Long idMedecin);
    Consultation getByRdv(Long idRdv);

    List<Consultation> getByDateBetween(LocalDate start, LocalDate end);
    List<Consultation> getByStatut(StatutConsultation statut);
}
