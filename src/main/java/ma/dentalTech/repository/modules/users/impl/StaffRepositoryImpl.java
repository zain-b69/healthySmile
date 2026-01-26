package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.entities.users.Staff;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.StaffRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StaffRepositoryImpl implements StaffRepository {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    private Connection cn() throws SQLException {
        return sessionFactory.getConnection();
    }

    // ===================== READ =====================

    @Override
    public List<Staff> findAll() {
        String sql = baseSelect() + " ORDER BY u.id_user";
        List<Staff> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapStaff(rs));
            return out;
        } catch (SQLException ex) {
            throw new RuntimeException("Staff.findAll failed", ex);
        }
    }

    @Override
    public List<Staff> findAllOrderByNom() {
        String sql = baseSelect() + " ORDER BY u.nom";
        List<Staff> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapStaff(rs));
            return out;
        } catch (SQLException ex) {
            throw new RuntimeException("Staff.findAllOrderByNom failed", ex);
        }
    }

    @Override
    public Staff findById(Long id) {
        return findByIdOpt(id).orElse(null);
    }

    private Optional<Staff> findByIdOpt(Long idUser) {
        String sql = baseSelect() + " WHERE u.id_user = ?";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapStaff(rs));
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Staff.findById failed", ex);
        }
    }

    @Override
    public List<Staff> findBySalaireBetween(Double min, Double max) {
        String sql = baseSelect() + " WHERE st.salaire BETWEEN ? AND ? ORDER BY u.nom";
        List<Staff> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setObject(1, min, Types.DOUBLE);
            ps.setObject(2, max, Types.DOUBLE);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStaff(rs));
            }
            return out;
        } catch (SQLException ex) {
            throw new RuntimeException("Staff.findBySalaireBetween failed", ex);
        }
    }

    @Override
    public List<Staff> findByDateRecrutementAfter(LocalDate date) {
        String sql = baseSelect() + " WHERE st.date_recrutement > ? ORDER BY st.date_recrutement DESC";
        List<Staff> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setDate(1, date != null ? Date.valueOf(date) : null);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStaff(rs));
            }
            return out;
        } catch (SQLException ex) {
            throw new RuntimeException("Staff.findByDateRecrutementAfter failed", ex);
        }
    }

    // ===================== CREATE =====================

    @Override
    public Staff create(Staff s) {
        if (s == null) throw new IllegalArgumentException("Staff is null");

        Connection c = null;
        boolean oldAutoCommit = true;

        try {
            c = cn();
            oldAutoCommit = c.getAutoCommit();
            c.setAutoCommit(false);

            Long idEntite = insertEntite(c, s.getCreePar(), s.getModifiePar());

            // rôle obligatoire: soit idRole soit type
            Long roleId = extractRoleIdOrDefault(c, s.getRole(), null);
            if (roleId == null) throw new IllegalArgumentException("Staff.id_role is required (role)");

            Long idUser = insertUtilisateur(c, idEntite, roleId, s);
            insertStaff(c, idUser, s);

            s.setId(idEntite);
            s.setIdUser(idUser);

            c.commit();
        } catch (SQLException ex) {
            rollbackQuietly(c);
            throw new RuntimeException("Staff.create failed", ex);
        } finally {
            resetAutoCommitQuietly(c, oldAutoCommit);
        }
        return null;
    }

    // ===================== UPDATE =====================

    @Override
    public Staff update(Staff s) {
        if (s == null) throw new IllegalArgumentException("Staff is null");
        if (s.getIdUser() == null) throw new IllegalArgumentException("Staff.idUser is null");

        Connection c = null;
        boolean oldAutoCommit = true;

        try {
            c = cn();
            oldAutoCommit = c.getAutoCommit();
            c.setAutoCommit(false);

            String sqlUser = """
                UPDATE utilisateur
                SET id_role=?,
                    nom=?, email=?, adresse=?, cin=?, telephone=?, sexe=?, login=?, mot_de_passe=?,
                    dernier_login=?, date_naissance=?
                WHERE id_user=?
                """;
            try (PreparedStatement ps = c.prepareStatement(sqlUser)) {
                Long roleId = extractRoleIdOrDefault(c, s.getRole(), null);
                if (roleId == null) throw new IllegalArgumentException("Staff.id_role is required (role)");

                ps.setLong(1, roleId);
                ps.setString(2, s.getNom());
                ps.setString(3, s.getEmail());
                ps.setString(4, s.getAdresse());
                ps.setString(5, s.getCin());
                ps.setString(6, s.getTel());
                ps.setString(7, s.getSexe() != null ? s.getSexe().name() : null);
                ps.setString(8, s.getLogin());
                ps.setString(9, s.getMotDePasse());
                ps.setDate(10, s.getLastLoginDate() != null ? Date.valueOf(s.getLastLoginDate()) : null);
                ps.setDate(11, s.getDateNaissance() != null ? Date.valueOf(s.getDateNaissance()) : null);
                ps.setLong(12, s.getIdUser());
                ps.executeUpdate();
            }

            String sqlStaff = """
                UPDATE staff
                SET cabinet_id=?, date_recrutement=?, salaire=?, prime=?, solde_conge=?
                WHERE id_staff=?
                """;
            try (PreparedStatement ps = c.prepareStatement(sqlStaff)) {
                Long cabinetId = (s.getCabinetMedical() != null) ? s.getCabinetMedical().getIdCabinet() : null;

                ps.setObject(1, cabinetId, Types.BIGINT);
                ps.setDate(2, s.getDateRecrutement() != null ? Date.valueOf(s.getDateRecrutement()) : null);
                ps.setObject(3, s.getSalaire(), Types.DOUBLE);
                ps.setObject(4, s.getPrime(), Types.DOUBLE);
                ps.setObject(5, s.getSoldeCongé(), Types.INTEGER);
                ps.setLong(6, s.getIdUser());
                ps.executeUpdate();
            }

            if (s.getId() != null) touchEntite(c, s.getId(), s.getModifiePar());

            c.commit();
        } catch (SQLException ex) {
            rollbackQuietly(c);
            throw new RuntimeException("Staff.update failed", ex);
        } finally {
            resetAutoCommitQuietly(c, oldAutoCommit);
        }
        return null;
    }

    // ===================== DELETE =====================

    @Override
    public void delete(Staff s) {
        if (s == null || s.getIdUser() == null) return;
        deleteById(s.getIdUser());
    }

    @Override
    public void deleteById(Long idUser) {
        Staff existing = findById(idUser);
        Long idEntite = (existing != null) ? existing.getId() : null;

        Connection c = null;
        boolean oldAutoCommit = true;

        try {
            c = cn();
            oldAutoCommit = c.getAutoCommit();
            c.setAutoCommit(false);

            try (PreparedStatement ps = c.prepareStatement("DELETE FROM staff WHERE id_staff=?")) {
                ps.setLong(1, idUser);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = c.prepareStatement("DELETE FROM utilisateur WHERE id_user=?")) {
                ps.setLong(1, idUser);
                ps.executeUpdate();
            }

            if (idEntite != null) {
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM entite WHERE id_entite=?")) {
                    ps.setLong(1, idEntite);
                    ps.executeUpdate();
                }
            }

            c.commit();
        } catch (SQLException ex) {
            rollbackQuietly(c);
            throw new RuntimeException("Staff.deleteById failed", ex);
        } finally {
            resetAutoCommitQuietly(c, oldAutoCommit);
        }
    }

    // ===================== SQL / HELPERS =====================

    private String baseSelect() {
        return """
            SELECT
                u.id_user, u.id_entite, u.id_role, u.nom, u.email, u.adresse, u.cin, u.telephone,
                u.sexe, u.login, u.mot_de_passe, u.dernier_login, u.date_naissance,

                st.cabinet_id, st.date_recrutement, st.salaire, st.prime, st.solde_conge,

                e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM utilisateur u
            JOIN staff st ON st.id_staff = u.id_user
            LEFT JOIN entite e ON e.id_entite = u.id_entite
            """;
    }

    private Long insertEntite(Connection c, String creePar, String modifiePar) throws SQLException {
        String sql = """
            INSERT INTO entite (date_creation, date_derniere_modification, cree_par, modifie_par)
            VALUES (CURRENT_DATE, CURRENT_TIMESTAMP, ?, ?)
            """;
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, creePar);
            ps.setString(2, modifiePar);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                throw new SQLException("Insert entite: no generated key");
            }
        }
    }

    private void touchEntite(Connection c, Long idEntite, String modifiePar) throws SQLException {
        String sql = """
            UPDATE entite
            SET date_derniere_modification = CURRENT_TIMESTAMP,
                modifie_par = ?
            WHERE id_entite = ?
            """;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, modifiePar);
            ps.setLong(2, idEntite);
            ps.executeUpdate();
        }
    }

    private Long insertUtilisateur(Connection c, Long idEntite, Long idRole, Staff s) throws SQLException {
        String sql = """
            INSERT INTO utilisateur (id_entite, id_role, nom, email, adresse, cin, telephone, sexe,
                                     login, mot_de_passe, dernier_login, date_naissance)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, idEntite, Types.BIGINT);
            ps.setLong(2, idRole);

            ps.setString(3, s.getNom());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getAdresse());
            ps.setString(6, s.getCin());
            ps.setString(7, s.getTel());
            ps.setString(8, s.getSexe() != null ? s.getSexe().name() : null);
            ps.setString(9, s.getLogin());
            ps.setString(10, s.getMotDePasse());
            ps.setDate(11, s.getLastLoginDate() != null ? Date.valueOf(s.getLastLoginDate()) : null);
            ps.setDate(12, s.getDateNaissance() != null ? Date.valueOf(s.getDateNaissance()) : null);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                throw new SQLException("Insert utilisateur: no generated key");
            }
        }
    }

    private void insertStaff(Connection c, Long idUser, Staff s) throws SQLException {
        String sql = """
            INSERT INTO staff (id_staff, cabinet_id, date_recrutement, salaire, prime, solde_conge)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idUser);

            Long cabinetId = null;
            CabinetMedical cab = s.getCabinetMedical();
            if (cab != null) cabinetId = cab.getIdCabinet();
            ps.setObject(2, cabinetId, Types.BIGINT);

            ps.setDate(3, s.getDateRecrutement() != null ? Date.valueOf(s.getDateRecrutement()) : null);
            ps.setObject(4, s.getSalaire(), Types.DOUBLE);
            ps.setObject(5, s.getPrime(), Types.DOUBLE);
            ps.setObject(6, s.getSoldeCongé(), Types.INTEGER);

            ps.executeUpdate();
        }
    }

    private Long extractRoleIdOrDefault(Connection c, Role role, RoleType defaultType) throws SQLException {
        if (role != null) {
            try {
                Object v = role.getClass().getMethod("getIdRole").invoke(role);
                if (v instanceof Long l && l != 0L) return l;
            } catch (Exception ignored) {}

            if (role.getType() != null) {
                return findRoleIdByLibelle(c, role.getType());
            }
        }
        if (defaultType != null) return findRoleIdByLibelle(c, defaultType);
        return null;
    }

    private Long findRoleIdByLibelle(Connection c, RoleType type) throws SQLException {
        String sql = "SELECT id_role FROM role WHERE libelle=? LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id_role");
                throw new SQLException("Role not found for libelle=" + type.name());
            }
        }
    }

    private void rollbackQuietly(Connection c) {
        if (c == null) return;
        try { c.rollback(); } catch (SQLException ignored) {}
    }

    private void resetAutoCommitQuietly(Connection c, boolean oldAutoCommit) {
        if (c == null) return;
        try { c.setAutoCommit(oldAutoCommit); } catch (SQLException ignored) {}
    }
}
