package ma.dentalTech.service.modules.dossierMedicale.api;

import ma.dentalTech.entities.dossierMedical.Medicament;

import java.util.List;

public interface MedicamentService {

    // ---- Métier : enregistrement avec règles ----
    Medicament enregistrerMedicament(String nom,
                                     String laboratoire,
                                     String type,
                                     String forme,
                                     Boolean remboursable,
                                     Double prixUnitaire,
                                     String description);

    Medicament mettreAJourInfos(Long idMct,
                                String nom,
                                String laboratoire,
                                String type,
                                String forme,
                                Boolean remboursable,
                                Double prixUnitaire,
                                String description);

    // ---- Métier : modification ciblée ----
    Medicament changerPrix(Long idMct, Double nouveauPrix);
    Medicament changerRemboursement(Long idMct, Boolean remboursable);

    // ---- Métier : recherches ----
    List<Medicament> rechercherParNom(String keyword);
    List<Medicament> rechercherParLaboratoire(String laboratoire);
    List<Medicament> rechercherParType(String type);
    List<Medicament> rechercherParRemboursable(boolean remboursable);

    // filtre combiné (use-case UI)
    List<Medicament> rechercher(String keywordNom,
                                String laboratoire,
                                String type,
                                Boolean remboursable);

    // ---- Contrôle ----
    boolean existeMedicamentSimilaire(String nom, String laboratoire, String forme);

    // ---- Utilitaires ----
    Medicament getById(Long idMct);
    List<Medicament> getAll();
}
