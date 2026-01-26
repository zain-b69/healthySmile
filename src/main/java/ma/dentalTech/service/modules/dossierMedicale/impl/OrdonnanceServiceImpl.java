package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.entities.dossierMedical.PrescriptionMedicament;
import ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepo;
import ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.OrdonnanceService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class OrdonnanceServiceImpl implements OrdonnanceService {

    private final OrdonnanceRepo ordonnanceRepo;
    private final PrescriptionRepo prescriptionRepo;

    public OrdonnanceServiceImpl(OrdonnanceRepo ordonnanceRepo, PrescriptionRepo prescriptionRepo) {
        this.ordonnanceRepo = Objects.requireNonNull(ordonnanceRepo);
        this.prescriptionRepo = Objects.requireNonNull(prescriptionRepo);
    }

    // =========================
    // Minimal CRUD utile
    // =========================

    @Override
    public Ordonnance getById(Long idOrd) {
        if (idOrd == null) throw new IllegalArgumentException("idOrd ne peut pas être null");
        Ordonnance o = ordonnanceRepo.findById(idOrd);
        if (o == null) throw new IllegalArgumentException("Ordonnance introuvable: id=" + idOrd);
        return o;
    }

    @Override
    public void deleteById(Long idOrd) {
        if (idOrd == null) return;
        ordonnanceRepo.deleteById(idOrd);
    }

    // =========================
    // Finders utiles
    // =========================

    @Override
    public List<Ordonnance> findByDossierMedical(Long idDm) {
        if (idDm == null) throw new IllegalArgumentException("idDm ne peut pas être null");
        return ordonnanceRepo.findByDossierMedical(idDm);
    }

    @Override
    public List<Ordonnance> findByConsultation(Long idConsultation) {
        if (idConsultation == null) throw new IllegalArgumentException("idConsultation ne peut pas être null");
        return ordonnanceRepo.findByConsultation(idConsultation);
    }

    @Override
    public List<Prescription> getPrescriptions(Long idOrd) {
        if (idOrd == null) throw new IllegalArgumentException("idOrd ne peut pas être null");
        return ordonnanceRepo.getPrescriptions(idOrd);
    }

    @Override
    public List<Ordonnance> findPage(int limit, int offset) {
        if (limit <= 0) limit = 10;
        if (offset < 0) offset = 0;
        return ordonnanceRepo.findPage(limit, offset);
    }

    // =========================
    // Use cases
    // =========================

    @Override
    public Ordonnance createOrdonnanceAvecPrescriptions(Ordonnance ordonnance, List<Prescription> prescriptions) throws SQLException {
        validateOrThrow(ordonnance);

        // default date si manquante
        if (ordonnance.getDate() == null) {
            ordonnance.setDate(LocalDate.now());
        }

        // choisir les prescriptions à valider/insérer
        List<Prescription> toInsert = (prescriptions != null && !prescriptions.isEmpty())
                ? prescriptions
                : ordonnance.getPrescriptions();

        validatePrescriptionsOrThrow(toInsert);

        // 1) créer ordonnance
        ordonnanceRepo.create(ordonnance);

        Long idOrd = ordonnance.getIdOrd();
        if (idOrd == null) {
            throw new IllegalStateException("idOrd est null après création ordonnance (vérifie RETURN_GENERATED_KEYS)");
        }

        // 2) insérer prescriptions liées
        if (toInsert != null && !toInsert.isEmpty()) {
            for (Prescription p : toInsert) {
                if (p == null) continue;
                p.setIdOrdonnance(idOrd);
                prescriptionRepo.create(p);
            }
        }

        return ordonnance;
    }

    @Override
    public Prescription addPrescriptionToOrdonnance(Long ordonnanceId, Prescription prescription) throws SQLException {
        if (ordonnanceId == null) throw new IllegalArgumentException("ordonnanceId ne peut pas être null");
        if (prescription == null) throw new IllegalArgumentException("prescription ne peut pas être null");

        validateExistingOrThrow(ordonnanceId);
        validatePrescriptionsOrThrow(List.of(prescription));

        prescription.setIdOrdonnance(ordonnanceId);
        prescriptionRepo.create(prescription);
        return prescription;
    }

    // =========================
    // Métier: validation + cohérence
    // =========================

    @Override
    public void validateOrThrow(Ordonnance ordonnance) {
        if (ordonnance == null) {
            throw new IllegalArgumentException("Ordonnance ne peut pas être null");
        }

        // au moins un lien fonctionnel
        if (ordonnance.getIdDm() == null && ordonnance.getIdConsultation() == null) {
            throw new IllegalArgumentException(
                    "Ordonnance doit être liée à un dossier médical (idDm) ou une consultation (idConsultation)"
            );
        }

        // date: pas future (règle simple)
        if (ordonnance.getDate() != null && ordonnance.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La date de l'ordonnance ne peut pas être dans le futur");
        }
    }

    @Override
    public void validateExistingOrThrow(Long idOrd) {
        if (idOrd == null) throw new IllegalArgumentException("idOrd ne peut pas être null");
        if (!ordonnanceRepo.existsById(idOrd)) {
            throw new IllegalArgumentException("Ordonnance introuvable: id=" + idOrd);
        }
    }

    @Override
    public void validatePrescriptionsOrThrow(List<Prescription> prescriptions) {
        if (prescriptions == null || prescriptions.isEmpty()) return;

        // 1) validation champs + cohérence
        for (Prescription p : prescriptions) {
            if (p == null) continue;

            // catégorie/frequence: obligatoires (selon ton modèle)
            if (isBlank(p.getCategorie())) {
                throw new IllegalArgumentException("Prescription.categorie est obligatoire");
            }
            if (isBlank(p.getFrequence())) {
                throw new IllegalArgumentException("Prescription.frequence est obligatoire");
            }

            // quantite/duree: si présents -> doivent être >0
            if (p.getQuantite() != null && p.getQuantite() <= 0) {
                throw new IllegalArgumentException("Prescription.quantite doit être > 0");
            }
            if (p.getDureeEnJours() != null && p.getDureeEnJours() <= 0) {
                throw new IllegalArgumentException("Prescription.dureeEnJours doit être > 0");
            }

            // au moins un médicament via medicaments ou via lignes
            boolean hasMeds = p.getMedicaments() != null && !p.getMedicaments().isEmpty();
            boolean hasLines = p.getLignes() != null && !p.getLignes().isEmpty();
            if (!hasMeds && !hasLines) {
                throw new IllegalArgumentException("Prescription doit contenir au moins un médicament (medicaments ou lignes)");
            }

            // cohérence: si lignes existent, pas de lignes vides/doublons
            if (hasLines) {
                validatePrescriptionLinesOrThrow(p.getLignes());
            }
        }

        // 2) cohérence globale: pas de doublon de médicament sur l'ordonnance (règle simple)
        // (si tu autorises le même médicament sur 2 prescriptions différentes, on enlève cette règle)
        Set<Object> medKeys = new HashSet<>();
        for (Prescription p : prescriptions) {
            if (p == null) continue;

            // via medicaments
            if (p.getMedicaments() != null) {
                for (Medicament m : p.getMedicaments()) {
                    if (m == null) continue;
                    Object key = buildMedicamentKey(m);
                    if (key != null && !medKeys.add(key)) {
                        throw new IllegalArgumentException("Médicament dupliqué détecté dans la même ordonnance");
                    }
                }
            }

            // via lignes
            if (p.getLignes() != null) {
                for (PrescriptionMedicament line : p.getLignes()) {
                    if (line == null) continue;
                    Object key = buildLineMedicamentKey(line);
                    if (key != null && !medKeys.add(key)) {
                        throw new IllegalArgumentException("Médicament dupliqué détecté dans la même ordonnance (via lignes)");
                    }
                }
            }
        }
    }

    private void validatePrescriptionLinesOrThrow(List<PrescriptionMedicament> lignes) {
        Set<Object> seen = new HashSet<>();
        for (PrescriptionMedicament l : lignes) {
            if (l == null) continue;

            Object key = buildLineMedicamentKey(l);
            if (key != null && !seen.add(key)) {
                throw new IllegalArgumentException("Même médicament dupliqué dans une prescription (lignes)");
            }

            // quantité par médicament si elle existe dans PrescriptionMedicament
            Integer q = tryGetQuantite(l);
            if (q != null && q <= 0) {
                throw new IllegalArgumentException("Quantité (ligne prescription) doit être > 0");
            }
        }
    }

    // -------- helpers clés (sans dépendre trop de tes entités)

    private Object buildMedicamentKey(Medicament m) {
        // on préfère un id si disponible, sinon fallback sur toString()
        Long id = tryGetLong(m, "getIdMed");
        if (id != null) return "MED#" + id;

        Long id2 = tryGetLong(m, "getId");
        if (id2 != null) return "MED#" + id2;

        String ts = String.valueOf(m);
        return (ts == null || ts.isBlank()) ? null : "MEDTS#" + ts;
    }

    private Object buildLineMedicamentKey(PrescriptionMedicament line) {
        // Essayer d'extraire un idMedicament ou l'objet medicament
        Long idMed = tryGetLong(line, "getIdMedicament");
        if (idMed != null) return "LMED#" + idMed;

        Object medObj = tryCall(line, "getMedicament");
        if (medObj instanceof Medicament m) return buildMedicamentKey(m);

        // fallback
        String ts = String.valueOf(line);
        return (ts == null || ts.isBlank()) ? null : "LTS#" + ts;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private Object tryCall(Object target, String methodName) {
        try {
            return target.getClass().getMethod(methodName).invoke(target);
        } catch (Exception ignored) {
            return null;
        }
    }

    private Long tryGetLong(Object target, String methodName) {
        Object v = tryCall(target, methodName);
        if (v instanceof Long l) return l;
        if (v instanceof Integer i) return i.longValue();
        return null;
    }

    private Integer tryGetQuantite(Object target) {
        Object v = tryCall(target, "getQuantite");
        if (v instanceof Integer i) return i;
        if (v instanceof Long l) return l.intValue();
        return null;
    }

    @Override
    public Ordonnance update(Ordonnance ordonnance) {
        if (ordonnance == null) throw new IllegalArgumentException("Ordonnance ne peut pas être null");
        if (ordonnance.getIdOrd() == null) throw new IllegalArgumentException("idOrd est obligatoire pour update");

        validateOrThrow(ordonnance);
        validateExistingOrThrow(ordonnance.getIdOrd());

        return ordonnanceRepo.update(ordonnance);
    }

    @Override
    public List<Ordonnance> findAll() {
        return ordonnanceRepo.findAll();
    }

}
