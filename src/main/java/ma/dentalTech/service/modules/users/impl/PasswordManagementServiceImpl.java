package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import ma.dentalTech.service.modules.auth.api.PasswordEncoder;
import ma.dentalTech.service.modules.auth.api.CredentialsValidator;
import ma.dentalTech.service.modules.users.api.PasswordManagementService;

public class PasswordManagementServiceImpl implements PasswordManagementService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final CredentialsValidator credentialsValidator;

    public PasswordManagementServiceImpl(UtilisateurRepository utilisateurRepository,
                                         PasswordEncoder passwordEncoder,
                                         CredentialsValidator credentialsValidator) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.credentialsValidator = credentialsValidator;
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null) throw new IllegalArgumentException("userId obligatoire");
        if (oldPassword == null || oldPassword.isBlank()) throw new IllegalArgumentException("oldPassword obligatoire");

        credentialsValidator.validateNewPassword(newPassword);

        Utilisateur u = utilisateurRepository.findById(userId);
        if (u == null) throw new IllegalArgumentException("USER_NOT_FOUND");

        String hashed = u.getMotDePasse();
        if (hashed == null || hashed.isBlank()) throw new IllegalStateException("USER_PASSWORD_NOT_SET");

        if (!passwordEncoder.matches(oldPassword, hashed)) {
            throw new IllegalArgumentException("OLD_PASSWORD_INCORRECT");
        }

        String newHashed = passwordEncoder.encode(newPassword);

        // ✅ meilleur: méthode dédiée repo
        utilisateurRepository.updatePassword(userId, newHashed);
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        if (userId == null) throw new IllegalArgumentException("userId obligatoire");

        credentialsValidator.validateNewPassword(newPassword);

        Utilisateur u = utilisateurRepository.findById(userId);
        if (u == null) throw new IllegalArgumentException("USER_NOT_FOUND");

        String newHashed = passwordEncoder.encode(newPassword);
        utilisateurRepository.updatePassword(userId, newHashed);
    }
}
