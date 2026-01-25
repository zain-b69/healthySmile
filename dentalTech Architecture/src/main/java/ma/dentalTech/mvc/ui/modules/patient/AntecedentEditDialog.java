package ma.dentalTech.mvc.ui.modules.patient;

import lombok.Getter;
import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.entities.patient.Antecedent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AntecedentEditDialog extends JDialog {

    @Getter private boolean succeeded = false;
    @Getter private Antecedent value;

    private final JTextField nom = new JTextField(18);
    private final JComboBox<CategorieAntecedent> categorie = new JComboBox<>(CategorieAntecedent.values());
    private final JComboBox<NiveauRisque> risque = new JComboBox<>(NiveauRisque.values());

    private final Long idAntecedent; // null si create

    public AntecedentEditDialog(Window owner, Antecedent existing) {
        super(owner, existing == null ? "Ajouter antécédent" : "Modifier antécédent",
                ModalityType.APPLICATION_MODAL);

        this.idAntecedent = (existing == null) ? null : existing.getIdAntecedent();

        setSize(420, 240);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setContentPane(build());

        if (existing != null) {
            nom.setText(existing.getNom());
            if (existing.getCategorie() != null) categorie.setSelectedItem(existing.getCategorie());
            if (existing.getNiveauDeRisque() != null) risque.setSelectedItem(existing.getNiveauDeRisque());
        }
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0;
        form.add(new JLabel("Nom:"), g);
        g.gridx = 1;
        form.add(nom, g);

        g.gridx = 0; g.gridy++;
        form.add(new JLabel("Catégorie:"), g);
        g.gridx = 1;
        form.add(categorie, g);

        g.gridx = 0; g.gridy++;
        form.add(new JLabel("Niveau de risque:"), g);
        g.gridx = 1;
        form.add(risque, g);

        root.add(form, BorderLayout.CENTER);

        JButton cancel = new JButton("Annuler");
        JButton ok = new JButton("Valider");
        cancel.addActionListener(e -> dispose());
        ok.addActionListener(e -> onOk());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(cancel);
        south.add(ok);
        root.add(south, BorderLayout.SOUTH);

        return root;
    }

    private void onOk() {
        String n = nom.getText();
        if (n == null || n.isBlank()) {
            JOptionPane.showMessageDialog(this, "Nom obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        value = Antecedent.builder()
                .idAntecedent(idAntecedent)
                .nom(n.trim())
                .categorie((CategorieAntecedent) categorie.getSelectedItem())
                .niveauDeRisque((NiveauRisque) risque.getSelectedItem())
                .build();

        succeeded = true;
        dispose();
    }
}
