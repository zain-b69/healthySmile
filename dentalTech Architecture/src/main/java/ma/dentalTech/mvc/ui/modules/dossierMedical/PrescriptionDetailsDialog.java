package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PrescriptionDetailsDialog extends JDialog {

    private final DossierMedicalController controller;
    private final Long idPr;

    /* ================== PALETTE ================== */
    private static final Color BG_MAIN = new Color(243, 246, 249);
    private static final Color HEADER_BG = new Color(21, 101, 192);
    private static final Color BORDER_LIGHT = new Color(220, 224, 229);

    public PrescriptionDetailsDialog(Window owner, DossierMedicalController controller, Long idPr) {
        super(owner, "Prescription #" + idPr, ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.idPr = idPr;

        setSize(450, 400);
        setMinimumSize(new Dimension(450, 400));
        setLocationRelativeTo(owner);
        setResizable(false);

        initUI();
        loadPrescription();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_MAIN);

        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Détails Prescription #" + idPr);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel subtitle = new JLabel("Informations complètes de la prescription");
        subtitle.setForeground(new Color(220, 235, 255));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);

        return header;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BG_MAIN);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        Font valueFont = new Font("SansSerif", Font.PLAIN, 12);

        int row = 0;

        String[] labels = {"ID:", "Catégorie:", "Fréquence:", "Durée (jours):", "Quantité:", "Ordonnance:", "Médicaments:"};
        for (String text : labels) {
            gbc.gridx = 0; gbc.gridy = row;
            mainPanel.add(new JLabel(text), gbc);
            gbc.gridx = 1;
            JLabel lbl = new JLabel();
            lbl.setName("lbl" + text.replaceAll("\\s|\\(|\\)|:", ""));
            lbl.setFont(valueFont);
            lbl.setForeground(Color.BLACK);
            mainPanel.add(lbl, gbc);
            row++;
        }

        return mainPanel;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(BG_MAIN);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_LIGHT));

        RoundedButton btnFermer = new RoundedButton("Fermer");
        ButtonStyles.primary(btnFermer);
        btnFermer.addActionListener(e -> dispose());

        footer.add(btnFermer);
        return footer;
    }

    private void loadPrescription() {
        try {
            Prescription prescription = controller.getPrescriptionById(idPr);

            if (prescription != null) {
                updateLabel("lblID", String.valueOf(prescription.getIdPr()));
                updateLabel("lblCatégorie", prescription.getCategorie() != null ? prescription.getCategorie() : "");
                updateLabel("lblFréquence", prescription.getFrequence() != null ? prescription.getFrequence() : "");
                updateLabel("lblDuréejours", prescription.getDureeEnJours() != null ? prescription.getDureeEnJours() + " jours" : "");
                updateLabel("lblQuantité", prescription.getQuantite() != null ? String.valueOf(prescription.getQuantite()) : "");
                updateLabel("lblOrdonnance", prescription.getIdOrdonnance() != null ? "Ordonnance #" + prescription.getIdOrdonnance() : "");

                if (prescription.getMedicaments() != null && !prescription.getMedicaments().isEmpty()) {
                    StringBuilder medsText = new StringBuilder();
                    for (Medicament med : prescription.getMedicaments()) {
                        if (med != null) {
                            medsText.append(med.getNom());
                            if (med.getLaboratoire() != null) medsText.append(" (").append(med.getLaboratoire()).append(")");
                            medsText.append(", ");
                        }
                    }
                    if (medsText.length() > 0) {
                        updateLabel("lblMédicaments", medsText.substring(0, medsText.length() - 2));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateLabel(String name, String text) {
        for (Component comp : getContentPane().getComponents()) {
            if (comp instanceof JPanel panel) {
                findAndUpdateLabel(panel, name, text);
            }
        }
    }

    private void findAndUpdateLabel(JPanel panel, String name, String text) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JLabel lbl && name.equals(lbl.getName())) {
                lbl.setText(text);
                return;
            } else if (comp instanceof JPanel inner) {
                findAndUpdateLabel(inner, name, text);
            }
        }
    }
}
