package ma.dentalTech.mvc.ui.modules.patient;

import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentController;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;
import ma.dentalTech.mvc.ui.modules.patient.PatientFormDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PatientsListPanel extends JPanel {

    private final PatientController patientController;
    private final DefaultTableModel model;
    private final JTable table;

    public PatientsListPanel(PatientController patientController, AntecedentController antecedentController) {
        super(new BorderLayout());
        this.patientController = patientController;

        JLabel title = new JLabel("Liste des patients");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"ID", "Nom", "Téléphone", "Sexe", "Naissance", "Assurance"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // edition via dialog uniquement
            }
        };

        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Actions CRUD
        RoundedButton refresh = new RoundedButton("Rafraîchir");
        RoundedButton addBtn = new RoundedButton("Ajouter");
        RoundedButton editBtn = new RoundedButton("Modifier");
        RoundedButton deleteBtn = new RoundedButton("Supprimer");
        RoundedButton antecedentsBtn = new RoundedButton("Antécédents...");

        ButtonStyles.primary(addBtn);
        ButtonStyles.primary(editBtn);
        ButtonStyles.primary(deleteBtn);
        ButtonStyles.primary(refresh);
        ButtonStyles.primary(antecedentsBtn);

        refresh.addActionListener(e -> reload());
        addBtn.addActionListener(e -> onCreate());
        editBtn.addActionListener(e -> onEdit());
        deleteBtn.addActionListener(e -> onDelete());
        antecedentsBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Sélectionne un patient.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Long idPatient = (Long) model.getValueAt(row, 0);
            Patient p = patientController.findById(idPatient);

            // ⚠️ il te faut antecedentController ici (passé au panel via constructeur)
            new PatientAntecedentsDialog(
                    SwingUtilities.getWindowAncestor(this),
                    patientController,
                    antecedentController,
                    p
            ).setVisible(true);
        });
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(addBtn);
        south.add(editBtn);
        south.add(deleteBtn);
        south.add(refresh);
        add(south, BorderLayout.SOUTH);
        south.add(antecedentsBtn);

        reload();
    }

    private void reload() {
        model.setRowCount(0);
        try {
            List<Patient> patients = patientController.findAll();
            for (Patient p : patients) {
                model.addRow(new Object[]{
                        p.getIdPatient(),
                        p.getNom(),
                        p.getTelephone(),
                        p.getSexe(),
                        p.getDateNaissance(),
                        p.getAssurance()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur chargement patients: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCreate() {
        PatientFormDialog dlg = new PatientFormDialog(
                SwingUtilities.getWindowAncestor(this),
                "Ajouter un patient",
                null
        );
        dlg.setVisible(true);

        if (!dlg.isSucceeded()) return;

        Patient p = dlg.getPatient();

        try {
            // Validation minimale côté UI (pour éviter Date.valueOf(null))
            if (p.getNom() == null || p.getNom().isBlank()) {
                JOptionPane.showMessageDialog(this, "Nom obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (p.getDateNaissance() == null) {
                JOptionPane.showMessageDialog(this, "Date de naissance obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            patientController.create(p);
            reload();
            JOptionPane.showMessageDialog(this, "Patient ajouté ✅");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur ajout patient: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        Long id = getSelectedPatientId();
        if (id == null) return;

        try {
            Patient existing = patientController.findById(id);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Patient introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            PatientFormDialog dlg = new PatientFormDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Modifier patient #" + id,
                    existing
            );
            dlg.setVisible(true);

            if (!dlg.isSucceeded()) return;

            Patient updated = dlg.getPatient();
            updated.setIdPatient(id);      // important
            updated.setId(existing.getId()); // si tu utilises entite.id (audit)

            patientController.update(updated);
            reload();
            JOptionPane.showMessageDialog(this, "Patient modifié ✅");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur modification: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        Long id = getSelectedPatientId();
        if (id == null) return;

        int ok = JOptionPane.showConfirmDialog(
                this,
                "Supprimer le patient #" + id + " ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
        );
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            patientController.deleteById(id);
            reload();
            JOptionPane.showMessageDialog(this, "Patient supprimé ✅");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur suppression: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Long getSelectedPatientId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionne un patient.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        Object val = model.getValueAt(row, 0);
        if (val == null) return null;
        return Long.valueOf(val.toString());
    }
}
