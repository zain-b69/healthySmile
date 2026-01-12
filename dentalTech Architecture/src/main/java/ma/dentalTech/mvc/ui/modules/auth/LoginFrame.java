package ma.dentalTech.mvc.ui.modules.auth;

import ma.dentalTech.service.modules.auth.api.AuthService;
import ma.dentalTech.service.modules.auth.dto.AuthRequest;
import ma.dentalTech.service.modules.auth.dto.AuthResult;
import ma.dentalTech.mvc.ui.common.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.controllers.modules.auth.impl.AuthSession;
import ma.dentalTech.service.modules.auth.dto.UserPrincipal;


public class LoginFrame extends JFrame {

    private final AuthService authService;

    private final JTextField loginField = new RoundedTextField(22, "Email");
    private final JPasswordField passwordField = new RoundedPasswordField(22, "Password");
    private final JLabel statusLabel = new JLabel(" ");

    // Palette proche de l'image
    private static final Color BG_BLUE = new Color(30, 62, 94);
    private static final Color CARD_BG = new Color(225, 225, 225);
    private static final Color BTN_BLUE = new Color(20, 52, 84);
    private static final Color LINK_BLUE = new Color(60, 95, 130);

    public LoginFrame(AuthService authService) {
        super("HealthySmile - Login");
        this.authService = authService;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_BLUE);

        // Left branding panel
        JPanel left = new BrandingPanel();
        left.setPreferredSize(new Dimension(480, 560));
        root.add(left, BorderLayout.WEST);

        // Right login area
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(BG_BLUE);

        RoundedPanel card = new RoundedPanel(28, CARD_BG);
        card.setPreferredSize(new Dimension(420, 420));
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(30, 34, 30, 34));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel title = new JLabel("Log in");
        title.setFont(new Font("SansSerif", Font.BOLD, 40));
        title.setForeground(BTN_BLUE);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        card.add(title, gbc);

        // Labels + fields
        JLabel emailLabel = smallLabel("Email");
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 6, 0);
        card.add(emailLabel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 14, 0);
        card.add(loginField, gbc);

        JLabel passLabel = smallLabel("Password");
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 6, 0);
        card.add(passLabel, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 14, 0);
        card.add(passwordField, gbc);

        // Forgot password link (right aligned)
//        JPanel linkRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
//        linkRow.setOpaque(false);
//        JLabel forgot = linkLabel("Forgot password?");
//        linkRow.add(forgot);
//
//        gbc.gridy = 5;
//        gbc.insets = new Insets(0, 0, 14, 0);
//        card.add(linkRow, gbc);

        // Sign in button (full width)
        JButton signInBtn = new JButton("Sign in");
        signInBtn.setFocusPainted(false);
        signInBtn.setBorderPainted(false);
        signInBtn.setOpaque(true);
        signInBtn.setBackground(BTN_BLUE);
        signInBtn.setForeground(Color.WHITE);
        signInBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        signInBtn.setPreferredSize(new Dimension(10, 44)); // largeur via fill H

        gbc.gridy = 6;
        gbc.insets = new Insets(2, 0, 6, 0);
        card.add(signInBtn, gbc);

        // Status label
        statusLabel.setForeground(new Color(160, 0, 0));
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridy = 7;
        gbc.insets = new Insets(6, 0, 0, 0);
        card.add(statusLabel, gbc);

        // Center card inside right panel
        GridBagConstraints rc = new GridBagConstraints();
        rc.gridx = 0; rc.gridy = 0;
        right.add(card, rc);

        root.add(right, BorderLayout.CENTER);
        setContentPane(root);

        // Actions
        signInBtn.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());

//        forgot.addMouseListener(new MouseAdapter() {
//            @Override public void mouseClicked(MouseEvent e) {
//                JOptionPane.showMessageDialog(
//                        LoginFrame.this,
//                        "Fonction 'Forgot password' à implémenter.",
//                        "Info",
//                        JOptionPane.INFORMATION_MESSAGE
//                );
//            }
//        });
    }

    private JLabel smallLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(60, 60, 60));
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return l;
    }

    private JLabel linkLabel(String text) {
        JLabel l = new JLabel("<html><u>" + text + "</u></html>");
        l.setForeground(LINK_BLUE);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return l;
    }

    private void doLogin() {
        String login = loginField.getText().trim();
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

        // TODO: ouvrir dashboard
        UserPrincipal principal = authService.loadUserPrincipalByLogin(login);
        AuthSession.setCurrentUser(principal);

        ApplicationContext.getBean(DashboardController.class).showDashboard(principal);
        dispose();

    }

    // ---------------- UI Components ----------------

    /** Panel de gauche (logo + slogan) */
    static class BrandingPanel extends JPanel {
        BrandingPanel() {
            setBackground(BG_BLUE);
            setBorder(new EmptyBorder(30, 30, 30, 30));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            // Logo (SVG)
            JLabel logo = new JLabel(SvgIcon.loadBetter("/assets/logo.svg", 360, 260));
            logo.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Slogan
            JLabel slogan = new JLabel("Healthy Smiles, Happy Patients");
            slogan.setForeground(new Color(235, 235, 235));
            slogan.setFont(new Font("SansSerif", Font.PLAIN, 18));
            slogan.setAlignmentX(Component.CENTER_ALIGNMENT);

            add(Box.createVerticalGlue());
            add(logo);
            add(Box.createRigidArea(new Dimension(0, 18)));
            add(slogan);
            add(Box.createVerticalGlue());
        }
    }


    /** Panel arrondi (carte) */
    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;

        RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w, h, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** TextField arrondi + placeholder */
    static class RoundedTextField extends JTextField {
        private final int radius = 14;
        private final String placeholder;

        RoundedTextField(int columns, String placeholder) {
            super(columns);
            this.placeholder = placeholder;
            setOpaque(false);
            setBorder(new EmptyBorder(12, 14, 12, 14));
            setFont(new Font("SansSerif", Font.PLAIN, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            super.paintComponent(g);

            if (getText().isEmpty() && !isFocusOwner()) {
                g2.setColor(new Color(160, 160, 160));
                g2.setFont(getFont());
                Insets ins = getInsets();
                g2.drawString(placeholder, ins.left, getHeight() / 2 + 5);
            }

            g2.dispose();
        }
    }

    /** PasswordField arrondi + placeholder */
    static class RoundedPasswordField extends JPasswordField {
        private final int radius = 14;
        private final String placeholder;

        RoundedPasswordField(int columns, String placeholder) {
            super(columns);
            this.placeholder = placeholder;
            setOpaque(false);
            setBorder(new EmptyBorder(12, 14, 12, 14));
            setFont(new Font("SansSerif", Font.PLAIN, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            super.paintComponent(g);

            if (getPassword().length == 0 && !isFocusOwner()) {
                g2.setColor(new Color(160, 160, 160));
                g2.setFont(getFont());
                Insets ins = getInsets();
                g2.drawString(placeholder, ins.left, getHeight() / 2 + 5);
            }

            g2.dispose();
        }
    }

    // Exemple de lancement:
    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(() -> new LoginFrame(new AuthServiceImpl()).setVisible(true));
    // }
}
