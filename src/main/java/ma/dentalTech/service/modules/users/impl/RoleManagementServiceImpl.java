package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.service.modules.users.api.RoleManagementService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleManagementServiceImpl implements RoleManagementService {

    // -------------------- CRUD role --------------------

    @Override
    public Role createRole(Role role) {
        if (role == null) throw new IllegalArgumentException("role obligatoire");
        if (role.getType() == null) throw new IllegalArgumentException("role.type obligatoire");

        // éviter doublons (optionnel mais propre)
        Role existing = getRoleByType(role.getType());
        if (existing != null) {
            throw new IllegalArgumentException("ROLE_ALREADY_EXISTS: " + role.getType());
        }

        String sql = "INSERT INTO role(id_entite, libelle) VALUES (?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try {
                Long entiteId = insertEntite(c, role.getModifiePar(), role.getCreePar());

                try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    if (entiteId != null) ps.setLong(1, entiteId);
                    else ps.setNull(1, Types.BIGINT);

                    ps.setString(2, role.getType().name());
                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            long idRole = keys.getLong(1);
                            role.setIdRole(idRole);
                            role.setId(entiteId); // BaseEntity.id = id_entite (comme dans tes repos)
                        }
                    }
                }

                c.commit();
                return role;

            } catch (Exception ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB_ERROR_CREATE_ROLE", e);
        }
    }

    @Override
    public Role updateRole(Role role) {
        if (role == null) throw new IllegalArgumentException("role obligatoire");
        if (role.getIdRole() == null) throw new IllegalArgumentException("role.idRole obligatoire");
        if (role.getType() == null) throw new IllegalArgumentException("role.type obligatoire");

        String sql = "UPDATE role SET libelle = ? WHERE id_role = ?";

        try (Connection c = SessionFactory.getInstance().getConnection()) {
            // update entite audit si on a id_entite
            if (role.getId() != null) {
                updateEntite(c, role.getId(), role.getModifiePar());
            }

            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, role.getType().name());
                ps.setLong(2, role.getIdRole());
                ps.executeUpdate();
            }

            return getRoleById(role.getIdRole());

        } catch (SQLException e) {
            throw new RuntimeException("DB_ERROR_UPDATE_ROLE", e);
        }
    }

    @Override
    public void deleteRole(Long roleId) {
        if (roleId == null) throw new IllegalArgumentException("roleId obligatoire");

        // ⚠️ peut échouer si des users référencent ce role (FK utilisateur.id_role)
        String sql = "DELETE FROM role WHERE id_role = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, roleId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("DB_ERROR_DELETE_ROLE", e);
        }
    }

    @Override
    public Role getRoleById(Long id) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");

        String sql = """
                SELECT r.id_role, r.id_entite, r.libelle,
                       e.date_creation, e.date_derniere_modification, e.modifie_par, e.cree_par
                FROM role r
                LEFT JOIN entite e ON e.id_entite = r.id_entite
                WHERE r.id_role = ?
                """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRole(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("DB_ERROR_GET_ROLE_BY_ID", e);
        }
    }

    @Override
    public Role getRoleByType(RoleType type) {
        if (type == null) throw new IllegalArgumentException("type obligatoire");

        String sql = """
                SELECT r.id_role, r.id_entite, r.libelle,
                       e.date_creation, e.date_derniere_modification, e.modifie_par, e.cree_par
                FROM role r
                LEFT JOIN entite e ON e.id_entite = r.id_entite
                WHERE r.libelle = ?
                """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRole(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("DB_ERROR_GET_ROLE_BY_TYPE", e);
        }
    }

    @Override
    public List<Role> getAllRoles() {
        String sql = """
                SELECT r.id_role, r.id_entite, r.libelle,
                       e.date_creation, e.date_derniere_modification, e.modifie_par, e.cree_par
                FROM role r
                LEFT JOIN entite e ON e.id_entite = r.id_entite
                ORDER BY r.id_role ASC
                """;

        List<Role> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(mapRole(rs));
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("DB_ERROR_GET_ALL_ROLES", e);
        }
    }

    // -------------------- Privileges (optionnel selon DB) --------------------

    // -------------------- helpers --------------------

    private static Role mapRole(ResultSet rs) throws SQLException {
        Role r = new Role();
        r.setIdRole(rs.getLong("id_role"));

        long idEntite = rs.getLong("id_entite");
        if (!rs.wasNull()) r.setId(idEntite);

        String libelle = rs.getString("libelle");
        if (libelle != null) r.setType(RoleType.valueOf(libelle));

        // BaseEntity audit fields (si BaseEntity les expose bien)
        r.setCreePar(rs.getString("cree_par"));
        r.setModifiePar(rs.getString("modifie_par"));
        r.setDateCreation(rs.getDate("date_creation") != null ? rs.getDate("date_creation").toLocalDate() : null);
        r.setDateDerniereModification(rs.getTimestamp("date_derniere_modification") != null
                ? rs.getTimestamp("date_derniere_modification").toLocalDateTime()
                : null);

        return r;
    }

    private static Long insertEntite(Connection c, String modifiePar, String creePar) throws SQLException {
        String sql = """
                INSERT INTO entite(date_creation, date_derniere_modification, modifie_par, cree_par)
                VALUES (CURDATE(), NOW(), ?, ?)
                """;
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, modifiePar);
            ps.setString(2, creePar);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                return null;
            }
        }
    }

    private static void updateEntite(Connection c, long entiteId, String modifiePar) throws SQLException {
        String sql = "UPDATE entite SET date_derniere_modification = NOW(), modifie_par = ? WHERE id_entite = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, modifiePar);
            ps.setLong(2, entiteId);
            ps.executeUpdate();
        }
    }
}
