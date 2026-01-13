package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Acte;

import java.util.List;
import java.util.Optional;

public interface ActeRepository {

    Acte save(Acte acte);
    Acte update(Acte acte);

    // ✅ update ciblé (éviter update full)
    boolean updatePrixDeBase(Long idActe, Double prixDeBase);

    Optional<Acte> findById(Long idActe);
    List<Acte> findAll();
    boolean deleteById(Long idActe);

    List<Acte> findByCategorie(String categorie);
    List<Acte> findByLibelleContains(String keyword);

    // si tu veux garder ces signatures, fais-les déléguer vers les méthodes existantes
    List<Acte> findByCategorieIgnoreCase(String categorie);
    List<Acte> findByLibelleContainingIgnoreCase(String keyword);
}
