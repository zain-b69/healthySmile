// Fichier: ma/dentalTech/mvc/ui/modules/dossierMedical/SituationsFinancieresPanel.java
package ma.dentalTech.mvc.ui.modules.dossierMedical;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import ma.dentalTech.entities.dossierMedical.SituationFinanciere;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;

public class SituationsFinancieresPanel extends JPanel {

    private final DossierMedicalController dossierController;
    private final PatientController patientController;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID_SF", "ID_DM", "PATIENT", "TOTAL ACTES", "PAYÉ", "CRÉDIT", "RESTE", "STATUT"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    // Filtres
    private JTextField idPatientField = new JTextField(10);
    private JComboBox<StatutSituationFinanciere> statutCombo = new JComboBox<>(StatutSituationFinanciere.values());

    public SituationsFinancieresPanel(DossierMedicalController dossierController,
                                      PatientController patientController) {
        this.dossierController = dossierController;
        this.patientController = patientController;

        setLayout(new BorderLayout(10, 10));
        initUI();
        chargerToutesLesSituations();
    }

    private void initUI() {
        // Panel supérieur avec filtres
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        topPanel.add(new JLabel("ID Patient:"));
        topPanel.add(idPatientField);

        topPanel.add(new JLabel("Statut:"));
        statutCombo.insertItemAt(null, 0); // Option "Tous"
        statutCombo.setSelectedIndex(0);
        topPanel.add(statutCombo);

        RoundedButton btnFiltrer = new RoundedButton("Filtrer");
        RoundedButton btnTout = new RoundedButton("Tout afficher");
        ButtonStyles.primary(btnFiltrer);
        ButtonStyles.primary(btnTout);

        btnFiltrer.addActionListener(e -> filtrerSituations());
        btnTout.addActionListener(e -> chargerToutesLesSituations());

        topPanel.add(btnFiltrer);
        topPanel.add(btnTout);

        add(topPanel, BorderLayout.NORTH);

        // Tableau des situations
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Configuration des colonnes
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID_SF
        table.getColumnModel().getColumn(1).setPreferredWidth(50);  // ID_DM
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // PATIENT
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // TOTAL ACTES
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // PAYÉ
        table.getColumnModel().getColumn(5).setPreferredWidth(80);  // CRÉDIT
        table.getColumnModel().getColumn(6).setPreferredWidth(80);  // RESTE
        table.getColumnModel().getColumn(7).setPreferredWidth(100); // STATUT

        // Panel inférieur avec boutons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        RoundedButton btnVoirDetails = new RoundedButton("Voir détails");
        RoundedButton btnEnregistrerPaiement = new RoundedButton("Paiement");
        RoundedButton btnExporterPDF = new RoundedButton("📄 Exporter PDF");
        ButtonStyles.primary(btnVoirDetails);
        ButtonStyles.primary(btnEnregistrerPaiement);
        ButtonStyles.primary(btnExporterPDF);

        btnVoirDetails.addActionListener(e -> voirDetails());
        btnEnregistrerPaiement.addActionListener(e -> enregistrerPaiement());
        btnExporterPDF.addActionListener(e -> exporterPDF());
        btnEnregistrerPaiement.setForeground(Color.WHITE);

        bottomPanel.add(btnVoirDetails);
        bottomPanel.add(btnEnregistrerPaiement);
        bottomPanel.add(Box.createHorizontalStrut(10));
        bottomPanel.add(btnExporterPDF);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Rafraîchit le tableau avec les données actuelles
     */
    private void rafraichirTableau() {
        // Conserver les filtres actuels
        if (!idPatientField.getText().trim().isEmpty() ||
                statutCombo.getSelectedItem() != null) {
            filtrerSituations();
        } else {
            chargerToutesLesSituations();
        }
    }

    private void chargerToutesLesSituations() {
        try {
            model.setRowCount(0);

            // Récupérer tous les dossiers
            var dossiers = dossierController.listAll();

            for (var dossier : dossiers) {
                SituationFinanciere situation = dossierController.getSituationByDossier(dossier.getIdDm());
                if (situation != null) {
                    ajouterSituationAuTableau(situation, dossier.getIdPatient());
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrerSituations() {
        try {
            model.setRowCount(0);

            // Filtrer par ID patient si fourni
            if (!idPatientField.getText().trim().isEmpty()) {
                Long idPatient = Long.parseLong(idPatientField.getText().trim());
                var dossier = dossierController.getByPatient(idPatient);
                if (dossier != null) {
                    SituationFinanciere situation = dossierController.getSituationByDossier(dossier.getIdDm());
                    if (situation != null) {
                        ajouterSituationAuTableau(situation, idPatient);
                    }
                }
            } else {
                // Filtrer par statut si sélectionné
                StatutSituationFinanciere statut = (StatutSituationFinanciere) statutCombo.getSelectedItem();

                if (statut != null) {
                    // Filtrer par statut (implémentation basique)
                    var dossiers = dossierController.listAll();
                    for (var dossier : dossiers) {
                        SituationFinanciere situation = dossierController.getSituationByDossier(dossier.getIdDm());
                        if (situation != null && situation.getStatut() == statut) {
                            ajouterSituationAuTableau(situation, dossier.getIdPatient());
                        }
                    }
                } else {
                    chargerToutesLesSituations();
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouterSituationAuTableau(SituationFinanciere situation, Long idPatient) {
        // Récupérer le nom du patient
        String nomPatient = "Inconnu";
        try {
            Patient patient = patientController.findById(idPatient);
            if (patient != null && patient.getNom() != null) {
                nomPatient = patient.getNom();
            }
        } catch (Exception e) {
            System.err.println("Erreur récupération patient " + idPatient + ": " + e.getMessage());
        }

        Double resteAPayer = dossierController.calculerResteAPayer(situation.getIdSf());
        resteAPayer = resteAPayer != null ? resteAPayer : 0.0;

        // Déterminer la couleur du statut
        String statutStr = situation.getStatut() != null ?
                situation.getStatut().toString() : "NON_DEFINI";

        model.addRow(new Object[]{
                situation.getIdSf(),
                situation.getIdDm(),
                nomPatient,
                String.format("%.2f DH", situation.getTotaleDesActes()),
                String.format("%.2f DH", situation.getTotalePaye()),
                String.format("%.2f DH", situation.getCredit()),
                String.format("%.2f DH", resteAPayer),
                statutStr
        });
    }

    private void voirDetails() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une situation",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        long idSf = ((Number) model.getValueAt(row, 0)).longValue();

        try {
            SituationFinanciere situation = dossierController.getSituationByDossier(
                    ((Number) model.getValueAt(row, 1)).longValue());

            if (situation != null) {
                Window parentWindow = getParentWindow();
                SituationFinanciereDialog dialog = new SituationFinanciereDialog(
                        parentWindow,
                        dossierController,
                        situation
                );

                // Ajouter un écouteur pour rafraîchir quand le dialogue se ferme
                dialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        // Rafraîchir le tableau quand le dialogue se ferme
                        rafraichirTableau();
                    }
                });

                dialog.setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enregistrerPaiement() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une situation",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        long idSf = ((Number) model.getValueAt(row, 0)).longValue();
        String patient = (String) model.getValueAt(row, 2);

        String montantStr = JOptionPane.showInputDialog(this,
                "Enregistrer paiement pour: " + patient + "\n\n" +
                        "Montant (DH):",
                "Enregistrer paiement",
                JOptionPane.QUESTION_MESSAGE);

        if (montantStr != null && !montantStr.trim().isEmpty()) {
            try {
                Double montant = Double.parseDouble(montantStr.trim());
                if (montant <= 0) {
                    JOptionPane.showMessageDialog(this, "Montant invalide",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                dossierController.enregistrerPaiement(idSf, montant);
                JOptionPane.showMessageDialog(this,
                        "Paiement de " + montant + " DH enregistré",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);

                // Rafraîchir
                rafraichirTableau();

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Montant invalide",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /* ================== EXPORT PDF ================== */
    private void exporterPDF() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une situation",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Récupérer les données de la ligne sélectionnée
        long idSf = ((Number) model.getValueAt(row, 0)).longValue();
        long idDm = ((Number) model.getValueAt(row, 1)).longValue();
        String nomPatient = (String) model.getValueAt(row, 2);

        // Demander où sauvegarder le PDF
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer le PDF");
        fileChooser.setSelectedFile(new File(
                String.format("Situation_Financiere_%s_Dossier_%d.pdf",
                        nomPatient.replaceAll("[^a-zA-Z0-9]", "_"), idDm)
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
                // Récupérer la situation complète
                SituationFinanciere situation = dossierController.getSituationByDossier(idDm);
                if (situation == null) {
                    JOptionPane.showMessageDialog(this,
                            "Impossible de récupérer les données financières",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                genererPDF(fileToSave, situation, nomPatient);
                JOptionPane.showMessageDialog(this,
                        "PDF généré avec succès :\n" + fileToSave.getAbsolutePath(),
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

    private void genererPDF(File fichier, SituationFinanciere situation, String nomPatient) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(fichier));
        document.open();

        // ========== EN-TÊTE ==========
        Font fontTitre = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font fontSousTitre = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Font fontNormal = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

        // Titre
        Paragraph titre = new Paragraph("RÉCAPITULATIF FINANCIER", fontTitre);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingAfter(20);
        document.add(titre);

        // Informations patient
        Paragraph infoPatient = new Paragraph();
        infoPatient.setFont(fontNormal);
        infoPatient.add(new Chunk("Dossier médical: ", fontBold));
        infoPatient.add(new Chunk("#" + situation.getIdDm() + "\n"));
        infoPatient.add(new Chunk("Patient: ", fontBold));
        infoPatient.add(new Chunk(nomPatient + "\n"));
        infoPatient.add(new Chunk("Date de génération: ", fontBold));
        infoPatient.add(new Chunk(java.time.LocalDate.now().toString() + "\n\n"));
        infoPatient.setSpacingAfter(15);
        document.add(infoPatient);

        // ========== RÉSUMÉ FINANCIER ==========
        Paragraph resumeTitre = new Paragraph("RÉSUMÉ FINANCIER", fontBold);
        resumeTitre.setSpacingAfter(10);
        document.add(resumeTitre);

        // Tableau des informations financières
        PdfPTable tableResume = new PdfPTable(2);
        tableResume.setWidthPercentage(100);
        tableResume.setSpacingBefore(10);
        tableResume.setSpacingAfter(15);

        // Configuration des cellules
        PdfPCell cellHeader = new PdfPCell(new Phrase("DÉSIGNATION", fontBold));
        cellHeader.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellHeader.setPadding(5);

        // Ajouter les lignes
        tableResume.addCell(cellHeader);
        tableResume.addCell(cellHeader);

        // Total des soins
        tableResume.addCell(new PdfPCell(new Phrase("Total des soins:", fontNormal)));
        tableResume.addCell(new PdfPCell(new Phrase(
                String.format("%.2f DH", situation.getTotaleDesActes() != null ? situation.getTotaleDesActes() : 0.0),
                fontNormal)));

        // Total payé
        tableResume.addCell(new PdfPCell(new Phrase("Total payé:", fontNormal)));
        tableResume.addCell(new PdfPCell(new Phrase(
                String.format("%.2f DH", situation.getTotalePaye() != null ? situation.getTotalePaye() : 0.0),
                fontNormal)));

        // Crédit patient
        tableResume.addCell(new PdfPCell(new Phrase("Crédit patient:", fontNormal)));
        tableResume.addCell(new PdfPCell(new Phrase(
                String.format("%.2f DH", situation.getCredit() != null ? situation.getCredit() : 0.0),
                fontNormal)));

        // Statut
        tableResume.addCell(new PdfPCell(new Phrase("Statut:", fontNormal)));
        String statutStr = situation.getStatut() != null ? situation.getStatut().toString() : "NON_DÉFINI";
        tableResume.addCell(new PdfPCell(new Phrase(statutStr, fontNormal)));

        // Promotion
        tableResume.addCell(new PdfPCell(new Phrase("Promotion:", fontNormal)));
        String promoStr = situation.getEnPromo() != null && situation.getEnPromo() ? "ACTIVE (-25%)" : "INACTIVE";
        tableResume.addCell(new PdfPCell(new Phrase(promoStr, fontNormal)));

        // Reste à payer
        Double reste = dossierController.calculerResteAPayer(situation.getIdSf());
        reste = reste != null ? reste : 0.0;

        tableResume.addCell(new PdfPCell(new Phrase("RESTE À PAYER:", fontBold)));
        PdfPCell cellReste = new PdfPCell(new Phrase(
                String.format("%.2f DH", reste),
                new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
        if (reste > 0) {
            cellReste.setBackgroundColor(BaseColor.RED);
        } else if (reste == 0) {
            cellReste.setBackgroundColor(BaseColor.GREEN);
        } else {
            cellReste.setBackgroundColor(BaseColor.CYAN);
        }
        cellReste.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tableResume.addCell(cellReste);

        document.add(tableResume);

        // ========== HISTORIQUE DES PAIEMENTS ==========
        Paragraph historiqueTitre = new Paragraph("HISTORIQUE DES PAIEMENTS", fontBold);
        historiqueTitre.setSpacingAfter(10);
        document.add(historiqueTitre);

        Paragraph historiqueMsg = new Paragraph(
                "À IMPLÉMENTER : Détail des paiements avec dates et méthodes.\n" +
                        "Cette fonctionnalité sera disponible dans la prochaine version.",
                fontNormal);
        historiqueMsg.setSpacingAfter(15);
        document.add(historiqueMsg);

        // ========== SOLDE ET OBSERVATIONS ==========
        Paragraph soldeTitre = new Paragraph("SOLDE ET OBSERVATIONS", fontBold);
        soldeTitre.setSpacingAfter(10);
        document.add(soldeTitre);

        Paragraph observations = new Paragraph();
        observations.setFont(fontNormal);

        if (reste > 0) {
            observations.add("Le patient a un solde impayé de " +
                    String.format("%.2f DH", reste) + ".\n");
            observations.add("Date limite de paiement recommandée : " +
                    java.time.LocalDate.now().plusDays(30) + "\n");
        } else if (reste == 0) {
            observations.add("Le compte est entièrement réglé. Aucun solde impayé.\n");
        } else {
            observations.add("Le patient a un crédit de " +
                    String.format("%.2f DH", Math.abs(reste)) +
                    " qui peut être utilisé pour des futurs soins.\n");
        }

        observations.add("\nObservations : \n");
        observations.add("Document généré automatiquement par le système HEALTHYSMILE.\n");
        observations.add("Pour toute question, contactez le secrétariat.\n");

        document.add(observations);

        // ========== PIED DE PAGE ==========
        Paragraph piedPage = new Paragraph();
        piedPage.setFont(new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC));
        piedPage.add("Document généré le " + java.time.LocalDateTime.now() + "\n");
        piedPage.add("© HEALTHYSMILE - Tous droits réservés\n");
        piedPage.setAlignment(Element.ALIGN_CENTER);
        document.add(piedPage);

        document.close();
    }

    private Window getParentWindow() {
        Container parent = getParent();
        while (parent != null && !(parent instanceof Window)) {
            parent = parent.getParent();
        }
        return (Window) parent;
    }
}