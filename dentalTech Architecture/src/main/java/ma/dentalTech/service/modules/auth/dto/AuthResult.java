package ma.dentalTech.service.modules.auth.dto;

import lombok.*;
import ma.dentalTech.entities.enums.RoleType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResult {

    private boolean success;
    private String message;

    // Infos utilisateur connecté
    private Long idUser;
    private String nom;
    private String email;
    private RoleType role;

    // Optionnel (utile pour audit / UI)
    private LocalDateTime authenticatedAt;

    // Optionnel: token si plus tard tu fais JWT/session
    private String token;

    // Optionnel: erreurs détaillées (validation, etc.)
    private Object errors;

    public static AuthResult ok(Long idUser, String nom, String email, RoleType role) {
        return AuthResult.builder()
                .success(true)
                .message("AUTH_OK")
                .idUser(idUser)
                .nom(nom)
                .email(email)
                .role(role)
                .authenticatedAt(LocalDateTime.now())
                .build();
    }

    public static AuthResult fail(String msg) {
        return AuthResult.builder()
                .success(false)
                .message(msg)
                .authenticatedAt(LocalDateTime.now())
                .build();
    }

    public static AuthResult failure(String message, Object errors) {
        return AuthResult.builder()
                .success(false)
                .message(message)
                .errors(errors)
                .authenticatedAt(LocalDateTime.now())
                .build();
    }
}
