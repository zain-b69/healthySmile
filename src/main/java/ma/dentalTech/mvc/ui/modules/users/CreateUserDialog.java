package ma.dentalTech.mvc.ui.modules.users;

import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.mvc.ui.common.RoundedButton;
import ma.dentalTech.service.modules.users.api.UserManagementService;
import ma.dentalTech.service.modules.users.dto.CreateAdminRequest;
import ma.dentalTech.service.modules.users.dto.CreateMedecinRequest;
import ma.dentalTech.service.modules.users.dto.CreateSecretaireRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class CreateUserDialog extends JDialog {

    private final UserManagementService userService;

    private boolean succeeded = false;

    private final JComboBox<String> type = new JComboBox<>(new String[]{"ADMIN", "MEDECIN", "SECRETAIRE"});

    private final JTextField nom = new JTextField(20);
    private final JTextField email = new JTextField(20);
    private final JTextField adresse = new JTextField(20);
    private final JTextField cin = new JTextField(20);
    private final JTextField tel = new JTextField(20);
    private final JComboBox<Sexe> sexe = new JComboBox<>(Sexe.values());
    private final JTextField login = new JTextField(20);
    private final JPasswordField pwd = new JPasswordField(20);
    private final JTextField naissance = new JTextField(10); // yyyy-MM-dd

    // champs optionnels (tu peux laisser vide)
    private final JTextField salaire = new JTextField(10);
    private final JTextField prime = new JTextField(10);
    private final JTextField dateRecrutement = new JTextField(10); // yyyy-MM-dd
    private final JTextField soldeConge = new JTextField(10);

    private final JTextField specialite = new JTextField(16); // medecin
    private final JTextField numCNSS = new JTextField(16);     // secretaire
    private final JTextField commission = new JTextField(10);  // secretaire

    public CreateUserDialog(Window owner, UserManagementService userService) {
        super(owner, "Ajouter utilisateur", ModalityType.APPLICATION_MODAL);
        this.userService = userService;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(520, 520);
        setLocationRelativeTo(owner);

        setContentPane(build());
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.gridy = 0;

        addRow(form, g, "Type:", type);
        addRow(form, g, "Nom*:", nom);
        addRow(form, g, "Email*:", email);
        addRow(form, g, "Adresse:", adresse);
        addRow(form, g, "CIN:", cin);
        addRow(form, g, "Tel:", tel);
        addRow(form, g, "Sexe:", sexe);
        addRow(form, g, "Login*:", login);
        addRow(form, g, "Mot de passe*:", pwd);
        addRow(form, g, "Naissance (yyyy-MM-dd):", naissance);

        addRow(form, g, "Salaire:", salaire);
        addRow(form, g, "Prime:", prime);
        addRow(form, g, "Date recrutement (yyyy-MM-dd):", dateRecrutement);
        addRow(form, g, "Solde congé:", soldeConge);

        addRow(form, g, "Spécialité (médecin):", specialite);
        addRow(form, g, "Num CNSS (secrétaire):", numCNSS);
        addRow(form, g, "Commission (secrétaire):", commission);

        root.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RoundedButton cancel = new RoundedButton("Annuler");
        RoundedButton ok = new RoundedButton("Créer");
        actions.add(cancel);
        actions.add(ok);
        root.add(actions, BorderLayout.SOUTH);

        cancel.addActionListener(e -> dispose());
        ok.addActionListener(e -> onCreate());

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

    private void onCreate() {
        try {
            String t = (String) type.getSelectedItem();

            String p = new String(pwd.getPassword());

            LocalDate dn = parseDateOrNull(naissance.getText());
            Double sal = parseDoubleOrNull(salaire.getText());
            Double pr = parseDoubleOrNull(prime.getText());
            LocalDate dr = parseDateOrNull(dateRecrutement.getText());
            Integer sc = parseIntOrNull(soldeConge.getText());

            if ("ADMIN".equals(t)) {
                userService.createAdmin(new CreateAdminRequest(
                        nom.getText(), email.getText(), adresse.getText(), cin.getText(), tel.getText(),
                        (Sexe) sexe.getSelectedItem(), login.getText(), p, dn,
                        sal, pr, dr, sc
                ));
            } else if ("MEDECIN".equals(t)) {
                userService.createMedecin(new CreateMedecinRequest(
                        nom.getText(), email.getText(), adresse.getText(), cin.getText(), tel.getText(),
                        (Sexe) sexe.getSelectedItem(), login.getText(), p, dn,
                        sal, pr, dr, sc,
                        emptyToNull(specialite.getText())
                ));
            } else {
                userService.createSecretaire(new CreateSecretaireRequest(
                        nom.getText(), email.getText(), adresse.getText(), cin.getText(), tel.getText(),
                        (Sexe) sexe.getSelectedItem(), login.getText(), p, dn,
                        sal, pr, dr, sc,
                        emptyToNull(numCNSS.getText()),
                        parseDoubleOrNull(commission.getText())
                ));
            }

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

    private static LocalDate parseDateOrNull(String s) {
        String x = emptyToNull(s);
        return (x == null) ? null : LocalDate.parse(x); // yyyy-MM-dd
    }

    private static Double parseDoubleOrNull(String s) {
        String x = emptyToNull(s);
        return (x == null) ? null : Double.parseDouble(x);
    }

    private static Integer parseIntOrNull(String s) {
        String x = emptyToNull(s);
        return (x == null) ? null : Integer.parseInt(x);
    }
}
