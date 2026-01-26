package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrdonnanceDetailsDialog extends JDialog {

    private final DossierMedicalController controller;
    private final Long idOrd;
    private boolean modified = false;

    private Ordonnance ordonnance;
    private final DefaultTableModel prescriptionsModel = new DefaultTableModel(
            new Object[]{"ID", "Catégorie", "Fréquence", "Durée", "Quantité"}, 0
    ) { @Override public boolean isCellEditable(int row, int col) { return false; } };
    private final JTable prescriptionsTable = new JTable(prescriptionsModel);

    /* ================== PALETTE ================== */
    private static final Color BG_MAIN = new Color(243, 246, 249);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color HEADER_BG = new Color(21, 101, 192);
    private static final Color BORDER_LIGHT = new Color(220, 224, 229);

    public OrdonnanceDetailsDialog(Window owner, DossierMedicalController controller, Long idOrd) {
        super(owner, "Ordonnance #" + idOrd, ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.idOrd = idOrd;

        setSize(750, 550);
        setMinimumSize(new Dimension(750, 550));
        setLocationRelativeTo(owner);
        setResizable(false);

        initUI();
        loadOrdonnance();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_MAIN);

        add(createHeader(), BorderLayout.NORTH);
        add(createTabbedContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    /* ================= HEADER ================= */
    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Détails Ordonnance #" + idOrd);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel subtitle = new JLabel("Informations et prescriptions associées");
        subtitle.setForeground(new Color(220, 235, 255));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);
        return header;
    }

    /* ================= CONTENT ================= */
    private JTabbedPane createTabbedContent() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Onglet Informations
        tabbedPane.addTab("Informations", createInfoPanel());

        // Onglet Prescriptions
        tabbedPane.addTab("Prescriptions", createPrescriptionsPanel());

        return tabbedPane;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_MAIN);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        Font labelFont = new Font("SansSerif", Font.BOLD, 12);
        Font valueFont = new Font("SansSerif", Font.PLAIN, 12);

        // ID Ordonnance
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("ID Ordonnance:"), gbc);
        gbc.gridx = 1;
        JLabel lblId = new JLabel(); lblId.setName("lblId"); lblId.setFont(valueFont);
        panel.add(lblId, gbc); row++;

        // Date
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        JLabel lblDate = new JLabel(); lblDate.setName("lblDate"); lblDate.setFont(valueFont);
        panel.add(lblDate, gbc); row++;

        // Dossier médical
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Dossier médical:"), gbc);
        gbc.gridx = 1;
        JLabel lblDossier = new JLabel(); lblDossier.setName("lblDossier"); lblDossier.setFont(valueFont);
        panel.add(lblDossier, gbc); row++;

        // Consultation
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Consultation:"), gbc);
        gbc.gridx = 1;
        JLabel lblConsultation = new JLabel(); lblConsultation.setName("lblConsultation"); lblConsultation.setFont(valueFont);
        panel.add(lblConsultation, gbc);

        return panel;
    }

    private JPanel createPrescriptionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_MAIN);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Tableau prescriptions
        prescriptionsTable.setRowHeight(28);
        prescriptionsTable.getTableHeader().setBackground(new Color(21, 101, 192));
        prescriptionsTable.getTableHeader().setForeground(Color.WHITE);
        prescriptionsTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        prescriptionsTable.setFillsViewportHeight(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < prescriptionsTable.getColumnCount(); i++) {
            prescriptionsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        panel.add(new JScrollPane(prescriptionsTable), BorderLayout.CENTER);

        // Boutons prescriptions
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setBackground(BG_MAIN);

        RoundedButton btnVoir = new RoundedButton("Voir détails");
        RoundedButton btnModifier = new RoundedButton("Modifier");
        RoundedButton btnSupprimer = new RoundedButton("Supprimer");

        ButtonStyles.primary(btnVoir);
        ButtonStyles.primary(btnModifier);
        ButtonStyles.danger(btnSupprimer);

        btnVoir.addActionListener(e -> voirDetailsPrescription());
        btnModifier.addActionListener(e -> modifierPrescription());
        btnSupprimer.addActionListener(e -> supprimerPrescription());

        btnPanel.add(btnVoir);
        btnPanel.add(btnModifier);
        btnPanel.add(btnSupprimer);

        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    /* ================= FOOTER ================= */
    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(BG_MAIN);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_LIGHT));

        RoundedButton btnAjouter = new RoundedButton("Ajouter presc.");
        RoundedButton btnSupprimerOrd = new RoundedButton("Supprimer ord.");
        RoundedButton btnFermer = new RoundedButton("Fermer");

        ButtonStyles.primary(btnAjouter);
        ButtonStyles.danger(btnSupprimerOrd);
        ButtonStyles.secondary(btnFermer);

        btnAjouter.addActionListener(e -> ajouterPrescription());
        btnSupprimerOrd.addActionListener(e -> supprimerOrdonnance());
        btnFermer.addActionListener(e -> dispose());

        footer.add(btnAjouter);
        footer.add(btnSupprimerOrd);
        footer.add(btnFermer);
        return footer;
    }

    /* ================= LOAD DATA ================= */
    private void loadOrdonnance() {
        try {
            ordonnance = controller.getOrdonnanceById(idOrd);
            if (ordonnance == null) {
                JOptionPane.showMessageDialog(this, "Ordonnance non trouvée", "Erreur", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }
            updateLabelsDirectly();
            loadPrescriptions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLabelsDirectly() {
        updateLabelInContainer(getContentPane());
    }

    private void updateLabelInContainer(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel label) {
                String name = label.getName();
                if (name != null) {
                    switch (name) {
                        case "lblId" -> label.setText(String.valueOf(ordonnance.getIdOrd()));
                        case "lblDate" -> label.setText(ordonnance.getDate() != null ? ordonnance.getDate().toString() : "");
                        case "lblDossier" -> label.setText("Dossier #" + ordonnance.getIdDm());
                        case "lblConsultation" -> label.setText("Consultation #" + ordonnance.getIdConsultation());
                    }
                }
            } else if (comp instanceof Container c) {
                updateLabelInContainer(c);
            }
        }
    }

    private void loadPrescriptions() {
        try {
            List<Prescription> prescriptions = controller.getPrescriptionsByOrdonnance(idOrd);
            prescriptionsModel.setRowCount(0);
            for (Prescription p : prescriptions) {
                prescriptionsModel.addRow(new Object[]{
                        p.getIdPr(), p.getCategorie(), p.getFrequence(),
                        p.getDureeEnJours() != null ? p.getDureeEnJours() + " jours" : "",
                        p.getQuantite()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement prescriptions: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* ================= ACTIONS ================= */
    private void ajouterPrescription() {
        AjouterPrescriptionDialog dialog = new AjouterPrescriptionDialog(this, controller, idOrd);
        dialog.setVisible(true);
        if (dialog.isSuccess()) { modified = true; loadPrescriptions(); }
    }

    private void voirDetailsPrescription() {
        int row = prescriptionsTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une prescription"); return; }
        Long idPr = (Long) prescriptionsModel.getValueAt(row, 0);
        PrescriptionDetailsDialog dialog = new PrescriptionDetailsDialog(this, controller, idPr);
        dialog.setVisible(true);
    }

    private void modifierPrescription() {
        int row = prescriptionsTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une prescription"); return; }
        Long idPr = (Long) prescriptionsModel.getValueAt(row, 0);
        ModifierPrescriptionDialog dialog = new ModifierPrescriptionDialog(this, controller, idPr);
        dialog.setVisible(true);
        if (dialog.isModified()) { modified = true; loadPrescriptions(); }
    }

    private void supprimerPrescription() {
        int row = prescriptionsTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une prescription"); return; }
        Long idPr = (Long) prescriptionsModel.getValueAt(row, 0);
        String cat = (String) prescriptionsModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer la prescription '" + cat + "'?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try { controller.deletePrescription(idPr); modified = true; loadPrescriptions(); }
            catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage()); }
        }
    }

    private void supprimerOrdonnance() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer cette ordonnance? Cette action est irréversible.",
                "Confirmation suppression", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try { controller.deleteOrdonnance(idOrd); modified = true; dispose(); }
            catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage()); }
        }
    }

    public boolean isModified() { return modified; }
}
