package ma.dentalTech.mvc.controllers.modules.auth.swing_implementation;

import ma.dentalTech.entities.utilisateur.Utilisateur;
import ma.dentalTech.mvc.controllers.modules.auth.api.AuthController;
import ma.dentalTech.mvc.ui.common.MainAppFrame; // La frame principale de l'application
import ma.dentalTech.mvc.ui.modules.auth.LoginWindow; // La vue de connexion
import ma.dentalTech.service.modules.auth.api.AuthService;

import javax.swing.JOptionPane;

public class AuthControllerImpl implements AuthController {

    private final AuthService authService;
    private final LoginWindow loginWindow;

    public AuthControllerImpl(AuthService authService, LoginWindow loginWindow) {
        this.authService = authService;
        this.loginWindow = loginWindow;

        // Attacher l'écouteur du bouton à la méthode de gestion de tentative
        this.loginWindow.addLoginListener(e -> {
            String login = loginWindow.getLoginInput();
            String password = loginWindow.getPasswordInput();
            handleLoginAttempt(login, password);
        });
    }

    @Override
    public void startAuthenticationFlow() {
        // Dans l'implémentation Swing, ceci consiste à rendre la fenêtre visible
        loginWindow.setVisible(true);
    }

    @Override
    public void handleLoginAttempt(String login, String password) {
        try {
            Utilisateur utilisateur = authService.authentifier(login, password);

            // Connexion réussie : Fermeture de la fenêtre de login
            loginWindow.dispose();

            // Redirection (MainAppFrame est le Dashboard conteneur)
            redirectToUserSpace(utilisateur);

        } catch (SecurityException e) {
            // Échec d'authentification (Mot de passe incorrect, etc.)
            JOptionPane.showMessageDialog(loginWindow, e.getMessage(),
                    "Erreur d'Authentification", JOptionPane.ERROR_MESSAGE);
            loginWindow.clearPasswordInput();

        } catch (RuntimeException e) {
            // Erreur système
            JOptionPane.showMessageDialog(loginWindow, "Erreur système : " + e.getMessage(),
                    "Erreur Critique", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Logique de redirection vers le dashboard approprié
    private void redirectToUserSpace(Utilisateur utilisateur) {
        String role = utilisateur.getRole().getLibelle().name();

        MainAppFrame mainFrame = new MainAppFrame(utilisateur);

        switch (role) {
            case "MEDECIN":
                mainFrame.showMedecinDashboard();
                break;
            case "SECRETAIRE":
                mainFrame.showSecretaireDashboard();
                break;
            // ... autres rôles
        }
        mainFrame.setVisible(true);
    }

    @Override
    public void handleLogout() {
        // Fermer la fenêtre principale et rouvrir la fenêtre de login
        // ... (Logique non détaillée ici)
    }
}