package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.DossierMedicalService;

import java.time.LocalDate;
import java.util.List;

public class DossierMedicalServiceImpl implements DossierMedicalService {

    private final DossierMedicalRepo dossierMedicalRepo;

    public DossierMedicalServiceImpl(DossierMedicalRepo dossierMedicalRepo) {
        this.dossierMedicalRepo = dossierMedicalRepo;
    }

    @Override
    public DossierMedical create(DossierMedical dm) {
        if (dm == null) throw new IllegalArgumentException("DossierMedical null");
        dm.setIdDm(null);
        return dossierMedicalRepo.save(dm);
    }

    @Override
    public DossierMedical update(DossierMedical dm) {
        if (dm == null || dm.getIdDm() == null) throw new IllegalArgumentException("idDm requis");
        getById(dm.getIdDm());
        return dossierMedicalRepo.update(dm);
    }

    @Override
    public DossierMedical getById(Long idDm) {
        return dossierMedicalRepo.findById(idDm)
                .orElseThrow(() -> new RuntimeException("DossierMedical introuvable: " + idDm));
    }

    @Override
    public List<DossierMedical> getAll() {
        return dossierMedicalRepo.findAll();
    }

    @Override
    public boolean delete(Long idDm) {
        return dossierMedicalRepo.deleteById(idDm);
    }

    @Override
    public DossierMedical getByPatient(Long idPatient) {
        return dossierMedicalRepo.findByIdPatient(idPatient)
                .orElseThrow(() -> new RuntimeException("DossierMedical introuvable pour patient: " + idPatient));
    }

    @Override
    public List<DossierMedical> getByDateCreationBetween(LocalDate start, LocalDate end) {
        return dossierMedicalRepo.findByDateCreationBetween(start, end);
    }
}
