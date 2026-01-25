package ma.dentalTech.mvc.ui.modules.patient;

import ma.dentalTech.entities.patient.Antecedent;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AntecedentSelectDialog extends JDialog {

    private Antecedent selected;

    private AntecedentSelectDialog(Window owner, List<Antecedent> antecedents) {
        super(owner, "Choisir un antécédent", ModalityType.APPLICATION_MODAL);
        setSize(420, 320);
        setLocationRelativeTo(owner);

        DefaultListModel<Antecedent> model = new DefaultListModel<>();
        for (Antecedent a : antecedents) model.addElement(a);

        JList<Antecedent> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton ok = new JButton("Ajouter");
        JButton cancel = new JButton("Annuler");

        ok.addActionListener(e -> {
            selected = list.getSelectedValue();
            dispose();
        });

        cancel.addActionListener(e -> {
            selected = null;
            dispose();
        });

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(cancel);
        south.add(ok);

        setLayout(new BorderLayout());
        add(new JScrollPane(list), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
    }

    public static Antecedent select(Window owner, List<Antecedent> antecedents) {
        AntecedentSelectDialog dlg = new AntecedentSelectDialog(owner, antecedents);
        dlg.setVisible(true);
        return dlg.selected;
    }
}
