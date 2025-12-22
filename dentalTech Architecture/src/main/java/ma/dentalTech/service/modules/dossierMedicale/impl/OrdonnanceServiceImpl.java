package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.OrdonnanceService;

import java.sql.SQLException;
import java.util.List;

public class OrdonnanceServiceImpl implements OrdonnanceService {

    private final OrdonnanceRepo ordonnanceRepo;

    public OrdonnanceServiceImpl(OrdonnanceRepo ordonnanceRepo) {
        this.ordonnanceRepo = ordonnanceRepo;
    }

    @Override
    public List<Ordonnance> findAll() {
        return ordonnanceRepo.findAll();
    }

    @Override
    public Ordonnance findById(Long idOrd) {
        return ordonnanceRepo.findById(idOrd);
    }

    @Override
    public void create(Ordonnance ordonnance) throws SQLException {
        ordonnanceRepo.create(ordonnance);
    }

    @Override
    public void update(Ordonnance ordonnance) {
        ordonnanceRepo.update(ordonnance);
    }

    @Override
    public void deleteById(Long idOrd) {
        ordonnanceRepo.deleteById(idOrd);
    }

    @Override
    public List<Ordonnance> findByDossierMedical(Long idDm) {
        return ordonnanceRepo.findByDossierMedical(idDm);
    }

    @Override
    public List<Ordonnance> findByConsultation(Long idConsultation) {
        return ordonnanceRepo.findByConsultation(idConsultation);
    }

    @Override
    public List<Prescription> getPrescriptions(Long idOrd) {
        return ordonnanceRepo.getPrescriptions(idOrd);
    }
}
