// Fichier: ma/dentalTech/mvc/ui/modules/dossierMedical/CreerOrdonnanceDialog.java
package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class CreerOrdonnanceDialog extends JDialog {

    private final DossierMedicalController controller;
    private final Long idDm;
    private boolean success = false;

    private final JTextField idConsultationField = new JTextField(10);
    private final JTextField dateField = new JTextField(10);
    private final JTextField idDossierField = new JTextField(10); // Champ pour saisir l'ID du dossier
    private final JCheckBox utiliserDossierActuel = new JCheckBox("Utiliser dossier actuel");

    public CreerOrdonnanceDialog(Window owner, DossierMedicalController controller, Long idDm) {
        super(owner, "Créer une ordonnance", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.idDm = idDm;

        initUI();
        setDefaultValues();

        setSize(400, 300);
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

        // Consultation (optionnelle)
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("ID Consultation (optionnel):"), gbc);
        gbc.gridx = 1;
        mainPanel.add(idConsultationField, gbc);
        row++;

        // Date
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        mainPanel.add(dateField, gbc);
        row++;

        // Dossier médical
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Dossier médical:"), gbc);
        gbc.gridx = 1;

        if (idDm != null) {
            // Si idDm est fourni, on l'affiche comme label
            utiliserDossierActuel.setSelected(true);
            utiliserDossierActuel.setEnabled(false);
            mainPanel.add(new JLabel("Dossier #" + idDm), gbc);
            idDossierField.setVisible(false);
        } else {
            // Sinon, on affiche un champ de saisie
            JPanel dossierPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            dossierPanel.add(new JLabel("ID Dossier: "));
            idDossierField.setColumns(10);
            dossierPanel.add(idDossierField);
            mainPanel.add(dossierPanel, gbc);
        }
        row++;

        add(mainPanel, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RoundedButton btnCreer = new RoundedButton("Créer");
        RoundedButton btnAnnuler = new RoundedButton("Annuler");
        ButtonStyles.primary(btnCreer);
        ButtonStyles.primary(btnAnnuler);

        btnCreer.addActionListener(e -> creerOrdonnance());
        btnAnnuler.addActionListener(e -> dispose());

        buttonPanel.add(btnCreer);
        buttonPanel.add(btnAnnuler);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setDefaultValues() {
        dateField.setText(LocalDate.now().toString());

        // Si idDm est fourni, on pré-remplit aussi le champ (pour la cohérence)
        if (idDm != null) {
            idDossierField.setText(String.valueOf(idDm));
        }
    }

    private void creerOrdonnance() {
        try {
            // 1. Valider et parser l'ID consultation
            Long idConsultation = null;
            String consultationText = idConsultationField.getText().trim();
            if (!consultationText.isEmpty()) {
                idConsultation = Long.parseLong(consultationText);
            }

            // 2. Valider et parser la date
            LocalDate date = LocalDate.parse(dateField.getText().trim());

            // 3. Déterminer l'ID du dossier
            Long dossierId = idDm; // Celui passé au constructeur

            if (dossierId == null) {
                // Si non fourni, le prendre depuis le champ
                String dossierText = idDossierField.getText().trim();
                if (dossierText.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Veuillez saisir un ID de dossier médical",
                            "Champ requis", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                dossierId = Long.parseLong(dossierText);
            }

            // 4. Créer l'ordonnance
            System.out.println("DEBUG: Création ordonnance - idDm=" + dossierId +
                    ", idConsultation=" + idConsultation + ", date=" + date);

            Ordonnance ordonnance = controller.creerOrdonnance(dossierId, idConsultation, date);

            if (ordonnance != null && ordonnance.getIdOrd() != null) {
                success = true;
                JOptionPane.showMessageDialog(this,
                        "Ordonnance créée avec succès (ID: " + ordonnance.getIdOrd() + ")",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);

                // Demander si on veut ajouter des prescriptions
                int option = JOptionPane.showConfirmDialog(this,
                        "Voulez-vous ajouter des prescriptions maintenant?",
                        "Prescriptions",
                        JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    OrdonnanceDetailsDialog detailsDialog = new OrdonnanceDetailsDialog(
                            this,
                            controller,
                            ordonnance.getIdOrd()
                    );
                    detailsDialog.setVisible(true);
                }

                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Échec de la création de l'ordonnance (objet null ou ID null)",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Format numérique invalide. Veuillez vérifier les IDs.",
                    "Erreur de format", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (java.time.format.DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Format de date invalide. Utilisez YYYY-MM-DD (ex: 2026-01-14).",
                    "Erreur de date", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // Afficher l'erreur complète pour débogage
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la création:\n" + e.getClass().getSimpleName() +
                            ": " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() {
        return success;
    }
}