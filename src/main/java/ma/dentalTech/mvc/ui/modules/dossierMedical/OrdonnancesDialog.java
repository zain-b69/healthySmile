// Fichier: ma/dentalTech/mvc/ui/modules/dossierMedical/OrdonnancesDialog.java
package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.Ordonnance;
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

public class OrdonnancesDialog extends JDialog {

    private final DossierMedicalController controller;
    private final long idDm;
    private final List<Ordonnance> ordonnances;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Date", "Consultation"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    /* ================== PALETTE ================== */
    private static final Color BG_MAIN = new Color(243, 246, 249);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color HEADER_BG = new Color(21, 101, 192);

    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color BORDER_LIGHT = new Color(220, 224, 229);

    public OrdonnancesDialog(Window owner, DossierMedicalController controller,
                             long idDm, List<Ordonnance> ordonnances) {
        super(owner, "Ordonnances - Dossier #" + idDm, ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.idDm = idDm;
        this.ordonnances = ordonnances;

        setSize(600, 400);
        setMinimumSize(new Dimension(600, 400));
        setResizable(false);
        setLocationRelativeTo(owner);

        initUI();
        loadOrdonnances();
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

        JLabel title = new JLabel("Ordonnances - Dossier #" + idDm);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel subtitle = new JLabel("Liste des ordonnances associées");
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

        // Table panel avec card
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(BG_CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JTable table = new JTable(model);
        table.setRowHeight(28);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        content.add(tableCard, BorderLayout.CENTER);
        return content;
    }

    /* ================= FOOTER ================= */
    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(BG_MAIN);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_LIGHT));

        RoundedButton btnFermer = new RoundedButton("Fermer");
        ButtonStyles.secondary(btnFermer);
        btnFermer.addActionListener(e -> dispose());
        footer.add(btnFermer);

        return footer;
    }

    /* ================= DATA ================= */
    private void loadOrdonnances() {
        model.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Ordonnance o : ordonnances) {
            String dateStr = o.getDate() != null ? o.getDate().format(fmt) : "";
            model.addRow(new Object[]{
                    o.getIdOrd(),
                    dateStr,
                    o.getIdConsultation()
            });
        }
    }
}
