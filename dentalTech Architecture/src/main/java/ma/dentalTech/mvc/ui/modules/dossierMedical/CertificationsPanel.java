// Fichier: ma/dentalTech/mvc/ui/modules/dossierMedical/CertificationsPanel.java
package ma.dentalTech.mvc.ui.modules.dossierMedical;

import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CertificationsPanel extends JPanel {

    private final DossierMedicalController dossierController;
    private final PatientController patientController;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID_CERTIF", "ID_DM", "ID_PATIENT", "NOM_PATIENT", "DÉBUT", "FIN", "DURÉE"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    // Filtres
    private JTextField idPatientField = new JTextField(10);
    private JTextField dateDebutField = new JTextField(10);
    private JTextField dateFinField = new JTextField(10);

    public CertificationsPanel(DossierMedicalController dossierController, PatientController patientController) {
        this.dossierController = dossierController;
        this.patientController = patientController;

        setLayout(new BorderLayout(10, 10));
        initUI();
        chargerTousLesCertificats();
    }

    private void initUI() {
        // Panel supérieur avec filtres
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        topPanel.add(new JLabel("ID Patient:"));
        topPanel.add(idPatientField);

        topPanel.add(new JLabel("Début (YYYY-MM-DD):"));
        topPanel.add(dateDebutField);

        topPanel.add(new JLabel("Fin (YYYY-MM-DD):"));
        topPanel.add(dateFinField);

        RoundedButton btnFiltrer = new RoundedButton("Filtrer");
        RoundedButton btnTout = new RoundedButton("Tout afficher");
        ButtonStyles.primary(btnFiltrer);
        ButtonStyles.primary(btnTout);


        btnFiltrer.addActionListener(e -> filtrerCertificats());
        btnTout.addActionListener(e -> chargerTousLesCertificats());

        topPanel.add(btnFiltrer);
        topPanel.add(btnTout);

        add(topPanel, BorderLayout.NORTH);

        // Tableau des certificats
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel inférieur avec boutons d'actions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        RoundedButton btnVoirDetails = new RoundedButton("Voir détails");
        RoundedButton btnGenererTexte = new RoundedButton("Générer texte");
        RoundedButton btnSupprimer = new RoundedButton("Supprimer");

        ButtonStyles.primary(btnVoirDetails);
        ButtonStyles.primary(btnGenererTexte);
        ButtonStyles.danger(btnSupprimer);

        btnVoirDetails.addActionListener(e -> voirDetails());
        btnGenererTexte.addActionListener(e -> genererTexte());
        btnSupprimer.addActionListener(e -> supprimerCertificat());
        btnSupprimer.setForeground(Color.WHITE);

        bottomPanel.add(btnVoirDetails);
        bottomPanel.add(btnGenererTexte);
        bottomPanel.add(btnSupprimer);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void chargerTousLesCertificats() {
        try {
            model.setRowCount(0);

            // Charger tous les dossiers
            var dossiers = dossierController.listAll();

            for (var dossier : dossiers) {
                List<Certificat> certificats = dossierController.getCertificatsByDossier(dossier.getIdDm());
                ajouterCertificatsAuTableau(certificats, dossier.getIdPatient());
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrerCertificats() {
        try {
            model.setRowCount(0);

            // Filtrer par ID patient si fourni
            if (!idPatientField.getText().trim().isEmpty()) {
                Long idPatient = Long.parseLong(idPatientField.getText().trim());
                var dossier = dossierController.getByPatient(idPatient);
                if (dossier != null) {
                    List<Certificat> certificats = dossierController.getCertificatsByDossier(dossier.getIdDm());
                    ajouterCertificatsAuTableau(certificats, idPatient);
                }
            } else {
                chargerTousLesCertificats();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouterCertificatsAuTableau(List<Certificat> certificats, Long idPatient) {
        if (certificats == null || certificats.isEmpty()) return;

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

        for (Certificat c : certificats) {
            String debutStr = c.getDateDebut() != null ? c.getDateDebut().format(fmt) : "";
            String finStr = c.getDateFin() != null ? c.getDateFin().format(fmt) : "";
            String dureeStr = c.getDuree() != null ? c.getDuree() + " jours" : "";

            model.addRow(new Object[]{
                    c.getIdCertif(),
                    c.getIdDm(),
                    idPatient,
                    nomPatient,
                    debutStr,
                    finStr,
                    dureeStr
            });
        }
    }

    private void voirDetails() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un certificat",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        long idCertif = ((Number) model.getValueAt(row, 0)).longValue();

        try {
            Certificat certificat = dossierController.getCertificatById(idCertif);
            if (certificat != null) {
                Window parentWindow = SwingUtilities.getWindowAncestor(this);
                DetailsCertificatDialog dialog = new DetailsCertificatDialog(
                        parentWindow,
                        dossierController,
                        certificat
                );
                dialog.setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void genererTexte() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un certificat",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        long idCertif = ((Number) model.getValueAt(row, 0)).longValue();

        try {
            Certificat certificat = dossierController.getCertificatById(idCertif);
            if (certificat != null) {
                String texte = dossierController.genererTexteCertificat(certificat);

                // CORRECTION ICI : Utiliser getParentFrame() au lieu de SwingUtilities.getWindowAncestor()
                Frame parentFrame = getParentFrame();
                JDialog texteDialog = new JDialog(parentFrame, "Texte du certificat", true);
                texteDialog.setSize(500, 400);
                texteDialog.setLocationRelativeTo(this);

                JTextArea textArea = new JTextArea(texte);
                textArea.setEditable(false);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

                JScrollPane scrollPane = new JScrollPane(textArea);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton btnCopier = new JButton("Copier");
                JButton btnFermer = new JButton("Fermer");

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
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // CORRECTION : Méthode utilitaire pour obtenir le Frame parent
    private Frame getParentFrame() {
        Container parent = getParent();
        while (parent != null && !(parent instanceof Frame)) {
            parent = parent.getParent();
        }
        return (Frame) parent;
    }

    private void supprimerCertificat() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un certificat à supprimer",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        long idCertif = ((Number) model.getValueAt(row, 0)).longValue();
        String dateDebut = (String) model.getValueAt(row, 4);

        int confirm = JOptionPane.showConfirmDialog(this,
                "⚠ SUPPRESSION DE CERTIFICAT ⚠\n\n" +
                        "Êtes-vous sûr de vouloir supprimer le certificat #" + idCertif + " ?\n" +
                        "Période: " + dateDebut + "\n\n" +
                        "Cette action est irréversible !\n\n" +
                        "Confirmer la suppression ?",
                "CONFIRMATION SUPPRESSION",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = dossierController.supprimerCertificat(idCertif);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Certificat supprimé avec succès",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                    chargerTousLesCertificats();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Échec de la suppression",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}