// Fichier: ma/dentalTech/mvc/ui/modules/dossierMedical/DossierMedicalDetailsDialog.java
package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.List;

public class DossierMedicalDetailsDialog extends JDialog {

    private final DossierMedicalController controller;
    private final long idDm;
    private final Runnable onCloseRefresh;

    private JLabel lblIdPatient;
    private JLabel lblDateCreation;

    /* ================== PALETTE FINALE ================== */
    private static final Color BG_MAIN = new Color(243, 246, 249);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color BG_INFO = new Color(248, 250, 253);
    private static final Color HEADER_BG = new Color(21, 101, 192);

    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(100, 100, 100);

    private static final Color BORDER_LIGHT = new Color(220, 224, 229);

    public DossierMedicalDetailsDialog(Window owner,
                                       DossierMedicalController controller,
                                       long idDm,
                                       Runnable onCloseRefresh) {
        super(owner, "Dossier Médical #" + idDm, ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.idDm = idDm;
        this.onCloseRefresh = onCloseRefresh;

        setSize(540, 700);
        setMinimumSize(new Dimension(540, 520));
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
        header.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel title = new JLabel("Dossier Médical");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel subtitle = new JLabel("Consultation des informations du dossier #" + idDm);
        subtitle.setForeground(new Color(220, 235, 255));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);

        return header;
    }

    /* ================= CONTENT ================= */
    private JPanel createContent() {
        JPanel content = new JPanel();
        content.setBackground(BG_MAIN);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        content.add(createInfoPanel());
        content.add(Box.createVerticalStrut(22));
        content.add(createActionsPanel());

        return content;
    }

    /* ================= INFO PANEL ================= */
    private JPanel createInfoPanel() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_INFO);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel sectionTitle = new JLabel("Informations générales");
        sectionTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        sectionTitle.setForeground(TEXT_PRIMARY);

        card.add(sectionTitle);
        card.add(Box.createVerticalStrut(12));
        card.add(createInfoRow("ID Dossier", String.valueOf(idDm)));
        card.add(createSeparator());
        card.add(createInfoRow("ID Patient", "Chargement...", true));
        card.add(createSeparator());
        card.add(createInfoRow("Date de création", "Chargement...", false));

        return card;
    }

    private JPanel createInfoRow(String label, String value) {
        return createInfoRow(label, value, false);
    }

    private JPanel createInfoRow(String label, String value, boolean isPatient) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(6, 0, 6, 0));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblLabel.setForeground(TEXT_SECONDARY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblValue.setForeground(TEXT_PRIMARY);

        if (isPatient) lblIdPatient = lblValue;
        else if (label.contains("Date")) lblDateCreation = lblValue;

        row.add(lblLabel, BorderLayout.WEST);
        row.add(lblValue, BorderLayout.EAST);
        return row;
    }

    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_LIGHT);
        return sep;
    }

    /* ================= ACTIONS PANEL ================= */
    private JPanel createActionsPanel() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel sectionTitle = new JLabel("Actions disponibles", SwingConstants.CENTER);
        sectionTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        sectionTitle.setForeground(TEXT_PRIMARY);
        sectionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(sectionTitle);


        card.add(sectionTitle);
        card.add(Box.createVerticalStrut(14));

        Dimension btnSize = new Dimension(Integer.MAX_VALUE, 40);
        Font btnFont = new Font("SansSerif", Font.PLAIN, 13);

        card.add(createActionButton("Consultations", btnFont, btnSize, this::ouvrirConsultations));
        card.add(Box.createVerticalStrut(10));
        card.add(createActionButton("Ordonnances", btnFont, btnSize, this::ouvrirOrdonnances));
        card.add(Box.createVerticalStrut(10));
        card.add(createActionButton("Certificats", btnFont, btnSize, this::ouvrirCertificats));
        card.add(Box.createVerticalStrut(10));
        card.add(createActionButton("Situation financière", btnFont, btnSize, this::ouvrirSituationFinanciere));
        card.add(Box.createVerticalStrut(18));

        RoundedButton btnSupprimer = new RoundedButton("Supprimer définitivement le dossier");
        ButtonStyles.danger(btnSupprimer);
        btnSupprimer.setFont(btnFont);
        btnSupprimer.setMaximumSize(btnSize);
        btnSupprimer.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSupprimer.setForeground(Color.WHITE);
        btnSupprimer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSupprimer.addActionListener(e -> supprimerDossier());

        card.add(btnSupprimer);
        return card;
    }

    private RoundedButton createActionButton(String text,
                                             Font font,
                                             Dimension size,
                                             Runnable action) {
        RoundedButton btn = new RoundedButton(text);
        ButtonStyles.primary(btn);
        btn.setFont(font);
        btn.setMaximumSize(size);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());
        return btn;
    }

    /* ================= FOOTER ================= */
    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(BG_MAIN);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_LIGHT));

        RoundedButton btnFermer = new RoundedButton("Fermer");
        ButtonStyles.secondary(btnFermer);
        btnFermer.setPreferredSize(new Dimension(110, 36));
        btnFermer.addActionListener(e -> dispose());

        footer.add(btnFermer);
        return footer;
    }

    /* ================= DATA ================= */
    private void loadData() {
        try {
            DossierMedical dm = controller.getById(idDm);
            lblIdPatient.setText(String.valueOf(dm.getIdPatient()));
            lblDateCreation.setText(
                    dm.getDateDeCreation() != null
                            ? dm.getDateDeCreation().toString()
                            : "Non définie"
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur chargement : " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // =================== ACTIONS ===================

    private void ouvrirConsultations() {
        try {
            List<ma.dentalTech.entities.dossierMedical.Consultation> consultations =
                    controller.getConsultationsByDossier(idDm);

            if (consultations.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Aucune consultation pour ce dossier",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            ConsultationsListDialog dialog =
                    new ConsultationsListDialog(this, controller, consultations);
            dialog.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ouvrirOrdonnances() {
        try {
            var ordonnances = controller.getOrdonnancesByDossier(idDm);
            if (ordonnances.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Aucune ordonnance pour ce dossier",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                new OrdonnancesDialog(this, controller, idDm, ordonnances).setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ouvrirCertificats() {
        try {
            var certificats = controller.getCertificatsByDossier(idDm);
            new CertificatsDialog(this, controller, idDm, certificats).setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ouvrirSituationFinanciere() {
        try {
            var situation = controller.getSituationByDossier(idDm);
            if (situation == null) {
                JOptionPane.showMessageDialog(this,
                        "Aucune situation financière pour ce dossier",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                new SituationFinanciereDialog(this, controller, situation).setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerDossier() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer définitivement le dossier #" + idDm + " ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String saisie = JOptionPane.showInputDialog(
                    this,
                    "Tapez SUPPRIMER pour confirmer :",
                    "Confirmation finale",
                    JOptionPane.WARNING_MESSAGE
            );

            if ("SUPPRIMER".equals(saisie)) {
                try {
                    if (controller.supprimerDossier(idDm)) {
                        if (onCloseRefresh != null) {
                            onCloseRefresh.run();
                        }
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Échec de la suppression",
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            e.getMessage(),
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
