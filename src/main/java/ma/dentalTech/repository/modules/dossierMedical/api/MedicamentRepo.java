package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Medicament;

import java.util.List;
import java.util.Optional;

public interface MedicamentRepo {

    Medicament save(Medicament medicament);
    Medicament update(Medicament medicament);

    Optional<Medicament> findById(Long idMct);
    List<Medicament> findAll();
    boolean deleteById(Long idMct);

    List<Medicament> findByNomContains(String keyword);
    List<Medicament> findByLaboratoire(String laboratoire);
    List<Medicament> findByType(String type);
    List<Medicament> findByRemboursable(boolean remboursable);
}
