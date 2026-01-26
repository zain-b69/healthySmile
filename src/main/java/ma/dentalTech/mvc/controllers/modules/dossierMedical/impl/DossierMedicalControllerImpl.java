// Fichier: ma/dentalTech/mvc/controllers/modules/dossierMedical/impl/DossierMedicalControllerImpl.java
package ma.dentalTech.mvc.controllers.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.*;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.service.modules.dossierMedicale.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DossierMedicalControllerImpl implements DossierMedicalController {

    private final DossierMedicalService dossierService;
    private final ConsultationService consultationService;
    private final InterventionMedecinService interventionService;
    private final ActeService acteService;
    private final MedicamentService medicamentService;
    private final OrdonnanceService ordonnanceService;
    private final PrescriptionService prescriptionService;
    private final SituationFinanciereService situationService;
    private final CertificatService certificatService;

    public DossierMedicalControllerImpl(DossierMedicalService dossierService,
                                        ConsultationService consultationService,
                                        InterventionMedecinService interventionService,
                                        ActeService acteService,
                                        MedicamentService medicamentService,
                                        OrdonnanceService ordonnanceService,
                                        PrescriptionService prescriptionService,
                                        SituationFinanciereService situationService,
                                        CertificatService certificatService) {
        this.dossierService = dossierService;
        this.consultationService = consultationService;
        this.interventionService = interventionService;
        this.acteService = acteService;
        this.medicamentService = medicamentService;
        this.ordonnanceService = ordonnanceService;
        this.prescriptionService = prescriptionService;
        this.situationService = situationService;
        this.certificatService = certificatService;
    }

    // === DOSSIER MÉDICAL ===
    @Override
    public List<DossierMedical> listAll() {
        return dossierService.listAll();
    }

    @Override
    public DossierMedical getById(Long idDm) {
        return dossierService.getById(idDm);
    }

    @Override
    public DossierMedical getByPatient(Long idPatient) {
        return dossierService.getByPatient(idPatient);
    }

    @Override
    public DossierMedical ouvrirDossier(Long idPatient) {
        return dossierService.ouvrirDossier(idPatient);
    }

    @Override
    public DossierMedical getOuCreerDossier(Long idPatient) {
        return dossierService.getOuCreerDossier(idPatient);
    }

    @Override
    public List<DossierMedical> dossiersCreesEntre(LocalDate start, LocalDate end) {
        return dossierService.dossiersCreesEntre(start, end);
    }

    @Override
    public DossierMedical corrigerDateCreation(Long idDm, LocalDate nouvelleDate) {
        return dossierService.corrigerDateCreation(idDm, nouvelleDate);
    }

    // === CONSULTATION ===
    @Override
    public Consultation ouvrirConsultation(Long idDm, Long idMedecin, LocalDate date) {
        // Note: Dans votre code, ouvrirDepuisRdv nécessite un idRdv
        // Ici on crée une consultation sans RDV (cas d'urgence)
        // Vous devrez adapter si votre modèle ne le permet pas
        throw new UnsupportedOperationException("Utilisez ouvrirDepuisRdv via RDVController");
    }

    @Override
    public Consultation getConsultationById(Long idConsultation) {
        return consultationService.getById(idConsultation);
    }

    @Override
    public List<Consultation> getConsultationsByDossier(Long idDm) {
        return consultationService.historiqueDossier(idDm);
    }

    @Override
    public List<Consultation> getConsultationsByMedecin(Long idMedecin) {
        return consultationService.historiqueMedecin(idMedecin);
    }

    @Override
    public void changerStatutConsultation(Long idConsultation, StatutConsultation statut) {
        consultationService.changerStatut(idConsultation, statut);
    }

    @Override
    public void ajouterObservationConsultation(Long idConsultation, String observation) {
        consultationService.ajouterOuRemplacerObservation(idConsultation, observation);
    }

    // === NOUVELLE MÉTHODE: Création consultation manuelle ===
    @Override
    public Consultation creerConsultationManuelle(Long idDm, Long idMedecin,
                                                  LocalDate date, String observations) {
        // Vérifiez si votre ConsultationService a cette méthode
        // Sinon, vous devez l'ajouter à l'interface et l'implémenter
        return consultationService.creerConsultationManuelle(idDm, idMedecin, date, observations);
    }

    // === INTERVENTION ===
    @Override
    public InterventionMedecin ajouterIntervention(Long idConsultation, Long idActe, Integer numDent, Double prix) {
        return interventionService.ajouterIntervention(idConsultation, idActe, numDent, prix);
    }

    @Override
    public List<InterventionMedecin> getInterventionsByConsultation(Long idConsultation) {
        return interventionService.interventionsDeConsultation(idConsultation);
    }

    @Override
    public Double calculerTotalConsultation(Long idConsultation) {
        return interventionService.calculerTotalConsultation(idConsultation);
    }

    // === ACTE ===
    @Override
    public List<Acte> getAllActes() {
        return acteService.getAll();
    }

    @Override
    public Acte getActeById(Long idActe) {
        return acteService.getById(idActe);
    }

    @Override
    public Acte creerActe(String libelle, String categorie, Double prixDeBase) {
        return acteService.creerActeCatalogue(libelle, categorie, prixDeBase);
    }

    @Override
    public void modifierActe(Long idActe, String libelle, String categorie, Double prixDeBase) {
        acteService.modifierActeCatalogue(idActe, libelle, categorie, prixDeBase);
    }

    // === MÉDICAMENT ===
    @Override
    public List<Medicament> getAllMedicaments() {
        return medicamentService.getAll();
    }

    @Override
    public Medicament getMedicamentById(Long idMct) {
        return medicamentService.getById(idMct);
    }

    @Override
    public Medicament creerMedicament(String nom, String laboratoire, String type, String forme,
                                      Boolean remboursable, Double prixUnitaire, String description) {
        return medicamentService.enregistrerMedicament(nom, laboratoire, type, forme,
                remboursable, prixUnitaire, description);
    }

    // === ORDONNANCE ===
    @Override
    public Ordonnance creerOrdonnance(Long idDm, Long idConsultation, LocalDate date) {
        Ordonnance ord = new Ordonnance();
        ord.setIdDm(idDm);
        ord.setIdConsultation(idConsultation);
        ord.setDate(date != null ? date : LocalDate.now());

        try {
            return ordonnanceService.createOrdonnanceAvecPrescriptions(ord, null);
        } catch (Exception e) {
            throw new RuntimeException("Erreur création ordonnance", e);
        }
    }

    @Override
    public List<Ordonnance> getOrdonnancesByDossier(Long idDm) {
        return ordonnanceService.findByDossierMedical(idDm);
    }

    @Override
    public List<Ordonnance> getOrdonnancesByConsultation(Long idConsultation) {
        return ordonnanceService.findByConsultation(idConsultation);
    }

    // === NOUVELLES MÉTHODES ORDONNANCE ===
    @Override
    public Ordonnance getOrdonnanceById(Long idOrd) {
        // Vous devez ajouter getById à OrdonnanceService
        return ordonnanceService.getById(idOrd);
    }

    @Override
    public List<Ordonnance> getAllOrdonnances() {
        // Vous devez ajouter findAll à OrdonnanceService
        return ordonnanceService.findAll();
    }

    @Override
    public void deleteOrdonnance(Long idOrd) {
        // Vous devez ajouter deleteById à OrdonnanceService
        ordonnanceService.deleteById(idOrd);
    }

    @Override
    public Ordonnance updateOrdonnance(Long idOrd, LocalDate date, Long idConsultation) {
        Ordonnance ordonnance = getOrdonnanceById(idOrd);
        if (ordonnance != null) {
            ordonnance.setDate(date);
            ordonnance.setIdConsultation(idConsultation);
            // Vous devez ajouter update à OrdonnanceService
            return ordonnanceService.update(ordonnance);
        }
        return null;
    }

    // === PRESCRIPTION ===
    @Override
    public Prescription ajouterPrescription(Long idOrdonnance, String categorie, String frequence,
                                            Integer dureeJours, Integer quantite) {
        Prescription p = new Prescription();
        p.setIdOrdonnance(idOrdonnance);
        p.setCategorie(categorie);
        p.setFrequence(frequence);
        p.setDureeEnJours(dureeJours);
        p.setQuantite(quantite);

        try {
            return prescriptionService.addPrescriptionToOrdonnance(idOrdonnance, p);
        } catch (Exception e) {
            throw new RuntimeException("Erreur ajout prescription", e);
        }
    }

    @Override
    public List<Prescription> getPrescriptionsByOrdonnance(Long idOrdonnance) {
        return prescriptionService.findByOrdonnance(idOrdonnance);
    }

    // === NOUVELLES MÉTHODES PRESCRIPTION ===
    @Override
    public Prescription getPrescriptionById(Long idPr) {
        // Vous devez ajouter getById à PrescriptionService
        return prescriptionService.getById(idPr);
    }

    @Override
    public void deletePrescription(Long idPr) {
        // Vous devez ajouter deleteById à PrescriptionService
        prescriptionService.deleteById(idPr);
    }

    @Override
    public Prescription updatePrescription(Long idPr, String categorie, String frequence,
                                           Integer dureeJours, Integer quantite) {
        try {
            // Récupérer la prescription complète avec ses médicaments
            Prescription prescription = getPrescriptionById(idPr);
            if (prescription == null) {
                throw new RuntimeException("Prescription introuvable: id=" + idPr);
            }

            // IMPORTANT: S'assurer que les médicaments sont chargés
            if (prescription.getMedicaments() == null || prescription.getMedicaments().isEmpty()) {
                // Charger les médicaments associés via le service
                List<Medicament> meds = prescriptionService.getMedicaments(idPr);
                if (meds != null && !meds.isEmpty()) {
                    prescription.setMedicaments(new ArrayList<>(meds));
                } else {
                    // S'il n'y a vraiment pas de médicaments, créer une liste vide
                    prescription.setMedicaments(new ArrayList<>());
                }
            }

            // Mettre à jour les champs
            prescription.setCategorie(categorie);
            prescription.setFrequence(frequence);
            prescription.setDureeEnJours(dureeJours);
            prescription.setQuantite(quantite);

            // Sauvegarder
            return prescriptionService.update(prescription);

        } catch (Exception e) {
            throw new RuntimeException("Erreur modification prescription: " + e.getMessage(), e);
        }
    }


    // === CERTIFICAT ===
    @Override
    public Certificat genererCertificat(Long idConsultation, Long idDm, LocalDate dateDebut,
                                        LocalDate dateFin, String note) {
        return certificatService.genererCertificat(idDm, idConsultation, dateDebut, dateFin, note);
    }

    @Override
    public List<Certificat> getCertificatsByDossier(Long idDm) {
        return certificatService.historiqueDossier(idDm);
    }

    @Override
    public List<Certificat> getCertificatsByConsultation(Long idConsultation) {
        return certificatService.certificatsDeConsultation(idConsultation);
    }

    @Override
    public String genererTexteCertificat(Certificat certificat) {
        return certificatService.genererTexteCertificat(certificat);
    }

    // === SITUATION FINANCIÈRE ===
    @Override
    public SituationFinanciere getSituationByDossier(Long idDm) {
        return situationService.findByDossierMedical(idDm);
    }

    @Override
    public void enregistrerPaiement(Long idSf, Double montant) {
        situationService.enregistrerPaiement(idSf, montant);
    }

    @Override
    public void appliquerPromo(Long idSf, boolean enPromo) {
        situationService.appliquerPromo(idSf, enPromo);
    }

    @Override
    public Double calculerResteAPayer(Long idSf) {
        return situationService.calculerResteAPayer(idSf);
    }

    // === RECHERCHES ===
    @Override
    public List<Acte> rechercherActes(String keyword, String categorie) {
        return acteService.rechercher(keyword, categorie);
    }

    @Override
    public List<Medicament> rechercherMedicaments(String keywordNom, String laboratoire,
                                                  String type, Boolean remboursable) {
        return medicamentService.rechercher(keywordNom, laboratoire, type, remboursable);
    }
    @Override
    public Prescription ajouterPrescriptionAvecMedicament(Long idOrdonnance, String categorie, String frequence,
                                                          Integer dureeJours, Integer quantite, Long idMedicament) {
        System.out.println("DEBUG ajouterPrescriptionAvecMedicament: idOrdonnance=" + idOrdonnance +
                ", idMedicament=" + idMedicament);

        Prescription p = new Prescription();
        p.setIdOrdonnance(idOrdonnance);
        p.setCategorie(categorie);
        p.setFrequence(frequence);
        p.setDureeEnJours(dureeJours);
        p.setQuantite(quantite);

        // Récupérer le médicament
        Medicament medicament = medicamentService.getById(idMedicament);

        // Créer une liste avec le médicament
        List<Medicament> medicaments = new ArrayList<>();
        medicaments.add(medicament);
        p.setMedicaments(medicaments);

        try {
            return prescriptionService.addPrescriptionToOrdonnance(idOrdonnance, p);
        } catch (Exception e) {
            System.err.println("ERROR dans ajouterPrescriptionAvecMedicament: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur ajout prescription avec médicament", e);
        }
    }
    @Override
    public Prescription ajouterMedicamentAPrescription(Long idPr, Long idMedicament) {
        try {
            // Récupérer la prescription
            Prescription prescription = getPrescriptionById(idPr);

            // Récupérer le médicament
            Medicament medicament = medicamentService.getById(idMedicament);

            // Ajouter le médicament à la liste
            if (prescription.getMedicaments() == null) {
                prescription.setMedicaments(new ArrayList<>());
            }

            // Vérifier si le médicament n'est pas déjà présent
            boolean existeDeja = prescription.getMedicaments().stream()
                    .anyMatch(m -> m.getIdMct().equals(idMedicament));

            if (!existeDeja) {
                prescription.getMedicaments().add(medicament);
                return prescriptionService.update(prescription);
            } else {
                throw new IllegalArgumentException("Ce médicament est déjà associé à la prescription");
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur ajout médicament à prescription", e);
        }
    }

    @Override
    public Prescription retirerMedicamentDePrescription(Long idPr, Long idMedicament) {
        try {
            Prescription prescription = getPrescriptionById(idPr);

            if (prescription.getMedicaments() != null) {
                // Retirer le médicament de la liste
                prescription.getMedicaments().removeIf(m ->
                        m != null && m.getIdMct().equals(idMedicament));

                return prescriptionService.update(prescription);
            }

            return prescription;

        } catch (Exception e) {
            throw new RuntimeException("Erreur retrait médicament de prescription", e);
        }
    }
    @Override
    public void supprimerMedicament(Long idMct) {
        try {
            medicamentService.deleteById(idMct);
        } catch (Exception e) {
            throw new RuntimeException("Erreur suppression médicament", e);
        }
    }

    @Override
    public Medicament modifierMedicament(Long idMct, String nom, String laboratoire,
                                         String type, String forme, Boolean remboursable,
                                         Double prixUnitaire, String description) {
        return medicamentService.mettreAJourInfos(idMct, nom, laboratoire, type,
                forme, remboursable, prixUnitaire, description);
    }
    // === NOUVELLE MÉTHODE : Suppression dossier ===
    @Override
    public boolean supprimerDossier(Long idDm) {
        try {
            // Vérifier d'abord si le dossier existe
            DossierMedical dossier = dossierService.getById(idDm);
            if (dossier == null) {
                throw new RuntimeException("Dossier médical #" + idDm + " introuvable");
            }

            // Vérifier si le dossier a des consultations
            List<Consultation> consultations = consultationService.historiqueDossier(idDm);
            if (consultations != null && !consultations.isEmpty()) {
                throw new IllegalStateException("Impossible de supprimer le dossier #" + idDm +
                        " car il contient " + consultations.size() + " consultation(s)");
            }

            // Vérifier si le dossier a des ordonnances
            List<Ordonnance> ordonnances = ordonnanceService.findByDossierMedical(idDm);
            if (ordonnances != null && !ordonnances.isEmpty()) {
                throw new IllegalStateException("Impossible de supprimer le dossier #" + idDm +
                        " car il contient " + ordonnances.size() + " ordonnance(s)");
            }

            // Supprimer le dossier (vous devez ajouter deleteById à DossierMedicalService)
            return dossierService.deleteById(idDm);

        } catch (Exception e) {
            throw new RuntimeException("Erreur suppression dossier: " + e.getMessage(), e);
        }
    }
    @Override
    public boolean supprimerConsultation(Long idConsultation) {
        try {
            // Vérifier si la consultation existe
            Consultation consultation = consultationService.getById(idConsultation);
            if (consultation == null) {
                throw new RuntimeException("Consultation #" + idConsultation + " introuvable");
            }

            // Vérifier s'il y a des ordonnances associées
            List<Ordonnance> ordonnances = ordonnanceService.findByConsultation(idConsultation);
            if (ordonnances != null && !ordonnances.isEmpty()) {
                // Supprimer les ordonnances associées d'abord
                for (Ordonnance ord : ordonnances) {
                    ordonnanceService.deleteById(ord.getIdOrd());
                }
            }

            // Vérifier s'il y a des certificats associés
            List<Certificat> certificats = certificatService.certificatsDeConsultation(idConsultation);
            if (certificats != null && !certificats.isEmpty()) {
                // Supprimer les certificats associés
                for (Certificat cert : certificats) {
                    // Utiliser getIdCertif() au lieu de getIdCertificat()
                    boolean deleted = certificatService.deleteById(cert.getIdCertif());
                    if (!deleted) {
                        System.err.println("Échec suppression certificat: " + cert.getIdCertif());
                    }
                }
            }

            // Supprimer les interventions associées
            List<InterventionMedecin> interventions = interventionService.interventionsDeConsultation(idConsultation);
            if (interventions != null && !interventions.isEmpty()) {
                for (InterventionMedecin im : interventions) {
                    interventionService.deleteById(im.getIdIm());
                }
            }

            // Supprimer la consultation elle-même
            return consultationService.deleteById(idConsultation);

        } catch (Exception e) {
            throw new RuntimeException("Erreur suppression consultation: " + e.getMessage(), e);

        }
    }
    @Override
    public boolean supprimerCertificat(Long idCertif) {
        try {
            // Vérifier si le certificat existe
            Certificat certificat = getCertificatById(idCertif);
            if (certificat == null) {
                throw new RuntimeException("Certificat #" + idCertif + " introuvable");
            }

            // Supprimer le certificat
            return certificatService.deleteById(idCertif);

        } catch (Exception e) {
            throw new RuntimeException("Erreur suppression certificat: " + e.getMessage(), e);
        }
    }

    @Override
    public Certificat getCertificatById(Long idCertif) {
        return certificatService.getById(idCertif);
    }

    @Override
    public boolean modifierCertificat(Long idCertif, LocalDate dateDebut, LocalDate dateFin, String note) {
        try {
            // Mettre à jour la période
            Certificat certif = certificatService.mettreAJourPeriode(idCertif, dateDebut, dateFin);

            // Mettre à jour la note si fournie
            if (note != null && !note.trim().isEmpty()) {
                certif = certificatService.ajouterNoteMedecin(idCertif, note);
            }

            return certif != null;

        } catch (Exception e) {
            throw new RuntimeException("Erreur modification certificat: " + e.getMessage(), e);
        }
    }

}