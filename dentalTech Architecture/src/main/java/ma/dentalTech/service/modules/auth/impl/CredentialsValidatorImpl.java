package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.service.modules.auth.api.CredentialsValidator;
import ma.dentalTech.service.modules.auth.dto.AuthRequest;

/**
 * Validation "fonctionnelle" avant accès repository.
 * Lève IllegalArgumentException si invalide.
 */
public class CredentialsValidatorImpl implements CredentialsValidator {

    private static final int MIN_LOGIN_LEN = 3;
    private static final int MIN_PASSWORD_LEN = 4;
    private static final int MIN_NEW_PASSWORD_LEN = 6;

    @Override
    public void validate(AuthRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("AuthRequest null");
        }

        String login = request.getLogin();
        String password = request.getPassword();

        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("Login obligatoire");
        }
        login = login.trim();

        if (login.length() < MIN_LOGIN_LEN) {
            throw new IllegalArgumentException("Login trop court (min " + MIN_LOGIN_LEN + ")");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Mot de passe obligatoire");
        }

        if (password.length() < MIN_PASSWORD_LEN) {
            throw new IllegalArgumentException("Mot de passe trop court (min " + MIN_PASSWORD_LEN + ")");
        }
    }

    @Override
    public void validateNewPassword(String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Nouveau mot de passe obligatoire");
        }

        String pwd = newPassword.trim();

        if (pwd.length() < MIN_NEW_PASSWORD_LEN) {
            throw new IllegalArgumentException("Nouveau mot de passe trop court (min " + MIN_NEW_PASSWORD_LEN + ")");
        }

        // Complexité simple: au moins 1 lettre + 1 chiffre (optionnel mais propre)
        boolean hasLetter = false;
        boolean hasDigit = false;

        for (int i = 0; i < pwd.length(); i++) {
            char c = pwd.charAt(i);
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c)) hasDigit = true;
        }

        if (!hasLetter || !hasDigit) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins une lettre et un chiffre");
        }
    }
}
