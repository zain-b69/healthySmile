// Fichier: ma/dentalTech/mvc/ui/modules/dossierMedical/ModifierCertificatDialog.java
package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ModifierCertificatDialog extends JDialog {

    private final DossierMedicalController controller;
    private final Certificat certificat;

    private JTextField dateDebutField = new JTextField();
    private JTextField dateFinField = new JTextField();
    private JTextArea noteArea = new JTextArea();

    /* ================== PALETTE ================== */
    private static final Color BG_MAIN = new Color(243, 246, 249);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color HEADER_BG = new Color(21, 101, 192);
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color BORDER_LIGHT = new Color(220, 224, 229);

    public ModifierCertificatDialog(Window owner, DossierMedicalController controller, Certificat certificat) {
        super(owner, "Modifier certificat #" + certificat.getIdCertif(), ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.certificat = certificat;

        setSize(600, 500);
        setMinimumSize(new Dimension(600, 500));
        setResizable(false);
        setLocationRelativeTo(owner);

        initUI();
        chargerDonnees();
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

        JLabel title = new JLabel("Modifier certificat #" + certificat.getIdCertif());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel subtitle = new JLabel("Modifiez les informations du certificat sélectionné");
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

        gbc.gridy = 0; card.add(new JLabel("Date début (AAAA-MM-JJ):"), gbc);
        gbc.gridy = 1; card.add(new JLabel("Date fin (AAAA-MM-JJ):"), gbc);
        gbc.gridy = 2; card.add(new JLabel("Note médecin:"), gbc);

        // Colonne champs
        gbc.weightx = 1.0;
        gbc.gridx = 1;

        gbc.gridy = 0; dateDebutField.setPreferredSize(new Dimension(200, 25)); card.add(dateDebutField, gbc);
        gbc.gridy = 1; dateFinField.setPreferredSize(new Dimension(200, 25)); card.add(dateFinField, gbc);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        JScrollPane noteScroll = new JScrollPane(noteArea);
        noteScroll.setPreferredSize(new Dimension(300, 120));
        card.add(noteScroll, gbc);

        // Ajouter card au content
        content.add(card, BorderLayout.CENTER);
        return content;
    }

    /* ================= FOOTER ================= */
    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(BG_MAIN);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_LIGHT));

        RoundedButton btnSauvegarder = new RoundedButton("Sauvegarder");
        RoundedButton btnAnnuler = new RoundedButton("Annuler");

        ButtonStyles.primary(btnSauvegarder);
        ButtonStyles.primary(btnAnnuler);

        btnSauvegarder.setForeground(new Color(0, 102, 0));
        btnSauvegarder.addActionListener(e -> modifierCertificat());
        btnAnnuler.addActionListener(e -> dispose());

        footer.add(btnSauvegarder);
        footer.add(btnAnnuler);

        return footer;
    }

    /* ================= MÉTHODE CHARGEMENT ================= */
    private void chargerDonnees() {
        if (certificat.getDateDebut() != null)
            dateDebutField.setText(certificat.getDateDebut().toString());
        if (certificat.getDateFin() != null)
            dateFinField.setText(certificat.getDateFin().toString());
        if (certificat.getNoteMedecin() != null)
            noteArea.setText(certificat.getNoteMedecin());
    }

    /* ================= MÉTHODE MODIFICATION ================= */
    private void modifierCertificat() {
        try {
            if (dateDebutField.getText().trim().isEmpty())
                throw new IllegalArgumentException("La date de début est requise");
            if (dateFinField.getText().trim().isEmpty())
                throw new IllegalArgumentException("La date de fin est requise");

            LocalDate dateDebut = LocalDate.parse(dateDebutField.getText().trim());
            LocalDate dateFin = LocalDate.parse(dateFinField.getText().trim());
            String note = noteArea.getText().trim();

            if (dateFin.isBefore(dateDebut))
                throw new IllegalArgumentException("La date de fin doit être après la date de début");

            boolean success = controller.modifierCertificat(certificat.getIdCertif(), dateDebut, dateFin, note);

            if (success) {
                JOptionPane.showMessageDialog(this, "Certificat modifié avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Échec de la modification", "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Format de date invalide. Utilisez AAAA-MM-JJ", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
