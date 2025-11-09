package ma.dentalTech.service.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.entities.patient.Patient;

import java.util.List;

public interface DossierMedicalService {
    List<DossierMedical> recupererDossiersParPatient(Long idPatient);

    DossierMedical creerNouveauDossier(Patient patientPlaceholder, String creePar);
}
