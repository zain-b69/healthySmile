package ma.dentalTech.mvc.controllers.modules.auth.batch_implementation;

import ma.dentalTech.entities.utilisateur.Utilisateur;
import ma.dentalTech.mvc.controllers.modules.auth.api.AuthController;
import ma.dentalTech.service.modules.auth.api.AuthService;

import java.util.Scanner;

public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    public AuthControllerImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void startAuthenticationFlow() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Authentification Console ===");
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Mot de passe: ");
        String password = scanner.nextLine();

        handleLoginAttempt(login, password);
    }

    @Override
    public void handleLoginAttempt(String login, String password) {
        try {
            Utilisateur user = authService.authentifier(login, password);
            System.out.printf("✅ Connexion réussie. Bienvenue %s (%s).%n",
                    user.getUsername(),user.getRole());

            // Logique de redirection batch (simplement afficher le rôle)
            System.out.println("Redirection vers le Dashboard " + user.getRole());

        } catch (SecurityException e) {
            System.err.println("❌ Échec de la connexion : " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("❌ Erreur système lors de la connexion. Consultez les logs.");
        }
    }

    @Override
    public void handleLogout() {
        System.out.println("Déconnexion de l'utilisateur Batch.");
        // Logique de nettoyage de session (si vous utilisiez une session)
    }
}