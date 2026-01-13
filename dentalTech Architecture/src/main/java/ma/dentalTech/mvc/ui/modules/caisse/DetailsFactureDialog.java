package ma.dentalTech.mvc.ui.modules.caisse;

import ma.dentalTech.entities.dossierMedical.Facture;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class DetailsFactureDialog extends JDialog {

    private final Facture facture;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public DetailsFactureDialog(Window owner, Facture facture) {
        super(owner, "Détails Facture", ModalityType.APPLICATION_MODAL);
        this.facture = facture;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(520, 360);
        setLocationRelativeTo(owner);

        setContentPane(buildUI());
    }

    private JComponent buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Détails Facture");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(10, 0, 10, 0));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        addRow(form, c, 0, "ID Facture :", safe(facture.getIdFacture()));
        addRow(form, c, 1, "ID Consultation :", safe(facture.getIdConsultation()));
        addRow(form, c, 2, "ID Situation Financière (SF) :", safe(facture.getIdSf()));
        addRow(form, c, 3, "Total Facture :", safeMoney(facture.getTotaleFacture()));
        addRow(form, c, 4, "Reste :", safeMoney(facture.getReste()));
        addRow(form, c, 5, "Statut :", facture.getStatut() == null ? "" : facture.getStatut().name());
        addRow(form, c, 6, "Date Facture :", facture.getDateFacture() == null ? "" : facture.getDateFacture().format(fmt));

        root.add(form, BorderLayout.CENTER);

        JButton close = new JButton("Fermer");
        close.addActionListener(e -> dispose());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.add(close);

        root.add(actions, BorderLayout.SOUTH);

        return root;
    }

    private void addRow(JPanel form, GridBagConstraints c, int row, String label, String value) {
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        form.add(new JLabel(label), c);

        JTextField tf = new JTextField(value, 26);
        tf.setEditable(false);
        tf.setBackground(Color.WHITE);

        c.gridx = 1; c.weightx = 1;
        form.add(tf, c);
    }

    private String safe(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    private String safeMoney(Double d) {
        return d == null ? "" : String.format("%.2f", d);
    }
}
