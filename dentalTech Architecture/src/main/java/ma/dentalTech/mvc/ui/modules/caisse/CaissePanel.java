package ma.dentalTech.mvc.ui.modules.caisse;

import ma.dentalTech.entities.dossierMedical.Facture;
import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.mvc.controllers.modules.caisse.api.CaisseController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Import pour PDF
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;

public class CaissePanel extends JPanel {

    private final CaisseController controller;

    private final DefaultTableModel model;
    private final JTable table;

    private final JComboBox<StatutFacture> cbStatut = new JComboBox<>();
    private final JTextField tfStart = new JTextField(16); // yyyy-MM-dd HH:mm
    private final JTextField tfEnd   = new JTextField(16);
    private final JTextField tfIdSf  = new JTextField(10);

    public CaissePanel(CaisseController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        add(buildTopFilters(), BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{
                "ID", "Consultation", "SF", "Total", "Reste", "Statut", "Date"
        }, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Long id = selectedId();
                    if (id != null) openDetails(id);
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);

        loadFactures();
    }

    // ---------------- UI builders ----------------

    private JComponent buildTopFilters() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4);
        c.anchor = GridBagConstraints.WEST;

        cbStatut.addItem(null); // Tous
        for (StatutFacture s : StatutFacture.values()) cbStatut.addItem(s);

        c.gridx=0; c.gridy=0; p.add(new JLabel("Statut:"), c);
        c.gridx=1; p.add(cbStatut, c);

        c.gridx=2; p.add(new JLabel("ID SF:"), c);
        c.gridx=3; p.add(tfIdSf, c);

        RoundedButton btnSearch = new RoundedButton("Rechercher");
        ButtonStyles.primary(btnSearch);
        btnSearch.addActionListener(e -> onSearch());

        RoundedButton btnReset = new RoundedButton("Reset");
        ButtonStyles.primary(btnReset);
        btnReset.addActionListener(e -> {
            cbStatut.setSelectedItem(null);
            tfStart.setText("");
            tfEnd.setText("");
            tfIdSf.setText("");
            loadFactures();
        });

        c.gridx=0; c.gridy=2; c.gridwidth=4;
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(btnSearch);
        right.add(btnReset);
        p.add(right, c);

        return p;
    }

    private JComponent buildActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        p.setOpaque(false);

        RoundedButton btnAdd = new RoundedButton("Ajouter facture");
        ButtonStyles.primary(btnAdd);
        btnAdd.addActionListener(e -> openCreate());

        RoundedButton btnPay = new RoundedButton("Encaisser");
        ButtonStyles.primary(btnPay);
        btnPay.addActionListener(e -> {
            Long id = selectedId();
            if (id == null) return;
            openEncaisser(id);
        });

        RoundedButton btnCancel = new RoundedButton("Annuler");
        ButtonStyles.primary(btnCancel);
        btnCancel.addActionListener(e -> {
            Long id = selectedId();
            if (id == null) return;
            onAnnuler(id);
        });

        RoundedButton btnDetails = new RoundedButton("Détails");
        ButtonStyles.primary(btnDetails);
        btnDetails.addActionListener(e -> {
            Long id = selectedId();
            if (id == null) return;
            openDetails(id);
        });

        // BOUTON EXPORT PDF AJOUTÉ
        RoundedButton btnExportPDF = new RoundedButton("📄 Exporter PDF");
        ButtonStyles.primary(btnExportPDF);
        btnExportPDF.addActionListener(e -> exporterPDF());

        p.add(btnAdd);
        p.add(btnPay);
        p.add(btnCancel);
        p.add(btnDetails);
        p.add(Box.createHorizontalStrut(10)); // Espacement
        p.add(btnExportPDF);

        return p;
    }

    // ---------------- Data loading ----------------

    private void loadFactures() {
        try {
            List<Facture> list = controller.findAll();
            fillTable(list);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    /**
     * Recherche multi-critères en utilisant tes méthodes controller existantes.
     * - Si un seul critère : on appelle directement le finder correspondant.
     * - Si plusieurs critères : on prend une base (priorité SF > Statut > Date > All)
     *   et on filtre côté UI (safe et simple).
     */
    private void onSearch() {
        try {
            StatutFacture statut = (StatutFacture) cbStatut.getSelectedItem();
            Long idSf = parseLongOrNull(tfIdSf.getText());

            boolean hasSf = idSf != null;
            boolean hasStatut = statut != null;

            List<Facture> base;

            if (hasSf) base = controller.findBySf(idSf);
            else if (hasStatut) base = controller.findByStatut(statut);
            else base = controller.findAll();

            // filtres supplémentaires si plusieurs critères
            List<Facture> filtered = new ArrayList<>();
            for (Facture f : base) {
                if (hasSf && !idSf.equals(f.getIdSf())) continue;
                if (hasStatut && f.getStatut() != statut) continue;

                filtered.add(f);
            }

            fillTable(filtered);

        } catch (Exception ex) {
            showError(ex);
        }
    }

    // ---------------- Actions ----------------

    private void openCreate() {
        CreateFactureDialog dlg = new CreateFactureDialog(SwingUtilities.getWindowAncestor(this));
        dlg.setVisible(true);
        if (!dlg.isOk()) return;

        try {
            Facture f = controller.creerFacture(dlg.getIdConsultation(), dlg.getIdSf(), dlg.getTotal());
            JOptionPane.showMessageDialog(this, "Facture créée: " + f.getIdFacture());
            loadFactures();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void openEncaisser(Long idFacture) {
        EncaisserDialog dlg = new EncaisserDialog(SwingUtilities.getWindowAncestor(this));
        dlg.setVisible(true);
        if (!dlg.isOk()) return;

        try {
            controller.encaisser(idFacture, dlg.getMontant());
            loadFactures();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void onAnnuler(Long idFacture) {
        int res = JOptionPane.showConfirmDialog(this,
                "Annuler la facture " + idFacture + " ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (res != JOptionPane.YES_OPTION) return;

        try {
            controller.annuler(idFacture);
            loadFactures();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void openDetails(Long idFacture) {
        try {
            Facture f = controller.getById(idFacture);
            DetailsFactureDialog dlg = new DetailsFactureDialog(SwingUtilities.getWindowAncestor(this), f);
            dlg.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    /* ================== EXPORT PDF (Nouvelle méthode) ================== */
    private void exporterPDF() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une facture",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Récupérer les données de la ligne sélectionnée
        Long idFacture = ((Number) model.getValueAt(row, 0)).longValue();
        Long idConsultation = ((Number) model.getValueAt(row, 1)).longValue();

        // Demander où sauvegarder le PDF
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer la facture PDF");
        fileChooser.setSelectedFile(new File(
                String.format("Facture_%d_Consultation_%d.pdf", idFacture, idConsultation)
        ));

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Fichiers PDF", "pdf");
        fileChooser.setFileFilter(filter);

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // S'assurer que le fichier a l'extension .pdf
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }

            // Vérifier si le fichier existe déjà
            if (fileToSave.exists()) {
                int response = JOptionPane.showConfirmDialog(this,
                        "Le fichier existe déjà. Voulez-vous le remplacer ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (response != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            try {
                // Récupérer la facture complète via le controller
                Facture facture = controller.getById(idFacture);
                if (facture == null) {
                    JOptionPane.showMessageDialog(this,
                            "Impossible de récupérer les données de la facture",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                genererPDF(fileToSave, facture);
                JOptionPane.showMessageDialog(this,
                        "Facture PDF générée avec succès :\n" + fileToSave.getAbsolutePath(),
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la génération du PDF :\n" + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void genererPDF(File fichier, Facture facture) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(fichier));
        document.open();

        // ========== POLICES ==========
        Font fontTitre = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font fontSousTitre = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Font fontNormal = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

        // ========== EN-TÊTE ==========
        Paragraph titre = new Paragraph("FACTURE HEALTHYSMILE", fontTitre);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingAfter(20);
        document.add(titre);

        // Informations de la facture
        Paragraph infoFacture = new Paragraph();
        infoFacture.setFont(fontNormal);
        infoFacture.add(new Chunk("N° Facture: ", fontBold));
        infoFacture.add(new Chunk("#" + facture.getIdFacture() + "\n"));
        infoFacture.add(new Chunk("Consultation: ", fontBold));
        infoFacture.add(new Chunk("#" + facture.getIdConsultation() + "\n"));
        infoFacture.add(new Chunk("Situation Financière: ", fontBold));
        infoFacture.add(new Chunk("#" + facture.getIdSf() + "\n"));
        infoFacture.add(new Chunk("Date: ", fontBold));
        infoFacture.add(new Chunk(facture.getDateFacture() != null
                ? facture.getDateFacture().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "Non définie" + "\n"));
        infoFacture.add(new Chunk("Statut: ", fontBold));

        String statutStr = facture.getStatut() != null ? facture.getStatut().toString() : "NON_DÉFINI";
        infoFacture.add(new Chunk(statutStr + "\n\n"));

        infoFacture.setSpacingAfter(15);
        document.add(infoFacture);

        // ========== DÉTAILS FINANCIERS ==========
        Paragraph detailsTitre = new Paragraph("DÉTAILS FINANCIERS", fontBold);
        detailsTitre.setSpacingAfter(10);
        document.add(detailsTitre);

        // Tableau des montants
        PdfPTable tableMontants = new PdfPTable(2);
        tableMontants.setWidthPercentage(100);
        tableMontants.setSpacingBefore(10);
        tableMontants.setSpacingAfter(15);

        // Configuration des cellules d'en-tête
        PdfPCell cellHeader = new PdfPCell(new Phrase("DÉSIGNATION", fontBold));
        cellHeader.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellHeader.setPadding(5);

        // En-têtes
        tableMontants.addCell(cellHeader);
        tableMontants.addCell(cellHeader);

        // Total facture
        tableMontants.addCell(new PdfPCell(new Phrase("Total facture:", fontNormal)));
        tableMontants.addCell(new PdfPCell(new Phrase(
                String.format("%.2f DH", facture.getTotaleFacture() != null ? facture.getTotaleFacture() : 0.0),
                fontNormal)));

        // Montant payé
        Double paye = facture.getTotaleFacture() != null && facture.getReste() != null
                ? facture.getTotaleFacture() - facture.getReste()
                : 0.0;
        tableMontants.addCell(new PdfPCell(new Phrase("Montant payé:", fontNormal)));
        tableMontants.addCell(new PdfPCell(new Phrase(
                String.format("%.2f DH", paye),
                fontNormal)));

        // Reste à payer
        tableMontants.addCell(new PdfPCell(new Phrase("RESTE À PAYER:", fontBold)));
        Double reste = facture.getReste() != null ? facture.getReste() : 0.0;
        PdfPCell cellReste = new PdfPCell(new Phrase(
                String.format("%.2f DH", reste),
                new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));

        // Couleur selon le reste
        if (reste > 0) {
            cellReste.setBackgroundColor(BaseColor.RED);
        } else if (reste == 0) {
            cellReste.setBackgroundColor(BaseColor.GREEN);
        } else {
            cellReste.setBackgroundColor(BaseColor.YELLOW);
        }
        cellReste.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tableMontants.addCell(cellReste);

        document.add(tableMontants);

        // ========== INFORMATIONS DE PAIEMENT ==========
        Paragraph infoPaiementTitre = new Paragraph("INFORMATIONS DE PAIEMENT", fontBold);
        infoPaiementTitre.setSpacingAfter(10);
        document.add(infoPaiementTitre);

        Paragraph infoPaiement = new Paragraph();
        infoPaiement.setFont(fontNormal);

        if (reste > 0) {
            infoPaiement.add("📋 FACTURE EN ATTENTE DE PAIEMENT\n");
            infoPaiement.add("Montant dû: " + String.format("%.2f DH", reste) + "\n");
            infoPaiement.add("Date limite de paiement recommandée: " +
                    java.time.LocalDate.now().plusDays(15).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n");
            infoPaiement.add("Modes de paiement acceptés: Espèces, Carte bancaire, Chèque, Virement\n");
        } else if (reste == 0) {
            infoPaiement.add("✅ FACTURE PAYÉE INTÉGRALEMENT\n");
            infoPaiement.add("Date de règlement: " +
                    java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n");
            infoPaiement.add("Merci pour votre confiance.\n");
        } else {
            infoPaiement.add("ℹ️  FACTURE AVEC SOLDE CRÉDITEUR\n");
            infoPaiement.add("Crédit disponible: " + String.format("%.2f DH", Math.abs(reste)) + "\n");
            infoPaiement.add("Ce crédit peut être utilisé pour des consultations futures.\n");
        }

        infoPaiement.add("\n");
        document.add(infoPaiement);

        // ========== MENTIONS LÉGALES ==========
        Paragraph mentions = new Paragraph();
        mentions.setFont(new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC));
        mentions.add("Facture établie conformément à la législation en vigueur.\n");
        mentions.add("TVA non applicable, article 293 B du CGI.\n");
        mentions.add("En cas de retard de paiement, indemnité forfaitaire de 40€ due au titre de l'article L.441-10 du code de commerce.\n");
        mentions.add("Pas d'escompte pour paiement anticipé.\n");
        mentions.setSpacingAfter(10);
        document.add(mentions);

        // ========== PIED DE PAGE ==========
        Paragraph piedPage = new Paragraph();
        piedPage.setFont(new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC));
        piedPage.add("Document généré le " + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n");
        piedPage.add("© HEALTHYSMILE Cabinet Médical - Tous droits réservés\n");
        piedPage.add("📞 Contact: +212 5 XX XX XX XX | ✉️ contact@HEALTHYSMILE.ma\n");
        piedPage.setAlignment(Element.ALIGN_CENTER);
        document.add(piedPage);

        document.close();
    }

    // ---------------- Table ----------------

    private void fillTable(List<Facture> list) {
        model.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Facture f : list) {
            String dateStr = f.getDateFacture() != null
                    ? f.getDateFacture().format(formatter)
                    : "";

            model.addRow(new Object[]{
                    f.getIdFacture(),
                    f.getIdConsultation(),
                    f.getIdSf(),
                    f.getTotaleFacture(),
                    f.getReste(),
                    f.getStatut(),
                    dateStr
            });
        }
    }

    private Long selectedId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionne une facture.");
            return null;
        }
        Object v = model.getValueAt(row, 0);
        return (v == null) ? null : Long.valueOf(v.toString());
    }

    // ---------------- Parsers + errors ----------------

    private Long parseLongOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        return Long.parseLong(s.trim());
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}