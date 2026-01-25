package ma.dentalTech.mvc.ui.modules.patient;

import lombok.Getter;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Patient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class PatientFormDialog extends JDialog {

    @Getter
    private boolean succeeded = false;

    @Getter
    private Patient patient;

    private final JTextField nom = new JTextField(18);
    private final JTextField tel = new JTextField(18);
    private final JTextField adresse = new JTextField(18);
    private final JTextField naissance = new JTextField(18); // yyyy-MM-dd

    private final JComboBox<Sexe> sexe = new JComboBox<>(Sexe.values());
    private final JComboBox<Assurance> assurance = new JComboBox<>(Assurance.values());

    public PatientFormDialog(Window owner, String title, Patient existing) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 300);
        setLocationRelativeTo(owner);

        if (existing != null) fill(existing);

        setContentPane(build());
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;

        addRow(form, g, y++, "Nom:", nom);
        addRow(form, g, y++, "Téléphone:", tel);
        addRow(form, g, y++, "Adresse:", adresse);
        addRow(form, g, y++, "Naissance (yyyy-MM-dd):", naissance);
        addRow(form, g, y++, "Sexe:", sexe);
        addRow(form, g, y++, "Assurance:", assurance);

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

    private void addRow(JPanel form, GridBagConstraints g, int y, String label, JComponent field) {
        g.gridx = 0; g.gridy = y; g.weightx = 0;
        form.add(new JLabel(label), g);
        g.gridx = 1; g.weightx = 1;
        form.add(field, g);
    }

    private void fill(Patient p) {
        nom.setText(nvl(p.getNom()));
        tel.setText(nvl(p.getTelephone()));
        adresse.setText(nvl(p.getAdresse()));
        naissance.setText(p.getDateNaissance() != null ? p.getDateNaissance().toString() : "");
        if (p.getSexe() != null) sexe.setSelectedItem(p.getSexe());
        if (p.getAssurance() != null) assurance.setSelectedItem(p.getAssurance());
    }

    private void onOk() {
        String n = nom.getText().trim();
        if (n.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nom obligatoire.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate dn = null;
        String dnTxt = naissance.getText().trim();
        if (!dnTxt.isEmpty()) {
            try {
                dn = LocalDate.parse(dnTxt); // format yyyy-MM-dd
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Date invalide. Format attendu: yyyy-MM-dd", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Tu peux rendre date obligatoire ici si tu veux :
        if (dn == null) {
            JOptionPane.showMessageDialog(this, "Date de naissance obligatoire.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        patient = Patient.builder()
                .nom(n)
                .telephone(emptyToNull(tel.getText()))
                .adresse(emptyToNull(adresse.getText()))
                .dateNaissance(dn)
                .sexe((Sexe) sexe.getSelectedItem())
                .assurance((Assurance) assurance.getSelectedItem())
                .build();

        succeeded = true;
        dispose();
    }

    private String nvl(String s) {
        return s == null ? "" : s;
    }

    private String emptyToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
