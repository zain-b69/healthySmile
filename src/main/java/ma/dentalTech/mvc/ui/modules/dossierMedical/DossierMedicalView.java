package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.mvc.dto.DossierMedicaleDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Vue Swing pour la gestion des dossiers médicaux d'un patient.
 */
public class DossierMedicalView extends JPanel {

    // --- Champs de la classe ---
    private final JButton nouveauDossierButton;
    private final JTable dossiersTable;
    private final DefaultTableModel tableModel; // Correction : Déclaration du champ

    // Définition des colonnes du tableau
    private static final String[] COLUMN_NAMES =
            {"ID Dossier", "Statut", "Date Création", "Nb Consultations", "Situation Financière"};

    public DossierMedicalView() {
        // Initialisation des composants
        setLayout(new BorderLayout());

        // Correction : Initialisation du tableModel avant la JTable
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0);

        // Exemple d'initialisation du bouton
        nouveauDossierButton = new JButton("Ajouter un nouveau dossier");

        // Exemple : un panneau de contrôle pour le bouton
        JPanel controlPanel = new JPanel();
        controlPanel.add(nouveauDossierButton);
        add(controlPanel, BorderLayout.NORTH);

        // Exemple : un tableau pour afficher les DossierMedicaleDTO
        dossiersTable = new JTable(tableModel); // Utilisation de tableModel
        dossiersTable.setFillsViewportHeight(true);
        add(new JScrollPane(dossiersTable), BorderLayout.CENTER);
    }

    // ----------------------------------------------------
    // Méthodes pour le Contrôleur
    // ----------------------------------------------------

    /**
     * Permet au DossierMedicalController d'injecter la logique de gestion
     * lors du clic sur le bouton d'ajout.
     */
    public void addNouveauDossierListener(ActionListener listener) {
        // Attache le gestionnaire d'événement au bouton
        nouveauDossierButton.addActionListener(listener);
    }

    /**
     * Met à jour le tableau avec la liste de DTO fournie par le Service (via le Contrôleur).
     */
    public void updateDossierTable(List<DossierMedicaleDTO> dtos) {
        // Supprime toutes les lignes actuelles du tableau
        tableModel.setRowCount(0);

        if (dtos != null && !dtos.isEmpty()) {
            for (DossierMedicaleDTO dto : dtos) {
                // Ajoute une nouvelle ligne pour chaque DTO
                tableModel.addRow(new Object[]{
                        dto.getIdDossier(),
                        dto.getStatut(),
                        dto.getDateCreation(),
                        dto.getNombreConsultations(),
                        dto.getSituationFinanciere()
                });
            }
        }
    }

    public void displayInfo(String s) {

    }
}