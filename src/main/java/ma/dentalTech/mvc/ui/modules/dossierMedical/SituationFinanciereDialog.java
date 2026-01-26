// Fichier: ma/dentalTech/mvc/ui/modules/dossierMedical/SituationFinanciereDialog.java
package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.SituationFinanciere;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SituationFinanciereDialog extends JDialog {

    private final DossierMedicalController controller;
    private SituationFinanciere situation; // Changé de final à mutable

    /* ================== PALETTE ================== */
    private static final Color BG_MAIN = new Color(243, 246, 249);
    private static final Color BG_CARD = Color.WHITE;
    private static final Color HEADER_BG = new Color(21, 101, 192);
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color BORDER_LIGHT = new Color(220, 224, 229);

    // Références aux composants pour les mettre à jour
    private JLabel lblTotalValue;
    private JLabel lblPayeValue;
    private JLabel lblCreditValue;
    private JLabel lblStatutValue;
    private JLabel lblResteValue;
    private JLabel lblMessage;
    private RoundedButton btnPromotion;

    public SituationFinanciereDialog(Window owner, DossierMedicalController controller,
                                     SituationFinanciere situation) {
        super(owner, "Situation Financière - Dossier #" + situation.getIdDm(), ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.situation = situation;

        setSize(500, 600);
        setMinimumSize(new Dimension(500, 450));
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

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(BG_MAIN);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_LIGHT));

        RoundedButton btnFermer = new RoundedButton("Fermer");
        ButtonStyles.secondary(btnFermer);
        btnFermer.addActionListener(e -> dispose());
        footer.add(btnFermer);

        return footer;
    }

    /* ================= HEADER ================= */
    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Situation Financière - Dossier #" + situation.getIdDm());
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel subtitle = new JLabel("Informations financières et actions disponibles");
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
        content.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Informations financières
        JPanel infoPanel = createInfoPanel();
        content.add(infoPanel);
        content.add(Box.createVerticalStrut(15));

        // Résumé
        JPanel resumePanel = createResumePanel();
        content.add(resumePanel);
        content.add(Box.createVerticalStrut(15));

        // Actions
        JPanel actionsPanel = createActionsPanel();
        content.add(actionsPanel);

        return content;
    }

    /* ================== INFO PANEL ================== */
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("📊 INFORMATIONS FINANCIÈRES"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("SansSerif", Font.BOLD, 12);
        Font valueFont = new Font("SansSerif", Font.PLAIN, 12);

        int row = 0;

        // Total des soins
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblTotal = new JLabel("Total des soins:");
        lblTotal.setFont(labelFont);
        panel.add(lblTotal, gbc);

        gbc.gridx = 1;
        lblTotalValue = new JLabel(String.format("%.2f DH",
                situation.getTotaleDesActes() != null ? situation.getTotaleDesActes() : 0.0));
        lblTotalValue.setFont(valueFont);
        panel.add(lblTotalValue, gbc);
        row++;

        // Total payé
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblPaye = new JLabel("Total payé:");
        lblPaye.setFont(labelFont);
        panel.add(lblPaye, gbc);

        gbc.gridx = 1;
        lblPayeValue = new JLabel(String.format("%.2f DH",
                situation.getTotalePaye() != null ? situation.getTotalePaye() : 0.0));
        lblPayeValue.setFont(valueFont);
        panel.add(lblPayeValue, gbc);
        row++;

        // Crédit
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblCredit = new JLabel("Crédit patient:");
        lblCredit.setFont(labelFont);
        panel.add(lblCredit, gbc);

        gbc.gridx = 1;
        lblCreditValue = new JLabel(String.format("%.2f DH",
                situation.getCredit() != null ? situation.getCredit() : 0.0));
        lblCreditValue.setFont(valueFont);
        if (situation.getCredit() != null && situation.getCredit() > 0) {
            lblCreditValue.setForeground(new Color(0, 102, 0));
        }
        panel.add(lblCreditValue, gbc);
        row++;

        // Statut
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblStatut = new JLabel("Statut:");
        lblStatut.setFont(labelFont);
        panel.add(lblStatut, gbc);

        gbc.gridx = 1;
        StatutSituationFinanciere statut = situation.getStatut();
        lblStatutValue = new JLabel(statut != null ? statut.toString() : "NON_DÉFINI");
        lblStatutValue.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblStatutValue.setForeground(getColorForStatut(statut));
        panel.add(lblStatutValue, gbc);
        row++;

        // Promotion - MAINTAINED but no label created here
        // The label will be added dynamically if needed

        return panel;
    }

    /* ================== RESUME PANEL ================== */
    private JPanel createResumePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("🧮 RÉSUMÉ"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        Font titleFont = new Font("SansSerif", Font.BOLD, 13);
        Font valueFont = new Font("SansSerif", Font.BOLD, 14);

        Double reste = controller.calculerResteAPayer(situation.getIdSf());
        reste = reste != null ? reste : 0.0;

        JPanel restePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        restePanel.setBackground(BG_CARD);

        JLabel lblResteTitle = new JLabel("RESTE À PAYER:");
        lblResteTitle.setFont(titleFont);

        lblResteValue = new JLabel(String.format("%.2f DH", reste));
        lblResteValue.setFont(valueFont);

        if (reste > 0) lblResteValue.setForeground(Color.RED);
        else if (reste == 0) lblResteValue.setForeground(new Color(0, 102, 0));
        else lblResteValue.setForeground(Color.BLUE);

        restePanel.add(lblResteTitle);
        restePanel.add(Box.createHorizontalStrut(10));
        restePanel.add(lblResteValue);

        lblMessage = new JLabel();
        lblMessage.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblMessage.setForeground(Color.DARK_GRAY);
        updateMessage(reste);

        panel.add(restePanel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lblMessage);

        return panel;
    }

    /* ================== ACTIONS PANEL ================== */
    private JPanel createActionsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_MAIN);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("ACTIONS"));

        Dimension buttonSize = new Dimension(200, 35);
        Font buttonFont = new Font("SansSerif", Font.PLAIN, 12);

        // Boutons centrés et espacés
        RoundedButton btnPaiement = new RoundedButton("Enregistrer paiement");
        btnPromotion = new RoundedButton(
                situation.getEnPromo() != null && situation.getEnPromo() ? "❌ Retirer promotion" : "🎁 Appliquer promotion");
        RoundedButton btnHistorique = new RoundedButton("📊 Voir historique");

        ButtonStyles.primary(btnPaiement);
        ButtonStyles.primary(btnPromotion);
        ButtonStyles.primary(btnHistorique);

        for (RoundedButton b : new RoundedButton[]{btnPaiement, btnPromotion, btnHistorique}) {
            b.setMaximumSize(buttonSize);
            b.setFont(buttonFont);
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(b);
            panel.add(Box.createVerticalStrut(10));
        }

        btnPaiement.addActionListener(e -> enregistrerPaiement());
        btnPromotion.addActionListener(e -> togglePromotion());
        btnHistorique.addActionListener(e -> voirHistorique());

        return panel;
    }

    private Color getColorForStatut(StatutSituationFinanciere statut) {
        if (statut == null) return Color.GRAY;
        return switch (statut) {
            case EN_REGLE -> new Color(0, 102, 0);
            case EN_CREDIT -> Color.ORANGE;
            case EN_PROMO -> new Color(128, 0, 128);
        };
    }

    // ================== NOUVELLES MÉTHODES POUR RAFRAÎCHISSEMENT ==================

    /**
     * Rafraîchit toutes les données affichées dans le dialogue
     */
    private void rafraichirAffichage() {
        // Recharger la situation depuis la base de données
        SituationFinanciere nouvelleSituation = controller.getSituationByDossier(situation.getIdDm());
        if (nouvelleSituation != null) {
            this.situation = nouvelleSituation;

            // Mettre à jour les valeurs affichées
            lblTotalValue.setText(String.format("%.2f DH",
                    situation.getTotaleDesActes() != null ? situation.getTotaleDesActes() : 0.0));
            lblPayeValue.setText(String.format("%.2f DH",
                    situation.getTotalePaye() != null ? situation.getTotalePaye() : 0.0));

            // Mettre à jour le crédit
            Double credit = situation.getCredit() != null ? situation.getCredit() : 0.0;
            lblCreditValue.setText(String.format("%.2f DH", credit));
            if (credit > 0) {
                lblCreditValue.setForeground(new Color(0, 102, 0));
            } else {
                lblCreditValue.setForeground(Color.BLACK);
            }

            // Mettre à jour le statut
            StatutSituationFinanciere statut = situation.getStatut();
            lblStatutValue.setText(statut != null ? statut.toString() : "NON_DÉFINI");
            lblStatutValue.setForeground(getColorForStatut(statut));

            // Mettre à jour le bouton promotion
            btnPromotion.setText(situation.getEnPromo() != null && situation.getEnPromo()
                    ? "❌ Retirer promotion" : "🎁 Appliquer promotion");

            // Mettre à jour le reste à payer
            Double reste = controller.calculerResteAPayer(situation.getIdSf());
            reste = reste != null ? reste : 0.0;
            lblResteValue.setText(String.format("%.2f DH", reste));

            if (reste > 0) lblResteValue.setForeground(Color.RED);
            else if (reste == 0) lblResteValue.setForeground(new Color(0, 102, 0));
            else lblResteValue.setForeground(Color.BLUE);

            // Mettre à jour le message
            updateMessage(reste);

            // Forcer la mise à jour de l'interface
            revalidate();
            repaint();
        }
    }

    /**
     * Met à jour le message en fonction du reste à payer
     */
    private void updateMessage(Double reste) {
        if (reste > 0)
            lblMessage.setText("Le patient a un solde impayé de " + String.format("%.2f DH", reste));
        else if (reste == 0)
            lblMessage.setText("Le compte est réglé. Aucun solde impayé.");
        else
            lblMessage.setText("Le patient a un crédit de " + String.format("%.2f DH", Math.abs(reste)));
    }

    // ================== MÉTHODES MODIFIÉES POUR RAFRAÎCHISSEMENT ==================

    private void enregistrerPaiement() {
        // Dialog amélioré pour le paiement
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));

        Double reste = controller.calculerResteAPayer(situation.getIdSf());
        reste = reste != null ? reste : 0.0;

        panel.add(new JLabel("Reste à payer:"));
        panel.add(new JLabel(String.format("%.2f DH", reste)));

        panel.add(new JLabel("Montant à payer:"));
        JTextField montantField = new JTextField();
        panel.add(montantField);

        panel.add(new JLabel("Mode paiement:"));
        JComboBox<String> modeCombo = new JComboBox<>(new String[]{"Espèces", "Carte", "Chèque", "Virement"});
        panel.add(modeCombo);

        panel.add(new JLabel("Commentaire:"));
        JTextField commentField = new JTextField();
        panel.add(commentField);

        int option = JOptionPane.showConfirmDialog(this, panel,
                "Enregistrement de paiement",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                Double montant = Double.parseDouble(montantField.getText().trim());
                if (montant <= 0) {
                    JOptionPane.showMessageDialog(this, "Montant invalide",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                controller.enregistrerPaiement(situation.getIdSf(), montant);

                // Afficher un reçu
                String mode = (String) modeCombo.getSelectedItem();
                String commentaire = commentField.getText().trim();

                String reçu = String.format(
                        "📋 REÇU DE PAIEMENT\n\n" +
                                "Dossier: #%d\n" +
                                "Montant: %.2f DH\n" +
                                "Mode: %s\n" +
                                "Date: %s\n" +
                                "Commentaire: %s\n\n" +
                                "✅ Paiement enregistré avec succès",
                        situation.getIdDm(), montant, mode,
                        java.time.LocalDate.now(),
                        commentaire.isEmpty() ? "Aucun" : commentaire
                );

                JOptionPane.showMessageDialog(this, reçu,
                        "Paiement confirmé", JOptionPane.INFORMATION_MESSAGE);

                // RAFRAÎCHIR L'AFFICHAGE AU LIEU DE FERMER
                rafraichirAffichage();

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Montant invalide",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void togglePromotion() {
        boolean actuel = situation.getEnPromo() != null && situation.getEnPromo();
        boolean nouveau = !actuel;

        String titre = nouveau ? "APPLIQUER PROMOTION" : "RETIRER PROMOTION";
        String message = nouveau ?
                "Appliquer une promotion de 25% sur les actes ?\n\n" +
                        "Cette action réduira le total des actes de 25%." :
                "Retirer la promotion ?\n\n" +
                        "Le total des actes retournera au montant original.";

        int confirm = JOptionPane.showConfirmDialog(this,
                message,
                titre,
                JOptionPane.YES_NO_OPTION,
                nouveau ? JOptionPane.QUESTION_MESSAGE : JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.appliquerPromo(situation.getIdSf(), nouveau);

                String resultat = nouveau ?
                        "✅ Promotion appliquée\nLe total des actes est réduit de 25%" :
                        "✅ Promotion retirée\nLe total des actes retourne au montant original";

                JOptionPane.showMessageDialog(this, resultat,
                        "Succès", JOptionPane.INFORMATION_MESSAGE);

                // RAFRAÎCHIR L'AFFICHAGE AU LIEU DE FERMER
                rafraichirAffichage();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void voirHistorique() {
        try {
            // Utiliser les données actuelles (après rafraîchissement)
            Double totaleActes = situation.getTotaleDesActes() != null ? situation.getTotaleDesActes() : 0.0;
            Double totalePaye = situation.getTotalePaye() != null ? situation.getTotalePaye() : 0.0;
            Double credit = situation.getCredit() != null ? situation.getCredit() : 0.0;
            Double reste = controller.calculerResteAPayer(situation.getIdSf());

            String historique = String.format(
                    "📊 HISTORIQUE FINANCIER\n\n" +
                            "Dossier médical: #%d\n" +
                            "Statut: %s\n" +
                            "Promotion: %s\n\n" +
                            "CHIFFRES CLÉS:\n" +
                            "• Total des soins: %.2f DH\n" +
                            "• Total payé: %.2f DH\n" +
                            "• Crédit patient: %.2f DH\n" +
                            "• Reste à payer: %.2f DH\n\n" +
                            "À IMPLÉMENTER:\n" +
                            "• Détail des paiements\n" +
                            "• Dates des transactions\n" +
                            "• Méthodes de paiement\n" +
                            "• Factures associées",
                    situation.getIdDm(),
                    situation.getStatut() != null ? situation.getStatut() : "NON_DÉFINI",
                    situation.getEnPromo() != null && situation.getEnPromo() ? "OUI (-25%)" : "NON",
                    totaleActes, totalePaye, credit,
                    reste != null ? reste : 0.0
            );

            JTextArea textArea = new JTextArea(historique);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(this, scrollPane,
                    "Historique financier", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}