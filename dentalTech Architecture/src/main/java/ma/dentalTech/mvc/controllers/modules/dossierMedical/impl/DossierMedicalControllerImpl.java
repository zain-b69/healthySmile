package ma.dentalTech.mvc.controllers.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.service.modules.dossierMedicale.api.DossierMedicalService;

import java.time.LocalDate;
import java.util.List;

public class DossierMedicalControllerImpl implements DossierMedicalController {

    private final DossierMedicalService dossierService;

    public DossierMedicalControllerImpl(DossierMedicalService dossierService) {
        if (dossierService == null) throw new IllegalArgumentException("DossierMedicalService requis");
        this.dossierService = dossierService;
    }

    @Override
    public List<DossierMedical> listAll() {
        return dossierService.listAll();
    }

    @Override
    public DossierMedical getById(Long idDm) {
        return dossierService.getById(idDm);
    }

    @Override
    public DossierMedical getByPatient(Long idPatient) {
        return dossierService.getByPatient(idPatient);
    }

    @Override
    public DossierMedical ouvrirDossier(Long idPatient) {
        return dossierService.ouvrirDossier(idPatient);
    }

    @Override
    public DossierMedical getOuCreerDossier(Long idPatient) {
        return dossierService.getOuCreerDossier(idPatient);
    }

    @Override
    public List<DossierMedical> dossiersCreesEntre(LocalDate start, LocalDate end) {
        return dossierService.dossiersCreesEntre(start, end);
    }

    @Override
    public DossierMedical corrigerDateCreation(Long idDm, LocalDate nouvelleDate) {
        return dossierService.corrigerDateCreation(idDm, nouvelleDate);
    }
}
