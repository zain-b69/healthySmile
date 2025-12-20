package ma.dentalTech.repository.test;

import ma.dentalTech.configuration.SessionFactory;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class SeedSecretaire {

    public static void main(String[] args) throws Exception {

        var sf = SessionFactory.getInstance();
        try (Connection cn = sf.getConnection()) {
            cn.setAutoCommit(false);

            // ---- INPUT ----
            String roleLibelle = "SECRETAIRE";
            String nom = "Secretaire Test";
            String baseEmail = "sec1@test.com";
            String baseLogin = "sec1";
            String plainPassword = "sec123";

            String adresse = "Rabat";
            String cin = "SC123456";
            String telephone = "0622222222";
            String sexeDb = "FEMININ"; // MASCULIN / FEMININ
            LocalDate dernierLogin = LocalDate.now();
            LocalDate dateNaissance = LocalDate.of(1996, 2, 20);

            // staff fields
            Double salaire = 6500.0;
            Double prime = 500.0;
            LocalDate dateRecrutement = LocalDate.of(2025, 1, 10);
            Integer soldeConge = 18;
            Long cabinetId = null; // mets un id existant si tu veux

            // secretaire fields
            String numCNSS = "CNSS-0001";
            Double commission = 250.0;

            try {
                Long roleId = ensureRoleExists(cn, roleLibelle, "seed", "seed");

                String email = makeUniqueEmail(cn, baseEmail);
                String login = makeUniqueLogin(cn, baseLogin);

                Long entiteId = insertEntite(cn, "seed", "seed");
                String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));

                long userId = insertUtilisateur(
                        cn, entiteId, roleId,
                        nom, email, adresse, cin, telephone, sexeDb,
                        login, hashed, dernierLogin, dateNaissance
                );

                insertStaffIfPossible(cn, userId, salaire, prime, dateRecrutement, soldeConge, cabinetId);
                insertSecretaireIfPossible(cn, userId, numCNSS, commission);

                cn.commit();

                System.out.println("✅ SECRETAIRE insérée !");
                System.out.println("   id_user=" + userId);
                System.out.println("   email=" + email);
                System.out.println("   login=" + login);
                System.out.println("   mdp clair=" + plainPassword);
                System.out.println("   mdp hash=" + hashed);
                System.out.println("✅ BCrypt check = " + BCrypt.checkpw(plainPassword, hashed));

            } catch (Exception e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }

    // ---------- ROLE helpers ----------

    private static Long ensureRoleExists(Connection cn, String libelle, String creePar, String modifiePar) throws SQLException {
        Long id = findRoleIdByLibelle(cn, libelle);
        if (id != null) return id;

        Long entiteId = insertEntite(cn, creePar, modifiePar);

        String sql = "INSERT INTO role (id_entite, libelle) VALUES (?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, entiteId, Types.BIGINT);
            ps.setString(2, libelle);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("Impossible de créer le role " + libelle);
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

    // ---------- ENTITE / UTILISATEUR ----------

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
            }
        }
        throw new SQLException("Impossible de récupérer l'id_entite généré.");
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
            }
        }
        throw new SQLException("Impossible de récupérer l'id_user généré.");
    }

    // ---------- STAFF / SECRETAIRE dynamic insert ----------

    private static void insertStaffIfPossible(Connection cn, long userId,
                                              Double salaire, Double prime, LocalDate dateRecrutement,
                                              Integer soldeConge, Long cabinetId) throws SQLException {
        if (!tableExists(cn, "staff")) return;

        Map<String, Object> values = new LinkedHashMap<>();
        if (columnExists(cn, "staff", "id_user")) values.put("id_user", userId);

        if (columnExists(cn, "staff", "salaire")) values.put("salaire", salaire);
        if (columnExists(cn, "staff", "prime")) values.put("prime", prime);
        if (columnExists(cn, "staff", "date_recrutement")) values.put("date_recrutement", dateRecrutement);
        if (columnExists(cn, "staff", "solde_conge")) values.put("solde_conge", soldeConge);

        if (cabinetId != null) {
            if (columnExists(cn, "staff", "cabinet_id")) values.put("cabinet_id", cabinetId);
            else if (columnExists(cn, "staff", "id_cabinet")) values.put("id_cabinet", cabinetId);
        }

        if (!values.isEmpty()) insertDynamic(cn, "staff", values);
    }

    private static void insertSecretaireIfPossible(Connection cn, long userId, String numCNSS, Double commission) throws SQLException {
        if (!tableExists(cn, "secretaire")) return;

        Map<String, Object> values = new LinkedHashMap<>();

        if (columnExists(cn, "secretaire", "id_user")) values.put("id_user", userId);
        else if (columnExists(cn, "secretaire", "id_secretaire")) values.put("id_secretaire", userId);

        if (columnExists(cn, "secretaire", "num_cnss")) values.put("num_cnss", numCNSS);
        if (columnExists(cn, "secretaire", "commission")) values.put("commission", commission);

        if (!values.isEmpty()) insertDynamic(cn, "secretaire", values);
    }

    // ---------- Generic SQL helpers ----------

    private static void insertDynamic(Connection cn, String table, Map<String, Object> values) throws SQLException {
        StringBuilder cols = new StringBuilder();
        StringBuilder qs = new StringBuilder();
        List<Object> params = new ArrayList<>();

        for (var e : values.entrySet()) {
            if (cols.length() > 0) { cols.append(", "); qs.append(", "); }
            cols.append(e.getKey());
            qs.append("?");
            params.add(e.getValue());
        }

        String sql = "INSERT INTO " + table + " (" + cols + ") VALUES (" + qs + ")";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                Object v = params.get(i);
                if (v instanceof LocalDate ld) ps.setDate(i + 1, Date.valueOf(ld));
                else ps.setObject(i + 1, v);
            }
            ps.executeUpdate();
        }
    }

    private static boolean tableExists(Connection cn, String table) throws SQLException {
        DatabaseMetaData md = cn.getMetaData();
        try (ResultSet rs = md.getTables(cn.getCatalog(), null, table, new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    private static boolean columnExists(Connection cn, String table, String column) throws SQLException {
        DatabaseMetaData md = cn.getMetaData();
        try (ResultSet rs = md.getColumns(cn.getCatalog(), null, table, column)) {
            return rs.next();
        }
    }

    private static boolean existsByEmail(Connection cn, String email) throws SQLException {
        String sql = "SELECT 1 FROM utilisateur WHERE email=? LIMIT 1";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    private static boolean existsByLogin(Connection cn, String login) throws SQLException {
        String sql = "SELECT 1 FROM utilisateur WHERE login=? LIMIT 1";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
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
