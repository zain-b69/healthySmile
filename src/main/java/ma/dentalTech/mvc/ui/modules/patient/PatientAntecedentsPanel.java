package ma.dentalTech.mvc.ui.modules.patient;

import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
// si tu as un AntecedentController pour le catalogue:
import ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientAntecedentsPanel extends JPanel {

    private final Long patientId;
    private final PatientController patientController;
    private final AntecedentController antecedentController;

    private final DefaultTableModel model;
    private final JTable table;

    public PatientAntecedentsPanel(Long patientId,
                                   PatientController patientController,
                                   AntecedentController antecedentController) {
        super(new BorderLayout());
        this.patientId = patientId;
        this.patientController = patientController;
        this.antecedentController = antecedentController;

        JLabel title = new JLabel("Antécédents du patient #" + patientId);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"ID", "Nom", "Description"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton add = new JButton("Ajouter");
        JButton remove = new JButton("Retirer");
        JButton refresh = new JButton("Rafraîchir");

        add.addActionListener(e -> onAdd());
        remove.addActionListener(e -> onRemove());
        refresh.addActionListener(e -> reload());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(add);
        south.add(remove);
        south.add(refresh);
        add(south, BorderLayout.SOUTH);

        reload();
    }

    private void reload() {
        model.setRowCount(0);
        try {
            List<Antecedent> ants = patientController.getAntecedentsOfPatient(patientId);
            for (Antecedent a : ants) {
                model.addRow(new Object[]{a.getIdAntecedent(), a.getNom(), a.getCategorie()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdd() {
        try {
            // 1) prendre la liste catalogue
            List<Antecedent> all = antecedentController.findAll();
            if (all.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucun antécédent dans le catalogue.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 2) popup de sélection simple
            Antecedent selected = AntecedentSelectDialog.select(
                    SwingUtilities.getWindowAncestor(this),
                    all
            );
            if (selected == null) return;

            // 3) lier
            patientController.addAntecedentToPatient(patientId, selected.getIdAntecedent());
            reload();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur ajout: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRemove() {
        Long antecedentId = getSelectedAntecedentId();
        if (antecedentId == null) return;

        int ok = JOptionPane.showConfirmDialog(this,
                "Retirer cet antécédent du patient ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (ok != JOptionPane.YES_OPTION) return;

        try {
            patientController.removeAntecedentFromPatient(patientId, antecedentId);
            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur suppression: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Long getSelectedAntecedentId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionne un antécédent.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        Object val = model.getValueAt(row, 0);
        return (val == null) ? null : Long.valueOf(val.toString());
    }
}
