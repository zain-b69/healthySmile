package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class DetailsCertificatDialog extends JDialog {

    private final DossierMedicalController controller;
    private final Certificat certificat;

    private static final Color BG_MAIN = new Color(243, 246, 249);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color HEADER_BG = new Color(21, 101, 192);
    private static final Color BORDER_LIGHT = new Color(220, 224, 229);

    public DetailsCertificatDialog(Window owner, DossierMedicalController controller, Certificat certificat) {
        super(owner, "Détails certificat #" + certificat.getIdCertif(), ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.certificat = certificat;

        setSize(600, 500);
        setMinimumSize(new Dimension(600, 500));
        setLocationRelativeTo(owner);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_MAIN);

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Détails Certificat #" + certificat.getIdCertif());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel subtitle = new JLabel("Informations et note du médecin");
        subtitle.setForeground(new Color(220, 235, 255));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);

        return header;
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(BG_MAIN);
        content.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Informations principales
        JPanel infoCard = new JPanel(new GridLayout(6, 2, 5, 5));
        infoCard.setBackground(BG_CARD);
        infoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT),
                new EmptyBorder(16, 16, 16, 16)
        ));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        infoCard.add(new JLabel("ID Certificat:"));
        infoCard.add(new JLabel(String.valueOf(certificat.getIdCertif())));

        infoCard.add(new JLabel("Dossier médical ID:"));
        infoCard.add(new JLabel(String.valueOf(certificat.getIdDm())));

        infoCard.add(new JLabel("Consultation ID:"));
        infoCard.add(new JLabel(String.valueOf(certificat.getIdConsultation())));

        infoCard.add(new JLabel("Date début:"));
        infoCard.add(new JLabel(certificat.getDateDebut() != null ?
                certificat.getDateDebut().format(fmt) : "Non définie"));

        infoCard.add(new JLabel("Date fin:"));
        infoCard.add(new JLabel(certificat.getDateFin() != null ?
                certificat.getDateFin().format(fmt) : "Non définie"));

        infoCard.add(new JLabel("Durée:"));
        infoCard.add(new JLabel(certificat.getDuree() != null ?
                certificat.getDuree() + " jours" : "Non définie"));

        content.add(infoCard, BorderLayout.NORTH);

        // Note du médecin
        JPanel notePanel = new JPanel(new BorderLayout(5, 5));
        notePanel.setBackground(BG_CARD);
        notePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JTextArea noteArea = new JTextArea(8, 40);
        noteArea.setText(certificat.getNoteMedecin() != null ?
                certificat.getNoteMedecin() : "Aucune note");
        noteArea.setEditable(false);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(noteArea);
        notePanel.add(scrollPane, BorderLayout.CENTER);

        content.add(notePanel, BorderLayout.CENTER);

        return content;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(BG_MAIN);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_LIGHT));

        RoundedButton btnGenererTexte = new RoundedButton("📄 Générer texte");
        RoundedButton btnFermer = new RoundedButton("Fermer");

        ButtonStyles.primary(btnGenererTexte);
        ButtonStyles.secondary(btnFermer); // <- Fermer en secondary

        btnGenererTexte.addActionListener(e -> genererTexte());
        btnFermer.addActionListener(e -> dispose());

        footer.add(btnGenererTexte);
        footer.add(btnFermer);

        return footer;
    }

    private void genererTexte() {
        try {
            String texte = controller.genererTexteCertificat(certificat);

            JDialog texteDialog = new JDialog(this, "Texte du certificat", true);
            texteDialog.setSize(500, 400);
            texteDialog.setLocationRelativeTo(this);

            JTextArea textArea = new JTextArea(texte);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            JScrollPane scrollPane = new JScrollPane(textArea);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            RoundedButton btnCopier = new RoundedButton("Copier");
            RoundedButton btnFermer = new RoundedButton("Fermer");

            ButtonStyles.primary(btnCopier);
            ButtonStyles.secondary(btnFermer);

            btnCopier.addActionListener(e -> {
                textArea.selectAll();
                textArea.copy();
                JOptionPane.showMessageDialog(texteDialog, "Texte copié !");
            });
            btnFermer.addActionListener(e -> texteDialog.dispose());

            buttonPanel.add(btnCopier);
            buttonPanel.add(btnFermer);

            texteDialog.setLayout(new BorderLayout());
            texteDialog.add(scrollPane, BorderLayout.CENTER);
            texteDialog.add(buttonPanel, BorderLayout.SOUTH);
            texteDialog.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la génération : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
