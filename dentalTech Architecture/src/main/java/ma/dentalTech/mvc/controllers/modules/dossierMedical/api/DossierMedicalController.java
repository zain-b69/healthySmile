package ma.dentalTech.mvc.controllers.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.DossierMedical;

import java.time.LocalDate;
import java.util.List;

public interface DossierMedicalController {

    // Page principale
    List<DossierMedical> listAll();

    DossierMedical getById(Long idDm);

    DossierMedical getByPatient(Long idPatient);

    // Actions métier
    DossierMedical ouvrirDossier(Long idPatient);          // crée si n'existe pas (ton service: ouvre uniquement si absent)

    DossierMedical getOuCreerDossier(Long idPatient);      // ton service: get ou créer

    // Recherche (déjà service)
    List<DossierMedical> dossiersCreesEntre(LocalDate start, LocalDate end);

    // Update ciblée
    DossierMedical corrigerDateCreation(Long idDm, LocalDate nouvelleDate);
}
