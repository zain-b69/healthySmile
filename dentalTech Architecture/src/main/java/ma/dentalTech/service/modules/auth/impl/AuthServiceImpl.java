package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import ma.dentalTech.service.modules.auth.api.AuthService;
import ma.dentalTech.service.modules.auth.api.CredentialsValidator;
import ma.dentalTech.service.modules.auth.api.PasswordEncoder;
import ma.dentalTech.service.modules.auth.dto.AuthRequest;
import ma.dentalTech.service.modules.auth.dto.AuthResult;
import ma.dentalTech.service.modules.auth.dto.UserPrincipal;

import java.util.Optional;

public class AuthServiceImpl implements AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final CredentialsValidator credentialsValidator;

    public AuthServiceImpl(UtilisateurRepository utilisateurRepository,
                           PasswordEncoder passwordEncoder,
                           CredentialsValidator credentialsValidator) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.credentialsValidator = credentialsValidator;
    }

    @Override
    public AuthResult authenticate(AuthRequest request) {
        try {
            credentialsValidator.validate(request);

            String login = request.getLogin().trim();

            // 1) find user by login OR email
            Optional<Utilisateur> optUser = utilisateurRepository.findByLogin(login);
            if (optUser.isEmpty()) {
                optUser = utilisateurRepository.findByEmail(login);
            }
            if (optUser.isEmpty()) {
                return AuthResult.fail("USER_NOT_FOUND");
            }

            Utilisateur user = optUser.get();

            // 2) check password
            if (!passwordEncoder.matches(request.getPassword(), user.getMotDePasse())) {
                return AuthResult.fail("BAD_CREDENTIALS");
            }

            // 3) role
            RoleType roleType = resolveRoleType(user);

            // 4) success
            return AuthResult.ok(
                    user.getIdUser(),
                    user.getNom(),
                    user.getEmail(),
                    roleType
            );

        } catch (IllegalArgumentException e) {
            return AuthResult.fail("INVALID_INPUT: " + e.getMessage());
        } catch (Exception e) {
            return AuthResult.fail("AUTH_ERROR: " + e.getMessage());
        }
    }

    @Override
    public UserPrincipal loadUserPrincipalByLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new IllegalArgumentException("login obligatoire");
        }

        Utilisateur user = utilisateurRepository.findByLogin(login.trim())
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

        RoleType roleType = resolveRoleType(user);

        return new UserPrincipal(
                user.getIdUser(),
                user.getLogin(),
                user.getEmail(),
                user.getNom(),
                roleType
        );
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null) throw new IllegalArgumentException("userId obligatoire");
        if (oldPassword == null || oldPassword.isBlank()) {
            throw new IllegalArgumentException("Ancien mot de passe obligatoire");
        }

        credentialsValidator.validateNewPassword(newPassword);

        // CrudRepository.findById retourne T (pas Optional)
        Utilisateur user = utilisateurRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("USER_NOT_FOUND");
        }

        if (!passwordEncoder.matches(oldPassword, user.getMotDePasse())) {
            throw new IllegalArgumentException("BAD_OLD_PASSWORD");
        }

        user.setMotDePasse(passwordEncoder.encode(newPassword));

        // CrudRepository.update(T newValuesElement)
        utilisateurRepository.update(user);
    }

    // ---------------- helpers ----------------

    private RoleType resolveRoleType(Utilisateur user) {
        // si user.role est déjà rempli, on le prend direct
        if (user.getRole() != null && user.getRole().getType() != null) {
            return user.getRole().getType();
        }

        // sinon on va en DB via repo
        Optional<Role> optRole = utilisateurRepository.findRoleByUserId(user.getIdUser());
        return optRole.map(Role::getType).orElse(null);
    }

    private static final boolean DEBUG_AUTH = true;

    private static void logAuth(String msg) {
        if (DEBUG_AUTH) System.out.println("[AUTH] " + msg);
    }

}
