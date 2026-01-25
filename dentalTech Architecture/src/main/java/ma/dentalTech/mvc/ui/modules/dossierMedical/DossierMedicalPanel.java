package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;
import ma.dentalTech.mvc.ui.modules.dossierMedical.DossierMedicalDetailsDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class DossierMedicalPanel extends JPanel {

    private final DossierMedicalController controller;

    private final JTextField idPatientField = new JTextField(10);
    private final JTextField startField = new JTextField(10); // YYYY-MM-DD
    private final JTextField endField = new JTextField(10);

    private final RoundedButton loadAllBtn = new RoundedButton("Tout");
    private final RoundedButton searchBetweenBtn = new RoundedButton("Rechercher période");
    private final RoundedButton createBtn = new RoundedButton("Créer dossier");
    private final RoundedButton openBtn = new RoundedButton("Ouvrir");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID_DM", "ID_PATIENT", "DATE_CREATION"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    private final JTable table = new JTable(model);

    public DossierMedicalPanel(DossierMedicalController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("ID Patient:"));
        top.add(idPatientField);
        ButtonStyles.primary(createBtn);
        top.add(createBtn);

        top.add(new JLabel("Start (YYYY-MM-DD):"));
        top.add(startField);
        top.add(new JLabel("End:"));
        top.add(endField);
        ButtonStyles.primary(searchBetweenBtn);
        top.add(searchBetweenBtn);
        ButtonStyles.primary(loadAllBtn);
        top.add(loadAllBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ButtonStyles.primary(openBtn);
        bottom.add(openBtn);
        add(bottom, BorderLayout.SOUTH);

        loadAllBtn.addActionListener(e -> loadAll());
        searchBetweenBtn.addActionListener(e -> searchBetween());
        createBtn.addActionListener(e -> createDossier());
        openBtn.addActionListener(e -> openSelected());

        loadAll();
    }

    private void loadAll() {
        try {
            fill(controller.listAll());
        } catch (Exception ex) {
            showErr(ex);
        }
    }

    private void searchBetween() {
        try {
            LocalDate start = LocalDate.parse(startField.getText().trim());
            LocalDate end = LocalDate.parse(endField.getText().trim());
            fill(controller.dossiersCreesEntre(start, end));
        } catch (Exception ex) {
            showErr(ex);
        }
    }

    private void createDossier() {
        try {
            long idPatient = Long.parseLong(idPatientField.getText().trim());
            controller.ouvrirDossier(idPatient);
            loadAll();
            JOptionPane.showMessageDialog(this, "Dossier créé.", "OK", JOptionPane.INFORMATION_MESSAGE);
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
                controller,
                idDm,
                this::loadAll
        );
        dlg.setVisible(true);
    }

    private void fill(List<DossierMedical> list) {
        model.setRowCount(0);
        for (DossierMedical dm : list) {
            model.addRow(new Object[]{dm.getIdDm(), dm.getIdPatient(), dm.getDateDeCreation()});
        }
    }

    private void showErr(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
