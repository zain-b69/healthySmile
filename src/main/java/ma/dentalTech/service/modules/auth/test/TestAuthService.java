package ma.dentalTech.service.modules.auth.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.service.modules.auth.api.AuthService;
import ma.dentalTech.service.modules.auth.dto.AuthRequest;
import ma.dentalTech.service.modules.auth.dto.AuthResult;

public class TestAuthService {

    public static void main(String[] args) {
        AuthService authService = ApplicationContext.getBean(AuthService.class);

        // 1) AUTH OK
        AuthResult ok = authService.authenticate(
                AuthRequest.builder()
                        .login("admin")       // ou email "admin@gmail.com"
                        .password("admin123") // mets le vrai mot de passe de ton user seedé BCrypt
                        .build()
        );

        System.out.println("---- AUTH OK TEST ----");
        System.out.println("success=" + ok.isSuccess());
        System.out.println("message=" + ok.getMessage());
        System.out.println("idUser=" + ok.getIdUser());
        System.out.println("nom=" + ok.getNom());
        System.out.println("email=" + ok.getEmail());
        System.out.println("role=" + ok.getRole());
        System.out.println("authenticatedAt=" + ok.getAuthenticatedAt());
        System.out.println();

        if (!ok.isSuccess()) {
            System.out.println("STOP: Auth failed, check seed user + BCrypt password.");
            return;
        }

        // 2) AUTH FAIL (wrong password)
        AuthResult bad = authService.authenticate(
                AuthRequest.builder()
                        .login("admin")
                        .password("wrong_password")
                        .build()
        );

        System.out.println("---- AUTH FAIL TEST ----");
        System.out.println("success=" + bad.isSuccess());
        System.out.println("message=" + bad.getMessage());
        System.out.println();

        // 3) LOAD PRINCIPAL
        System.out.println("---- LOAD PRINCIPAL TEST ----");
        var principal = authService.loadUserPrincipalByLogin("admin");
        System.out.println(principal);
        System.out.println();
    }
}
