package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.InterventionMedecinService;

import java.util.List;

public class InterventionMedecinServiceImpl implements InterventionMedecinService {

    private final InterventionMedecinRepo interventionRepo;

    public InterventionMedecinServiceImpl(InterventionMedecinRepo interventionRepo) {
        this.interventionRepo = interventionRepo;
    }

    @Override
    public InterventionMedecin create(InterventionMedecin im) {
        if (im == null) throw new IllegalArgumentException("Intervention null");
        im.setIdIm(null);
        return interventionRepo.save(im);
    }

    @Override
    public InterventionMedecin update(InterventionMedecin im) {
        if (im == null || im.getIdIm() == null) throw new IllegalArgumentException("idIm requis");
        getById(im.getIdIm());
        return interventionRepo.update(im);
    }

    @Override
    public InterventionMedecin getById(Long idIm) {
        return interventionRepo.findById(idIm)
                .orElseThrow(() -> new RuntimeException("Intervention introuvable: " + idIm));
    }

    @Override
    public List<InterventionMedecin> getAll() {
        return interventionRepo.findAll();
    }

    @Override
    public boolean delete(Long idIm) {
        return interventionRepo.deleteById(idIm);
    }

    @Override
    public List<InterventionMedecin> getByConsultation(Long idConsultation) {
        return interventionRepo.findByIdConsultation(idConsultation);
    }

    @Override
    public List<InterventionMedecin> getByActe(Long idActe) {
        return interventionRepo.findByIdActe(idActe);
    }

    @Override
    public List<InterventionMedecin> getByConsultationAndNumDent(Long idConsultation, Integer numDent) {
        return interventionRepo.findByIdConsultationAndNumDent(idConsultation, numDent);
    }
}
