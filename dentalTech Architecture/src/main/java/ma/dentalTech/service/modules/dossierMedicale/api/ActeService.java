package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Acte;

import java.util.List;

public interface ActeService {
    Acte create(Acte acte);
    Acte update(Acte acte);
    Acte getById(Long idActe);
    List<Acte> getAll();
    boolean delete(Long idActe);

    List<Acte> searchByCategorie(String categorie);
    List<Acte> searchByLibelle(String keyword);
}
