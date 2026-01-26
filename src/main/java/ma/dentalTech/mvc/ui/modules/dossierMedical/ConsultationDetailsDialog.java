// Fichier: ma/dentalTech/mvc/ui/modules/dossierMedical/ConsultationDetailsDialog.java
package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ConsultationDetailsDialog extends JDialog {

    private final DossierMedicalController controller;
    private final Consultation consultation;

    private final JTextArea observationArea = new JTextArea(5, 40);
    private final DefaultTableModel interventionsModel = new DefaultTableModel(
            new Object[]{"ID", "Acte", "Dent", "Prix"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
    private final JTable interventionsTable = new JTable(interventionsModel);

    /* ================== PALETTE ================== */
    private static final Color BG_MAIN = new Color(243, 246, 249);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color HEADER_BG = new Color(21, 101, 192);

    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color BORDER_LIGHT = new Color(220, 224, 229);

    public ConsultationDetailsDialog(Window owner, DossierMedicalController controller, Consultation consultation) {
        super(owner, "Consultation #" + consultation.getIdConsultation(), ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.consultation = consultation;

        setSize(800, 600);
        setMinimumSize(new Dimension(800, 600));
        setResizable(false);
        setLocationRelativeTo(owner);

        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_MAIN);

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    /* ================= HEADER ================= */
    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Détails Consultation #" + consultation.getIdConsultation());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel subtitle = new JLabel("Informations et interventions associées");
        subtitle.setForeground(new Color(220, 235, 255));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);

        return header;
    }

    /* ================= CONTENT ================= */
    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(BG_MAIN);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel central avec info + onglets
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BG_MAIN);

        // Info panel
        JPanel infoCard = new JPanel(new GridLayout(4, 2, 5, 5));
        infoCard.setBackground(BG_CARD);
        infoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT),
                new EmptyBorder(16, 16, 16, 16)
        ));

        infoCard.add(new JLabel("ID:"));
        infoCard.add(new JLabel(String.valueOf(consultation.getIdConsultation())));

        infoCard.add(new JLabel("Date:"));
        infoCard.add(new JLabel(consultation.getDate() != null ? consultation.getDate().toString() : ""));

        infoCard.add(new JLabel("Médecin ID:"));
        infoCard.add(new JLabel(String.valueOf(consultation.getIdMedecin())));

        infoCard.add(new JLabel("Statut:"));
        infoCard.add(new JLabel(consultation.getStatut() != null ? consultation.getStatut().name() : ""));

        centerPanel.add(infoCard, BorderLayout.NORTH);

        // Onglets
        JTabbedPane tabbedPane = new JTabbedPane();

        // Observation
        JPanel obsPanel = new JPanel(new BorderLayout());
        observationArea.setLineWrap(true);
        observationArea.setWrapStyleWord(true);
        obsPanel.add(new JScrollPane(observationArea), BorderLayout.CENTER);

        JPanel obsButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        RoundedButton btnSaveObs = new RoundedButton("Sauvegarder");
        ButtonStyles.primary(btnSaveObs);
        btnSaveObs.addActionListener(e -> saveObservation());
        obsButtons.add(btnSaveObs);
        obsPanel.add(obsButtons, BorderLayout.SOUTH);

        tabbedPane.addTab("Observation", obsPanel);

        // Interventions
        JPanel interPanel = new JPanel(new BorderLayout());
        interventionsTable.setRowHeight(28);
        interPanel.add(new JScrollPane(interventionsTable), BorderLayout.CENTER);

        JPanel interButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        RoundedButton btnAddInter = new RoundedButton("Ajouter interv.");
        RoundedButton btnCalcTotal = new RoundedButton("Calculer total");
        ButtonStyles.primary(btnAddInter);
        ButtonStyles.primary(btnCalcTotal);
        btnAddInter.addActionListener(e -> addIntervention());
        btnCalcTotal.addActionListener(e -> calculateTotal());
        interButtons.add(btnAddInter);
        interButtons.add(btnCalcTotal);
        interPanel.add(interButtons, BorderLayout.SOUTH);

        tabbedPane.addTab("Interventions", interPanel);

        centerPanel.add(tabbedPane, BorderLayout.CENTER);
        content.add(centerPanel, BorderLayout.CENTER);

        return content;
    }

    /* ================= FOOTER ================= */
    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(BG_MAIN);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_LIGHT));

        RoundedButton btnClose = new RoundedButton("Fermer");
        ButtonStyles.secondary(btnClose);
        btnClose.addActionListener(e -> dispose());

        footer.add(btnClose);
        return footer;
    }

    /* ================= DATA ================= */
    private void loadData() {
        observationArea.setText(consultation.getObservationMedecin() != null ?
                consultation.getObservationMedecin() : "");

        interventionsModel.setRowCount(0);
        List<InterventionMedecin> interventions = controller.getInterventionsByConsultation(
                consultation.getIdConsultation());

        for (InterventionMedecin im : interventions) {
            interventionsModel.addRow(new Object[]{
                    im.getIdIm(),
                    "Acte #" + im.getIdActe(),
                    im.getNumDent(),
                    im.getPrixDePatient() != null ? String.format("%.2f DH", im.getPrixDePatient()) : "0.00 DH"
            });
        }
    }

    /* ================= ACTIONS ================= */
    private void saveObservation() {
        try {
            controller.ajouterObservationConsultation(
                    consultation.getIdConsultation(),
                    observationArea.getText().trim()
            );
            JOptionPane.showMessageDialog(this, "Observation sauvegardée");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addIntervention() {
        AjouterInterventionDialog dialog = new AjouterInterventionDialog(
                this, controller, consultation.getIdConsultation());
        dialog.setVisible(true);
        loadData();
    }

    private void calculateTotal() {
        try {
            Double total = controller.calculerTotalConsultation(consultation.getIdConsultation());
            JOptionPane.showMessageDialog(this,
                    String.format("Total de la consultation: %.2f DH", total),
                    "Total", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
