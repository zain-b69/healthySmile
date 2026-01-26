// Fichier: ma/dentalTech/mvc/ui/modules/dossierMedical/CreerCertificatDialog.java
package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CreerCertificatDialog extends JDialog {

    private final DossierMedicalController controller;
    private final long idDm;

    private JTextField idConsultationField = new JTextField();
    private JTextField dateDebutField = new JTextField();
    private JTextField dateFinField = new JTextField();
    private JTextArea noteArea = new JTextArea();

    /* ================== PALETTE ================== */
    private static final Color BG_MAIN = new Color(243, 246, 249);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color HEADER_BG = new Color(21, 101, 192);
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color BORDER_LIGHT = new Color(220, 224, 229);

    public CreerCertificatDialog(Window owner, DossierMedicalController controller, long idDm) {
        super(owner, "Créer un certificat - Dossier #" + idDm, ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.idDm = idDm;

        setSize(600, 500);
        setMinimumSize(new Dimension(600, 500));
        setResizable(false);
        setLocationRelativeTo(owner);

        initUI();
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

        JLabel title = new JLabel("Créer un certificat - Dossier #" + idDm);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel subtitle = new JLabel("Remplissez les informations pour créer un certificat");
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

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT),
                new EmptyBorder(16, 16, 16, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Colonne labels
        gbc.weightx = 0.0;
        gbc.gridx = 0;

        gbc.gridy = 0; card.add(new JLabel("ID Consultation:"), gbc);
        gbc.gridy = 1; card.add(new JLabel("Date début (AAAA-MM-JJ):"), gbc);
        gbc.gridy = 2; card.add(new JLabel("Date fin (AAAA-MM-JJ):"), gbc);
        gbc.gridy = 3; card.add(new JLabel("Note médecin:"), gbc);

        // Colonne champs
        gbc.weightx = 1.0;
        gbc.gridx = 1;

        gbc.gridy = 0; idConsultationField.setPreferredSize(new Dimension(200, 25)); card.add(idConsultationField, gbc);
        gbc.gridy = 1; dateDebutField.setPreferredSize(new Dimension(200, 25)); card.add(dateDebutField, gbc);
        gbc.gridy = 2; dateFinField.setPreferredSize(new Dimension(200, 25)); card.add(dateFinField, gbc);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        JScrollPane noteScroll = new JScrollPane(noteArea);
        noteScroll.setPreferredSize(new Dimension(300, 120));
        card.add(noteScroll, gbc);

        content.add(card, BorderLayout.CENTER);
        return content;
    }

    /* ================= FOOTER ================= */
    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(BG_MAIN);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_LIGHT));

        RoundedButton btnCreer = new RoundedButton("Créer le certificat");
        RoundedButton btnAnnuler = new RoundedButton("Annuler");

        ButtonStyles.primary(btnCreer);
        ButtonStyles.primary(btnAnnuler);

        btnCreer.setForeground(new Color(0, 102, 0)); // Vert

        btnCreer.addActionListener(e -> creerCertificat());
        btnAnnuler.addActionListener(e -> dispose());

        footer.add(btnCreer);
        footer.add(btnAnnuler);

        return footer;
    }

    /* ================= MÉTHODE CREATION ================= */
    private void creerCertificat() {
        try {
            if (idConsultationField.getText().trim().isEmpty())
                throw new IllegalArgumentException("L'ID consultation est requis");
            if (dateDebutField.getText().trim().isEmpty())
                throw new IllegalArgumentException("La date de début est requise");
            if (dateFinField.getText().trim().isEmpty())
                throw new IllegalArgumentException("La date de fin est requise");

            Long idConsultation = Long.parseLong(idConsultationField.getText().trim());
            LocalDate dateDebut = LocalDate.parse(dateDebutField.getText().trim());
            LocalDate dateFin = LocalDate.parse(dateFinField.getText().trim());
            String note = noteArea.getText().trim();

            if (dateFin.isBefore(dateDebut))
                throw new IllegalArgumentException("La date de fin doit être après la date de début");

            var certif = controller.genererCertificat(idConsultation, idDm, dateDebut, dateFin, note);
            JOptionPane.showMessageDialog(this,
                    "Certificat créé avec succès (ID: " + certif.getIdCertif() + ")",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Format de date invalide. Utilisez AAAA-MM-JJ", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "L'ID consultation doit être un nombre", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
