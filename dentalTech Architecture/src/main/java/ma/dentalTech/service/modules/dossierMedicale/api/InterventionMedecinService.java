package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.InterventionMedecin;

import java.util.List;

public interface InterventionMedecinService {

    // ---- Use-case 1 : ajouter une intervention (avec règles) ----
    InterventionMedecin ajouterIntervention(Long idConsultation,
                                            Long idActe,
                                            Integer numDent,
                                            Double prixDePatient);

    // ---- Use-case 2 : ajuster le prix patient (remise, négociation) ----
    InterventionMedecin changerPrixPatient(Long idIm, Double nouveauPrix);

    // ---- Use-case 3 : total d'une consultation ----
    double calculerTotalConsultation(Long idConsultation);

    // ---- Use-case 4 : listes utiles ----
    List<InterventionMedecin> interventionsDeConsultation(Long idConsultation);
    List<InterventionMedecin> interventionsDeConsultationPourDent(Long idConsultation, Integer numDent);

    // ---- Utilitaires ----
    InterventionMedecin getById(Long idIm);
}
