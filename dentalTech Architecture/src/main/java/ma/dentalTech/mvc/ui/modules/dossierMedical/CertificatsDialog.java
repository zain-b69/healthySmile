// Fichier: ma/dentalTech/mvc/ui/modules/dossierMedical/CertificatsDialog.java
package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.Certificat;
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

public class CertificatsDialog extends JDialog {

    private final DossierMedicalController controller;
    private final long idDm;
    private List<Certificat> certificats;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Début", "Fin", "Durée", "Note"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    /* ================== PALETTE ================== */
    private static final Color BG_MAIN = new Color(243, 246, 249);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color HEADER_BG = new Color(21, 101, 192);

    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color BORDER_LIGHT = new Color(220, 224, 229);

    private final JTable table = new JTable(model);

    public CertificatsDialog(Window owner, DossierMedicalController controller,
                             long idDm, List<Certificat> certificats) {
        super(owner, "Certificats - Dossier #" + idDm, ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.idDm = idDm;
        this.certificats = certificats;

        setSize(800, 400);
        setMinimumSize(new Dimension(800, 400));
        setResizable(false);
        setLocationRelativeTo(owner);

        initUI();
        loadCertificats();
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

        JLabel title = new JLabel("Certificats - Dossier #" + idDm);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel subtitle = new JLabel("Liste des certificats associés");
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

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(BG_CARD);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT),
                new EmptyBorder(16, 16, 16, 16)
        ));

        table.setRowHeight(28);

        // Config colonnes
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);

        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        content.add(tableCard, BorderLayout.CENTER);

        return content;
    }

    /* ================= FOOTER ================= */
    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(BG_MAIN);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_LIGHT));

        RoundedButton btnCreer = new RoundedButton("➕ Créer certificat");
        RoundedButton btnVoirDetails = new RoundedButton("📋 Voir détails");
        RoundedButton btnModifier = new RoundedButton("✏️ Modifier");
        RoundedButton btnSupprimer = new RoundedButton("❌ Supprimer");
        RoundedButton btnGenererTexte = new RoundedButton("📄 Générer texte");
        RoundedButton btnFermer = new RoundedButton("Fermer");

        ButtonStyles.primary(btnCreer);
        ButtonStyles.primary(btnVoirDetails);
        ButtonStyles.primary(btnModifier);
        ButtonStyles.danger(btnSupprimer);
        ButtonStyles.primary(btnGenererTexte);
        ButtonStyles.primary(btnFermer);

        btnCreer.setForeground(Color.WHITE);
        btnSupprimer.setForeground(Color.WHITE);

        btnCreer.addActionListener(e -> creerCertificat());
        btnVoirDetails.addActionListener(e -> voirDetailsCertificat());
        btnModifier.addActionListener(e -> modifierCertificat());
        btnSupprimer.addActionListener(e -> supprimerCertificat());
        btnGenererTexte.addActionListener(e -> genererTexteCertificat());
        btnFermer.addActionListener(e -> dispose());

        footer.add(btnCreer);
        footer.add(btnVoirDetails);
        footer.add(btnModifier);
        footer.add(btnSupprimer);
        footer.add(btnGenererTexte);
        footer.add(btnFermer);

        return footer;
    }

    /* ================= DATA ================= */
    private void loadCertificats() {
        model.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Certificat c : certificats) {
            String debutStr = c.getDateDebut() != null ? c.getDateDebut().format(fmt) : "";
            String finStr = c.getDateFin() != null ? c.getDateFin().format(fmt) : "";
            String dureeStr = c.getDuree() != null ? c.getDuree() + " jours" : "";
            String noteStr = c.getNoteMedecin() != null ?
                    (c.getNoteMedecin().length() > 30 ? c.getNoteMedecin().substring(0, 30) + "..." : c.getNoteMedecin())
                    : "";

            model.addRow(new Object[]{c.getIdCertif(), debutStr, finStr, dureeStr, noteStr});
        }
    }

    // =================== MÉTHODES ===================
    private void creerCertificat() {
        CreerCertificatDialog dialog = new CreerCertificatDialog(this, controller, idDm);
        dialog.setVisible(true);
        rafraichirListe();
    }

    private void voirDetailsCertificat() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un certificat", "Info", JOptionPane.INFORMATION_MESSAGE); return; }

        long idCertif = ((Number) model.getValueAt(row, 0)).longValue();
        Certificat selected = certificats.stream().filter(c -> c.getIdCertif().equals(idCertif)).findFirst().orElse(null);
        if (selected != null) new DetailsCertificatDialog(this, controller, selected).setVisible(true);
    }

    private void modifierCertificat() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un certificat à modifier", "Info", JOptionPane.INFORMATION_MESSAGE); return; }

        long idCertif = ((Number) model.getValueAt(row, 0)).longValue();
        Certificat selected = certificats.stream().filter(c -> c.getIdCertif().equals(idCertif)).findFirst().orElse(null);
        if (selected != null) new ModifierCertificatDialog(this, controller, selected).setVisible(true);
        rafraichirListe();
    }

    private void supprimerCertificat() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un certificat à supprimer", "Info", JOptionPane.INFORMATION_MESSAGE); return; }

        long idCertif = ((Number) model.getValueAt(row, 0)).longValue();
        int confirm = JOptionPane.showConfirmDialog(this,
                "⚠ SUPPRESSION DE CERTIFICAT ⚠\nConfirmer la suppression du certificat #" + idCertif + " ?",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = controller.supprimerCertificat(idCertif);
                if (success) rafraichirListe();
                else JOptionPane.showMessageDialog(this, "Échec de la suppression", "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE); }
        }
    }

    private void genererTexteCertificat() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un certificat", "Info", JOptionPane.INFORMATION_MESSAGE); return; }

        long idCertif = ((Number) model.getValueAt(row, 0)).longValue();
        try {
            Certificat c = controller.getCertificatById(idCertif);
            if (c != null) {
                String texte = controller.genererTexteCertificat(c);

                JDialog dlg = new JDialog(this, "Texte du certificat #" + idCertif, true);
                dlg.setSize(500, 400); dlg.setLocationRelativeTo(this);
                JTextArea ta = new JTextArea(texte);
                ta.setEditable(false); ta.setLineWrap(true); ta.setWrapStyleWord(true); ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
                dlg.setLayout(new BorderLayout());
                dlg.add(new JScrollPane(ta), BorderLayout.CENTER);

                JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton btnCopier = new JButton("Copier dans presse-papier");
                JButton btnFermer = new JButton("Fermer");
                btnCopier.addActionListener(e -> { ta.selectAll(); ta.copy(); JOptionPane.showMessageDialog(dlg, "Texte copié !"); });
                btnFermer.addActionListener(e -> dlg.dispose());
                pnlBtns.add(btnCopier); pnlBtns.add(btnFermer);
                dlg.add(pnlBtns, BorderLayout.SOUTH);

                dlg.setVisible(true);
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE); }
    }

    private void rafraichirListe() {
        try { certificats = controller.getCertificatsByDossier(idDm); loadCertificats(); }
        catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur rafraîchissement : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE); }
    }
}
