package ma.dentalTech.mvc.ui.modules.caisse;

import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CreateFactureDialog extends JDialog {

    private boolean ok = false;

    private final JTextField tfIdConsultation = new JTextField(18);
    private final JTextField tfIdSf = new JTextField(18);
    private final JTextField tfTotal = new JTextField(18);

    public CreateFactureDialog(Window owner) {
        super(owner, "Créer Facture", ModalityType.APPLICATION_MODAL);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(480, 300);
        setLocationRelativeTo(owner);

        setContentPane(buildUI());
    }

    private JComponent buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Créer une facture");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(10, 0, 10, 0));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        addRow(form, c, 0, "ID Consultation * :", tfIdConsultation);
        addRow(form, c, 1, "ID Situation Financière (SF) * :", tfIdSf);
        addRow(form, c, 2, "Total facture * :", tfTotal);

        root.add(form, BorderLayout.CENTER);

        RoundedButton btnSave = new RoundedButton("Créer");
        ButtonStyles.primary(btnSave);
        btnSave.addActionListener(e -> onSave());

        RoundedButton btnCancel = new RoundedButton("Annuler");
        ButtonStyles.primary(btnCancel);
        btnCancel.addActionListener(e -> dispose());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.add(btnCancel);
        actions.add(btnSave);

        root.add(actions, BorderLayout.SOUTH);

        return root;
    }

    private void addRow(JPanel form, GridBagConstraints c, int row, String label, JTextField field) {
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        form.add(new JLabel(label), c);

        c.gridx = 1; c.weightx = 1;
        form.add(field, c);
    }

    private void onSave() {
        try {
            Long idConsultation = parseLong(tfIdConsultation.getText(), "ID Consultation requis");
            Long idSf = parseLong(tfIdSf.getText(), "ID SF requis");
            Double total = parseDouble(tfTotal.getText(), "Total requis");

            if (total < 0) throw new IllegalArgumentException("Total invalide (doit être >= 0)");

            ok = true;
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Long parseLong(String s, String err) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(err);
        return Long.parseLong(s.trim());
    }

    private Double parseDouble(String s, String err) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(err);
        return Double.parseDouble(s.trim());
    }

    public boolean isOk() {
        return ok;
    }

    public Long getIdConsultation() {
        return Long.parseLong(tfIdConsultation.getText().trim());
    }

    public Long getIdSf() {
        return Long.parseLong(tfIdSf.getText().trim());
    }

    public Double getTotal() {
        return Double.parseDouble(tfTotal.getText().trim());
    }
}
