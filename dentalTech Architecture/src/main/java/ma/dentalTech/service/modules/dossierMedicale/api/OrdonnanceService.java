package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;

import java.sql.SQLException;
import java.util.List;

public interface OrdonnanceService {

    // ---- minimal CRUD utile côté API / use-cases
    Ordonnance getById(Long idOrd);
    void deleteById(Long idOrd);

    // ---- finders utiles
    List<Ordonnance> findByDossierMedical(Long idDm);
    List<Ordonnance> findByConsultation(Long idConsultation);
    List<Ordonnance> findPage(int limit, int offset);
    List<Prescription> getPrescriptions(Long idOrd);

    // ---- use cases
    Ordonnance createOrdonnanceAvecPrescriptions(Ordonnance ordonnance, List<Prescription> prescriptions) throws SQLException;
    Prescription addPrescriptionToOrdonnance(Long ordonnanceId, Prescription prescription) throws SQLException;

    // ---- métier: validation + cohérence
    void validateOrThrow(Ordonnance ordonnance);
    void validateExistingOrThrow(Long idOrd);
    void validatePrescriptionsOrThrow(List<Prescription> prescriptions);
}
