package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.DossierMedicalService;

import java.time.LocalDate;
import java.util.List;

public class DossierMedicalServiceImpl implements DossierMedicalService {

    private final DossierMedicalRepo dossierMedicalRepo;

    public DossierMedicalServiceImpl(DossierMedicalRepo dossierMedicalRepo) {
        if (dossierMedicalRepo == null) throw new IllegalArgumentException("DossierMedicalRepo requis");
        this.dossierMedicalRepo = dossierMedicalRepo;
    }

    // -------------------------
    // Use-case 1 : ouvrir dossier
    // -------------------------

    @Override
    public DossierMedical ouvrirDossier(Long idPatient) {
        if (idPatient == null) throw new IllegalArgumentException("idPatient requis");

        // règle métier: 1 patient -> 1 dossier
        if (dossierMedicalRepo.findByIdPatient(idPatient).isPresent()) {
            throw new IllegalStateException("DossierMedical déjà existant pour patient: " + idPatient);
        }

        DossierMedical dm = new DossierMedical();
        dm.setIdDm(null);
        dm.setIdPatient(idPatient);

        // règle métier : date de création automatique si absente
        dm.setDateDeCreation(LocalDate.now());

        return dossierMedicalRepo.save(dm);
    }

    // -------------------------
    // Use-case 2 : get ou créer
    // -------------------------

    @Override
    public DossierMedical getOuCreerDossier(Long idPatient) {
        if (idPatient == null) throw new IllegalArgumentException("idPatient requis");

        return dossierMedicalRepo.findByIdPatient(idPatient)
                .orElseGet(() -> {
                    DossierMedical dm = new DossierMedical();
                    dm.setIdDm(null);
                    dm.setIdPatient(idPatient);
                    dm.setDateDeCreation(LocalDate.now());
                    return dossierMedicalRepo.save(dm);
                });
    }

    // -------------------------
    // Use-case 3 : existence
    // -------------------------

    @Override
    public boolean patientADossier(Long idPatient) {
        if (idPatient == null) throw new IllegalArgumentException("idPatient requis");
        return dossierMedicalRepo.findByIdPatient(idPatient).isPresent();
    }

    // -------------------------
    // Use-case 4 : recherche période
    // -------------------------

    @Override
    public List<DossierMedical> dossiersCreesEntre(LocalDate start, LocalDate end) {
        if (start == null || end == null) throw new IllegalArgumentException("Dates start/end requises");
        if (end.isBefore(start)) throw new IllegalArgumentException("Intervalle invalide: end < start");

        return dossierMedicalRepo.findByDateCreationBetween(start, end);
    }

    // -------------------------
    // Use-case 5 : accès ciblé
    // -------------------------

    @Override
    public DossierMedical getByPatient(Long idPatient) {
        if (idPatient == null) throw new IllegalArgumentException("idPatient requis");

        return dossierMedicalRepo.findByIdPatient(idPatient)
                .orElseThrow(() -> new RuntimeException("DossierMedical introuvable pour patient: " + idPatient));
    }

    @Override
    public DossierMedical getById(Long idDm) {
        if (idDm == null) throw new IllegalArgumentException("idDm requis");

        return dossierMedicalRepo.findById(idDm)
                .orElseThrow(() -> new RuntimeException("DossierMedical introuvable: " + idDm));
    }

    // -------------------------
    // Optionnel : corriger date création
    // -------------------------

    @Override
    public DossierMedical corrigerDateCreation(Long idDm, LocalDate nouvelleDate) {
        if (idDm == null) throw new IllegalArgumentException("idDm requis");
        if (nouvelleDate == null) throw new IllegalArgumentException("nouvelleDate requise");

        DossierMedical dm = getById(idDm);

        // règle métier simple: une date de création ne doit pas être dans le futur
        if (nouvelleDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("dateDeCreation invalide (future)");
        }

        dossierMedicalRepo.updateDateCreation(idDm, nouvelleDate);
        return getById(idDm);
    }

    @Override
    public List<DossierMedical> listAll() {
        return dossierMedicalRepo.findAll();
    }

    // NOUVELLE MÉTHODE
    @Override
    public boolean deleteById(Long idDm) {
        if (idDm == null) {
            throw new IllegalArgumentException("idDm requis");
        }

        // Vérifier si le dossier existe
        if (!dossierMedicalRepo.findById(idDm).isPresent()) {
            throw new RuntimeException("Dossier médical #" + idDm + " introuvable");
        }

        // Supprimer le dossier
        return dossierMedicalRepo.deleteById(idDm);
    }
}
