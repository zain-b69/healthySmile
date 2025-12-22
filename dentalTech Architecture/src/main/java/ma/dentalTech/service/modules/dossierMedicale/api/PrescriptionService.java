package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.entities.dossierMedical.PrescriptionMedicament;

import java.sql.SQLException;
import java.util.List;

public interface PrescriptionService {

    List<Prescription> findAll();

    Prescription findById(Long idPr);

    void create(Prescription prescription) throws SQLException;

    void update(Prescription prescription);

    void deleteById(Long idPr);

    List<Prescription> findByOrdonnance(Long idOrd);

    void addMedicamentToPrescription(Long idPr, Long idMct, Integer quantite);

    void removeMedicamentFromPrescription(Long idPr, Long idMct);

    void removeAllMedicamentsFromPrescription(Long idPr);

    List<Medicament> getMedicamentsOfPrescription(Long idPr);

    List<PrescriptionMedicament> getLignes(Long idPr);
}
