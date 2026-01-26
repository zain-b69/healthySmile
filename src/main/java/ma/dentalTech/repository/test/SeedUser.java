package ma.dentalTech.repository.test;

import ma.dentalTech.configuration.SessionFactory;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDate;

public class SeedUser {

    public static void main(String[] args) throws Exception {
        var sf = SessionFactory.getInstance();

        try (Connection cn = sf.getConnection()) {
            cn.setAutoCommit(false);

            String roleLibelle = "ADMIN";              // ADMIN / MEDECIN / SECRETAIRE / PATIENT
            String nom = "Admin 2";
            String baseEmail = "admin2@test.com";
            String baseLogin = "admin2";
            String plainPassword = "admin123";

//            String roleLibelle = "MEDECIN";
//            String nom = "Dr Medecin Test";
//            String baseEmail = "med1@test.com";
//            String baseLogin = "med1";
//            String plainPassword = "medecin123";

            try {
                Long roleId = findRoleIdByLibelle(cn, roleLibelle);
                if (roleId == null) {
                    throw new IllegalStateException("Role '" + roleLibelle + "' introuvable. Insère d'abord les rôles.");
                }

                String email = makeUniqueEmail(cn, baseEmail);
                String login = makeUniqueLogin(cn, baseLogin);

                Long entiteId = insertEntite(cn, "seed", "seed");

                String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));

                long userId = insertUtilisateur(
                        cn,
                        entiteId,
                        roleId,
                        nom,
                        email,
                        "Rabat",
                        "AA123456",
                        "0600000000",
                        "MASCULIN",            // DOIT être MASCULIN ou FEMININ (comme ta table)
                        login,
                        hashed,
                        LocalDate.now(),
                        LocalDate.of(1999, 1, 1)
                );

                cn.commit();
                System.out.println("✅ Utilisateur inséré !");
                System.out.println("   id_user=" + userId);
                System.out.println("   email=" + email);
                System.out.println("   login=" + login);
                System.out.println("   mdp clair=" + plainPassword);
                System.out.println("   mdp hash=" + hashed);

                // Vérification bcrypt (optionnel)
                System.out.println("✅ BCrypt check = " + BCrypt.checkpw(plainPassword, hashed));

            } catch (Exception e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }

    private static Long findRoleIdByLibelle(Connection cn, String libelle) throws SQLException {
        String sql = "SELECT id_role FROM role WHERE libelle = ? LIMIT 1";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, libelle);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : null;
            }
        }
    }

    private static Long insertEntite(Connection cn, String creePar, String modifiePar) throws SQLException {
        String sql = """
            INSERT INTO entite (date_creation, date_derniere_modification, cree_par, modifie_par)
            VALUES (CURRENT_DATE, CURRENT_TIMESTAMP, ?, ?)
            """;
        try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, creePar);
            ps.setString(2, modifiePar);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                throw new SQLException("Impossible de récupérer l'id_entite généré.");
            }
        }
    }

    private static long insertUtilisateur(
            Connection cn,
            Long idEntite,
            Long idRole,
            String nom,
            String email,
            String adresse,
            String cin,
            String telephone,
            String sexe,
            String login,
            String motDePasseHash,
            LocalDate dernierLogin,
            LocalDate dateNaissance
    ) throws SQLException {

        String sql = """
            INSERT INTO utilisateur
            (id_entite, id_role, nom, email, adresse, cin, telephone, sexe, login, mot_de_passe, dernier_login, date_naissance)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, idEntite, Types.BIGINT);
            ps.setLong(2, idRole);
            ps.setString(3, nom);
            ps.setString(4, email);
            ps.setString(5, adresse);
            ps.setString(6, cin);
            ps.setString(7, telephone);
            ps.setString(8, sexe);
            ps.setString(9, login);
            ps.setString(10, motDePasseHash);
            ps.setDate(11, dernierLogin != null ? Date.valueOf(dernierLogin) : null);
            ps.setDate(12, dateNaissance != null ? Date.valueOf(dateNaissance) : null);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                throw new SQLException("Impossible de récupérer l'id_user généré.");
            }
        }
    }

    private static boolean existsByEmail(Connection cn, String email) throws SQLException {
        String sql = "SELECT 1 FROM utilisateur WHERE email=? LIMIT 1";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static boolean existsByLogin(Connection cn, String login) throws SQLException {
        String sql = "SELECT 1 FROM utilisateur WHERE login=? LIMIT 1";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static String makeUniqueEmail(Connection cn, String baseEmail) throws SQLException {
        if (!existsByEmail(cn, baseEmail)) return baseEmail;

        int at = baseEmail.indexOf('@');
        String left = at > 0 ? baseEmail.substring(0, at) : baseEmail;
        String domain = at > 0 ? baseEmail.substring(at) : "@test.com";

        for (int i = 2; i < 10_000; i++) {
            String candidate = left + i + domain;
            if (!existsByEmail(cn, candidate)) return candidate;
        }
        throw new IllegalStateException("Impossible de générer un email unique à partir de " + baseEmail);
    }

    private static String makeUniqueLogin(Connection cn, String baseLogin) throws SQLException {
        if (!existsByLogin(cn, baseLogin)) return baseLogin;

        for (int i = 2; i < 10_000; i++) {
            String candidate = baseLogin + i;
            if (!existsByLogin(cn, candidate)) return candidate;
        }
        throw new IllegalStateException("Impossible de générer un login unique à partir de " + baseLogin);
    }
}
