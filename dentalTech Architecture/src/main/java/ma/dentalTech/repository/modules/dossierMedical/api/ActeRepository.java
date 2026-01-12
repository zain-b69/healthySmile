package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Acte;
import java.util.List;
import java.util.Optional;

public interface ActeRepository {

    Acte save(Acte acte);                 // create
    Acte update(Acte acte);               // update
    Optional<Acte> findById(Long idActe);  // read
    List<Acte> findAll();                 // read all
    boolean deleteById(Long idActe);       // delete

    List<Acte> findByCategorie(String categorie);
    List<Acte> findByLibelleContains(String keyword);

    List<Acte> findByCategorieIgnoreCase(String categorie);

    List<Acte> findByLibelleContainingIgnoreCase(String keyword);
}
