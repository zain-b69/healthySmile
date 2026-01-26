// Fichier: ma/dentalTech/mvc/ui/modules/dossierMedical/NouvelleConsultationDialog.java
package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class NouvelleConsultationDialog extends JDialog {

    private final DossierMedicalController controller;
    private final long idDm;
    private final long idPatient;

    private final JTextField idMedecinField = new JTextField(10);
    private final JTextField dateField = new JTextField(10);
    private final JTextArea observationsArea = new JTextArea(5, 30);

    public NouvelleConsultationDialog(Window owner, DossierMedicalController controller,
                                      long idDm, long idPatient) {
        super(owner, "Nouvelle Consultation - Dossier #" + idDm, ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.idDm = idDm;
        this.idPatient = idPatient;

        initUI();
        setDefaultValues();

        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // ID Médecin
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("ID Médecin:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(idMedecinField, gbc);
        row++;

        // Date
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        mainPanel.add(dateField, gbc);
        row++;

        // Observations
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Observations:"), gbc);
        gbc.gridx = 1;
        JScrollPane scrollPane = new JScrollPane(observationsArea);
        scrollPane.setPreferredSize(new Dimension(300, 100));
        mainPanel.add(scrollPane, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RoundedButton btnCreer = new RoundedButton("Créer");
        RoundedButton btnAnnuler = new RoundedButton("Annuler");
        ButtonStyles.primary(btnCreer);
        ButtonStyles.primary(btnAnnuler);

        btnCreer.addActionListener(e -> creerConsultation());
        btnAnnuler.addActionListener(e -> dispose());

        buttonPanel.add(btnCreer);
        buttonPanel.add(btnAnnuler);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setDefaultValues() {
        dateField.setText(LocalDate.now().toString());
    }

    private void creerConsultation() {
        try {
            if (idMedecinField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez saisir l'ID du médecin",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            long idMedecin = Long.parseLong(idMedecinField.getText().trim());
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            String observations = observationsArea.getText().trim();

            // Créer la consultation manuellement (sans RDV)
            controller.creerConsultationManuelle(idDm, idMedecin, date, observations);

            JOptionPane.showMessageDialog(this, "Consultation créée avec succès",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID Médecin invalide",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}