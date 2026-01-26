package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.RoleRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoleRepositoryImpl implements RoleRepository {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    private Connection cn() throws SQLException {
        return sessionFactory.getConnection();
    }

    // -------------------- CRUD --------------------

    @Override
    public List<Role> findAll() {
        String sql = """
            SELECT r.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM role r
            LEFT JOIN entite e ON e.id_entite = r.id_entite
            ORDER BY r.id_role
            """;

        List<Role> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapRole(rs));
        } catch (SQLException ex) {
            throw new RuntimeException("Role.findAll failed", ex);
        }
        return out;
    }

    @Override
    public Role findById(Long id) {
        return findByIdOpt(id).orElse(null);
    }

    private Optional<Role> findByIdOpt(Long idRole) {
        String sql = """
            SELECT r.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM role r
            LEFT JOIN entite e ON e.id_entite = r.id_entite
            WHERE r.id_role = ?
            """;

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idRole);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapRole(rs)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Role.findById failed", ex);
        }
    }

    @Override
    public Role create(Role r) {
        if (r == null) return null;

        try {
            Long idEntite = insertEntiteIfNeeded(r);

            String sql = """
                INSERT INTO role (id_entite, libelle)
                VALUES (?, ?)
                """;

            try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setObject(1, idEntite, Types.BIGINT);
                ps.setString(2, r.getType() != null ? r.getType().name() : null);

                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) r.setIdRole(keys.getLong(1));
                }

                // BaseEntity.id = id_entite (audit)
                r.setId(idEntite);
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Role.create failed", ex);
        }
        return null;
    }

    @Override
    public Role update(Role r) {
        if (r == null) return null;
        if (r.getIdRole() == null) throw new IllegalArgumentException("Role.idRole is null");

        String sql = """
            UPDATE role
            SET libelle = ?
            WHERE id_role = ?
            """;

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, r.getType() != null ? r.getType().name() : null);
            ps.setLong(2, r.getIdRole());

            ps.executeUpdate();

            // audit update
            Long idEntite = (r.getId() != null) ? r.getId() : findEntiteIdByRoleId(r.getIdRole());
            updateEntiteTouch(idEntite, r.getModifiePar());

        } catch (SQLException ex) {
            throw new RuntimeException("Role.update failed", ex);
        }
        return null;
    }

    @Override
    public void delete(Role r) {
        if (r == null || r.getIdRole() == null) return;
        deleteById(r.getIdRole());
    }

    @Override
    public void deleteById(Long idRole) {
        Long idEntite = findEntiteIdByRoleId(idRole);

        String sql = "DELETE FROM role WHERE id_role = ?";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idRole);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Role.deleteById failed", ex);
        }

        // optionnel: supprimer entite aussi
        if (idEntite != null) deleteEntite(idEntite);
    }

    // -------------------- Méthodes spécifiques --------------------

    @Override
    public Optional<Role> findByLibelle(RoleType type) {
        String sql = """
            SELECT r.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM role r
            LEFT JOIN entite e ON e.id_entite = r.id_entite
            WHERE r.libelle = ?
            """;

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, type != null ? type.name() : null);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapRole(rs)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Role.findByLibelle failed", ex);
        }
    }

    @Override
    public boolean existsByLibelle(RoleType type) {
        String sql = "SELECT 1 FROM role WHERE libelle = ? LIMIT 1";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, type != null ? type.name() : null);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Role.existsByLibelle failed", ex);
        }
    }

    // -------------------- entite helpers --------------------

    private Long insertEntiteIfNeeded(Role r) throws SQLException {
        if (r.getId() != null) return r.getId();

        String sql = """
            INSERT INTO entite (date_creation, date_derniere_modification, cree_par, modifie_par)
            VALUES (CURRENT_DATE, CURRENT_TIMESTAMP, ?, ?)
            """;

        try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getCreePar());
            ps.setString(2, r.getModifiePar());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : null;
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

    private Long findEntiteIdByRoleId(Long idRole) {
        String sql = "SELECT id_entite FROM role WHERE id_role = ?";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idRole);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getObject("id_entite", Long.class) : null;
            }
        } catch (SQLException ex) {
            return null;
        }
    }

    private void deleteEntite(Long idEntite) {
        String sql = "DELETE FROM entite WHERE id_entite = ?";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idEntite);
            ps.executeUpdate();
        } catch (SQLException ignored) {
            // FK peut bloquer -> on ignore
        }
    }
}
