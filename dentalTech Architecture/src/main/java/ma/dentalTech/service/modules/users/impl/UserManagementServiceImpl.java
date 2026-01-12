package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import ma.dentalTech.service.modules.auth.api.PasswordEncoder;
import ma.dentalTech.service.modules.users.api.UserManagementService;
import ma.dentalTech.service.modules.users.dto.*;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserManagementServiceImpl implements UserManagementService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementServiceImpl(UtilisateurRepository utilisateurRepository,
                                     PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // -------------------- CREATE --------------------

    @Override
    public UserAccountDto createAdmin(CreateAdminRequest req) {
        return createUserCommon(
                req.nom(),
                req.email(),
                req.adresse(),
                req.cin(),
                req.tel(),
                req.sexe(),
                req.login(),
                req.motDePasse(),
                req.dateNaissance(),
                RoleType.ADMIN
        );
    }

    @Override
    public UserAccountDto createMedecin(CreateMedecinRequest req) {
        return createUserCommon(
                req.nom(),
                req.email(),
                req.adresse(),
                req.cin(),
                req.tel(),
                req.sexe(),
                req.login(),
                req.motDePasse(),
                req.dateNaissance(),
                RoleType.MEDECIN
        );
    }

    @Override
    public UserAccountDto createSecretaire(CreateSecretaireRequest req) {
        return createUserCommon(
                req.nom(),
                req.email(),
                req.adresse(),
                req.cin(),
                req.tel(),
                req.sexe(),
                req.login(),
                req.motDePasse(),
                req.dateNaissance(),
                RoleType.SECRETAIRE
        );
    }

    private UserAccountDto createUserCommon(
            String nom,
            String email,
            String adresse,
            String cin,
            String tel,
            ma.dentalTech.entities.enums.Sexe sexe,
            String login,
            String rawPassword,
            java.time.LocalDate dateNaissance,
            RoleType roleType
    ) {
        requireNotBlank(nom, "nom obligatoire");
        requireNotBlank(email, "email obligatoire");
        requireNotBlank(login, "login obligatoire");
        requireNotBlank(rawPassword, "motDePasse obligatoire");

        if (utilisateurRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("EMAIL_ALREADY_USED");
        }
        if (utilisateurRepository.existsByLogin(login)) {
            throw new IllegalArgumentException("LOGIN_ALREADY_USED");
        }

        String hashed = passwordEncoder.encode(rawPassword);

        long idRole = resolveRoleId(roleType);

        Utilisateur u = Utilisateur.builder()
                .nom(nom)
                .email(email)
                .adresse(adresse)
                .cin(cin)
                .tel(tel)
                .sexe(sexe)
                .login(login)
                .motDePasse(hashed)
                .dateNaissance(dateNaissance)
                .role(Role.builder().idRole(idRole).type(roleType).build())
                .build();

        try {
            utilisateurRepository.create(u);
        } catch (SQLException e) {
            throw new RuntimeException("DB_ERROR_CREATE_USER", e);
        }

        // Si ton repo ne set pas u.idUser via keys, recharge
        if (u.getIdUser() == null) {
            u = utilisateurRepository.findByLogin(login)
                    .orElseThrow(() -> new IllegalStateException("USER_CREATED_BUT_NOT_FOUND"));
        }

        return toAccountDto(u, roleType);
    }

    // -------------------- READ --------------------

    @Override
    public UserAccountDto getUserById(Long id) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");

        Utilisateur u = utilisateurRepository.findById(id);
        if (u == null) throw new IllegalArgumentException("USER_NOT_FOUND");

        RoleType roleType = resolveRoleType(u);
        return toAccountDto(u, roleType);
    }

    @Override
    public List<UserAccountDto> getAllUsers() {
        return utilisateurRepository.findAll().stream()
                .map(u -> toAccountDto(u, resolveRoleType(u)))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserAccountDto> searchUsersByKeyword(String keyword) {
        return utilisateurRepository.searchByNom(keyword).stream()
                .map(u -> toAccountDto(u, resolveRoleType(u)))
                .collect(Collectors.toList());
    }

    // -------------------- UPDATE --------------------

    @Override
    public UserAccountDto updateUserProfile(UpdateUserProfileRequest req) {
        if (req == null) throw new IllegalArgumentException("request obligatoire");
        if (req.id() == null) throw new IllegalArgumentException("id obligatoire");

        Utilisateur u = utilisateurRepository.findById(req.id());
        if (u == null) throw new IllegalArgumentException("USER_NOT_FOUND");

        if (notBlank(req.nom())) u.setNom(req.nom());
        if (notBlank(req.adresse())) u.setAdresse(req.adresse());
        if (notBlank(req.tel())) u.setTel(req.tel());
        if (req.sexe() != null) u.setSexe(req.sexe());
        if (req.dateNaissance() != null) u.setDateNaissance(req.dateNaissance());

        if (notBlank(req.email()) && !req.email().equalsIgnoreCase(u.getEmail())) {
            if (utilisateurRepository.existsByEmail(req.email())) {
                throw new IllegalArgumentException("EMAIL_ALREADY_USED");
            }
            u.setEmail(req.email());
        }

        utilisateurRepository.update(u);

        RoleType roleType = resolveRoleType(u);
        return toAccountDto(u, roleType);
    }

    @Override
    public void assignRoleToUser(Long utilisateurId, RoleType roleType) {
        if (utilisateurId == null) throw new IllegalArgumentException("utilisateurId obligatoire");
        if (roleType == null) throw new IllegalArgumentException("roleType obligatoire");

        // Vérifie que l'utilisateur existe
        Utilisateur u = utilisateurRepository.findById(utilisateurId);
        if (u == null) throw new IllegalArgumentException("USER_NOT_FOUND");

        long idRole = resolveRoleId(roleType);

        // DB: 1 seul rôle => update direct
        utilisateurRepository.updateRole(utilisateurId, idRole);

        // Optionnel: mettre à jour l'objet en mémoire si besoin
        if (u.getRole() == null) u.setRole(Role.builder().build());
        u.getRole().setIdRole(idRole);
        u.getRole().setType(roleType);
    }

    @Override
    public void removeRoleFromUser(Long utilisateurId, RoleType roleType) {
        if (utilisateurId == null) throw new IllegalArgumentException("utilisateurId obligatoire");
        if (roleType == null) throw new IllegalArgumentException("roleType obligatoire");

        Utilisateur u = utilisateurRepository.findById(utilisateurId);
        if (u == null) throw new IllegalArgumentException("USER_NOT_FOUND");

        // On récupère le rôle actuel
        RoleType current = resolveRoleType(u);

        // Si le rôle à "retirer" n'est pas celui actuel, rien à faire
        if (current == null || current != roleType) {
            return; // ou throw new IllegalArgumentException("ROLE_NOT_ASSIGNED");
        }

        // Comme utilisateur.id_role est NOT NULL, on doit mettre un rôle par défaut
        // Choisis le défaut selon tes règles métier (souvent SECRETAIRE ou PATIENT)
        RoleType defaultRole = RoleType.SECRETAIRE;

        long idRoleDefault = resolveRoleId(defaultRole);
        utilisateurRepository.updateRole(utilisateurId, idRoleDefault);

        // Optionnel: mise à jour objet
        if (u.getRole() == null) u.setRole(Role.builder().build());
        u.getRole().setIdRole(idRoleDefault);
        u.getRole().setType(defaultRole);
    }


    // -------------------- mapping helpers --------------------

    private UserAccountDto toAccountDto(Utilisateur u, RoleType roleType) {
        // roles = singleton du rôle (ton système stocke 1 seul rôle via id_role)
        Set<RoleType> roles = (roleType != null) ? Set.of(roleType) : Collections.emptySet();

        // privileges: tu n'as pas encore de modèle/privilèges en repo, donc vide
        Set<String> privileges = Collections.emptySet();

        return new UserAccountDto(
                u.getIdUser(),
                u.getNom(),
                u.getEmail(),
                u.getLogin(),
                u.getSexe(),
                u.getDateNaissance(),
                roles,
                privileges
        );
    }

    private RoleType resolveRoleType(Utilisateur u) {
        if (u.getRole() != null && u.getRole().getType() != null) {
            return u.getRole().getType();
        }
        return utilisateurRepository.findRoleByUserId(u.getIdUser())
                .map(Role::getType)
                .orElse(null);
    }

    /**
     * ⚠️ Ajuste selon ta table ROLE (id_role).
     * Si tes IDs sont différents, change ici.
     */
    private long resolveRoleId(RoleType roleType) {
        return switch (roleType) {
            case ADMIN -> 5L;
            case MEDECIN -> 14L;
            case SECRETAIRE -> 15L;
        };
    }

    private static void requireNotBlank(String s, String msg) {
        if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException(msg);
    }

    public static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
