package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.entities.dossierMedical.PrescriptionMedicament;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

/**
 * DAO JDBC - Prescription.
 */
public interface PrescriptionRepo extends CrudRepository<Prescription, Long> {

    List<Prescription> findByOrdonnance(Long idOrd);

    // --- Many-to-many : prescription <-> medicament (table pivot prescription_medicament) ---
    void addMedicamentToPrescription(Long idPr, Long idMct, Integer quantite);

    void removeMedicamentFromPrescription(Long idPr, Long idMct);

    void removeAllMedicamentsFromPrescription(Long idPr);

    List<Medicament> getMedicamentsOfPrescription(Long idPr);

    List<PrescriptionMedicament> getLignes(Long idPr);

    boolean existsById(Long id);

    long count();

    List<Prescription> findPage(int limit, int offset);
}
