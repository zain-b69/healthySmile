package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.DossierMedical;

import java.time.LocalDate;
import java.util.List;

public interface DossierMedicalService {

    // ---- Use-case 1 : ouvrir le dossier d'un patient (1 patient -> 1 dossier) ----
    DossierMedical ouvrirDossier(Long idPatient);

    // ---- Use-case 2 : récupérer ou créer automatiquement ----
    DossierMedical getOuCreerDossier(Long idPatient);

    // ---- Use-case 3 : vérifier existence ----
    boolean patientADossier(Long idPatient);

    // ---- Use-case 4 : recherche par période de création (administratif/stat) ----
    List<DossierMedical> dossiersCreesEntre(LocalDate start, LocalDate end);

    // ---- Use-case 5 : récupérer par patient / id ----
    DossierMedical getByPatient(Long idPatient);
    DossierMedical getById(Long idDm);

    // ---- Optionnel : corriger la date de création (si prof veut) ----
    DossierMedical corrigerDateCreation(Long idDm, LocalDate nouvelleDate);

    List<DossierMedical> listAll();
}
