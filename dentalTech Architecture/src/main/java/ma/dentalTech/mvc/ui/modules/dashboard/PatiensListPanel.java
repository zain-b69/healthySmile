package ma.dentalTech.mvc.ui.modules.dashboard;

import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatiensListPanel extends JPanel {

    private final PatientController patientController;
    private final DefaultTableModel model;

    public PatiensListPanel(PatientController patientController) {
        super(new BorderLayout());
        this.patientController = patientController;

        JLabel title = new JLabel("Liste des patients");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"ID", "Nom", "Téléphone", "Sexe", "Naissance", "Assurance"},
                0
        );

        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refresh = new JButton("Rafraîchir");
        refresh.addActionListener(e -> reload());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(refresh);
        add(south, BorderLayout.SOUTH);

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
}
