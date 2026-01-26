package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.enums.StatutConsultation;

import java.time.LocalDate;
import java.util.List;

public interface ConsultationService {

    // ---- Use-case 1 : ouvrir une consultation depuis un RDV (RDV unique) ----
    Consultation ouvrirDepuisRdv(Long idRdv, Long idMedecin, Long idDm, LocalDate date);

    // ---- Use-case 2 : workflow de statut ----
    Consultation changerStatut(Long idConsultation, StatutConsultation nouveauStatut);

    // ---- Use-case 3 : notes/observation du médecin ----
    Consultation ajouterOuRemplacerObservation(Long idConsultation, String observation);

    // ---- Use-case 4 : replanifier (changer date / médecin) ----
    Consultation replanifier(Long idConsultation, LocalDate nouvelleDate, Long nouveauIdMedecin);

    // ---- Use-case 5 : historiques / recherche ----
    List<Consultation> historiqueDossier(Long idDm);
    List<Consultation> historiqueMedecin(Long idMedecin);
    List<Consultation> consultationsDansPeriode(LocalDate start, LocalDate end);
    List<Consultation> consultationsParStatut(StatutConsultation statut);

    // ---- Contrôle ----
    Consultation getByRdv(Long idRdv);
    Consultation getById(Long idConsultation);

    Consultation creerConsultationManuelle(Long idDm, Long idMedecin,
                                           LocalDate date, String observations);

    boolean deleteById(Long idConsultation);
}
