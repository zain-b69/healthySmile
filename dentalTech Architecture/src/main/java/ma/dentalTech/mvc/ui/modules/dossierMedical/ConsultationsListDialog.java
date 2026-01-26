// Fichier: ma/dentalTech/mvc/ui/modules/dossierMedical/ConsultationsListDialog.java
package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConsultationsListDialog extends JDialog {

    private final DossierMedicalController controller;
    private final List<Consultation> consultations;

    /* ================== PALETTE ================== */
    private static final Color BG_MAIN = new Color(243, 246, 249);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color HEADER_BG = new Color(21, 101, 192);

    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color BORDER_LIGHT = new Color(220, 224, 229);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Date", "Médecin ID", "Statut"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable table = new JTable(model);

    public ConsultationsListDialog(Window owner,
                                   DossierMedicalController controller,
                                   List<Consultation> consultations) {
        super(owner, "Consultations du dossier", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.consultations = consultations;

        setSize(650, 480);
        setMinimumSize(new Dimension(650, 480));
        setResizable(false);
        setLocationRelativeTo(owner);

        initUI();
        loadConsultations();
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

        JLabel title = new JLabel("Liste des consultations");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel subtitle = new JLabel("Consultations associées au dossier médical");
        subtitle.setForeground(new Color(220, 235, 255));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);

        return header;
    }

    /* ================= CONTENT ================= */
    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BG_MAIN);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT));

        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        card.add(scrollPane, BorderLayout.CENTER);
        content.add(card, BorderLayout.CENTER);

        return content;
    }

    /* ================= FOOTER ================= */
    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(BG_MAIN);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_LIGHT));

        RoundedButton btnVoirDetails = new RoundedButton("Voir détails");
        RoundedButton btnSupprimer = new RoundedButton("Supprimer");
        RoundedButton btnFermer = new RoundedButton("Fermer");

        ButtonStyles.primary(btnVoirDetails);
        ButtonStyles.danger(btnSupprimer);
        ButtonStyles.secondary(btnFermer);

        btnVoirDetails.addActionListener(e -> voirDetailsConsultation());
        btnSupprimer.addActionListener(e -> supprimerConsultation());
        btnFermer.addActionListener(e -> dispose());

        footer.add(btnVoirDetails);
        footer.add(btnSupprimer);
        footer.add(btnFermer);

        return footer;
    }

    /* ================= DATA ================= */
    private void loadConsultations() {
        model.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Consultation c : consultations) {
            String dateStr = c.getDate() != null ? c.getDate().format(fmt) : "";
            String statutStr = c.getStatut() != null ? c.getStatut().name() : "";

            model.addRow(new Object[]{
                    c.getIdConsultation(),
                    dateStr,
                    c.getIdMedecin(),
                    statutStr
            });
        }
    }

    /* ================= ACTIONS ================= */
    private void voirDetailsConsultation() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Sélectionnez une consultation",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        long idConsultation = ((Number) model.getValueAt(row, 0)).longValue();

        Consultation selected = null;
        for (Consultation c : consultations) {
            if (c.getIdConsultation() != null && c.getIdConsultation().equals(idConsultation)) {
                selected = c;
                break;
            }
        }

        if (selected != null) {
            new ConsultationDetailsDialog(this, controller, selected).setVisible(true);
            loadConsultations();
        }
    }

    private void supprimerConsultation() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Sélectionnez une consultation à supprimer",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        long idConsultation = ((Number) model.getValueAt(row, 0)).longValue();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer la consultation #" + idConsultation + " ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = controller.supprimerConsultation(idConsultation);
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Consultation supprimée avec succès.",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadConsultations();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Échec de la suppression.",
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
