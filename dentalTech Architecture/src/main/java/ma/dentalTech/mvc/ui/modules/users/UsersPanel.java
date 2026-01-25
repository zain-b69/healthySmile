package ma.dentalTech.mvc.ui.modules.users;

import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;
import ma.dentalTech.service.modules.auth.dto.UserPrincipal;
import ma.dentalTech.service.modules.users.api.PasswordManagementService;
import ma.dentalTech.service.modules.users.api.UserManagementService;
import ma.dentalTech.service.modules.users.dto.UpdateUserProfileRequest;
import ma.dentalTech.service.modules.users.dto.UserAccountDto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UsersPanel extends JPanel {

    private final UserPrincipal principal;
    private final UserManagementService userService;
    private final PasswordManagementService pwdService;

    private final JTextField searchField = new JTextField(22);
    private final RoundedButton searchBtn = new RoundedButton("Rechercher");
    private final RoundedButton refreshBtn = new RoundedButton("Rafraîchir");

    private final RoundedButton createBtn = new RoundedButton("Ajouter");
    private final RoundedButton editBtn = new RoundedButton("Modifier");
    private final RoundedButton roleBtn = new RoundedButton("Changer rôle");
    private final RoundedButton resetPwdBtn = new RoundedButton("Reset password");
    private final RoundedButton deleteBtn = new RoundedButton("Retirer");

    private final DefaultTableModel model;
    private final JTable table;

    private List<UserAccountDto> current = new ArrayList<>();

    public UsersPanel(UserPrincipal principal,
                      UserManagementService userService,
                      PasswordManagementService pwdService) {
        this.principal = principal;
        this.userService = userService;
        this.pwdService = pwdService;

        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        // Top bar
        add(buildTopBar(), BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new Object[]{
                "ID", "Nom", "Email", "Login", "Rôle", "Sexe", "Naissance"
        }, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Actions
        add(buildBottomBar(), BorderLayout.SOUTH);

        wireActions();
        reloadAll();
    }

    private JComponent buildTopBar() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);

        JLabel title = new JLabel("Utilisateurs");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        p.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        right.add(new JLabel("Mot-clé:"));
        right.add(searchField);
        ButtonStyles.primary(searchBtn);
        ButtonStyles.primary(refreshBtn);
        right.add(searchBtn);
        right.add(refreshBtn);

        p.add(right, BorderLayout.EAST);
        return p;
    }

    private JComponent buildBottomBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        p.setOpaque(false);

        ButtonStyles.primary(createBtn);
        ButtonStyles.primary(editBtn);
        ButtonStyles.primary(roleBtn);
        ButtonStyles.primary(deleteBtn);
        ButtonStyles.primary(resetPwdBtn);

        p.add(createBtn);
        p.add(editBtn);
        p.add(roleBtn);
        p.add(resetPwdBtn);
        p.add(deleteBtn);

        return p;
    }

    private void wireActions() {
        refreshBtn.addActionListener(e -> reloadAll());

        searchBtn.addActionListener(e -> {
            String kw = searchField.getText();
            if (kw == null || kw.isBlank()) reloadAll();
            else reloadSearch(kw.trim());
        });

        createBtn.addActionListener(e -> {
            CreateUserDialog dlg = new CreateUserDialog(SwingUtilities.getWindowAncestor(this), userService);
            dlg.setVisible(true);
            if (dlg.isSucceeded()) reloadAll();
        });

        editBtn.addActionListener(e -> {
            UserAccountDto u = getSelected();
            if (u == null) return;

            EditUserDialog dlg = new EditUserDialog(SwingUtilities.getWindowAncestor(this), u);
            dlg.setVisible(true);

            if (dlg.isSucceeded()) {
                UpdateUserProfileRequest req = dlg.getRequest();
                try {
                    userService.updateUserProfile(req);
                    reloadAll();
                } catch (Exception ex) {
                    showError("Erreur update", ex);
                }
            }
        });

        roleBtn.addActionListener(e -> {
            UserAccountDto u = getSelected();
            if (u == null) return;

            RoleType chosen = (RoleType) JOptionPane.showInputDialog(
                    this,
                    "Choisir le nouveau rôle:",
                    "Changer rôle",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    RoleType.values(),
                    inferRole(u)
            );

            if (chosen == null) return;

            try {
                userService.assignRoleToUser(u.id(), chosen);
                reloadAll();
            } catch (Exception ex) {
                showError("Erreur changement rôle", ex);
            }
        });

        resetPwdBtn.addActionListener(e -> {
            UserAccountDto u = getSelected();
            if (u == null) return;

            ResetPasswordDialog dlg = new ResetPasswordDialog(SwingUtilities.getWindowAncestor(this), u);
            dlg.setVisible(true);

            if (dlg.isSucceeded()) {
                try {
                    pwdService.resetPassword(u.id(), dlg.getNewPassword());
                    JOptionPane.showMessageDialog(this, "Mot de passe réinitialisé.", "OK", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    showError("Erreur reset password", ex);
                }
            }
        });
    }

    private void reloadAll() {
        try {
            current = userService.getAllUsers();
            fillTable(current);
        } catch (Exception ex) {
            showError("Erreur chargement utilisateurs", ex);
        }
    }

    private void reloadSearch(String kw) {
        try {
            current = userService.searchUsersByKeyword(kw);
            fillTable(current);
        } catch (Exception ex) {
            showError("Erreur recherche", ex);
        }
    }

    private void fillTable(List<UserAccountDto> list) {
        model.setRowCount(0);
        for (UserAccountDto u : list) {
            model.addRow(new Object[]{
                    u.id(),
                    u.nom(),
                    u.email(),
                    u.login(),
                    inferRole(u),
                    u.sexe(),
                    u.dateNaissance()
            });
        }
    }

    private UserAccountDto getSelected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= current.size()) {
            JOptionPane.showMessageDialog(this, "Sélectionne un utilisateur.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return current.get(row);
    }

    private RoleType inferRole(UserAccountDto u) {
        if (u.roles() == null || u.roles().isEmpty()) return null;
        return u.roles().iterator().next(); // ton système = 1 rôle
    }

    private void showError(String title, Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                title + ":\n" + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
