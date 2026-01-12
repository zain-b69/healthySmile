package ma.dentalTech.service.modules.auth.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest {
    private String login;      // ou email, selon ton choix
    private String password;
}
