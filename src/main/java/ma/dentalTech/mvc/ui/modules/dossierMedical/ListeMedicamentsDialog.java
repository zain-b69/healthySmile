package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ListeMedicamentsDialog extends JDialog {

    private final DossierMedicalController controller;
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nom", "Laboratoire", "Type", "Forme", "Prix", "Remboursable"}, 0
    );

    public ListeMedicamentsDialog(Window owner, DossierMedicalController controller) {
        super(owner, "Liste des médicaments", ModalityType.APPLICATION_MODAL);
        this.controller = controller;

        setSize(800, 500);
        setLocationRelativeTo(owner);
        initUI();
        chargerMedicaments();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // Table
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        RoundedButton btnAjouter = new RoundedButton("➕ Ajouter");
        RoundedButton btnModifier = new RoundedButton("✏️ Modifier");
        RoundedButton btnSupprimer = new RoundedButton("🗑️ Supprimer");
        RoundedButton btnFermer = new RoundedButton("Fermer");

        ButtonStyles.primary(btnAjouter);
        ButtonStyles.primary(btnModifier);
        ButtonStyles.danger(btnSupprimer);
        ButtonStyles.primary(btnFermer);

        btnAjouter.addActionListener(e -> ajouterMedicament());
        btnModifier.addActionListener(e -> modifierMedicament(table.getSelectedRow()));
        btnSupprimer.addActionListener(e -> supprimerMedicament(table.getSelectedRow()));
        btnFermer.addActionListener(e -> dispose());

        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnFermer);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void chargerMedicaments() {
        try {
            List<Medicament> medicaments = controller.getAllMedicaments();
            model.setRowCount(0);

            for (Medicament m : medicaments) {
                model.addRow(new Object[]{
                        m.getIdMct(),
                        m.getNom(),
                        m.getLaboratoire(),
                        m.getType(),
                        m.getForme(),
                        m.getPrixUnitaire() != null ? m.getPrixUnitaire() + " DH" : "",
                        m.getRemboursable() != null && m.getRemboursable() ? "✓" : "✗"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouterMedicament() {
        AjouterMedicamentDialog dialog = new AjouterMedicamentDialog(this, controller);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            chargerMedicaments(); // Recharger
        }
    }

    private void modifierMedicament(int row) {
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Sélectionnez un médicament à modifier",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Long id = (Long) model.getValueAt(row, 0);
        String nom = (String) model.getValueAt(row, 1);

        // Ouvrir le dialog de modification
        ModifierMedicamentDialog dialog = new ModifierMedicamentDialog(
                this,
                controller,
                id
        );
        dialog.setVisible(true);

        if (dialog.isModified()) {
            chargerMedicaments(); // Recharger la liste après modification
            JOptionPane.showMessageDialog(this,
                    "Médicament '" + nom + "' modifié avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void supprimerMedicament(int row) {
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Sélectionnez un médicament",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Long id = (Long) model.getValueAt(row, 0);
        String nom = (String) model.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer le médicament '" + nom + "' ?\nCette action est irréversible.",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.supprimerMedicament(id);
                JOptionPane.showMessageDialog(this,
                        "Médicament supprimé avec succès",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                chargerMedicaments(); // Recharger la liste
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Erreur: " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}