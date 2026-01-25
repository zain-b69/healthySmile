package ma.dentalTech.mvc.ui.modules.users;

import lombok.Getter;
import ma.dentalTech.service.modules.users.dto.UserAccountDto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ResetPasswordDialog extends JDialog {

    @Getter
    private boolean succeeded = false;
    @Getter
    private String newPassword;

    private final JPasswordField p1 = new JPasswordField(18);
    private final JPasswordField p2 = new JPasswordField(18);

    public ResetPasswordDialog(Window owner, UserAccountDto u) {
        super(owner, "Reset password: " + u.nom(), ModalityType.APPLICATION_MODAL);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setContentPane(build());

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.gridy = 0;

        form.add(new JLabel("Nouveau mot de passe:"), g);
        g.gridx = 1; form.add(p1, g);

        g.gridx = 0; g.gridy++;
        form.add(new JLabel("Confirmer:"), g);
        g.gridx = 1; form.add(p2, g);

        root.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Annuler");
        JButton ok = new JButton("Valider");
        actions.add(cancel);
        actions.add(ok);
        root.add(actions, BorderLayout.SOUTH);

        cancel.addActionListener(e -> dispose());
        ok.addActionListener(e -> onOk());

        return root;
    }

    private void onOk() {
        String a = new String(p1.getPassword());
        String b = new String(p2.getPassword());

        if (a.isBlank()) {
            JOptionPane.showMessageDialog(this, "Mot de passe vide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!a.equals(b)) {
            JOptionPane.showMessageDialog(this, "Les mots de passe ne correspondent pas.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.newPassword = a;
        this.succeeded = true;
        dispose();
    }
}
