package ma.dentalTech.mvc.controllers.modules.dossierMedical.api;

/**
 * Contrat pour gérer les interactions utilisateur liées au Dossier Médical.
 */
public interface DossierMedicalController {

    /** * Affiche ou rafraîchit la liste des dossiers pour le patient actuellement sélectionné.
     * Généralement appelé après une action (création, archivage).
     */
    void displayDossiersForPatient(Long idPatient);

    /** * Déclenche la procédure d'ajout d'un nouveau dossier (via une boîte de dialogue).
     * Gère la création de l'entité et l'appel au service.
     */
    void handleNouveauDossier();

    /**
     * Ouvre la vue détaillée d'un dossier pour permettre la consultation
     * des antécédents, consultations, ordonnances, etc.
     */
    void handleConsultationDetaillee(Long idDossier);
}