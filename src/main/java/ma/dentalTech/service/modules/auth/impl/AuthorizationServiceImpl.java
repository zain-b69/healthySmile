package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.service.modules.auth.api.AuthorizationService;
import ma.dentalTech.service.modules.auth.dto.UserPrincipal;
import ma.dentalTech.service.modules.auth.exception.AuthorizationException;

import java.util.*;

/**
 * Impl simple:
 * - UserPrincipal contient 1 seul rôle (RoleType).
 * - Les privilèges ne sont pas en DB dans ton projet => mapping "in-memory".
 */
public class AuthorizationServiceImpl implements AuthorizationService {

    private final Map<RoleType, Set<String>> privilegesByRole;

    public AuthorizationServiceImpl() {
        this(defaultPrivileges());
    }

    public AuthorizationServiceImpl(Map<RoleType, Set<String>> privilegesByRole) {
        this.privilegesByRole = normalize(privilegesByRole);
    }

    @Override
    public boolean hasRole(UserPrincipal principal, RoleType role) {
        return principal != null && role != null && principal.getRole() == role;
    }

    @Override
    public boolean hasAnyRole(UserPrincipal principal, RoleType... roles) {
        if (principal == null || roles == null || roles.length == 0) return false;
        RoleType current = principal.getRole();
        if (current == null) return false;

        for (RoleType r : roles) {
            if (r != null && r == current) return true;
        }
        return false;
    }

    @Override
    public boolean hasPrivilege(UserPrincipal principal, String privilege) {
        if (principal == null) return false;
        if (privilege == null || privilege.trim().isEmpty()) return false;

        // ADMIN => tout autorisé
        if (principal.getRole() == RoleType.ADMIN) return true;

        // Si on passe "ROLE_ADMIN" / "ADMIN" etc, on le traite comme un rôle
        RoleType roleFromPrivilege = parseRoleFromPrivilege(privilege);
        if (roleFromPrivilege != null) {
            return hasRole(principal, roleFromPrivilege);
        }

        RoleType role = principal.getRole();
        if (role == null) return false;

        String p = normalizePrivilege(privilege);
        Set<String> allowed = privilegesByRole.getOrDefault(role, Collections.emptySet());

        // wildcard
        if (allowed.contains("*") || allowed.contains("ALL")) return true;

        return allowed.contains(p);
    }

    @Override
    public void checkRole(UserPrincipal principal, RoleType role) {
        if (!hasRole(principal, role)) {
            throw new AuthorizationException("FORBIDDEN_ROLE: required=" + role + ", actual=" + safeRole(principal));
        }
    }

    @Override
    public void checkAnyRole(UserPrincipal principal, RoleType... roles) {
        if (!hasAnyRole(principal, roles)) {
            throw new AuthorizationException("FORBIDDEN_ROLE_ANY: required=" + Arrays.toString(roles)
                    + ", actual=" + safeRole(principal));
        }
    }

    @Override
    public void checkPrivilege(UserPrincipal principal, String privilege) {
        if (!hasPrivilege(principal, privilege)) {
            throw new AuthorizationException("FORBIDDEN_PRIVILEGE: required=" + privilege
                    + ", actualRole=" + safeRole(principal));
        }
    }

    // ---------------- Helpers ----------------

    private static String safeRole(UserPrincipal principal) {
        return (principal == null || principal.getRole() == null) ? "NONE" : principal.getRole().name();
    }

    private static RoleType parseRoleFromPrivilege(String privilege) {
        String p = privilege.trim().toUpperCase(Locale.ROOT);
        if (p.startsWith("ROLE_")) p = p.substring("ROLE_".length());
        try {
            return RoleType.valueOf(p);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String normalizePrivilege(String privilege) {
        return privilege.trim().toUpperCase(Locale.ROOT);
    }

    private static Map<RoleType, Set<String>> normalize(Map<RoleType, Set<String>> raw) {
        Map<RoleType, Set<String>> out = new EnumMap<>(RoleType.class);
        if (raw == null) return out;

        for (var e : raw.entrySet()) {
            if (e.getKey() == null) continue;
            Set<String> normalized = new HashSet<>();
            if (e.getValue() != null) {
                for (String p : e.getValue()) {
                    if (p != null && !p.trim().isEmpty()) {
                        normalized.add(normalizePrivilege(p));
                    }
                }
            }
            out.put(e.getKey(), Collections.unmodifiableSet(normalized));
        }
        return Collections.unmodifiableMap(out);
    }

    /**
     * Tu peux modifier ici les privilèges par défaut selon ton cahier des charges.
     * Astuce: laisse ADMIN = "*" pour tout autoriser.
     */
    private static Map<RoleType, Set<String>> defaultPrivileges() {
        Map<RoleType, Set<String>> map = new EnumMap<>(RoleType.class);

        map.put(RoleType.ADMIN, Set.of("*"));

        map.put(RoleType.MEDECIN, Set.of(
                "PATIENT_READ", "PATIENT_WRITE",
                "RDV_READ", "RDV_WRITE",
                "CONSULTATION_READ", "CONSULTATION_WRITE",
                "DOSSIER_MEDICAL_READ", "DOSSIER_MEDICAL_WRITE"
        ));

        map.put(RoleType.SECRETAIRE, Set.of(
                "PATIENT_READ", "PATIENT_WRITE",
                "RDV_READ", "RDV_WRITE"
        ));

        return map;
    }
}
