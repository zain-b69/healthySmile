// Fichier: ma/dentalTech/mvc/ui/modules/dossierMedical/AjouterInterventionDialog.java
package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AjouterInterventionDialog extends JDialog {

    private final DossierMedicalController controller;
    private final Long idConsultation;

    private final JComboBox<String> acteCombo = new JComboBox<>();
    private final JSpinner dentSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 32, 1));
    private final JTextField prixField = new JTextField(10);

    public AjouterInterventionDialog(Window owner, DossierMedicalController controller, Long idConsultation) {
        super(owner, "Ajouter intervention", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.idConsultation = idConsultation;

        initUI();
        loadActes();

        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Acte
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Acte:"), gbc);
        gbc.gridx = 1;
        add(acteCombo, gbc);
        row++;

        // Dent
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Numéro dent (1-32):"), gbc);
        gbc.gridx = 1;
        add(dentSpinner, gbc);
        row++;

        // Prix
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Prix patient (laisser vide pour prix de base):"), gbc);
        gbc.gridx = 1;
        add(prixField, gbc);
        row++;

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RoundedButton btnAjouter = new RoundedButton("Ajouter");
        RoundedButton btnAnnuler = new RoundedButton("Annuler");
        ButtonStyles.primary(btnAjouter);
        ButtonStyles.primary(btnAnnuler);

        btnAjouter.addActionListener(e -> ajouterIntervention());
        btnAnnuler.addActionListener(e -> dispose());

        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnAnnuler);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
    }

    private void loadActes() {
        List<Acte> actes = controller.getAllActes();
        acteCombo.removeAllItems();
        acteCombo.addItem("-- Sélectionner un acte --");
        for (Acte a : actes) {
            acteCombo.addItem(a.getIdActe() + " - " + a.getLibelle() + " (" + a.getPrixDeBase() + " DH)");
        }
    }

    private void ajouterIntervention() {
        try {
            if (acteCombo.getSelectedIndex() <= 0) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un acte");
                return;
            }

            // Extraire ID acte de la sélection
            String selection = (String) acteCombo.getSelectedItem();
            Long idActe = Long.parseLong(selection.split(" - ")[0]);

            Integer numDent = (Integer) dentSpinner.getValue();
            Double prix = null;

            if (!prixField.getText().trim().isEmpty()) {
                prix = Double.parseDouble(prixField.getText().trim());
                if (prix < 0) {
                    JOptionPane.showMessageDialog(this, "Prix invalide");
                    return;
                }
            }

            controller.ajouterIntervention(idConsultation, idActe, numDent, prix);
            JOptionPane.showMessageDialog(this, "Intervention ajoutée avec succès");
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Prix invalide");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}