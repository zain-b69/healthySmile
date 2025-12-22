package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;

import java.sql.SQLException;
import java.util.List;

public interface OrdonnanceService {

    List<Ordonnance> findAll();

    Ordonnance findById(Long idOrd);

    void create(Ordonnance ordonnance) throws SQLException;

    void update(Ordonnance ordonnance);

    void deleteById(Long idOrd);

    List<Ordonnance> findByDossierMedical(Long idDm);

    List<Ordonnance> findByConsultation(Long idConsultation);

    List<Prescription> getPrescriptions(Long idOrd);
}
