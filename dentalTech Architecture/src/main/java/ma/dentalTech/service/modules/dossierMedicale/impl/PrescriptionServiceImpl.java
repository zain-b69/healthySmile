package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.entities.dossierMedical.PrescriptionMedicament;
import ma.dentalTech.mvc.dto.PrescriptionSummaryDTO;
import ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepo;
import ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.PrescriptionService;

import java.sql.SQLException;
import java.util.*;

public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepo prescriptionRepo;
    private final OrdonnanceRepo ordonnanceRepo;

    public PrescriptionServiceImpl(PrescriptionRepo prescriptionRepo, OrdonnanceRepo ordonnanceRepo) {
        this.prescriptionRepo = Objects.requireNonNull(prescriptionRepo);
        this.ordonnanceRepo = Objects.requireNonNull(ordonnanceRepo);
    }

    // =========================
    // Minimal CRUD
    // =========================

    @Override
    public Prescription getById(Long idPr) {
        if (idPr == null) throw new IllegalArgumentException("idPr ne peut pas être null");
        Prescription p = prescriptionRepo.findById(idPr);
        if (p == null) throw new IllegalArgumentException("Prescription introuvable: id=" + idPr);
        return p;
    }

    @Override
    public void deleteById(Long idPr) {
        if (idPr == null) return;
        prescriptionRepo.deleteById(idPr);
    }

    // =========================
    // Finders
    // =========================

    @Override
    public List<Prescription> findByOrdonnance(Long idOrd) {
        if (idOrd == null) throw new IllegalArgumentException("idOrd ne peut pas être null");
        return prescriptionRepo.findByOrdonnance(idOrd);
    }

    @Override
    public List<Prescription> findPage(int limit, int offset) {
        if (limit <= 0) limit = 10;
        if (offset < 0) offset = 0;
        return prescriptionRepo.findPage(limit, offset);
    }

    // =========================
    // 1) Validation + cohérence
    // =========================

    @Override
    public void validateOrThrow(Prescription p) {
        if (p == null) throw new IllegalArgumentException("Prescription ne peut pas être null");

        validateRequiredFieldsOrThrow(p);
        validateNumbersOrThrow(p);
        validateContentOrThrow(p);
    }

    @Override
    public void validateForOrdonnanceOrThrow(Long idOrd, Prescription p) {
        if (idOrd == null) throw new IllegalArgumentException("idOrd ne peut pas être null");
        if (!ordonnanceRepo.existsById(idOrd)) {
            throw new IllegalArgumentException("Ordonnance introuvable: id=" + idOrd);
        }

        validateOrThrow(p);

        if (p.getIdOrdonnance() != null && !Objects.equals(p.getIdOrdonnance(), idOrd)) {
            throw new IllegalArgumentException("Prescription.idOrdonnance incohérent (attendu " + idOrd + ")");
        }
    }

    // =========================
    // 2) Gestion des médicaments
    // =========================

    @Override
    public void addMedicament(Long prescriptionId, Medicament med) {
        validateExistingOrThrow(prescriptionId);

        if (med == null) throw new IllegalArgumentException("med ne peut pas être null");
        if (med.getIdMct() == null) throw new IllegalArgumentException("med.idMct est obligatoire");

        addLine(prescriptionId, PrescriptionMedicament.builder()
                .idPr(prescriptionId)
                .idMct(med.getIdMct())
                .quantite(null)
                .build());
    }

    @Override
    public void removeMedicament(Long prescriptionId, Long medicamentId) {
        validateExistingOrThrow(prescriptionId);
        if (medicamentId == null) throw new IllegalArgumentException("medicamentId ne peut pas être null");
        prescriptionRepo.removeMedicamentFromPrescription(prescriptionId, medicamentId);
    }

    @Override
    public void replaceMedicaments(Long prescriptionId, List<Medicament> meds) {
        validateExistingOrThrow(prescriptionId);

        prescriptionRepo.removeAllMedicamentsFromPrescription(prescriptionId);
        if (meds == null || meds.isEmpty()) return;

        Set<Long> seen = new HashSet<>();
        for (Medicament m : meds) {
            if (m == null) continue;
            if (m.getIdMct() == null) throw new IllegalArgumentException("Medicament.idMct est obligatoire");

            if (!seen.add(m.getIdMct())) {
                throw new IllegalArgumentException("Médicament dupliqué dans la liste (idMct=" + m.getIdMct() + ")");
            }
            prescriptionRepo.addMedicamentToPrescription(prescriptionId, m.getIdMct(), null);
        }
    }

    // =========================
    // 2-bis) Pivot / lignes
    // =========================

    @Override
    public void addLine(Long prescriptionId, PrescriptionMedicament line) {
        validateExistingOrThrow(prescriptionId);

        if (line == null) throw new IllegalArgumentException("line ne peut pas être null");
        if (line.getIdMct() == null) throw new IllegalArgumentException("line.idMct est obligatoire");

        Integer q = line.getQuantite();
        if (q != null && q <= 0) throw new IllegalArgumentException("line.quantite doit être > 0");

        List<PrescriptionMedicament> existing = prescriptionRepo.getLignes(prescriptionId);
        boolean exists = existing.stream()
                .filter(Objects::nonNull)
                .anyMatch(l -> Objects.equals(l.getIdMct(), line.getIdMct()));
        if (exists) {
            throw new IllegalArgumentException("Ce médicament est déjà associé à la prescription (idMct=" + line.getIdMct() + ")");
        }

        prescriptionRepo.addMedicamentToPrescription(prescriptionId, line.getIdMct(), q);
    }

    @Override
    public void updateLineQuantity(Long prescriptionId, Long medicamentId, int quantite) {
        validateExistingOrThrow(prescriptionId);

        if (medicamentId == null) {
            throw new IllegalArgumentException("medicamentId ne peut pas être null");
        }
        if (quantite <= 0) {
            throw new IllegalArgumentException("quantite doit être > 0");
        }

        // ✅ Upsert : si update=0 => la ligne n'existe pas => on l'ajoute
        boolean updated = prescriptionRepo.updateQuantiteMedicament(prescriptionId, medicamentId, quantite);
        if (!updated) {
            prescriptionRepo.addMedicamentToPrescription(prescriptionId, medicamentId, quantite);
        }
    }


    @Override
    public List<Medicament> getMedicaments(Long idPr) {
        validateExistingOrThrow(idPr);
        return prescriptionRepo.getMedicamentsOfPrescription(idPr);
    }

    @Override
    public List<PrescriptionMedicament> getLignes(Long idPr) {
        validateExistingOrThrow(idPr);
        return prescriptionRepo.getLignes(idPr);
    }

    @Override
    public void clearMedicaments(Long idPr) {
        validateExistingOrThrow(idPr);
        prescriptionRepo.removeAllMedicamentsFromPrescription(idPr);
    }

    // =========================
    // 3) Calculs / synthèse
    // =========================

    @Override
    public int calculerQuantiteTotale(Long prescriptionId) {
        validateExistingOrThrow(prescriptionId);

        List<PrescriptionMedicament> lignes = prescriptionRepo.getLignes(prescriptionId);
        if (lignes != null && !lignes.isEmpty()) {
            int total = 0;
            for (PrescriptionMedicament l : lignes) {
                if (l == null) continue;
                Integer q = l.getQuantite();
                if (q != null) total += q;
            }
            return total;
        }

        Prescription p = getById(prescriptionId);
        return p.getQuantite() != null ? p.getQuantite() : 0;
    }

    @Override
    public PrescriptionSummaryDTO resumerPrescription(Long id) {
        Prescription p = getById(id);

        int nbMeds = compterMedicamentsIntern(id);

        return PrescriptionSummaryDTO.builder()
                .idPr(p.getIdPr())
                .idOrdonnance(p.getIdOrdonnance())
                .categorie(p.getCategorie())
                .frequence(p.getFrequence())
                .dureeEnJours(p.getDureeEnJours())
                .nbMedicaments(nbMeds)
                .quantiteTotale(calculerQuantiteTotale(id))
                .build();
    }

    // =========================
    // 4) Normalisation / enrichissement
    // =========================

    @Override
    public String normaliserFrequence(Prescription p) {
        if (p == null || isBlank(p.getFrequence())) return null;

        String f = p.getFrequence().trim().toLowerCase(Locale.ROOT);

        f = f.replace("fois", "x");
        f = f.replace("par jour", "/j");
        f = f.replace("chaque jour", "/j");

        f = f.replaceAll("\\s+", "");

        if (f.contains("matin") && f.contains("midi") && f.contains("soir")) return "3x/j";
        if (f.contains("matin") && f.contains("soir")) return "2x/j";

        return f;
    }

    @Override
    public List<Long> detecterDoublonsMeds(Prescription p) {
        if (p == null) return List.of();

        List<Long> ids = new ArrayList<>();

        if (p.getLignes() != null && !p.getLignes().isEmpty()) {
            for (PrescriptionMedicament l : p.getLignes()) {
                if (l == null) continue;
                if (l.getIdMct() != null) ids.add(l.getIdMct());
            }
        } else if (p.getMedicaments() != null && !p.getMedicaments().isEmpty()) {
            for (Medicament m : p.getMedicaments()) {
                if (m == null) continue;
                if (m.getIdMct() != null) ids.add(m.getIdMct());
            }
        }

        Set<Long> seen = new HashSet<>();
        Set<Long> dup = new LinkedHashSet<>();
        for (Long medId : ids) {
            if (medId == null) continue;
            if (!seen.add(medId)) dup.add(medId);
        }
        return new ArrayList<>(dup);
    }

    @Override
    public Prescription fusionnerPrescriptions(Prescription a, Prescription b) {
        if (a == null && b == null) return null;
        if (a == null) return clonePrescription(b);
        if (b == null) return clonePrescription(a);

        String categorie = !isBlank(a.getCategorie()) ? a.getCategorie() : b.getCategorie();
        String frequence = !isBlank(a.getFrequence()) ? a.getFrequence() : b.getFrequence();
        Integer duree = a.getDureeEnJours() != null ? a.getDureeEnJours() : b.getDureeEnJours();

        Prescription merged = Prescription.builder()
                .idOrdonnance(a.getIdOrdonnance() != null ? a.getIdOrdonnance() : b.getIdOrdonnance())
                .categorie(categorie)
                .frequence(frequence)
                .dureeEnJours(duree)
                .quantite(null)
                .build();

        Map<Long, PrescriptionMedicament> map = new LinkedHashMap<>();
        for (PrescriptionMedicament l : safeLines(a)) mergeLine(map, l);
        for (PrescriptionMedicament l : safeLines(b)) mergeLine(map, l);

        if (!map.isEmpty()) {
            merged.setLignes(new ArrayList<>(map.values()));
        } else {
            Map<Long, Medicament> meds = new LinkedHashMap<>();
            for (Medicament m : safeMeds(a)) if (m != null && m.getIdMct() != null) meds.putIfAbsent(m.getIdMct(), m);
            for (Medicament m : safeMeds(b)) if (m != null && m.getIdMct() != null) meds.putIfAbsent(m.getIdMct(), m);
            merged.setMedicaments(new ArrayList<>(meds.values()));
        }

        merged.setFrequence(normaliserFrequence(merged));
        return merged;
    }

    // =========================
    // Private helpers
    // =========================

    private void validateExistingOrThrow(Long idPr) {
        if (idPr == null) throw new IllegalArgumentException("idPr ne peut pas être null");
        if (!prescriptionRepo.existsById(idPr)) {
            throw new IllegalArgumentException("Prescription introuvable: id=" + idPr);
        }
    }

    private void validateRequiredFieldsOrThrow(Prescription p) {
        if (isBlank(p.getCategorie())) {
            throw new IllegalArgumentException("Prescription.categorie est obligatoire");
        }
        if (isBlank(p.getFrequence())) {
            throw new IllegalArgumentException("Prescription.frequence est obligatoire");
        }
    }

    private void validateNumbersOrThrow(Prescription p) {
        if (p.getQuantite() != null && p.getQuantite() <= 0) {
            throw new IllegalArgumentException("Prescription.quantite doit être > 0");
        }
        if (p.getDureeEnJours() != null && p.getDureeEnJours() <= 0) {
            throw new IllegalArgumentException("Prescription.dureeEnJours doit être > 0");
        }
    }

    private void validateContentOrThrow(Prescription p) {
        boolean hasMeds = p.getMedicaments() != null && !p.getMedicaments().isEmpty();
        boolean hasLines = p.getLignes() != null && !p.getLignes().isEmpty();

        if (!hasMeds && !hasLines) {
            throw new IllegalArgumentException("Prescription doit contenir au moins un médicament (medicaments ou lignes)");
        }

        if (hasLines) {
            Set<Long> seen = new HashSet<>();
            for (PrescriptionMedicament l : p.getLignes()) {
                if (l == null) continue;

                if (l.getIdMct() == null) {
                    throw new IllegalArgumentException("Ligne: idMct est obligatoire");
                }
                if (!seen.add(l.getIdMct())) {
                    throw new IllegalArgumentException("Ligne dupliquée: même idMct=" + l.getIdMct());
                }
                if (l.getQuantite() != null && l.getQuantite() <= 0) {
                    throw new IllegalArgumentException("Ligne: quantite doit être > 0");
                }
            }
        }
    }

    private int compterMedicamentsIntern(Long idPr) {
        List<PrescriptionMedicament> lignes = prescriptionRepo.getLignes(idPr);
        if (lignes != null && !lignes.isEmpty()) {
            return (int) lignes.stream().filter(Objects::nonNull).count();
        }
        List<Medicament> meds = prescriptionRepo.getMedicamentsOfPrescription(idPr);
        return (meds != null) ? meds.size() : 0;
    }

    private void mergeLine(Map<Long, PrescriptionMedicament> map, PrescriptionMedicament l) {
        if (l == null || l.getIdMct() == null) return;

        PrescriptionMedicament existing = map.get(l.getIdMct());
        if (existing == null) {
            map.put(l.getIdMct(), PrescriptionMedicament.builder()
                    .idMct(l.getIdMct())
                    .quantite(l.getQuantite())
                    .build());
            return;
        }

        Integer q1 = existing.getQuantite();
        Integer q2 = l.getQuantite();
        if (q1 != null && q2 != null) existing.setQuantite(q1 + q2);
        else if (q1 == null) existing.setQuantite(q2);
    }

    private List<PrescriptionMedicament> safeLines(Prescription p) {
        return (p != null && p.getLignes() != null) ? p.getLignes() : List.of();
    }

    private List<Medicament> safeMeds(Prescription p) {
        return (p != null && p.getMedicaments() != null) ? p.getMedicaments() : List.of();
    }

    private Prescription clonePrescription(Prescription p) {
        Prescription c = Prescription.builder()
                .idOrdonnance(p.getIdOrdonnance())
                .categorie(p.getCategorie())
                .frequence(p.getFrequence())
                .dureeEnJours(p.getDureeEnJours())
                .quantite(p.getQuantite())
                .build();
        if (p.getMedicaments() != null) c.setMedicaments(new ArrayList<>(p.getMedicaments()));
        if (p.getLignes() != null) c.setLignes(new ArrayList<>(p.getLignes()));
        return c;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    public Prescription update(Prescription prescription) {
        if (prescription == null) throw new IllegalArgumentException("Prescription ne peut pas être null");
        if (prescription.getIdPr() == null) throw new IllegalArgumentException("idPr est obligatoire pour update");

        validateOrThrow(prescription);
        validateExistingOrThrow(prescription.getIdPr());

        return prescriptionRepo.update(prescription);
    }

    @Override
    public Prescription addPrescriptionToOrdonnance(Long idOrdonnance, Prescription p) throws SQLException {
        if (idOrdonnance == null) throw new IllegalArgumentException("idOrdonnance ne peut pas être null");
        if (p == null) throw new IllegalArgumentException("Prescription ne peut pas être null");

        // Validation de l'ordonnance existante
        validateExistingOrdonnanceOrThrow(idOrdonnance);

        // Validation de la prescription
        validateForOrdonnanceOrThrow(idOrdonnance, p);

        // S'assurer que l'ID de l'ordonnance est bien défini
        p.setIdOrdonnance(idOrdonnance);

        // Création dans la base de données
        return prescriptionRepo.create(p);
    }

    private void validateExistingOrdonnanceOrThrow(Long ordonnanceId) {
        if (!ordonnanceRepo.existsById(ordonnanceId)) {
            throw new IllegalArgumentException("Ordonnance introuvable: id=" + ordonnanceId);
        }
    }

}
