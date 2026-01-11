package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Acte;

import java.util.List;

public interface ActeService {

    // --- Métier : gestion du catalogue d'actes ---
    Acte creerActeCatalogue(String libelle, String categorie, Double prixDeBase);

    Acte modifierActeCatalogue(Long idActe, String libelle, String categorie, Double prixDeBase);

    Acte changerPrixDeBase(Long idActe, Double nouveauPrixDeBase);

    // --- Métier : recherche / filtres (pour UI) ---
    List<Acte> rechercherParCategorie(String categorie);     // ignore-case
    List<Acte> rechercherParLibelle(String keyword);         // contains ignore-case
    List<Acte> rechercher(String keyword, String categorie); // combinaison

    // --- Métier : contrôle ---
    boolean existeActeSimilaire(String libelle, String categorie);

    // --- Utilitaires (pour UI / contrôleurs) ---
    Acte getById(Long idActe);
    List<Acte> getAll();
    boolean supprimer(Long idActe);
}
