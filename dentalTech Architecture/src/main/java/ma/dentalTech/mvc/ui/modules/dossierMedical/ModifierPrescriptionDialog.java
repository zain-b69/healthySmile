package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModifierPrescriptionDialog extends JDialog {

    private final DossierMedicalController controller;
    private final Long idPr;
    private boolean modified = false;

    private final JTextField categorieField = new JTextField(15);
    private final JTextField frequenceField = new JTextField(15);
    private final JSpinner dureeSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
    private final JSpinner quantiteSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

    // NOUVEAU : Gestion des médicaments
    private final JComboBox<Medicament> medicamentCombo = new JComboBox<>();
    private final JButton btnRechercherMedicament = new JButton("Rechercher...");
    private final JButton btnRetirerMedicament = new JButton("Retirer");
    private final List<Medicament> medicamentsDisponibles = new ArrayList<>();
    private Medicament medicamentActuel = null;
    private Prescription prescriptionOriginale;

    public ModifierPrescriptionDialog(Window owner, DossierMedicalController controller, Long idPr) {
        super(owner, "Modifier Prescription #" + idPr, ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.idPr = idPr;

        initUI();
        chargerMedicaments();
        loadPrescription();

        pack();
        setSize(500, 450); // Augmenté pour accommoder les nouveaux champs
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // ID Prescription (en lecture seule)
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("ID Prescription:"), gbc);
        gbc.gridx = 1;
        JLabel lblId = new JLabel(String.valueOf(idPr));
        lblId.setForeground(Color.GRAY);
        add(lblId, gbc);
        row++;

        // Catégorie
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Catégorie*:"), gbc);
        gbc.gridx = 1;
        add(categorieField, gbc);
        row++;

        // Fréquence
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Fréquence*:"), gbc);
        gbc.gridx = 1;
        add(frequenceField, gbc);
        row++;

        // NOUVEAU : Médicament
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Médicament:"), gbc);
        gbc.gridx = 1;

        JPanel medicamentPanel = new JPanel(new BorderLayout(5, 0));
        medicamentCombo.setPreferredSize(new Dimension(200, 25));
        medicamentCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Medicament) {
                    Medicament m = (Medicament) value;
                    String text = m.getNom();
                    if (m.getLaboratoire() != null && !m.getLaboratoire().isEmpty()) {
                        text += " (" + m.getLaboratoire() + ")";
                    }
                    setText(text);
                } else if (value == null) {
                    setText("-- Aucun médicament --");
                }
                return this;
            }
        });

        medicamentPanel.add(medicamentCombo, BorderLayout.CENTER);

        JPanel medicamentButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        medicamentButtons.add(btnRechercherMedicament);
        medicamentButtons.add(btnRetirerMedicament);
        medicamentPanel.add(medicamentButtons, BorderLayout.EAST);

        add(medicamentPanel, gbc);
        row++;

        // Durée
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Durée (jours):"), gbc);
        gbc.gridx = 1;
        add(dureeSpinner, gbc);
        row++;

        // Quantité
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Quantité:"), gbc);
        gbc.gridx = 1;
        add(quantiteSpinner, gbc);
        row++;

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RoundedButton btnModifier = new RoundedButton("Modifier");
        RoundedButton btnAnnuler = new RoundedButton("Annuler");
        ButtonStyles.primary(btnModifier);
        ButtonStyles.primary(btnAnnuler);

        btnModifier.addActionListener(e -> modifierPrescription());
        btnAnnuler.addActionListener(e -> dispose());
        btnRechercherMedicament.addActionListener(e -> rechercherMedicament());
        btnRetirerMedicament.addActionListener(e -> retirerMedicament());

        buttonPanel.add(btnModifier);
        buttonPanel.add(btnAnnuler);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        add(buttonPanel, gbc);
    }

    private void chargerMedicaments() {
        try {
            medicamentsDisponibles.clear();
            medicamentCombo.removeAllItems();

            // Charger tous les médicaments
            List<Medicament> medicaments = controller.getAllMedicaments();
            medicamentsDisponibles.addAll(medicaments);

            // Ajouter un item "Aucun médicament"
            medicamentCombo.addItem(null);

            // Ajouter les médicaments au combo
            for (Medicament med : medicaments) {
                medicamentCombo.addItem(med);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur chargement médicaments: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rechercherMedicament() {
        String recherche = JOptionPane.showInputDialog(this,
                "Rechercher un médicament (nom, laboratoire, type):",
                "Recherche médicament",
                JOptionPane.QUESTION_MESSAGE);

        if (recherche != null && !recherche.trim().isEmpty()) {
            try {
                List<Medicament> resultats = controller.rechercherMedicaments(
                        recherche, null, null, null);

                if (resultats.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Aucun médicament trouvé pour: " + recherche,
                            "Résultat", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Créer un dialog de sélection
                    String[] options = resultats.stream()
                            .map(m -> m.getIdMct() + " - " + m.getNom() +
                                    (m.getLaboratoire() != null ? " (" + m.getLaboratoire() + ")" : ""))
                            .toArray(String[]::new);

                    String choix = (String) JOptionPane.showInputDialog(this,
                            "Sélectionnez un médicament:",
                            "Sélection médicament",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]);

                    if (choix != null) {
                        // Trouver le médicament sélectionné
                        Long idSelected = Long.parseLong(choix.split(" - ")[0]);
                        for (Medicament med : resultats) {
                            if (med.getIdMct().equals(idSelected)) {
                                medicamentCombo.setSelectedItem(med);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Erreur recherche: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void retirerMedicament() {
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Retirer le médicament associé à cette prescription?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            medicamentCombo.setSelectedItem(null);
        }
    }

    private void loadPrescription() {
        try {
            prescriptionOriginale = controller.getPrescriptionById(idPr);

            if (prescriptionOriginale != null) {
                // Remplir les champs de base
                categorieField.setText(prescriptionOriginale.getCategorie() != null ?
                        prescriptionOriginale.getCategorie() : "");
                frequenceField.setText(prescriptionOriginale.getFrequence() != null ?
                        prescriptionOriginale.getFrequence() : "");

                if (prescriptionOriginale.getDureeEnJours() != null) {
                    dureeSpinner.setValue(prescriptionOriginale.getDureeEnJours());
                }

                if (prescriptionOriginale.getQuantite() != null) {
                    quantiteSpinner.setValue(prescriptionOriginale.getQuantite());
                }

                // Charger le médicament associé si présent
                if (prescriptionOriginale.getMedicaments() != null &&
                        !prescriptionOriginale.getMedicaments().isEmpty()) {

                    // Prendre le premier médicament (on suppose une relation 1-N)
                    medicamentActuel = prescriptionOriginale.getMedicaments().get(0);

                    // Trouver et sélectionner ce médicament dans la liste
                    for (int i = 0; i < medicamentCombo.getItemCount(); i++) {
                        Medicament med = medicamentCombo.getItemAt(i);
                        if (med != null && med.getIdMct().equals(medicamentActuel.getIdMct())) {
                            medicamentCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifierPrescription() {
        try {
            String categorie = categorieField.getText().trim();
            String frequence = frequenceField.getText().trim();

            if (categorie.isEmpty() || frequence.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Catégorie et Fréquence sont obligatoires",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Integer duree = (Integer) dureeSpinner.getValue();
            Integer quantite = (Integer) quantiteSpinner.getValue();

            // Récupérer le médicament sélectionné
            Medicament medicamentSelectionne = (Medicament) medicamentCombo.getSelectedItem();
            Long idMedicament = (medicamentSelectionne != null) ? medicamentSelectionne.getIdMct() : null;

            // Vérifier si le médicament a changé
            boolean medicamentChange = false;
            if (medicamentActuel == null && medicamentSelectionne != null) {
                medicamentChange = true; // Ajout d'un médicament
            } else if (medicamentActuel != null && medicamentSelectionne == null) {
                medicamentChange = true; // Retrait du médicament
            } else if (medicamentActuel != null && medicamentSelectionne != null &&
                    !medicamentActuel.getIdMct().equals(medicamentSelectionne.getIdMct())) {
                medicamentChange = true; // Changement de médicament
            }

            // Modifier la prescription
            // Remplacer la section de modification par :
            Prescription updated = controller.updatePrescription(idPr, categorie, frequence, duree, quantite);

            if (updated != null && medicamentChange) {
                if (medicamentActuel != null && medicamentSelectionne == null) {
                    // Retirer l'ancien médicament
                    controller.retirerMedicamentDePrescription(idPr, medicamentActuel.getIdMct());
                } else if (medicamentSelectionne != null) {
                    // Ajouter/Changer le médicament
                    if (medicamentActuel != null) {
                        // Retirer l'ancien d'abord
                        controller.retirerMedicamentDePrescription(idPr, medicamentActuel.getIdMct());
                    }
                    // Ajouter le nouveau
                    controller.ajouterMedicamentAPrescription(idPr, medicamentSelectionne.getIdMct());
                }
            }

            if (medicamentChange) {
                // TODO: Vous devrez créer une méthode spécifique pour gérer le changement de médicament
                // Pour l'instant, on utilise updatePrescription standard
                updated = controller.updatePrescription(idPr, categorie, frequence, duree, quantite);

                // Message d'avertissement
                if (updated != null && medicamentSelectionne != null) {
                    JOptionPane.showMessageDialog(this,
                            "Prescription modifiée. Note: Le médicament doit être géré séparément.",
                            "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                // Pas de changement de médicament, modification normale
                updated = controller.updatePrescription(idPr, categorie, frequence, duree, quantite);
            }

            if (updated != null) {
                modified = true;
                JOptionPane.showMessageDialog(this,
                        "Prescription modifiée avec succès",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur détaillée:\n" + e.getClass().getName() + ": " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isModified() {
        return modified;
    }
}