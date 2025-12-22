package ma.dentalTech.mvc.ui.modules.auth;

import ma.dentalTech.service.modules.auth.api.AuthService;
import ma.dentalTech.service.modules.auth.dto.AuthRequest;
import ma.dentalTech.service.modules.auth.dto.AuthResult;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final AuthService authService;

    private final JTextField loginField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JLabel statusLabel = new JLabel(" ");

    public LoginFrame(AuthService authService) {
        super("HealthySmile - Login");
        this.authService = authService;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 240);
        setLocationRelativeTo(null); // center

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Connexion");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        // Login
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Login:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(loginField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Mot de passe:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(passwordField, gbc);

        // Buttons
        JButton signInBtn = new JButton("Sign in");
        JButton quitBtn = new JButton("Quit");

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(quitBtn);
        btns.add(signInBtn);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(btns, gbc);

        // Status label
        statusLabel.setForeground(new Color(180, 0, 0));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panel.add(statusLabel, gbc);

        // Actions
        signInBtn.addActionListener(e -> doLogin());
        quitBtn.addActionListener(e -> System.exit(0));

        // Enter triggers login
        passwordField.addActionListener(e -> doLogin());

        setContentPane(panel);
    }

    private void doLogin() {
        String login = loginField.getText();
        String password = new String(passwordField.getPassword());

        AuthResult res = authService.authenticate(
                AuthRequest.builder()
                        .login(login)
                        .password(password)
                        .build()
        );

        if (!res.isSuccess()) {
            statusLabel.setText("❌ " + res.getMessage());
            return;
        }

        statusLabel.setText(" ");
        JOptionPane.showMessageDialog(
                this,
                "✅ Auth OK\nNom: " + res.getNom() + "\nRole: " + res.getRole(),
                "Succès",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Ici tu peux ouvrir l'écran suivant (dashboard) si tu veux
        // dispose();
        // new DashboardFrame(...).setVisible(true);
    }

}
