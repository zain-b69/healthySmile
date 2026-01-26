package ma.dentalTech.mvc.controllers.modules.auth.impl.swing;

import ma.dentalTech.mvc.controllers.modules.auth.api.AuthController;
import ma.dentalTech.mvc.ui.modules.auth.LoginFrame;
import ma.dentalTech.service.modules.auth.api.AuthService;

import javax.swing.*;

public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    public AuthControllerImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void showLogin() {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame(authService);
            frame.setVisible(true);
        });
    }

    @Override
    public void showSignUp() {
        // Optionnel: fenêtre d’inscription (si tu veux la faire plus tard)
        JOptionPane.showMessageDialog(null, "SignUp non implémenté pour le moment.");
    }

    @Override
    public void showSignIn() {
        showLogin();
    }
}
