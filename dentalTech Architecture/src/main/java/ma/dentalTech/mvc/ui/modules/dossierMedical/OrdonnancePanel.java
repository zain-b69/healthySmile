// Fichier: ma/dentalTech/mvc/ui/modules/dossierMedical/OrdonnancePanel.java
package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class OrdonnancePanel extends JPanel {

    private final DossierMedicalController controller;
    private final Long idDm; // Peut être null pour voir toutes les ordonnances

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Date", "Consultation", "Dossier", "Actions"}, 0
    ) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return c == 4; // Seule la colonne Actions est éditable
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return column == 4 ? JButton.class : Object.class;
        }
    };

    private final JTable table = new JTable(model);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public OrdonnancePanel(DossierMedicalController controller) {
        this(controller, null);
    }

    public OrdonnancePanel(DossierMedicalController controller, Long idDm) {
        this.controller = controller;
        this.idDm = idDm;

        initUI();
        loadOrdonnances();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // Titre
        String title = idDm != null ?
                "Ordonnances - Dossier #" + idDm :
                "Toutes les ordonnances";
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // Table avec renderer pour les boutons
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));
        table.setRowHeight(30);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Boutons d'action - MODIFIÉ ICI
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        RoundedButton btnNouvelleOrdonnance = new RoundedButton("Nv. ordonnance");
        RoundedButton btnGererMedicaments = new RoundedButton("📦 Médicaments");
        RoundedButton btnRefresh = new RoundedButton("Actualiser");

        ButtonStyles.primary(btnNouvelleOrdonnance);
        ButtonStyles.primary(btnGererMedicaments);
        ButtonStyles.primary(btnRefresh);

        btnNouvelleOrdonnance.addActionListener(e -> creerOrdonnance());
        btnGererMedicaments.addActionListener(e -> gererMedicaments(e)); // NOUVEAU
        btnRefresh.addActionListener(e -> loadOrdonnances());

        buttonPanel.add(btnNouvelleOrdonnance);
        buttonPanel.add(btnGererMedicaments); // NOUVEAU
        buttonPanel.add(btnRefresh);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadOrdonnances() {
        try {
            List<Ordonnance> ordonnances;

            if (idDm != null) {
                ordonnances = controller.getOrdonnancesByDossier(idDm);
            } else {
                // Charger toutes les ordonnances (vous devrez peut-être ajouter cette méthode)
                ordonnances = controller.getAllOrdonnances();
            }

            model.setRowCount(0);

            for (Ordonnance ord : ordonnances) {
                String dateStr = ord.getDate() != null ?
                        ord.getDate().format(dateFormatter) : "";

                model.addRow(new Object[]{
                        ord.getIdOrd(),
                        dateStr,
                        ord.getIdConsultation() != null ? "Consult. #" + ord.getIdConsultation() : "",
                        ord.getIdDm() != null ? "Dossier #" + ord.getIdDm() : "",
                        "Détails"
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void creerOrdonnance() {
        Long dossierId = idDm;

        // Si on n'a pas d'ID de dossier (mode "toutes les ordonnances")
        if (dossierId == null) {
            // Demander l'ID du dossier à l'utilisateur
            String input = JOptionPane.showInputDialog(this,
                    "Pour créer une ordonnance, veuillez saisir l'ID du dossier médical:",
                    "ID Dossier médical",
                    JOptionPane.QUESTION_MESSAGE);

            if (input == null || input.trim().isEmpty()) {
                return; // Annulé
            }

            try {
                dossierId = Long.parseLong(input.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "ID de dossier invalide. Veuillez saisir un nombre.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Ouvrir le dialog de création avec l'ID du dossier
        CreerOrdonnanceDialog dialog = new CreerOrdonnanceDialog(
                SwingUtilities.getWindowAncestor(this),
                controller,
                dossierId
        );
        dialog.setVisible(true);

        // Recharger après création
        if (dialog.isSuccess()) {
            loadOrdonnances();
        }
    }

    private void voirDetailsOrdonnance(int row) {
        Long idOrd = (Long) model.getValueAt(row, 0);
        if (idOrd != null) {
            OrdonnanceDetailsDialog dialog = new OrdonnanceDetailsDialog(
                    SwingUtilities.getWindowAncestor(this),
                    controller,
                    idOrd
            );
            dialog.setVisible(true);

            // Recharger si modifications
            if (dialog.isModified()) {
                loadOrdonnances();
            }
        }
    }

    // NOUVELLE MÉTHODE
    private void gererMedicaments(ActionEvent e) {
        // Ouvrir un menu contextuel pour les médicaments
        JPopupMenu menu = new JPopupMenu();

        JMenuItem itemAjouter = new JMenuItem("➕ Ajouter un médicament");
        JMenuItem itemListe = new JMenuItem("📋 Liste des médicaments");
        JMenuItem itemChargerDefaut = new JMenuItem("⚡ Charger médicaments par défaut");

        itemAjouter.addActionListener(ev -> ouvrirAjouterMedicament());
        itemListe.addActionListener(ev -> ouvrirListeMedicaments());
        itemChargerDefaut.addActionListener(ev -> chargerMedicamentsParDefaut());

        menu.add(itemAjouter);
        menu.add(itemListe);
        menu.addSeparator();
        menu.add(itemChargerDefaut);

        // Afficher le menu près du bouton
        Component source = (Component) e.getSource();
        menu.show(source, 0, source.getHeight());
    }

    private void ouvrirAjouterMedicament() {
        AjouterMedicamentDialog dialog = new AjouterMedicamentDialog(
                SwingUtilities.getWindowAncestor(this),
                controller
        );
        dialog.setVisible(true);
    }

    private void ouvrirListeMedicaments() {
        // Utilisez votre nouveau dialog !
        ListeMedicamentsDialog dialog = new ListeMedicamentsDialog(
                SwingUtilities.getWindowAncestor(this),
                controller
        );
        dialog.setVisible(true);
    }

    private void chargerMedicamentsParDefaut() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Charger 12 médicaments dentaires courants?\n(Doliprane, Amoxicilline, etc.)",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                chargerMedicamentsStandards();
                JOptionPane.showMessageDialog(this,
                        "12 médicaments chargés avec succès !\nRechargez les prescriptions pour les voir.",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Erreur: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void chargerMedicamentsStandards() {
        List<Object[]> medicaments = Arrays.asList(
                new Object[]{"Doliprane", "Sanofi", "Antalgique", "Comprimé", true, 15.0, "Paracétamol 500mg"},
                new Object[]{"Amoxicilline", "GSK", "Antibiotique", "Comprimé", true, 45.0, "Antibiotique pénicilline"},
                new Object[]{"Ibuprofène", "Pfizer", "Anti-inflammatoire", "Comprimé", true, 25.0, "AINS 400mg"},
                new Object[]{"Chlorhexidine", "Pierre Fabre", "Antiseptique", "Solution", true, 30.0, "Bain de bouche 0.12%"},
                new Object[]{"Lidocaïne", "AstraZeneca", "Anesthésique", "Pommade", true, 35.0, "Gel anesthésique"},
                new Object[]{"Nystatine", "Bristol Myers", "Antifongique", "Suspension", true, 40.0, "Traitement muguet"},
                new Object[]{"Prednisolone", "Merck", "Corticoïde", "Comprimé", true, 50.0, "Corticoïde 20mg"},
                new Object[]{"Calcium", "Roche", "Complément", "Comprimé", false, 20.0, "Carbonate de calcium"},
                new Object[]{"Vitamine C", "Bayer", "Vitamine", "Comprimé", false, 15.0, "Acide ascorbique 500mg"},
                new Object[]{"Eludril", "Pierre Fabre", "Antiseptique", "Solution", true, 35.0, "Chlorhexidine"},
                new Object[]{"Xylocaïne", "Aspen", "Anesthésique", "Gel", true, 40.0, "Lidocaïne 2%"},
                new Object[]{"Clamoxyl", "GSK", "Antibiotique", "Comprimé", true, 50.0, "Amoxicilline + acide"}
        );

        int compteur = 0;
        for (Object[] med : medicaments) {
            try {
                controller.creerMedicament(
                        (String) med[0], (String) med[1], (String) med[2], (String) med[3],
                        (Boolean) med[4], (Double) med[5], (String) med[6]
                );
                compteur++;
                System.out.println("✓ " + med[0] + " ajouté");
            } catch (Exception e) {
                System.err.println("✗ Erreur " + med[0] + ": " + e.getMessage());
            }
        }
        System.out.println("Total: " + compteur + " médicaments ajoutés");
    }

    // Classe interne pour le rendu des boutons
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // Classe interne pour l'édition des boutons
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.row = row;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                voirDetailsOrdonnance(row);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}