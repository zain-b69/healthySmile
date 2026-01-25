package ma.dentalTech.mvc.ui.modules.users;

import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.service.modules.users.dto.UpdateUserProfileRequest;
import ma.dentalTech.service.modules.users.dto.UserAccountDto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class EditUserDialog extends JDialog {

    private boolean succeeded = false;
    private UpdateUserProfileRequest request;

    private final JTextField nom = new JTextField(20);
    private final JTextField email = new JTextField(20);
    private final JTextField adresse = new JTextField(20);
    private final JTextField tel = new JTextField(20);
    private final JComboBox<Sexe> sexe = new JComboBox<>(Sexe.values());
    private final JTextField naissance = new JTextField(10);

    private final Long id;

    public EditUserDialog(Window owner, UserAccountDto u) {
        super(owner, "Modifier utilisateur", ModalityType.APPLICATION_MODAL);
        this.id = u.id();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(440, 320);
        setLocationRelativeTo(owner);

        // init fields
        nom.setText(u.nom());
        email.setText(u.email());
        adresse.setText(null);
        tel.setText(null);
        if (u.sexe() != null) sexe.setSelectedItem(u.sexe());
        naissance.setText(u.dateNaissance() != null ? u.dateNaissance().toString() : "");

        setContentPane(build());
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public UpdateUserProfileRequest getRequest() {
        return request;
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.gridy = 0;

        addRow(form, g, "Nom:", nom);
        addRow(form, g, "Email:", email);
        addRow(form, g, "Adresse:", adresse);
        addRow(form, g, "Tel:", tel);
        addRow(form, g, "Sexe:", sexe);
        addRow(form, g, "Naissance (yyyy-MM-dd):", naissance);

        root.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Annuler");
        JButton ok = new JButton("Enregistrer");
        actions.add(cancel);
        actions.add(ok);
        root.add(actions, BorderLayout.SOUTH);

        cancel.addActionListener(e -> dispose());
        ok.addActionListener(e -> onSave());

        return root;
    }

    private void addRow(JPanel form, GridBagConstraints g, String label, JComponent comp) {
        g.gridx = 0;
        g.weightx = 0;
        form.add(new JLabel(label), g);

        g.gridx = 1;
        g.weightx = 1;
        form.add(comp, g);

        g.gridy++;
    }

    private void onSave() {
        try {
            LocalDate dn = null;
            String ds = naissance.getText();
            if (ds != null && !ds.isBlank()) dn = LocalDate.parse(ds.trim());

            request = new UpdateUserProfileRequest(
                    id,
                    emptyToNull(nom.getText()),
                    emptyToNull(email.getText()),
                    emptyToNull(adresse.getText()),
                    emptyToNull(tel.getText()),
                    (Sexe) sexe.getSelectedItem(),
                    dn
            );

            succeeded = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String emptyToNull(String s) {
        if (s == null) return null;
        String x = s.trim();
        return x.isEmpty() ? null : x;
    }
}
