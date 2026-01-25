package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class DossierMedicalDetailsDialog extends JDialog {

    private final DossierMedicalController controller;
    private final long idDm;
    private final Runnable onCloseRefresh;

    private final JLabel idDmLabel = new JLabel();
    private final JLabel idPatientLabel = new JLabel();
    private final JTextField dateCreationField = new JTextField(10);
    private final RoundedButton saveDateBtn = new RoundedButton("Corriger date");

    public DossierMedicalDetailsDialog(Window owner, DossierMedicalController controller, long idDm, Runnable onCloseRefresh) {
        super(owner, "Dossier Médical #" + idDm, ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.idDm = idDm;
        this.onCloseRefresh = onCloseRefresh;

        setSize(420, 220);
        setLocationRelativeTo(owner);

        JPanel p = new JPanel(new GridLayout(4, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        p.add(new JLabel("ID Dossier:"));
        p.add(idDmLabel);

        p.add(new JLabel("ID Patient:"));
        p.add(idPatientLabel);

        p.add(new JLabel("Date création (YYYY-MM-DD):"));
        p.add(dateCreationField);

        p.add(new JLabel(""));
        ButtonStyles.primary(saveDateBtn);
        p.add(saveDateBtn);

        add(p);

        saveDateBtn.addActionListener(e -> saveDate());
        load();
    }

    private void load() {
        DossierMedical dm = controller.getById(idDm);
        idDmLabel.setText(String.valueOf(dm.getIdDm()));
        idPatientLabel.setText(String.valueOf(dm.getIdPatient()));
        dateCreationField.setText(dm.getDateDeCreation() == null ? "" : dm.getDateDeCreation().toString());
    }

    private void saveDate() {
        try {
            LocalDate d = LocalDate.parse(dateCreationField.getText().trim());
            controller.corrigerDateCreation(idDm, d);
            JOptionPane.showMessageDialog(this, "Date corrigée.", "OK", JOptionPane.INFORMATION_MESSAGE);
            load();
            if (onCloseRefresh != null) onCloseRefresh.run();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
