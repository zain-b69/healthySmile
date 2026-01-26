package ma.dentalTech.service.modules.auth.api;

import ma.dentalTech.service.modules.auth.dto.*;

public interface AuthService {

    /**
     * Authentifie un utilisateur par login/mot de passe.
     */
    AuthResult authenticate(AuthRequest request);

    /**
     * Charge un utilisateur (principal) par login sans authentifier le mot de passe.
     */
    UserPrincipal loadUserPrincipalByLogin(String login);

    /**
     * Change le mot de passe d'un utilisateur.
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
}
