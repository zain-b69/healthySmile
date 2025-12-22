package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.entities.dossierMedical.PrescriptionMedicament;
import ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.PrescriptionService;

import java.sql.SQLException;
import java.util.List;

public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepo prescriptionRepo;

    public PrescriptionServiceImpl(PrescriptionRepo prescriptionRepo) {
        this.prescriptionRepo = prescriptionRepo;
    }

    @Override
    public List<Prescription> findAll() {
        return prescriptionRepo.findAll();
    }

    @Override
    public Prescription findById(Long idPr) {
        return prescriptionRepo.findById(idPr);
    }

    @Override
    public void create(Prescription prescription) throws SQLException {
        prescriptionRepo.create(prescription);
    }

    @Override
    public void update(Prescription prescription) {
        prescriptionRepo.update(prescription);
    }

    @Override
    public void deleteById(Long idPr) {
        prescriptionRepo.deleteById(idPr);
    }

    @Override
    public List<Prescription> findByOrdonnance(Long idOrd) {
        return prescriptionRepo.findByOrdonnance(idOrd);
    }

    @Override
    public void addMedicamentToPrescription(Long idPr, Long idMct, Integer quantite) {
        prescriptionRepo.addMedicamentToPrescription(idPr, idMct, quantite);
    }

    @Override
    public void removeMedicamentFromPrescription(Long idPr, Long idMct) {
        prescriptionRepo.removeMedicamentFromPrescription(idPr, idMct);
    }

    @Override
    public void removeAllMedicamentsFromPrescription(Long idPr) {
        prescriptionRepo.removeAllMedicamentsFromPrescription(idPr);
    }

    @Override
    public List<Medicament> getMedicamentsOfPrescription(Long idPr) {
        return prescriptionRepo.getMedicamentsOfPrescription(idPr);
    }

    @Override
    public List<PrescriptionMedicament> getLignes(Long idPr) {
        return prescriptionRepo.getLignes(idPr);
    }
}
