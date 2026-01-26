package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import java.awt.*;

public class ModifierMedicamentDialog extends JDialog {

    private final DossierMedicalController controller;
    private final Long idMct;
    private boolean modified = false;

    private final JTextField nomField = new JTextField(20);
    private final JTextField laboratoireField = new JTextField(20);
    private final JTextField typeField = new JTextField(15);
    private final JTextField formeField = new JTextField(15);
    private final JCheckBox remboursableCheck = new JCheckBox("Remboursable");
    private final JSpinner prixSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10000.0, 0.5));
    private final JTextArea descriptionArea = new JTextArea(3, 20);

    public ModifierMedicamentDialog(Window owner, DossierMedicalController controller, Long idMct) {
        super(owner, "Modifier médicament", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.idMct = idMct;

        initUI();
        chargerMedicament();

        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Nom
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Nom*:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(nomField, gbc);
        row++;

        // Laboratoire
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Laboratoire:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(laboratoireField, gbc);
        row++;

        // Type
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(typeField, gbc);
        row++;

        // Forme
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Forme:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(formeField, gbc);
        row++;

        // Remboursable
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Remboursable:"), gbc);
        gbc.gridx = 1;
        remboursableCheck.setSelected(true);
        mainPanel.add(remboursableCheck, gbc);
        row++;

        // Prix
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Prix unitaire (DH):"), gbc);
        gbc.gridx = 1;
        mainPanel.add(prixSpinner, gbc);
        row++;

        // Description
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        mainPanel.add(new JScrollPane(descriptionArea), gbc);
        row++;

        add(mainPanel, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RoundedButton btnModifier = new RoundedButton("Modifier");
        RoundedButton btnAnnuler = new RoundedButton("Annuler");
        ButtonStyles.primary(btnModifier);
        ButtonStyles.primary(btnAnnuler);

        btnModifier.addActionListener(e -> modifierMedicament());
        btnAnnuler.addActionListener(e -> dispose());

        buttonPanel.add(btnModifier);
        buttonPanel.add(btnAnnuler);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void chargerMedicament() {
        try {
            Medicament medicament = controller.getMedicamentById(idMct);

            if (medicament != null) {
                nomField.setText(medicament.getNom());
                laboratoireField.setText(medicament.getLaboratoire());
                typeField.setText(medicament.getType());
                formeField.setText(medicament.getForme());
                remboursableCheck.setSelected(medicament.getRemboursable() != null && medicament.getRemboursable());

                if (medicament.getPrixUnitaire() != null) {
                    prixSpinner.setValue(medicament.getPrixUnitaire());
                }

                descriptionArea.setText(medicament.getDescription());
            } else {
                JOptionPane.showMessageDialog(this,
                        "Médicament non trouvé avec l'ID: " + idMct,
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                dispose();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void modifierMedicament() {
        try {
            String nom = nomField.getText().trim();

            if (nom.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Le nom du médicament est obligatoire",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String laboratoire = laboratoireField.getText().trim();
            String type = typeField.getText().trim();
            String forme = formeField.getText().trim();
            Boolean remboursable = remboursableCheck.isSelected();
            Double prix = ((Number) prixSpinner.getValue()).doubleValue();
            String description = descriptionArea.getText().trim();

            // Modifier le médicament
            controller.modifierMedicament(idMct, nom, laboratoire, type, forme,
                    remboursable, prix, description);

            modified = true;
            dispose();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isModified() {
        return modified;
    }
}