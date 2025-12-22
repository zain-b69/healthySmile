package ma.dentalTech.mvc.controllers.modules.auth.impl.console;

import ma.dentalTech.mvc.controllers.modules.auth.api.AuthController;
import ma.dentalTech.mvc.controllers.modules.auth.impl.AuthSession;
import ma.dentalTech.service.modules.auth.api.AuthService;
import ma.dentalTech.service.modules.auth.dto.AuthRequest;
import ma.dentalTech.service.modules.auth.dto.AuthResult;
import ma.dentalTech.service.modules.auth.dto.UserPrincipal;

import java.util.Scanner;

public class AuthControllerImpl implements AuthController {

    private final AuthService authService;
    private final Scanner scanner = new Scanner(System.in);

    public AuthControllerImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void showLogin() {
        while (true) {
            System.out.println("\n==== AUTH MENU ====");
            System.out.println("1) Sign in");
            System.out.println("2) Sign up");
            System.out.println("0) Quit");
            System.out.print("Choix: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showSignIn();
                case "2" -> showSignUp();
                case "0" -> {
                    System.out.println("Au revoir.");
                    return;
                }
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    @Override
    public void showSignUp() {
        // IMPORTANT: dans ton projet, créer un user exige rôle (FK id_role NOT NULL)
        // + d'autres champs. Comme tu n'as pas un RegistrationService prêt ici,
        // on laisse un message clair (prof-friendly) et tu peux brancher plus tard.
        System.out.println("\n---- SIGN UP ----");
        System.out.println("Inscription non implémentée pour le moment.");
        System.out.println("👉 Utilise SeedUser / script admin pour créer un utilisateur (BCrypt).");
    }

    @Override
    public void showSignIn() {
        System.out.println("\n---- SIGN IN ----");
        System.out.print("Login1: ");
        String login = scanner.nextLine();

        System.out.print("Mot de passe: ");
        String password = scanner.nextLine();

        AuthResult res = authService.authenticate(
                AuthRequest.builder()
                        .login(login)
                        .password(password)
                        .build()
        );

        if (!res.isSuccess()) {
            System.out.println("❌ Auth échouée: " + res.getMessage());
            return;
        }

        System.out.println("✅ Auth OK: " + res.getNom() + " (" + res.getRole() + ")");

        // Optionnel: garder un "current user" en mémoire
        UserPrincipal principal = new UserPrincipal(
                res.getIdUser(),
                login,              // on garde ce que l'utilisateur a tapé
                res.getEmail(),
                res.getNom(),
                res.getRole()
        );

        AuthSession.setCurrentUser(principal);
        System.out.println("Session user = " + AuthSession.getCurrentUser());
    }
}
