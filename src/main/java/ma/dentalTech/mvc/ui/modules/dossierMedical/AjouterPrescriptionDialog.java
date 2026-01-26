package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class AjouterPrescriptionDialog extends JDialog {

    private final DossierMedicalController controller;
    private final Long idOrd;
    private boolean success = false;

    private final JTextField categorieField = new JTextField(15);
    private final JTextField frequenceField = new JTextField(15);
    private final JSpinner dureeSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
    private final JSpinner quantiteSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

    // NOUVEAU : Sélection de médicament
    private final JComboBox<Medicament> medicamentCombo = new JComboBox<>();
    private final JButton btnRechercherMedicament = new JButton("Rechercher...");
    private final List<Medicament> medicamentsDisponibles = new ArrayList<>();

    public AjouterPrescriptionDialog(Window owner, DossierMedicalController controller, Long idOrd) {
        super(owner, "Ajouter une prescription", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.idOrd = idOrd;

        initUI();
        chargerMedicaments();

        setSize(500, 400); // Augmenté la taille
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

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

        // Custom renderer pour afficher les médicaments correctement
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
        medicamentPanel.add(btnRechercherMedicament, BorderLayout.EAST);
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
        RoundedButton btnAjouter = new RoundedButton("Ajouter");
        RoundedButton btnAnnuler = new RoundedButton("Annuler");
        RoundedButton btnGererMedicaments = new RoundedButton("📦 Gérer médicaments");

        ButtonStyles.primary(btnAjouter);
        ButtonStyles.primary(btnAnnuler);
        ButtonStyles.primary(btnGererMedicaments);

        btnAjouter.addActionListener(e -> ajouterPrescription());
        btnAnnuler.addActionListener(e -> dispose());
        btnRechercherMedicament.addActionListener(e -> rechercherMedicament());
        btnGererMedicaments.addActionListener(e -> ouvrirGestionMedicaments(e));

        buttonPanel.add(btnGererMedicaments);
        buttonPanel.add(btnAjouter);
        buttonPanel.add(btnAnnuler);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);
    }

    private void chargerMedicaments() {
        try {
            medicamentsDisponibles.clear();
            medicamentCombo.removeAllItems();

            // Charger tous les médicaments
            List<Medicament> medicaments = controller.getAllMedicaments();

            if (medicaments.isEmpty()) {
                medicamentCombo.addItem(null);
                medicamentCombo.setToolTipText("Aucun médicament disponible. Cliquez sur 'Gérer médicaments' pour en ajouter.");
            } else {
                medicamentsDisponibles.addAll(medicaments);
                medicamentCombo.addItem(null); // Option "Aucun médicament"

                for (Medicament med : medicaments) {
                    medicamentCombo.addItem(med);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur chargement médicaments: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rechercherMedicament() {
        // Ouvrir un dialog pour rechercher un médicament
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
                    // Afficher la liste pour sélection
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

    private void ouvrirGestionMedicaments(ActionEvent e) {
        // Créer un menu contextuel simple
        JPopupMenu menu = new JPopupMenu();

        JMenuItem itemAjouter = new JMenuItem("➕ Ajouter un médicament");
        JMenuItem itemChargerDefaut = new JMenuItem("⚡ Charger médicaments par défaut");
        JMenuItem itemRafraichir = new JMenuItem("🔄 Rafraîchir la liste");

        itemAjouter.addActionListener(ev -> {
            // Ouvrir le dialog d'ajout de médicament
            AjouterMedicamentDialog dialog = new AjouterMedicamentDialog(
                    SwingUtilities.getWindowAncestor(this),
                    controller
            );
            dialog.setVisible(true);
            // Recharger la liste après ajout
            if (dialog.isSuccess()) {
                chargerMedicaments();
            }
        });

        itemChargerDefaut.addActionListener(ev -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Charger 12 médicaments dentaires courants?\n(Doliprane, Amoxicilline, etc.)",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // Charger les médicaments standards directement depuis ici
                    chargerMedicamentsStandardsDirectement();
                    chargerMedicaments(); // Recharger la liste
                    JOptionPane.showMessageDialog(this,
                            "Médicaments chargés avec succès !",
                            "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Erreur: " + ex.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        itemRafraichir.addActionListener(ev -> chargerMedicaments());

        menu.add(itemAjouter);
        menu.add(itemChargerDefaut);
        menu.addSeparator();
        menu.add(itemRafraichir);

        // Afficher le menu près du bouton
        Component source = (Component) e.getSource();
        menu.show(source, 0, source.getHeight());
    }

    private void chargerMedicamentsStandardsDirectement() {
        // Liste réduite de médicaments courants
        String[][] medicaments = {
                {"Doliprane", "Sanofi", "Antalgique", "Comprimé", "15.0", "Paracétamol 500mg"},
                {"Amoxicilline", "GSK", "Antibiotique", "Comprimé", "45.0", "Antibiotique pénicilline"},
                {"Ibuprofène", "Pfizer", "Anti-inflammatoire", "Comprimé", "25.0", "AINS 400mg"},
                {"Chlorhexidine", "Pierre Fabre", "Antiseptique", "Solution", "30.0", "Bain de bouche 0.12%"},
                {"Lidocaïne", "AstraZeneca", "Anesthésique", "Pommade", "35.0", "Gel anesthésique"},
                {"Nystatine", "Bristol Myers", "Antifongique", "Suspension", "40.0", "Traitement muguet"}
        };

        int compteur = 0;
        for (String[] med : medicaments) {
            try {
                // Utiliser la méthode existante du contrôleur
                controller.creerMedicament(
                        med[0], // nom
                        med[1], // laboratoire
                        med[2], // type
                        med[3], // forme
                        true,    // remboursable (valeur par défaut)
                        Double.parseDouble(med[4]), // prix
                        med[5]  // description
                );
                compteur++;
                System.out.println("✓ " + med[0] + " ajouté depuis AjouterPrescriptionDialog");
            } catch (Exception ex) {
                System.err.println("✗ Erreur " + med[0] + ": " + ex.getMessage());
                // Continuer avec les autres médicaments même en cas d'erreur
            }
        }
        System.out.println("Total: " + compteur + " médicaments ajoutés depuis AjouterPrescriptionDialog");
    }

    private void ajouterPrescription() {
        try {
            String categorie = categorieField.getText().trim();
            String frequence = frequenceField.getText().trim();

            if (categorie.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "La catégorie est obligatoire",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                categorieField.requestFocus();
                return;
            }

            Integer duree = (Integer) dureeSpinner.getValue();
            Integer quantite = (Integer) quantiteSpinner.getValue();

            // Récupérer le médicament sélectionné
            Medicament medicamentSelectionne = (Medicament) medicamentCombo.getSelectedItem();
            Long idMedicament = (medicamentSelectionne != null) ? medicamentSelectionne.getIdMct() : null;

            // Ajouter la prescription
            Prescription prescription;

            if (idMedicament != null) {
                // Utiliser la nouvelle méthode avec médicament
                prescription = controller.ajouterPrescriptionAvecMedicament(idOrd, categorie, frequence, duree, quantite, idMedicament);
            } else {
                // Sans médicament (comme avant)
                prescription = controller.ajouterPrescription(idOrd, categorie, frequence, duree, quantite);
            }

            if (prescription != null) {
                success = true;
                JOptionPane.showMessageDialog(this,
                        "Prescription ajoutée avec succès (ID: " + prescription.getIdPr() + ")",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() {
        return success;
    }
}