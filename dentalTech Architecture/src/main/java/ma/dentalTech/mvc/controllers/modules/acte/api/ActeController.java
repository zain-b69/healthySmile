package ma.dentalTech.mvc.controllers.modules.acte.api;

import ma.dentalTech.entities.dossierMedical.Acte;
import java.util.List;

public interface ActeController {

    List<Acte> findAll();
    List<Acte> search(String keyword, String categorie);

    Acte create(String libelle, String categorie, Double prixDeBase);
    Acte update(Long idActe, String libelle, String categorie, Double prixDeBase);

    // update ciblé
    Acte updatePrix(Long idActe, Double nouveauPrix);

    boolean delete(Long idActe);
    Acte getById(Long idActe);
}
