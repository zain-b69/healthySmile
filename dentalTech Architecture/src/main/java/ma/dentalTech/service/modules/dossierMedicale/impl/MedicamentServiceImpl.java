package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.MedicamentService;

import java.util.ArrayList;
import java.util.List;

public class MedicamentServiceImpl implements MedicamentService {

    private final MedicamentRepo medicamentRepo;

    public MedicamentServiceImpl(MedicamentRepo medicamentRepo) {
        if (medicamentRepo == null) throw new IllegalArgumentException("MedicamentRepo requis");
        this.medicamentRepo = medicamentRepo;
    }

    // -------------------------
    // Métier : enregistrement
    // -------------------------

    @Override
    public Medicament enregistrerMedicament(String nom,
                                            String laboratoire,
                                            String type,
                                            String forme,
                                            Boolean remboursable,
                                            Double prixUnitaire,
                                            String description) {

        String n = normalizeRequiredText(nom, "nom requis");
        String lab = normalizeOptionalText(laboratoire);
        String t = normalizeOptionalText(type);
        String f = normalizeOptionalText(forme);
        String desc = normalizeOptionalText(description);

        validatePrix(prixUnitaire);

        // Anti-doublon "soft" (option prof)
        if (existeMedicamentSimilaire(n, lab, f)) {
            throw new IllegalStateException("Médicament déjà existant (nom + laboratoire + forme)");
        }

        Medicament m = new Medicament();
        m.setIdMct(null);
        m.setNom(n);
        m.setLaboratoire(lab);
        m.setType(t);
        m.setForme(f);
        m.setRemboursable(remboursable);
        m.setPrixUnitaire(prixUnitaire);
        m.setDescription(desc);

        return medicamentRepo.save(m);
    }

    @Override
    public Medicament mettreAJourInfos(Long idMct,
                                       String nom,
                                       String laboratoire,
                                       String type,
                                       String forme,
                                       Boolean remboursable,
                                       Double prixUnitaire,
                                       String description) {

        if (idMct == null) throw new IllegalArgumentException("idMct requis");

        Medicament current = getById(idMct);

        String n = normalizeRequiredText(nom, "nom requis");
        String lab = normalizeOptionalText(laboratoire);
        String t = normalizeOptionalText(type);
        String f = normalizeOptionalText(forme);
        String desc = normalizeOptionalText(description);

        validatePrix(prixUnitaire);

        boolean changeKey =
                !safe(current.getNom()).equalsIgnoreCase(n) ||
                        !safe(current.getLaboratoire()).equalsIgnoreCase(safe(lab)) ||
                        !safe(current.getForme()).equalsIgnoreCase(safe(f));

        if (changeKey && existeMedicamentSimilaire(n, lab, f)) {
            throw new IllegalStateException("Un médicament similaire existe déjà (nom + laboratoire + forme)");
        }

        current.setNom(n);
        current.setLaboratoire(lab);
        current.setType(t);
        current.setForme(f);
        current.setRemboursable(remboursable);
        current.setPrixUnitaire(prixUnitaire);
        current.setDescription(desc);

        return medicamentRepo.update(current);
    }

    // -------------------------
    // Métier : modifications ciblées
    // -------------------------

    @Override
    public Medicament changerPrix(Long idMct, Double nouveauPrix) {
        if (idMct == null) throw new IllegalArgumentException("idMct requis");
        validatePrix(nouveauPrix);

        Medicament m = getById(idMct);
        m.setPrixUnitaire(nouveauPrix);

        return medicamentRepo.update(m);
    }

    @Override
    public Medicament changerRemboursement(Long idMct, Boolean remboursable) {
        if (idMct == null) throw new IllegalArgumentException("idMct requis");

        Medicament m = getById(idMct);
        m.setRemboursable(remboursable);

        return medicamentRepo.update(m);
    }

    // -------------------------
    // Métier : recherches simples
    // -------------------------

    @Override
    public List<Medicament> rechercherParNom(String keyword) {
        String kw = normalizeRequiredText(keyword, "keyword requis");
        return medicamentRepo.findByNomContains(kw);
    }

    @Override
    public List<Medicament> rechercherParLaboratoire(String laboratoire) {
        String lab = normalizeRequiredText(laboratoire, "laboratoire requis");
        return medicamentRepo.findByLaboratoire(lab);
    }

    @Override
    public List<Medicament> rechercherParType(String type) {
        String t = normalizeRequiredText(type, "type requis");
        return medicamentRepo.findByType(t);
    }

    @Override
    public List<Medicament> rechercherParRemboursable(boolean remboursable) {
        return medicamentRepo.findByRemboursable(remboursable);
    }

    // -------------------------
    // Métier : recherche combinée (use-case UI)
    // -------------------------

    @Override
    public List<Medicament> rechercher(String keywordNom,
                                       String laboratoire,
                                       String type,
                                       Boolean remboursable) {

        boolean hasKw = keywordNom != null && !keywordNom.isBlank();
        boolean hasLab = laboratoire != null && !laboratoire.isBlank();
        boolean hasType = type != null && !type.isBlank();
        boolean hasRemb = remboursable != null;

        if (!hasKw && !hasLab && !hasType && !hasRemb) {
            return medicamentRepo.findAll();
        }

        // Stratégie : choisir une base la plus restrictive possible (évite de charger tout)
        List<Medicament> base;

        if (hasLab) {
            base = medicamentRepo.findByLaboratoire(laboratoire.trim());
        } else if (hasType) {
            base = medicamentRepo.findByType(type.trim());
        } else if (hasKw) {
            base = medicamentRepo.findByNomContains(keywordNom.trim());
        } else {
            base = medicamentRepo.findAll();
        }

        List<Medicament> filtered = new ArrayList<>();
        String kwLower = hasKw ? keywordNom.trim().toLowerCase() : null;
        String labLower = hasLab ? laboratoire.trim().toLowerCase() : null;
        String typeLower = hasType ? type.trim().toLowerCase() : null;

        for (Medicament m : base) {
            if (hasKw && !safe(m.getNom()).toLowerCase().contains(kwLower)) continue;
            if (hasLab && !safe(m.getLaboratoire()).toLowerCase().equals(labLower)) continue;
            if (hasType && !safe(m.getType()).toLowerCase().equals(typeLower)) continue;
            if (hasRemb) {
                // Attention: Boolean peut être null en BD, donc on compare proprement
                if (m.getRemboursable() == null || !m.getRemboursable().equals(remboursable)) continue;
            }
            filtered.add(m);
        }

        return filtered;
    }

    // -------------------------
    // Métier : anti-doublon
    // -------------------------

    @Override
    public boolean existeMedicamentSimilaire(String nom, String laboratoire, String forme) {
        String n = normalizeRequiredText(nom, "nom requis");
        String lab = normalizeOptionalText(laboratoire);
        String f = normalizeOptionalText(forme);

        // Stratégie simple: on cherche par nom, puis on compare labo+forme
        List<Medicament> list = medicamentRepo.findByNomContains(n);

        for (Medicament m : list) {
            boolean sameNom = safe(m.getNom()).equalsIgnoreCase(n);
            boolean sameLab = safe(m.getLaboratoire()).equalsIgnoreCase(safe(lab));
            boolean sameForme = safe(m.getForme()).equalsIgnoreCase(safe(f));

            if (sameNom && sameLab && sameForme) return true;
        }
        return false;
    }

    // -------------------------
    // Utilitaires
    // -------------------------

    @Override
    public Medicament getById(Long idMct) {
        if (idMct == null) throw new IllegalArgumentException("idMct requis");
        return medicamentRepo.findById(idMct)
                .orElseThrow(() -> new RuntimeException("Medicament introuvable: " + idMct));
    }

    @Override
    public List<Medicament> getAll() {
        return medicamentRepo.findAll();
    }

    // -------------------------
    // Helpers
    // -------------------------

    private String normalizeRequiredText(String value, String errorMessage) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(errorMessage);
        return value.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null) return null;
        String v = value.trim();
        return v.isBlank() ? null : v;
    }

    private void validatePrix(Double prix) {
        if (prix == null) return; // ton repo accepte null
        if (prix < 0) throw new IllegalArgumentException("prixUnitaire invalide");
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    @Override
    public void deleteById(Long idMct) {
        if (idMct == null) return;
        medicamentRepo.deleteById(idMct);
    }
}
