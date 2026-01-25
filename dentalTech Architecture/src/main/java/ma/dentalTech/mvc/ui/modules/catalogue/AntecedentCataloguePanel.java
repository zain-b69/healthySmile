package ma.dentalTech.mvc.ui.modules.catalogue;

import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AntecedentCataloguePanel extends JPanel {

    private final AntecedentController controller;

    private final JTextField keywordField = new JTextField(14);
    private final RoundedButton searchBtn = new RoundedButton("Rechercher");
    private final RoundedButton resetBtn = new RoundedButton("Reset");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nom", "Catégorie", "Risque"}, 0
    ) { @Override public boolean isCellEditable(int r, int c){ return false; } };

    private final JTable table = new JTable(model);

    private final RoundedButton addBtn = new RoundedButton("Ajouter");
    private final RoundedButton editBtn = new RoundedButton("Modifier");
    private final RoundedButton delBtn = new RoundedButton("Supprimer");
    private final RoundedButton refreshBtn = new RoundedButton("Rafraîchir");

    public AntecedentCataloguePanel(AntecedentController controller) {
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
        ButtonStyles.primary(delBtn);
        p.add(delBtn);
        ButtonStyles.primary(refreshBtn);
        p.add(refreshBtn);
        return p;
    }

    private void wire() {
        searchBtn.addActionListener(e -> doSearch());
        resetBtn.addActionListener(e -> { keywordField.setText(""); loadAll(); });

        refreshBtn.addActionListener(e -> loadAll());
        addBtn.addActionListener(e -> openForm(null));
        editBtn.addActionListener(e -> {
            Antecedent a = selected();
            if (a == null) return;
            openForm(a);
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
            // si tu n’as pas search côté controller: fais simple findAll() ou filtre en Java
            List<Antecedent> all = controller.findAll();
            if (kw == null || kw.isBlank()) { fill(all); return; }

            String k = kw.trim().toLowerCase();
            fill(all.stream().filter(a -> a.getNom()!=null && a.getNom().toLowerCase().contains(k)).toList());
        } catch (Exception ex) { showErr(ex); }
    }

    private Antecedent selected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionne un antécédent", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        Long id = ((Number) model.getValueAt(row, 0)).longValue();
        String nom = (String) model.getValueAt(row, 1);

        Antecedent a = new Antecedent();
        a.setIdAntecedent(id);
        a.setNom(nom);
        // categorie + risque affichés en table, tu peux aussi les remettre dans l’objet si besoin
        return a;
    }

    private void openForm(Antecedent antecedent) {
        JDialog dlg = new AntecedentFormDialog(
                SwingUtilities.getWindowAncestor(this),
                controller,
                antecedent,
                this::loadAll
        );
        dlg.setVisible(true);
    }

    private void deleteSelected() {
        Antecedent a = selected();
        if (a == null) return;

        int res = JOptionPane.showConfirmDialog(this,
                "Supprimer l'antécédent #" + a.getIdAntecedent() + " ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (res != JOptionPane.YES_OPTION) return;

        try {
            controller.deleteById(a.getIdAntecedent());
            loadAll();
        } catch (Exception ex) { showErr(ex); }
    }

    private void fill(List<Antecedent> list) {
        model.setRowCount(0);
        for (Antecedent a : list) {
            model.addRow(new Object[]{
                    a.getIdAntecedent(),
                    a.getNom(),
                    a.getCategorie(),
                    a.getNiveauDeRisque()
            });
        }
    }

    private void showErr(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    // -------- Dialog create/update --------
    static class AntecedentFormDialog extends JDialog {

        public AntecedentFormDialog(Window owner,
                                    AntecedentController controller,
                                    Antecedent a,
                                    Runnable onDone) {
            super(owner, (a == null ? "Ajouter Antécédent" : "Modifier Antécédent"), ModalityType.APPLICATION_MODAL);
            setSize(520, 260);
            setLocationRelativeTo(owner);

            JTextField nomField = new JTextField(a != null ? a.getNom() : "", 20);

            JComboBox<CategorieAntecedent> catCombo = new JComboBox<>(CategorieAntecedent.values());
            if (a != null && a.getCategorie() != null) catCombo.setSelectedItem(a.getCategorie());

            JComboBox<NiveauRisque> risqueCombo =
                    new JComboBox<>(NiveauRisque.values());
            if (a != null && a.getNiveauDeRisque() != null) risqueCombo.setSelectedItem(a.getNiveauDeRisque());

            RoundedButton save = new RoundedButton(a == null ? "Créer" : "Enregistrer");

            JPanel p = new JPanel(new GridBagLayout());
            p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(6, 6, 6, 6);
            c.fill = GridBagConstraints.HORIZONTAL;

            c.gridx = 0; c.gridy = 0; p.add(new JLabel("Nom:"), c);
            c.gridx = 1; p.add(nomField, c);

            c.gridx = 0; c.gridy = 1; p.add(new JLabel("Catégorie:"), c);
            c.gridx = 1; p.add(catCombo, c);

            c.gridx = 0; c.gridy = 2; p.add(new JLabel("Niveau de risque:"), c);
            c.gridx = 1; p.add(risqueCombo, c);

            c.gridx = 1; c.gridy = 3;
            c.anchor = GridBagConstraints.EAST;
            p.add(save, c);

            setContentPane(p);

            save.addActionListener(e -> {
                try {
                    String nom = nomField.getText().trim();
                    if (nom.isBlank()) throw new IllegalArgumentException("Nom obligatoire");

                    CategorieAntecedent cat = (CategorieAntecedent) catCombo.getSelectedItem();
                    var risque = (NiveauRisque) risqueCombo.getSelectedItem();

                    if (cat == null) throw new IllegalArgumentException("Catégorie obligatoire");
                    if (risque == null) throw new IllegalArgumentException("Risque obligatoire");

                    if (a == null) {
                        Antecedent created = Antecedent.builder()
                                .nom(nom)
                                .categorie(cat)
                                .niveauDeRisque(risque)
                                .build();

                        controller.create(created);
                    } else {
                        a.setNom(nom);
                        a.setCategorie(cat);
                        a.setNiveauDeRisque(risque);

                        controller.update(a);
                    }

                    if (onDone != null) onDone.run();
                    dispose();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }

}
