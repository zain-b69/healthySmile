package ma.dentalTech.mvc.controllers.modules.dossierMedical.swing_implementation;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.dto.DossierMedicaleDTO; // DTO à créer
import ma.dentalTech.mvc.dto.PatientDTO;
import ma.dentalTech.mvc.ui.modules.dossierMedical.DossierMedicalView; // La vue Swing
import ma.dentalTech.mvc.ui.modules.patient.PatientView;
import ma.dentalTech.service.modules.dossierMedical.api.DossierMedicalService;
import ma.dentalTech.service.modules.patient.api.PatientService;

import javax.swing.JOptionPane;
import java.util.List;

public class DossierMedicalControllerImpl implements DossierMedicalController {

    private final DossierMedicalService dossierService;
    private final DossierMedicalView dossierView;
    private Long currentPatientId; // Garde une référence au patient dont on affiche les dossiers

    // Injection du service et de la vue
    public DossierMedicalControllerImpl(DossierMedicalService dossierService, DossierMedicalView dossierView) {
        this.dossierService = dossierService;
        this.dossierView = dossierView;

        // Attacher les écouteurs de la vue (boutons 'Nouveau', 'Actualiser')
        this.dossierView.addNouveauDossierListener(e -> handleNouveauDossier());
    }

    @Override
    public void displayDossiersForPatient(Long idPatient) {
        this.currentPatientId = idPatient;
        try {
            // 1. Appel au Service pour récupérer les données métier
            // NOTE: Le service devra mapper les DossierMedicale en DTO (DossierMedicaleDTO)
            List<DossierMedical> dossiers = dossierService.recupererDossiersParPatient(idPatient);

            // 2. Mettre à jour la vue (affichage dans un JTable par exemple)
            //dossierView.updateDossierTable(dossierService.mapToDTO(dossiers));

            dossierView.displayInfo("Dossiers affichés pour Patient ID: " + idPatient);

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(dossierView,
                    "Erreur lors de l'affichage des dossiers: " + e.getMessage(),
                    "Erreur Système", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void handleNouveauDossier() {
        if (currentPatientId == null) {
            JOptionPane.showMessageDialog(dossierView, "Veuillez sélectionner un patient d'abord.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 1. Demander à l'utilisateur de confirmer/saisir les infos initiales (via une boîte de dialogue)
            String creePar = JOptionPane.showInputDialog(dossierView, "Créé par (Nom d'utilisateur) :");

            // 2. Appel au Service
            // Dans une vraie app, on récupérerait l'objet Patient complet par currentPatientId
            Patient patientPlaceholder = new Patient(); // À remplacer par l'objet Patient réel
            patientPlaceholder.setId(currentPatientId);

            DossierMedical nouveauDossier = dossierService.creerNouveauDossier(patientPlaceholder, creePar);

            JOptionPane.showMessageDialog(dossierView,
                    "Nouveau Dossier Médical créé (ID: " + nouveauDossier.getId() + ")",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);

            // 3. Rafraîchir l'affichage
            displayDossiersForPatient(currentPatientId);

        } catch (IllegalStateException e) {
            // Gérer les règles métier (ex: "Patient a déjà un dossier actif")
            JOptionPane.showMessageDialog(dossierView, e.getMessage(), "Règle Métier Violée", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dossierView, "Échec de la création du dossier: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void handleConsultationDetaillee(Long idDossier) {
        try {
            // 1. Appel au service pour récupérer le dossier complet et ses entités liées
            // Note : Cette méthode doit exister dans votre Service pour retourner une entité ou un DTO détaillé
            // DossierMedicale dossierDetail = dossierService.trouverDossierComplet(idDossier);

            // 2. Ouvrir la nouvelle fenêtre de détail
            // Supposons une méthode pour ouvrir la vue des détails
            // DossierDetailWindow detailWindow = new DossierDetailWindow(dossierDetail);
            // detailWindow.setVisible(true);

            JOptionPane.showMessageDialog(dossierView.getComponent(0),
                    "Ouverture de la vue détaillée pour le Dossier ID: " + idDossier,
                    "Consultation", JOptionPane.INFORMATION_MESSAGE);

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(dossierView.getComponent(0),
                    "Impossible d'ouvrir le détail du dossier : " + e.getMessage(),
                    "Erreur Système", JOptionPane.ERROR_MESSAGE);
        }
    }


    }
