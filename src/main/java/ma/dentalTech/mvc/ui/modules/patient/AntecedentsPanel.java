package ma.dentalTech.mvc.ui.modules.patient;

import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AntecedentsPanel extends JPanel {

    private final AntecedentController controller;
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nom", "Catégorie", "Risque"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    public AntecedentsPanel(AntecedentController controller) {
        super(new BorderLayout());
        this.controller = controller;

        JLabel title = new JLabel("Catalogue des antécédents");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        RoundedButton addBtn = new RoundedButton("Ajouter");
        RoundedButton editBtn = new RoundedButton("Modifier");
        RoundedButton delBtn = new RoundedButton("Supprimer");
        RoundedButton refreshBtn = new RoundedButton("Rafraîchir");

        ButtonStyles.primary(addBtn);
        ButtonStyles.primary(editBtn);
        ButtonStyles.primary(delBtn);
        ButtonStyles.primary(refreshBtn);

        addBtn.addActionListener(e -> onAdd());
        editBtn.addActionListener(e -> onEdit());
        delBtn.addActionListener(e -> onDelete());
        refreshBtn.addActionListener(e -> reload());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(addBtn);
        south.add(editBtn);
        south.add(delBtn);
        south.add(refreshBtn);
        add(south, BorderLayout.SOUTH);

        reload();
    }

    private void reload() {
        model.setRowCount(0);
        List<Antecedent> list = controller.findAll();
        for (Antecedent a : list) {
            model.addRow(new Object[]{
                    a.getIdAntecedent(),
                    a.getNom(),
                    a.getCategorie(),
                    a.getNiveauDeRisque()
            });
        }
    }

    private Long selectedId() {
        int r = table.getSelectedRow();
        if (r < 0) return null;
        Object v = model.getValueAt(r, 0);
        return (v == null) ? null : Long.valueOf(v.toString());
    }

    private void onAdd() {
        AntecedentEditDialog dlg = new AntecedentEditDialog(SwingUtilities.getWindowAncestor(this), null);
        dlg.setVisible(true);
        if (!dlg.isSucceeded()) return;

        try {
            controller.create(dlg.getValue());
            reload();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        Long id = selectedId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Sélectionne un antécédent.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Antecedent a = controller.findById(id);
        if (a == null) {
            JOptionPane.showMessageDialog(this, "Introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        AntecedentEditDialog dlg = new AntecedentEditDialog(SwingUtilities.getWindowAncestor(this), a);
        dlg.setVisible(true);
        if (!dlg.isSucceeded()) return;

        try {
            controller.update(dlg.getValue());
            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        Long id = selectedId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Sélectionne un antécédent.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this, "Supprimer cet antécédent ?", "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            controller.deleteById(id);
            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
