package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.users.Secretaire;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.SecretaireRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SecretaireRepositoryImpl implements SecretaireRepository {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    private Connection cn() throws SQLException {
        return sessionFactory.getConnection();
    }

    // ===================== READ =====================

    @Override
    public List<Secretaire> findAll() {
        String sql = baseSelect() + " ORDER BY u.id_user";
        List<Secretaire> out = new ArrayList<>();

        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapSecretaire(rs));
            return out;
        } catch (SQLException ex) {
            throw new RuntimeException("Secretaire.findAll failed", ex);
        }
    }

    @Override
    public List<Secretaire> findAllOrderByNom() {
        String sql = baseSelect() + " ORDER BY u.nom";
        List<Secretaire> out = new ArrayList<>();

        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapSecretaire(rs));
            return out;
        } catch (SQLException ex) {
            throw new RuntimeException("Secretaire.findAllOrderByNom failed", ex);
        }
    }

    @Override
    public Secretaire findById(Long id) {
        return findByIdOpt(id).orElse(null);
    }

    private Optional<Secretaire> findByIdOpt(Long idUser) {
        String sql = baseSelect() + " WHERE u.id_user = ?";

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapSecretaire(rs));
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Secretaire.findById failed", ex);
        }
    }

    @Override
    public Optional<Secretaire> findByNumCNSS(String numCNSS) {
        String sql = baseSelect() + " WHERE sc.num_cnss = ?";

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, numCNSS);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapSecretaire(rs));
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Secretaire.findByNumCNSS failed", ex);
        }
    }

    @Override
    public List<Secretaire> findByCommissionMin(Double minCommission) {
        String sql = baseSelect() + " WHERE sc.commission >= ? ORDER BY sc.commission DESC";
        List<Secretaire> out = new ArrayList<>();

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setDouble(1, minCommission != null ? minCommission : 0.0);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapSecretaire(rs));
                return out;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Secretaire.findByCommissionMin failed", ex);
        }
    }

    // ===================== CREATE =====================

    @Override
    public void create(Secretaire s) {
        if (s == null) throw new IllegalArgumentException("Secretaire is null");

        Connection c = null;
        boolean oldAutoCommit = true;

        try {
            c = cn();
            oldAutoCommit = c.getAutoCommit();
            c.setAutoCommit(false);

            // 1) entite (audit)
            Long idEntite = insertEntite(c, s.getCreePar(), s.getModifiePar());

            // 2) utilisateur (role = SECRETAIRE)
            Long roleId = findRoleIdByLibelle(c, RoleType.SECRETAIRE);

            Long idUser = insertUtilisateur(c, idEntite, roleId, s);

            // 3) staff
            insertStaff(c, idUser, s);

            // 4) secretaire
            insertSecretaire(c, idUser, s);

            // set ids back
            s.setId(idEntite);
            s.setIdUser(idUser);

            c.commit();
        } catch (SQLException ex) {
            rollbackQuietly(c);
            throw new RuntimeException("Secretaire.create failed", ex);
        } finally {
            resetAutoCommitQuietly(c, oldAutoCommit);
        }
    }

    // ===================== UPDATE =====================

    @Override
    public void update(Secretaire s) {
        if (s == null) throw new IllegalArgumentException("Secretaire is null");
        if (s.getIdUser() == null) throw new IllegalArgumentException("Secretaire.idUser is null");

        Connection c = null;
        boolean oldAutoCommit = true;

        try {
            c = cn();
            oldAutoCommit = c.getAutoCommit();
            c.setAutoCommit(false);

            // utilisateur
            String sqlUser = """
                UPDATE utilisateur
                SET nom=?, email=?, adresse=?, cin=?, telephone=?, sexe=?, login=?, mot_de_passe=?,
                    dernier_login=?, date_naissance=?
                WHERE id_user=?
                """;
            try (PreparedStatement ps = c.prepareStatement(sqlUser)) {
                ps.setString(1, s.getNom());
                ps.setString(2, s.getEmail());
                ps.setString(3, s.getAdresse());
                ps.setString(4, s.getCin());
                ps.setString(5, s.getTel());
                ps.setString(6, s.getSexe() != null ? s.getSexe().name() : null);
                ps.setString(7, s.getLogin());
                ps.setString(8, s.getMotDePasse());
                ps.setDate(9, s.getLastLoginDate() != null ? Date.valueOf(s.getLastLoginDate()) : null);
                ps.setDate(10, s.getDateNaissance() != null ? Date.valueOf(s.getDateNaissance()) : null);
                ps.setLong(11, s.getIdUser());
                ps.executeUpdate();
            }

            // staff
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

            // secretaire
            String sqlSec = """
                UPDATE secretaire
                SET num_cnss=?, commission=?
                WHERE id_staff=?
                """;
            try (PreparedStatement ps = c.prepareStatement(sqlSec)) {
                ps.setString(1, s.getNumCNSS());
                ps.setObject(2, s.getCommission(), Types.DOUBLE);
                ps.setLong(3, s.getIdUser());
                ps.executeUpdate();
            }

            // audit entite
            if (s.getId() != null) touchEntite(c, s.getId(), s.getModifiePar());

            c.commit();
        } catch (SQLException ex) {
            rollbackQuietly(c);
            throw new RuntimeException("Secretaire.update failed", ex);
        } finally {
            resetAutoCommitQuietly(c, oldAutoCommit);
        }
    }

    // ===================== DELETE =====================

    @Override
    public void delete(Secretaire s) {
        if (s == null || s.getIdUser() == null) return;
        deleteById(s.getIdUser());
    }

    @Override
    public void deleteById(Long idUser) {
        Secretaire existing = findById(idUser);
        Long idEntite = (existing != null) ? existing.getId() : null;

        Connection c = null;
        boolean oldAutoCommit = true;

        try {
            c = cn();
            oldAutoCommit = c.getAutoCommit();
            c.setAutoCommit(false);

            // staff delete => cascade secretaire (car FK secretaire -> staff ON DELETE CASCADE)
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
            throw new RuntimeException("Secretaire.deleteById failed", ex);
        } finally {
            resetAutoCommitQuietly(c, oldAutoCommit);
        }
    }

    // ===================== SQL Helpers =====================

    private String baseSelect() {
        return """
            SELECT
                u.id_user, u.id_entite, u.id_role, u.nom, u.email, u.adresse, u.cin, u.telephone,
                u.sexe, u.login, u.mot_de_passe, u.dernier_login, u.date_naissance,

                st.cabinet_id, st.date_recrutement, st.salaire, st.prime, st.solde_conge,

                sc.num_cnss, sc.commission,

                e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM utilisateur u
            JOIN staff st ON st.id_staff = u.id_user
            JOIN secretaire sc ON sc.id_staff = st.id_staff
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

    private Long insertUtilisateur(Connection c, Long idEntite, Long idRole, Secretaire s) throws SQLException {
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

    private void insertStaff(Connection c, Long idUser, Secretaire s) throws SQLException {
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

    private void insertSecretaire(Connection c, Long idUser, Secretaire s) throws SQLException {
        String sql = "INSERT INTO secretaire (id_staff, num_cnss, commission) VALUES (?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idUser);
            ps.setString(2, s.getNumCNSS());
            ps.setObject(3, s.getCommission(), Types.DOUBLE);
            ps.executeUpdate();
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
