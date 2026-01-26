// Fichier: ma/dentalTech/mvc/ui/modules/dossierMedical/DossierMedicalPanel.java
package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class DossierMedicalPanel extends JPanel {

    private final DossierMedicalController dossierController;
    private final PatientController patientController;

    private final JTextField startField = new JTextField(10);
    private final JTextField endField = new JTextField(10);

    // RoundedButton avec tailles ajustées pour textes longs
    RoundedButton loadAllBtn = new RoundedButton("Tout");
    RoundedButton searchBetweenBtn = new RoundedButton("Par période");
    RoundedButton openBtn = new RoundedButton("Ouvrir");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID_DM", "ID_PATIENT", "NOM", "DATE_CREATION"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    private final JTable table = new JTable(model);

    public DossierMedicalPanel(DossierMedicalController dossierController, PatientController patientController) {
        this.dossierController = dossierController;
        this.patientController = patientController;
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // RETIRÉ: top.add(new JLabel("ID Patient:"));
        // RETIRÉ: top.add(idPatientField);

        top.add(new JLabel("Start (YYYY-MM-DD):"));
        top.add(startField);
        top.add(new JLabel("End:"));
        top.add(endField);
        top.add(searchBetweenBtn);
        top.add(loadAllBtn);

        // Appliquer les styles avec tailles personnalisées
        applyButtonStyles();

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(openBtn);

        RoundedButton btnNouvelleConsultation = new RoundedButton("Nv. consultation");
        // Taille personnalisée pour texte long
        btnNouvelleConsultation.setPreferredSize(new Dimension(180, 36));
        ButtonStyles.primary(btnNouvelleConsultation);
        bottom.add(btnNouvelleConsultation);

        add(bottom, BorderLayout.SOUTH);

        loadAllBtn.addActionListener(e -> loadAll());
        searchBetweenBtn.addActionListener(e -> searchBetween());
        openBtn.addActionListener(e -> openSelected());

        btnNouvelleConsultation.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Sélectionnez un dossier d'abord");
                return;
            }
            long idDm = ((Number) model.getValueAt(row, 0)).longValue();
            long idPatient = ((Number) model.getValueAt(row, 1)).longValue();

            NouvelleConsultationDialog dialog = new NouvelleConsultationDialog(
                    SwingUtilities.getWindowAncestor(this),
                    dossierController,
                    idDm,
                    idPatient
            );
            dialog.setVisible(true);
        });

        loadAll();
    }

    private void applyButtonStyles() {
        // Styles normaux
        ButtonStyles.primary(loadAllBtn);
        ButtonStyles.primary(openBtn);

        // Bouton avec texte long - taille personnalisée
        searchBetweenBtn.setPreferredSize(new Dimension(160, 36));
        ButtonStyles.primary(searchBetweenBtn);
    }


    private void loadAll() {
        try {
            fill(dossierController.listAll());
        } catch (Exception ex) {
            showErr(ex);
        }
    }

    private void searchBetween() {
        try {
            LocalDate start = LocalDate.parse(startField.getText().trim());
            LocalDate end = LocalDate.parse(endField.getText().trim());
            fill(dossierController.dossiersCreesEntre(start, end));
        } catch (Exception ex) {
            showErr(ex);
        }
    }

    private void openSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionne un dossier.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        long idDm = ((Number) model.getValueAt(row, 0)).longValue();

        JDialog dlg = new DossierMedicalDetailsDialog(
                SwingUtilities.getWindowAncestor(this),
                dossierController,
                idDm,
                this::loadAll
        );
        dlg.setVisible(true);
    }

    private void fill(List<DossierMedical> list) {
        model.setRowCount(0);
        for (DossierMedical dm : list) {
            Long idPatient = dm.getIdPatient();
            String nomPatient = "Inconnu"; // Valeur par défaut

            if (idPatient != null) {
                try {
                    // Récupérer le vrai patient depuis PatientController
                    Patient patient = patientController.findById(idPatient);
                    if (patient != null && patient.getNom() != null) {
                        nomPatient = patient.getNom(); // Le VRAI nom comme "Dupont"
                    } else {
                        nomPatient = "Patient " + idPatient;
                    }
                } catch (Exception e) {
                    nomPatient = "Erreur";
                    System.err.println("Erreur récupération patient " + idPatient + ": " + e.getMessage());
                }
            }

            model.addRow(new Object[]{
                    dm.getIdDm(),
                    dm.getIdPatient(),
                    nomPatient,  // ← VRAI NOM du patient
                    dm.getDateDeCreation()
            });
        }
    }

    private void showErr(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}