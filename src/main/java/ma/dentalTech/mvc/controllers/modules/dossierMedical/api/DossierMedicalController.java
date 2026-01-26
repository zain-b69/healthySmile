// Fichier: ma/dentalTech/mvc/controllers/modules/dossierMedical/api/DossierMedicalController.java
package ma.dentalTech.mvc.controllers.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.*;
import ma.dentalTech.entities.enums.StatutConsultation;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface DossierMedicalController {

    // === DOSSIER MÉDICAL ===
    List<DossierMedical> listAll();
    DossierMedical getById(Long idDm);
    DossierMedical getByPatient(Long idPatient);
    DossierMedical ouvrirDossier(Long idPatient) throws SQLException;
    DossierMedical getOuCreerDossier(Long idPatient);
    List<DossierMedical> dossiersCreesEntre(LocalDate start, LocalDate end);
    DossierMedical corrigerDateCreation(Long idDm, LocalDate nouvelleDate);

    // === CONSULTATION ===
    Consultation ouvrirConsultation(Long idDm, Long idMedecin, LocalDate date);
    Consultation getConsultationById(Long idConsultation);
    List<Consultation> getConsultationsByDossier(Long idDm);
    List<Consultation> getConsultationsByMedecin(Long idMedecin);
    void changerStatutConsultation(Long idConsultation, StatutConsultation statut);
    void ajouterObservationConsultation(Long idConsultation, String observation);

    // === INTERVENTION ===
    InterventionMedecin ajouterIntervention(Long idConsultation, Long idActe, Integer numDent, Double prix);
    List<InterventionMedecin> getInterventionsByConsultation(Long idConsultation);
    Double calculerTotalConsultation(Long idConsultation);

    // === ACTE (CATALOGUE) ===
    List<Acte> getAllActes();
    Acte getActeById(Long idActe);
    Acte creerActe(String libelle, String categorie, Double prixDeBase);
    void modifierActe(Long idActe, String libelle, String categorie, Double prixDeBase);

    // === MÉDICAMENT ===
    List<Medicament> getAllMedicaments();
    Medicament getMedicamentById(Long idMct);
    Medicament creerMedicament(String nom, String laboratoire, String type, String forme,
                               Boolean remboursable, Double prixUnitaire, String description);

    // === ORDONNANCE ===
    Ordonnance creerOrdonnance(Long idDm, Long idConsultation, LocalDate date);
    List<Ordonnance> getOrdonnancesByDossier(Long idDm);
    List<Ordonnance> getOrdonnancesByConsultation(Long idConsultation);

    // === PRESCRIPTION ===
    Prescription ajouterPrescription(Long idOrdonnance, String categorie, String frequence,
                                     Integer dureeJours, Integer quantite);
    List<Prescription> getPrescriptionsByOrdonnance(Long idOrdonnance);

    // === CERTIFICAT ===
    Certificat genererCertificat(Long idConsultation, Long idDm, LocalDate dateDebut,
                                 LocalDate dateFin, String note);
    List<Certificat> getCertificatsByDossier(Long idDm);
    List<Certificat> getCertificatsByConsultation(Long idConsultation);
    String genererTexteCertificat(Certificat certificat);

    // === SITUATION FINANCIÈRE ===
    SituationFinanciere getSituationByDossier(Long idDm);
    void enregistrerPaiement(Long idSf, Double montant);
    void appliquerPromo(Long idSf, boolean enPromo);
    Double calculerResteAPayer(Long idSf);

    // === RECHERCHES ===
    List<Acte> rechercherActes(String keyword, String categorie);
    List<Medicament> rechercherMedicaments(String keywordNom, String laboratoire,
                                           String type, Boolean remboursable);
    Consultation creerConsultationManuelle(Long idDm, Long idMedecin, LocalDate date, String observations);

    // Pour les ordonnances
    Ordonnance getOrdonnanceById(Long idOrd);
    List<Ordonnance> getAllOrdonnances();
    void deleteOrdonnance(Long idOrd);
    Ordonnance updateOrdonnance(Long idOrd, LocalDate date, Long idConsultation);


    // Pour les prescriptions
    Prescription getPrescriptionById(Long idPr);
    void deletePrescription(Long idPr);
    Prescription updatePrescription(Long idPr, String categorie, String frequence,
                                    Integer dureeJours, Integer quantite);

    // Ajoutez cette méthode
    Prescription ajouterPrescriptionAvecMedicament(Long idOrdonnance, String categorie, String frequence,
                                                   Integer dureeJours, Integer quantite, Long idMedicament);
    Prescription ajouterMedicamentAPrescription(Long idPr, Long idMedicament);
    Prescription retirerMedicamentDePrescription(Long idPr, Long idMedicament);

    // Ajoutez ces méthodes si elles n'existent pas
    void supprimerMedicament(Long idMct);
    Medicament modifierMedicament(Long idMct, String nom, String laboratoire,
                                  String type, String forme, Boolean remboursable,
                                  Double prixUnitaire, String description);

    boolean supprimerDossier(Long idDm);
    boolean supprimerConsultation(Long idConsultation);
    boolean supprimerCertificat(Long idCertif);

    Certificat getCertificatById(Long idCertif);

    boolean modifierCertificat(Long idCertif, LocalDate dateDebut, LocalDate dateFin, String note);
}