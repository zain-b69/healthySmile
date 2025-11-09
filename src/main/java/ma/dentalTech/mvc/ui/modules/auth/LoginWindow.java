package ma.dentalTech.mvc.ui.modules.auth;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Fenêtre de connexion (Login Window).
 * Elle sert de vue et de source d'événements pour l'AuthController.
 */
public class LoginWindow extends JFrame {

    // --- Composants UI ---
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginWindow() {
        super("DentalSoft - Authentification");
        initializeFrame();
        buildUI();
    }

    private void initializeFrame() {
        // Configuration de base de la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setResizable(false);
        setLocationRelativeTo(null); // Centrer l'application à l'écran
    }

    private void buildUI() {
        // Utilisation de GridBagLayout pour un formulaire structuré
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();

        // Style pour l'application (peut être remplacé par un thème FlatLaf)
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        // --- Titre ---
        JLabel titleLabel = new JLabel("Connexion au Système");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 25, 0); // Espace en bas
        panel.add(titleLabel, gbc);

        // --- 1. Champ Login ---
        JLabel loginLabel = new JLabel("Login (Email) :");
        loginLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 5, 10);
        panel.add(loginLabel, gbc);

        loginField = new JTextField(15);
        loginField.setFont(fieldFont);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(loginField, gbc);

        // --- 2. Champ Mot de Passe ---
        JLabel passwordLabel = new JLabel("Mot de passe :");
        passwordLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 15, 10);
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(fieldFont);
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(passwordField, gbc);

        // --- 3. Bouton de Connexion ---
        loginButton = new JButton("Se Connecter");
        loginButton.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 0, 0, 0);
        panel.add(loginButton, gbc);

        // Ajouter le panel au JFrame
        add(panel);
        pack(); // Ajuste la taille de la fenêtre aux composants
    }

    // -------------------------------------------------------------------------
    // Méthodes pour le Controller (pour lire les données)
    // -------------------------------------------------------------------------

    /** Récupère le texte du champ login. */
    public String getLoginInput() {
        return loginField.getText().trim();
    }

    /** Récupère le texte du champ mot de passe. */
    public String getPasswordInput() {
        return new String(passwordField.getPassword());
    }

    /** Vide le champ mot de passe après une tentative ratée (sécurité). */
    public void clearPasswordInput() {
        passwordField.setText("");
    }

    // -------------------------------------------------------------------------
    // Méthode pour le Controller (pour injecter l'événement)
    // -------------------------------------------------------------------------

    /**
     * Permet à l'AuthController d'attacher un écouteur au bouton de connexion.
     * C'est le cœur du pattern MVC.
     */
    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
        // Ajout d'un écouteur sur la touche Entrée pour plus de confort
        passwordField.addActionListener(listener);
    }
}