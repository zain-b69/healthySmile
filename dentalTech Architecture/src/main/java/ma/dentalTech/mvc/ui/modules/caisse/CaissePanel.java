package ma.dentalTech.mvc.ui.modules.caisse;

import ma.dentalTech.entities.dossierMedical.Facture;
import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.mvc.controllers.modules.caisse.api.CaisseController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

        p.add(btnAdd);
        p.add(btnPay);
        p.add(btnCancel);
        p.add(btnDetails);

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

    // ---------------- Table ----------------

    private void fillTable(List<Facture> list) {
        model.setRowCount(0);
        for (Facture f : list) {
            model.addRow(new Object[]{
                    f.getIdFacture(),
                    f.getIdConsultation(),
                    f.getIdSf(),
                    f.getTotaleFacture(),
                    f.getReste(),
                    f.getStatut()
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