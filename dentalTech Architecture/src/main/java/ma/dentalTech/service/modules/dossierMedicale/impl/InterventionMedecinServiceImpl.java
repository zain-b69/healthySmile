package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository;
import ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.InterventionMedecinService;

import java.util.List;

public class InterventionMedecinServiceImpl implements InterventionMedecinService {

    private final InterventionMedecinRepo interventionRepo;
    private final ActeRepository acteRepository;

    public InterventionMedecinServiceImpl(InterventionMedecinRepo interventionRepo,
                                          ActeRepository acteRepository) {
        if (interventionRepo == null) throw new IllegalArgumentException("InterventionMedecinRepo requis");
        if (acteRepository == null) throw new IllegalArgumentException("ActeRepository requis");
        this.interventionRepo = interventionRepo;
        this.acteRepository = acteRepository;
    }

    // -------------------------
    // Use-case 1 : ajouter intervention
    // -------------------------

    @Override
    public InterventionMedecin ajouterIntervention(Long idConsultation,
                                                   Long idActe,
                                                   Integer numDent,
                                                   Double prixDePatient) {

        if (idConsultation == null) throw new IllegalArgumentException("idConsultation requis");
        if (idActe == null) throw new IllegalArgumentException("idActe requis");

        validateNumDent(numDent);

        // règle métier : si prixPatient non fourni -> utiliser prix de base de l'acte
        Double prixFinal = prixDePatient;
        if (prixFinal == null) {
            Acte acte = acteRepository.findById(idActe)
                    .orElseThrow(() -> new RuntimeException("Acte introuvable: " + idActe));
            prixFinal = acte.getPrixDeBase();
        }

        if (prixFinal != null && prixFinal < 0) {
            throw new IllegalArgumentException("prixDePatient invalide");
        }

        InterventionMedecin im = new InterventionMedecin();
        im.setIdIm(null);
        im.setIdConsultation(idConsultation);
        im.setIdActe(idActe);
        im.setNumDent(numDent);
        im.setPrixDePatient(prixFinal);

        return interventionRepo.save(im);
    }

    // -------------------------
    // Use-case 2 : ajuster prix patient
    // -------------------------

    @Override
    public InterventionMedecin changerPrixPatient(Long idIm, Double nouveauPrix) {
        if (idIm == null) throw new IllegalArgumentException("idIm requis");

        if (nouveauPrix != null && nouveauPrix < 0) {
            throw new IllegalArgumentException("prixDePatient invalide");
        }

        InterventionMedecin im = getById(idIm);
        im.setPrixDePatient(nouveauPrix);

        return interventionRepo.update(im);
    }

    // -------------------------
    // Use-case 3 : total consultation
    // -------------------------

    @Override
    public double calculerTotalConsultation(Long idConsultation) {
        if (idConsultation == null) throw new IllegalArgumentException("idConsultation requis");

        List<InterventionMedecin> list = interventionRepo.findByIdConsultation(idConsultation);

        double total = 0.0;
        for (InterventionMedecin im : list) {
            if (im.getPrixDePatient() != null) {
                total += im.getPrixDePatient();
            }
        }
        return total;
    }

    // -------------------------
    // Use-case 4 : listes
    // -------------------------

    @Override
    public List<InterventionMedecin> interventionsDeConsultation(Long idConsultation) {
        if (idConsultation == null) throw new IllegalArgumentException("idConsultation requis");
        return interventionRepo.findByIdConsultation(idConsultation);
    }

    @Override
    public List<InterventionMedecin> interventionsDeConsultationPourDent(Long idConsultation, Integer numDent) {
        if (idConsultation == null) throw new IllegalArgumentException("idConsultation requis");
        validateNumDent(numDent);
        return interventionRepo.findByIdConsultationAndNumDent(idConsultation, numDent);
    }

    // -------------------------
    // Utilitaires
    // -------------------------

    @Override
    public InterventionMedecin getById(Long idIm) {
        if (idIm == null) throw new IllegalArgumentException("idIm requis");
        return interventionRepo.findById(idIm)
                .orElseThrow(() -> new RuntimeException("Intervention introuvable: " + idIm));
    }

    // -------------------------
    // Helpers métier
    // -------------------------

    private void validateNumDent(Integer numDent) {
        // numDent peut être null (selon ton repo qui accepte null)
        if (numDent == null) return;

        // Règle métier "cabinet" classique : 1..32 (adulte).
        // Si ton prof veut 1..28 ou autre, adapte ici.
        if (numDent < 1 || numDent > 32) {
            throw new IllegalArgumentException("numDent invalide (1..32)");
        }
    }
}
