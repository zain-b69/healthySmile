package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.users.Admin;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.AdminRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminRepositoryImpl implements AdminRepository {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    private Connection cn() throws SQLException {
        return sessionFactory.getConnection();
    }

    // ---------------- CRUD ----------------

    @Override
    public List<Admin> findAll() {
        // par défaut : tri par nom
        return findAllOrderByNom();
    }

    @Override
    public Admin findById(Long id) {
        return findByIdOpt(id).orElse(null);
    }

    private Optional<Admin> findByIdOpt(Long idUser) {
        String sql = """
            SELECT
                u.*,
                s.cabinet_id, s.date_recrutement, s.salaire, s.prime, s.solde_conge,
                r.id_role, r.libelle,
                e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM utilisateur u
            JOIN staff s ON s.id_staff = u.id_user
            JOIN role r ON r.id_role = u.id_role
            LEFT JOIN entite e ON e.id_entite = u.id_entite
            WHERE u.id_user = ? AND r.libelle = 'ADMIN'
            """;

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapAdmin(rs));
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("findById admin failed", ex);
        }
    }

    @Override
    public void create(Admin a) {
        try {
            // 1) entite (audit)
            Long idEntite = insertEntiteIfNeeded(a);

            // 2) role ADMIN (si pas fourni)
            Long roleId = extractRoleIdOrDefaultAdmin(a);

            // 3) insert utilisateur (PK auto)
            String sqlUser = """
                INSERT INTO utilisateur (id_entite, id_role, nom, email, adresse, cin, telephone, sexe,
                                         login, mot_de_passe, dernier_login, date_naissance)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

            try (PreparedStatement ps = cn().prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setObject(1, idEntite, Types.BIGINT);
                ps.setLong(2, roleId);

                ps.setString(3, a.getNom());
                ps.setString(4, a.getEmail());
                ps.setString(5, a.getAdresse());
                ps.setString(6, a.getCin());
                ps.setString(7, a.getTel());
                ps.setString(8, a.getSexe() != null ? a.getSexe().name() : null);
                ps.setString(9, a.getLogin());
                ps.setString(10, a.getMotDePasse());
                ps.setDate(11, a.getLastLoginDate() != null ? Date.valueOf(a.getLastLoginDate()) : null);
                ps.setDate(12, a.getDateNaissance() != null ? Date.valueOf(a.getDateNaissance()) : null);

                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        a.setIdUser(keys.getLong(1));
                    } else {
                        throw new SQLException("No generated key returned for utilisateur.id_user");
                    }
                }
            }

            a.setId(idEntite);

            // 4) insert staff (PK = id_user)
            String sqlStaff = """
                INSERT INTO staff (id_staff, cabinet_id, date_recrutement, salaire, prime, solde_conge)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

            try (PreparedStatement ps = cn().prepareStatement(sqlStaff)) {
                ps.setLong(1, a.getIdUser());

                Long cabinetId = (a.getCabinetMedical() != null) ? a.getCabinetMedical().getIdCabinet() : null;
                ps.setObject(2, cabinetId, Types.BIGINT);

                ps.setDate(3, a.getDateRecrutement() != null ? Date.valueOf(a.getDateRecrutement()) : null);
                ps.setObject(4, a.getSalaire(), Types.DOUBLE);
                ps.setObject(5, a.getPrime(), Types.DOUBLE);
                ps.setObject(6, a.getSoldeCongé(), Types.INTEGER);

                ps.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new RuntimeException("create admin failed", ex);
        }
    }

    @Override
    public void update(Admin a) {
        if (a.getIdUser() == null) throw new IllegalArgumentException("Admin.idUser is null");

        try {
            Long roleId = extractRoleIdOrDefaultAdmin(a);

            String sqlUser = """
                UPDATE utilisateur
                SET id_role=?,
                    nom=?,
                    email=?,
                    adresse=?,
                    cin=?,
                    telephone=?,
                    sexe=?,
                    login=?,
                    mot_de_passe=?,
                    dernier_login=?,
                    date_naissance=?
                WHERE id_user=?
                """;

            try (PreparedStatement ps = cn().prepareStatement(sqlUser)) {
                ps.setLong(1, roleId);
                ps.setString(2, a.getNom());
                ps.setString(3, a.getEmail());
                ps.setString(4, a.getAdresse());
                ps.setString(5, a.getCin());
                ps.setString(6, a.getTel());
                ps.setString(7, a.getSexe() != null ? a.getSexe().name() : null);
                ps.setString(8, a.getLogin());
                ps.setString(9, a.getMotDePasse());
                ps.setDate(10, a.getLastLoginDate() != null ? Date.valueOf(a.getLastLoginDate()) : null);
                ps.setDate(11, a.getDateNaissance() != null ? Date.valueOf(a.getDateNaissance()) : null);
                ps.setLong(12, a.getIdUser());
                ps.executeUpdate();
            }

            String sqlStaff = """
                UPDATE staff
                SET cabinet_id=?,
                    date_recrutement=?,
                    salaire=?,
                    prime=?,
                    solde_conge=?
                WHERE id_staff=?
                """;

            try (PreparedStatement ps = cn().prepareStatement(sqlStaff)) {
                Long cabinetId = (a.getCabinetMedical() != null) ? a.getCabinetMedical().getIdCabinet() : null;
                ps.setObject(1, cabinetId, Types.BIGINT);
                ps.setDate(2, a.getDateRecrutement() != null ? Date.valueOf(a.getDateRecrutement()) : null);
                ps.setObject(3, a.getSalaire(), Types.DOUBLE);
                ps.setObject(4, a.getPrime(), Types.DOUBLE);
                ps.setObject(5, a.getSoldeCongé(), Types.INTEGER);
                ps.setLong(6, a.getIdUser());
                ps.executeUpdate();
            }

            // audit entite (si présent)
            updateEntiteTouch(a.getId(), a.getModifiePar());

        } catch (SQLException ex) {
            throw new RuntimeException("update admin failed", ex);
        }
    }

    @Override
    public void delete(Admin a) {
        if (a == null || a.getIdUser() == null) return;
        deleteById(a.getIdUser());
    }

    @Override
    public void deleteById(Long idUser) {
        Admin a = findById(idUser);

        try {
            // staff d'abord (FK vers utilisateur)
            try (PreparedStatement ps = cn().prepareStatement("DELETE FROM staff WHERE id_staff=?")) {
                ps.setLong(1, idUser);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = cn().prepareStatement("DELETE FROM utilisateur WHERE id_user=?")) {
                ps.setLong(1, idUser);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("delete admin failed", ex);
        }

        // entite optionnel
        if (a != null && a.getId() != null) deleteEntite(a.getId());
    }

    // ---------------- Custom AdminRepository ----------------

    @Override
    public List<Admin> findAllOrderByNom() {
        String sql = """
            SELECT
                u.*,
                s.cabinet_id, s.date_recrutement, s.salaire, s.prime, s.solde_conge,
                r.id_role, r.libelle,
                e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM utilisateur u
            JOIN staff s ON s.id_staff = u.id_user
            JOIN role r ON r.id_role = u.id_role
            LEFT JOIN entite e ON e.id_entite = u.id_entite
            WHERE r.libelle = 'ADMIN'
            ORDER BY u.nom
            """;

        List<Admin> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapAdmin(rs));
        } catch (SQLException ex) {
            throw new RuntimeException("findAllOrderByNom admin failed", ex);
        }
        return out;
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        String sql = """
            SELECT
                u.*,
                s.cabinet_id, s.date_recrutement, s.salaire, s.prime, s.solde_conge,
                r.id_role, r.libelle,
                e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM utilisateur u
            JOIN staff s ON s.id_staff = u.id_user
            JOIN role r ON r.id_role = u.id_role
            LEFT JOIN entite e ON e.id_entite = u.id_entite
            WHERE u.email = ? AND r.libelle = 'ADMIN'
            """;

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapAdmin(rs));
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("findByEmail admin failed", ex);
        }
    }

    // ---------------- Mapping ----------------

    private Admin mapAdmin(ResultSet rs) throws SQLException {
        Admin a = new Admin();

        // utilisateur
        a.setIdUser(rs.getLong("id_user"));
        a.setId(rs.getObject("id_entite", Long.class));

        a.setNom(rs.getString("nom"));
        a.setEmail(rs.getString("email"));
        a.setAdresse(rs.getString("adresse"));
        a.setCin(rs.getString("cin"));
        a.setTel(rs.getString("telephone"));

        String sx = rs.getString("sexe");
        a.setSexe(sx != null ? Sexe.valueOf(sx) : null);

        a.setLogin(rs.getString("login"));
        a.setMotDePasse(rs.getString("mot_de_passe"));

        Date dl = rs.getDate("dernier_login");
        a.setLastLoginDate(dl != null ? dl.toLocalDate() : null);

        Date dn = rs.getDate("date_naissance");
        a.setDateNaissance(dn != null ? dn.toLocalDate() : null);

        // role (si ton Role a bien idRole + type)
        Long idRole = rs.getObject("id_role", Long.class);
        if (idRole != null) {
            Role r = new Role();
            try {
                r.getClass().getMethod("setIdRole", Long.class).invoke(r, idRole);
            } catch (Exception ignored) {
                // fallback
                r.setId(idRole);
            }
            String lib = rs.getString("libelle");
            if (lib != null) r.setType(RoleType.valueOf(lib));
            a.setRole(r);
        }

        // staff
        a.setSalaire(rs.getObject("salaire", Double.class));
        a.setPrime(rs.getObject("prime", Double.class));

        Date dr = rs.getDate("date_recrutement");
        a.setDateRecrutement(dr != null ? dr.toLocalDate() : null);

        a.setSoldeCongé(rs.getObject("solde_conge", Integer.class));

        Long cabinetId = rs.getObject("cabinet_id", Long.class);
        if (cabinetId != null) {
            CabinetMedical cab = new CabinetMedical();
            cab.setIdCabinet(cabinetId);
            a.setCabinetMedical(cab);
        }

        // audit (entite join)
        RowMappers.fillAudit(a, rs); // si fillAudit est private chez toi, copie la logique ici

        return a;
    }

    // ---------------- entite helpers ----------------

    private Long insertEntiteIfNeeded(Admin a) throws SQLException {
        if (a.getId() != null) return a.getId();

        String sql = """
            INSERT INTO entite (date_creation, date_derniere_modification, cree_par, modifie_par)
            VALUES (CURRENT_DATE, CURRENT_TIMESTAMP, ?, ?)
            """;
        try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getCreePar());
            ps.setString(2, a.getModifiePar());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                return null;
            }
        }
    }

    private void updateEntiteTouch(Long idEntite, String modifiePar) throws SQLException {
        if (idEntite == null) return;
        String sql = """
            UPDATE entite
            SET date_derniere_modification = CURRENT_TIMESTAMP,
                modifie_par = ?
            WHERE id_entite = ?
            """;
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, modifiePar);
            ps.setLong(2, idEntite);
            ps.executeUpdate();
        }
    }

    private void deleteEntite(Long idEntite) {
        String sql = "DELETE FROM entite WHERE id_entite=?";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idEntite);
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    private Long extractRoleIdOrDefaultAdmin(Admin a) throws SQLException {
        // si role fourni
        if (a.getRole() != null) {
            // si tu as getIdRole()
            try {
                Object v = a.getRole().getClass().getMethod("getIdRole").invoke(a.getRole());
                if (v instanceof Long l && l != null) return l;
            } catch (Exception ignored) {}

            // fallback: BaseEntity.id
            if (a.getRole().getId() != null) return a.getRole().getId();
        }

        // sinon: récupérer id_role de ADMIN
        String sql = "SELECT id_role FROM role WHERE libelle='ADMIN' LIMIT 1";
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong("id_role");
        }
        throw new IllegalStateException("Role ADMIN not found in table role");
    }
}
