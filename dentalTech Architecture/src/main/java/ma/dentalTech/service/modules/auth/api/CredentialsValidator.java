package ma.dentalTech.service.modules.auth.api;

import ma.dentalTech.service.modules.auth.dto.*;

/**
 * Validation “fonctionnelle” des credentials AVANT d’aller au repository (login/password non vides, longueur min, etc.)
 */
public interface CredentialsValidator {

    /**
     * Valide les données de login (format / non null / longueur...).
     * Lève IllegalArgumentException si invalide.
     */
    void validate(AuthRequest request);

    /**
     * Valide les règles de complexité d'un nouveau mot de passe.
     */
    void validateNewPassword(String newPassword);
}
