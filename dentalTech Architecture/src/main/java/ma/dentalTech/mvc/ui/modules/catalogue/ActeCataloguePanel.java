package ma.dentalTech.mvc.ui.modules.catalogue;

import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.mvc.controllers.modules.acte.api.ActeController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ActeCataloguePanel extends JPanel {

    private final ActeController controller;

    private final JTextField keywordField = new JTextField(14);
    private final JTextField categorieField = new JTextField(12);
    private final RoundedButton searchBtn = new RoundedButton("Rechercher");
    private final RoundedButton resetBtn = new RoundedButton("Reset");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Libellé", "Catégorie", "Prix"}, 0
    ) { @Override public boolean isCellEditable(int r, int c){ return false; } };

    private final JTable table = new JTable(model);

    private final RoundedButton addBtn = new RoundedButton("Ajouter");
    private final RoundedButton editBtn = new RoundedButton("Modifier");
    private final RoundedButton priceBtn = new RoundedButton("Changer prix");
    private final RoundedButton delBtn = new RoundedButton("Supprimer");
    private final RoundedButton refreshBtn = new RoundedButton("Rafraîchir");

    public ActeCataloguePanel(ActeController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10,10));

        add(buildTop(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);

        wire();
        loadAll();
    }

    private JComponent buildTop() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel("Mot clé:"));
        p.add(keywordField);
        p.add(new JLabel("Catégorie:"));
        p.add(categorieField);
        ButtonStyles.primary(searchBtn);
        p.add(searchBtn);
        ButtonStyles.primary(resetBtn);
        p.add(resetBtn);
        return p;
    }

    private JComponent buildBottom() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        ButtonStyles.primary(addBtn);
        p.add(addBtn);
        ButtonStyles.primary(editBtn);
        p.add(editBtn);
        ButtonStyles.primary(priceBtn);
        p.add(priceBtn);
        ButtonStyles.primary(delBtn);
        p.add(delBtn);
        ButtonStyles.primary(refreshBtn);
        p.add(refreshBtn);
        return p;
    }

    private void wire() {
        searchBtn.addActionListener(e -> doSearch());
        resetBtn.addActionListener(e -> { keywordField.setText(""); categorieField.setText(""); loadAll(); });

        refreshBtn.addActionListener(e -> loadAll());
        addBtn.addActionListener(e -> openForm(null));
        editBtn.addActionListener(e -> {
            Acte a = selected();
            if (a == null) return;
            openForm(a);
        });
        priceBtn.addActionListener(e -> {
            Acte a = selected();
            if (a == null) return;
            openPrixDialog(a);
        });
        delBtn.addActionListener(e -> deleteSelected());
    }

    private void loadAll() {
        try {
            fill(controller.findAll());
        } catch (Exception ex) { showErr(ex); }
    }

    private void doSearch() {
        try {
            String kw = keywordField.getText();
            String cat = categorieField.getText();
            fill(controller.search(kw, cat));
        } catch (Exception ex) { showErr(ex); }
    }

    private Acte selected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionne un acte", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        Long id = ((Number) model.getValueAt(row, 0)).longValue();
        String lib = (String) model.getValueAt(row, 1);
        String cat = (String) model.getValueAt(row, 2);
        Double prix = null;
        Object pv = model.getValueAt(row, 3);
        if (pv != null) prix = ((Number) pv).doubleValue();

        Acte a = new Acte();
        a.setIdActe(id);
        a.setLibelle(lib);
        a.setCategorie(cat);
        a.setPrixDeBase(prix);
        return a;
    }

    private void openForm(Acte acte) {
        JDialog dlg = new ActeFormDialog(
                SwingUtilities.getWindowAncestor(this),
                controller,
                acte,
                this::loadAll
        );
        dlg.setVisible(true);
    }

    private void openPrixDialog(Acte acte) {
        String input = JOptionPane.showInputDialog(this, "Nouveau prix:", acte.getPrixDeBase());
        if (input == null) return;

        try {
            double prix = Double.parseDouble(input.trim());
            controller.updatePrix(acte.getIdActe(), prix);
            loadAll();
        } catch (Exception ex) { showErr(ex); }
    }

    private void deleteSelected() {
        Acte a = selected();
        if (a == null) return;

        int res = JOptionPane.showConfirmDialog(this,
                "Supprimer l'acte #" + a.getIdActe() + " ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (res != JOptionPane.YES_OPTION) return;

        try {
            controller.delete(a.getIdActe());
            loadAll();
        } catch (Exception ex) { showErr(ex); }
    }

    private void fill(List<Acte> list) {
        model.setRowCount(0);
        for (Acte a : list) {
            model.addRow(new Object[]{a.getIdActe(), a.getLibelle(), a.getCategorie(), a.getPrixDeBase()});
        }
    }

    private void showErr(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    // ---- dialog create/update ----
    static class ActeFormDialog extends JDialog {
        public ActeFormDialog(Window owner, ActeController controller, Acte acte, Runnable onDone) {
            super(owner, (acte == null ? "Ajouter Acte" : "Modifier Acte"), ModalityType.APPLICATION_MODAL);
            setSize(420, 260);
            setLocationRelativeTo(owner);

            JTextField libField = new JTextField(acte != null ? acte.getLibelle() : "", 20);
            JTextField catField = new JTextField(acte != null ? acte.getCategorie() : "", 20);
            JTextField prixField = new JTextField(acte != null && acte.getPrixDeBase()!=null ? String.valueOf(acte.getPrixDeBase()) : "", 10);

            RoundedButton save = new RoundedButton(acte == null ? "Créer" : "Enregistrer");

            JPanel p = new JPanel(new GridBagLayout());
            p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(6,6,6,6);
            c.fill = GridBagConstraints.HORIZONTAL;

            c.gridx=0; c.gridy=0; p.add(new JLabel("Libellé:"), c);
            c.gridx=1; p.add(libField, c);

            c.gridx=0; c.gridy=1; p.add(new JLabel("Catégorie:"), c);
            c.gridx=1; p.add(catField, c);

            c.gridx=0; c.gridy=2; p.add(new JLabel("Prix de base:"), c);
            c.gridx=1; p.add(prixField, c);

            c.gridx=1; c.gridy=3;
            c.anchor = GridBagConstraints.EAST;
            p.add(save, c);

            setContentPane(p);

            save.addActionListener(e -> {
                try {
                    String lib = libField.getText();
                    String cat = catField.getText();
                    Double prix = Double.parseDouble(prixField.getText().trim());

                    if (acte == null) controller.create(lib, cat, prix);
                    else controller.update(acte.getIdActe(), lib, cat, prix);

                    if (onDone != null) onDone.run();
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }
}
