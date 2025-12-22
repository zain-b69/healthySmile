package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Medicament;

import java.util.List;

public interface MedicamentService {
    Medicament create(Medicament medicament);
    Medicament update(Medicament medicament);
    Medicament getById(Long idMct);
    List<Medicament> getAll();
    boolean delete(Long idMct);

    List<Medicament> searchByNom(String keyword);
    List<Medicament> getByLaboratoire(String laboratoire);
    List<Medicament> getByType(String type);
    List<Medicament> getByRemboursable(boolean remboursable);
}
