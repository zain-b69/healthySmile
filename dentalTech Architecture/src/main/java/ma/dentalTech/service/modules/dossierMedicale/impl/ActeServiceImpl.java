package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository;
import ma.dentalTech.service.modules.dossierMedicale.api.ActeService;

import java.util.ArrayList;
import java.util.List;

public class ActeServiceImpl implements ActeService {

    private final ActeRepository acteRepository;

    public ActeServiceImpl(ActeRepository acteRepository) {
        if (acteRepository == null) throw new IllegalArgumentException("ActeRepository requis");
        this.acteRepository = acteRepository;
    }

    // -------------------------
    // Métier : Catalogue d'actes
    // -------------------------

    @Override
    public Acte creerActeCatalogue(String libelle, String categorie, Double prixDeBase) {
        String lib = normalizeRequiredText(libelle, "libelle requis");
        String cat = normalizeRequiredText(categorie, "categorie requise");
        Double prix = validatePrix(prixDeBase);

        // Anti-doublon métier : même (libelle,categorie) déjà existant
        if (existeActeSimilaire(lib, cat)) {
            throw new IllegalStateException("Acte déjà existant (libelle + categorie)");
        }

        Acte a = new Acte();
        a.setIdActe(null);
        a.setLibelle(lib);
        a.setCategorie(cat);
        a.setPrixDeBase(prix);

        return acteRepository.save(a);
    }

    @Override
    public Acte modifierActeCatalogue(Long idActe, String libelle, String categorie, Double prixDeBase) {
        if (idActe == null) throw new IllegalArgumentException("idActe requis");

        Acte current = getById(idActe);

        String lib = normalizeRequiredText(libelle, "libelle requis");
        String cat = normalizeRequiredText(categorie, "categorie requise");
        Double prix = validatePrix(prixDeBase);

        boolean changeKey =
                !safe(current.getLibelle()).equalsIgnoreCase(lib) ||
                        !safe(current.getCategorie()).equalsIgnoreCase(cat);

        if (changeKey && existeActeSimilaire(lib, cat)) {
            throw new IllegalStateException("Un acte similaire existe déjà (libelle + categorie)");
        }

        current.setLibelle(lib);
        current.setCategorie(cat);
        current.setPrixDeBase(prix);

        return acteRepository.update(current);
    }

    @Override
    public Acte changerPrixDeBase(Long idActe, Double nouveauPrixDeBase) {
        if (idActe == null) throw new IllegalArgumentException("idActe requis");

        Acte current = getById(idActe);
        current.setPrixDeBase(validatePrix(nouveauPrixDeBase));

        return acteRepository.update(current);
    }

    // -------------------------
    // Métier : Recherche / filtres
    // -------------------------

    @Override
    public List<Acte> rechercherParCategorie(String categorie) {
        String cat = normalizeRequiredText(categorie, "categorie requise");

        // IMPORTANT: on utilise findByCategorie (déjà ignore-case dans ton SQL)
        return acteRepository.findByCategorie(cat);
    }

    @Override
    public List<Acte> rechercherParLibelle(String keyword) {
        String kw = normalizeRequiredText(keyword, "keyword requis");

        // IMPORTANT: on utilise findByLibelleContains (déjà ignore-case dans ton SQL)
        return acteRepository.findByLibelleContains(kw);
    }

    @Override
    public List<Acte> rechercher(String keyword, String categorie) {
        boolean hasKw = keyword != null && !keyword.isBlank();
        boolean hasCat = categorie != null && !categorie.isBlank();

        if (!hasKw && !hasCat) {
            return acteRepository.findAll();
        }

        List<Acte> base;

        if (hasCat) {
            base = rechercherParCategorie(categorie);
        } else {
            base = acteRepository.findAll();
        }

        if (hasKw) {
            String kw = keyword.trim().toLowerCase();
            List<Acte> filtered = new ArrayList<>();
            for (Acte a : base) {
                if (safe(a.getLibelle()).toLowerCase().contains(kw)) {
                    filtered.add(a);
                }
            }
            return filtered;
        }

        return base;
    }

    // -------------------------
    // Métier : contrôle / règles
    // -------------------------

    @Override
    public boolean existeActeSimilaire(String libelle, String categorie) {
        String lib = normalizeRequiredText(libelle, "libelle requis");
        String cat = normalizeRequiredText(categorie, "categorie requise");

        // Stratégie: catégorie (ignore-case déjà fait en SQL), puis comparaison libellé
        List<Acte> acts = acteRepository.findByCategorie(cat);
        for (Acte a : acts) {
            if (safe(a.getLibelle()).equalsIgnoreCase(lib)) return true;
        }
        return false;
    }

    // -------------------------
    // Utilitaires UI
    // -------------------------

    @Override
    public Acte getById(Long idActe) {
        if (idActe == null) throw new IllegalArgumentException("idActe requis");
        return acteRepository.findById(idActe)
                .orElseThrow(() -> new RuntimeException("Acte introuvable: " + idActe));
    }

    @Override
    public List<Acte> getAll() {
        return acteRepository.findAll();
    }

    @Override
    public boolean supprimer(Long idActe) {
        if (idActe == null) throw new IllegalArgumentException("idActe requis");
        getById(idActe);
        return acteRepository.deleteById(idActe);
    }

    // -------------------------
    // Helpers
    // -------------------------

    private String normalizeRequiredText(String value, String errorMessage) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(errorMessage);
        return value.trim();
    }

    private Double validatePrix(Double prix) {
        if (prix == null) throw new IllegalArgumentException("prixDeBase requis");
        if (prix < 0) throw new IllegalArgumentException("prixDeBase invalide");
        return prix;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
