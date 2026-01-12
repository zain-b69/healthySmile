package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.enums.StatutConsultation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConsultationRepo {

    Consultation save(Consultation consultation);
    Consultation update(Consultation consultation);

    Optional<Consultation> findById(Long idConsultation);
    List<Consultation> findAll();
    boolean deleteById(Long idConsultation);

    List<Consultation> findByIdDm(Long idDm);
    List<Consultation> findByIdMedecin(Long idMedecin);

    Optional<Consultation> findByIdRdv(Long idRdv); // FK unique
    List<Consultation> findByDateBetween(LocalDate start, LocalDate end);
    List<Consultation> findByStatut(StatutConsultation statut);
}
