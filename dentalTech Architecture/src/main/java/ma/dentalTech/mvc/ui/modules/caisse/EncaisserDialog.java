package ma.dentalTech.mvc.ui.modules.caisse;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EncaisserDialog extends JDialog {

    private boolean ok = false;

    private final JTextField tfMontant = new JTextField(18);

    public EncaisserDialog(Window owner) {
        super(owner, "Encaisser", ModalityType.APPLICATION_MODAL);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 220);
        setLocationRelativeTo(owner);

        setContentPane(buildUI());
    }

    private JComponent buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Encaisser un paiement");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(12, 0, 12, 0));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Montant encaissé * :"), c);

        c.gridx = 1;
        form.add(tfMontant, c);

        root.add(form, BorderLayout.CENTER);

        JButton btnOk = new JButton("Valider");
        btnOk.addActionListener(e -> onValidate());

        JButton btnCancel = new JButton("Annuler");
        btnCancel.addActionListener(e -> dispose());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.add(btnCancel);
        actions.add(btnOk);

        root.add(actions, BorderLayout.SOUTH);

        return root;
    }

    private void onValidate() {
        try {
            Double montant = parseDouble(tfMontant.getText(), "Montant requis");
            if (montant <= 0) throw new IllegalArgumentException("Montant invalide (doit être > 0)");
            ok = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Double parseDouble(String s, String err) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(err);
        return Double.parseDouble(s.trim());
    }

    public boolean isOk() {
        return ok;
    }

    public Double getMontant() {
        return Double.parseDouble(tfMontant.getText().trim());
    }
}
