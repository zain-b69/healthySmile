package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.entities.dossierMedical.PrescriptionMedicament;
import ma.dentalTech.mvc.dto.PrescriptionSummaryDTO;

import java.util.List;

public interface PrescriptionService {

    // ---- minimal CRUD utile
    Prescription getById(Long idPr);
    void deleteById(Long idPr);

    // ---- finders
    List<Prescription> findByOrdonnance(Long idOrd);
    List<Prescription> findPage(int limit, int offset);

    // 1) Validation + cohérence
    void validateOrThrow(Prescription p);
    void validateForOrdonnanceOrThrow(Long idOrd, Prescription p);

    // 2) Gestion des médicaments (objets)
    void addMedicament(Long prescriptionId, Medicament med);
    void removeMedicament(Long prescriptionId, Long medicamentId);
    void replaceMedicaments(Long prescriptionId, List<Medicament> meds);

    // 2-bis) Gestion via pivot (lignes)
    void addLine(Long prescriptionId, PrescriptionMedicament line);
    void updateLineQuantity(Long prescriptionId, Long medicamentId, int quantite);

    List<Medicament> getMedicaments(Long idPr);
    List<PrescriptionMedicament> getLignes(Long idPr);
    void clearMedicaments(Long idPr);

    // 3) Calculs / synthèse
    int calculerQuantiteTotale(Long prescriptionId);
    PrescriptionSummaryDTO resumerPrescription(Long id);

    // 4) Normalisation / enrichissement
    String normaliserFrequence(Prescription p);
    List<Long> detecterDoublonsMeds(Prescription p);
    Prescription fusionnerPrescriptions(Prescription a, Prescription b);
}
