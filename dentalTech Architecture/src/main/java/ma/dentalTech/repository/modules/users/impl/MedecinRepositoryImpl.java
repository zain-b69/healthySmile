package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.users.Medecin;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.MedecinRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedecinRepositoryImpl implements MedecinRepository {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    private Connection cn() throws SQLException {
        return sessionFactory.getConnection();
    }

    // -------------------- READ --------------------

    @Override
    public List<Medecin> findAll() {
        String sql = """
            SELECT u.*,
                   s.cabinet_id, s.date_recrutement, s.salaire, s.prime, s.solde_conge,
                   m.specialite,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM medecin m
            JOIN staff s       ON s.id_staff = m.id_medecin
            JOIN utilisateur u ON u.id_user  = s.id_staff
            LEFT JOIN entite e ON e.id_entite = u.id_entite
            """;
        List<Medecin> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapMedecin(rs));
        } catch (SQLException ex) {
            throw new RuntimeException("findAll medecin failed", ex);
        }
        return out;
    }

    @Override
    public Medecin findById(Long id) {
        String sql = """
            SELECT u.*,
                   s.cabinet_id, s.date_recrutement, s.salaire, s.prime, s.solde_conge,
                   m.specialite,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM medecin m
            JOIN staff s       ON s.id_staff = m.id_medecin
            JOIN utilisateur u ON u.id_user  = s.id_staff
            LEFT JOIN entite e ON e.id_entite = u.id_entite
            WHERE u.id_user = ?
            """;
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapMedecin(rs) : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("findById medecin failed", ex);
        }
    }

    @Override
    public List<Medecin> findAllOrderByNom() {
        String sql = """
            SELECT u.*,
                   s.cabinet_id, s.date_recrutement, s.salaire, s.prime, s.solde_conge,
                   m.specialite,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM medecin m
            JOIN staff s       ON s.id_staff = m.id_medecin
            JOIN utilisateur u ON u.id_user  = s.id_staff
            LEFT JOIN entite e ON e.id_entite = u.id_entite
            ORDER BY u.nom
            """;
        List<Medecin> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapMedecin(rs));
        } catch (SQLException ex) {
            throw new RuntimeException("findAllOrderByNom medecin failed", ex);
        }
        return out;
    }

    @Override
    public List<Medecin> findBySpecialite(String specialiteLike) {
        String sql = """
            SELECT u.*,
                   s.cabinet_id, s.date_recrutement, s.salaire, s.prime, s.solde_conge,
                   m.specialite,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM medecin m
            JOIN staff s       ON s.id_staff = m.id_medecin
            JOIN utilisateur u ON u.id_user  = s.id_staff
            LEFT JOIN entite e ON e.id_entite = u.id_entite
            WHERE m.specialite LIKE ?
            ORDER BY u.nom
            """;
        List<Medecin> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, "%" + (specialiteLike == null ? "" : specialiteLike) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapMedecin(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("findBySpecialite medecin failed", ex);
        }
        return out;
    }

    // -------------------- CREATE --------------------

    @Override
    public void create(Medecin m) {
        if (m == null) throw new IllegalArgumentException("Medecin is null");

        Connection conn = null;
        boolean oldAutoCommit = true;

        try {
            conn = cn();
            oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            // 1) entite (audit)
            Long idEntite = insertEntiteIfNeeded(conn, m);

            // 2) utilisateur
            Long roleId = (m.getRole() != null) ? extractRoleId(m.getRole()) : null;
            if (roleId == null) throw new IllegalArgumentException("Medecin.role (id_role) is required");

            String sqlUser = """
                INSERT INTO utilisateur (id_entite, id_role, nom, email, adresse, cin, telephone, sexe,
                                        login, mot_de_passe, dernier_login, date_naissance)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
            try (PreparedStatement ps = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setObject(1, idEntite, Types.BIGINT);
                ps.setLong(2, roleId);

                ps.setString(3, m.getNom());
                ps.setString(4, m.getEmail());
                ps.setString(5, m.getAdresse());
                ps.setString(6, m.getCin());
                ps.setString(7, m.getTel());
                ps.setString(8, m.getSexe() != null ? m.getSexe().name() : null);
                ps.setString(9, m.getLogin());
                ps.setString(10, m.getMotDePasse());
                ps.setDate(11, m.getLastLoginDate() != null ? Date.valueOf(m.getLastLoginDate()) : null);
                ps.setDate(12, m.getDateNaissance() != null ? Date.valueOf(m.getDateNaissance()) : null);

                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) m.setIdUser(keys.getLong(1));
                }
            }

            if (m.getIdUser() == null) throw new SQLException("id_user not generated");

            // 3) staff
            Long cabinetId = (m.getCabinetMedical() != null) ? m.getCabinetMedical().getIdCabinet() : null;

            String sqlStaff = """
                INSERT INTO staff (id_staff, cabinet_id, date_recrutement, salaire, prime, solde_conge)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
            try (PreparedStatement ps = conn.prepareStatement(sqlStaff)) {
                ps.setLong(1, m.getIdUser());
                if (cabinetId != null) ps.setLong(2, cabinetId); else ps.setNull(2, Types.BIGINT);
                ps.setDate(3, m.getDateRecrutement() != null ? Date.valueOf(m.getDateRecrutement()) : null);
                ps.setObject(4, m.getSalaire(), Types.DOUBLE);
                ps.setObject(5, m.getPrime(), Types.DOUBLE);
                ps.setObject(6, m.getSoldeCongé(), Types.INTEGER);
                ps.executeUpdate();
            }

            // 4) medecin
            String sqlMed = "INSERT INTO medecin (id_medecin, specialite) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlMed)) {
                ps.setLong(1, m.getIdUser());
                ps.setString(2, m.getSpecialite());
                ps.executeUpdate();
            }

            conn.commit();

            // sync BaseEntity.id = id_entite
            m.setId(idEntite);

        } catch (SQLException ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new RuntimeException("create medecin failed", ex);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(oldAutoCommit); } catch (SQLException ignored) {}
            }
        }
    }

    // -------------------- UPDATE --------------------

    @Override
    public void update(Medecin m) {
        if (m == null || m.getIdUser() == null) throw new IllegalArgumentException("Medecin.idUser is null");

        Connection conn = null;
        boolean oldAutoCommit = true;

        try {
            conn = cn();
            oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            Long roleId = (m.getRole() != null) ? extractRoleId(m.getRole()) : null;
            if (roleId == null) throw new IllegalArgumentException("Medecin.role (id_role) is required");

            // 1) utilisateur
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
            try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                ps.setLong(1, roleId);
                ps.setString(2, m.getNom());
                ps.setString(3, m.getEmail());
                ps.setString(4, m.getAdresse());
                ps.setString(5, m.getCin());
                ps.setString(6, m.getTel());
                ps.setString(7, m.getSexe() != null ? m.getSexe().name() : null);
                ps.setString(8, m.getLogin());
                ps.setString(9, m.getMotDePasse());
                ps.setDate(10, m.getLastLoginDate() != null ? Date.valueOf(m.getLastLoginDate()) : null);
                ps.setDate(11, m.getDateNaissance() != null ? Date.valueOf(m.getDateNaissance()) : null);
                ps.setLong(12, m.getIdUser());
                ps.executeUpdate();
            }

            // 2) staff
            Long cabinetId = (m.getCabinetMedical() != null) ? m.getCabinetMedical().getIdCabinet() : null;

            String sqlStaff = """
                UPDATE staff
                SET cabinet_id=?,
                    date_recrutement=?,
                    salaire=?,
                    prime=?,
                    solde_conge=?
                WHERE id_staff=?
                """;
            try (PreparedStatement ps = conn.prepareStatement(sqlStaff)) {
                if (cabinetId != null) ps.setLong(1, cabinetId); else ps.setNull(1, Types.BIGINT);
                ps.setDate(2, m.getDateRecrutement() != null ? Date.valueOf(m.getDateRecrutement()) : null);
                ps.setObject(3, m.getSalaire(), Types.DOUBLE);
                ps.setObject(4, m.getPrime(), Types.DOUBLE);
                ps.setObject(5, m.getSoldeCongé(), Types.INTEGER);
                ps.setLong(6, m.getIdUser());
                ps.executeUpdate();
            }

            // 3) medecin
            String sqlMed = "UPDATE medecin SET specialite=? WHERE id_medecin=?";
            try (PreparedStatement ps = conn.prepareStatement(sqlMed)) {
                ps.setString(1, m.getSpecialite());
                ps.setLong(2, m.getIdUser());
                ps.executeUpdate();
            }

            // 4) audit entite
            updateEntiteTouch(conn, m.getId(), m.getModifiePar());

            conn.commit();
        } catch (SQLException ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new RuntimeException("update medecin failed", ex);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(oldAutoCommit); } catch (SQLException ignored) {}
            }
        }
    }

    // -------------------- DELETE --------------------

    @Override
    public void delete(Medecin m) {
        if (m == null || m.getIdUser() == null) return;
        deleteById(m.getIdUser());
    }

    @Override
    public void deleteById(Long idUser) {
        Medecin m = findById(idUser);

        Connection conn = null;
        boolean oldAutoCommit = true;

        try {
            conn = cn();
            oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            // 1) delete staff (cascade -> medecin)
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM staff WHERE id_staff=?")) {
                ps.setLong(1, idUser);
                ps.executeUpdate();
            }

            // 2) delete utilisateur
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM utilisateur WHERE id_user=?")) {
                ps.setLong(1, idUser);
                ps.executeUpdate();
            }

            // 3) delete entite (optionnel)
            if (m != null && m.getId() != null) {
                deleteEntite(conn, m.getId());
            }

            conn.commit();
        } catch (SQLException ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new RuntimeException("delete medecin failed", ex);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(oldAutoCommit); } catch (SQLException ignored) {}
            }
        }
    }

    // -------------------- entite helpers --------------------

    private Long insertEntiteIfNeeded(Connection conn, Medecin m) throws SQLException {
        if (m.getId() != null) return m.getId();

        String sql = """
            INSERT INTO entite (date_creation, date_derniere_modification, cree_par, modifie_par)
            VALUES (CURRENT_DATE, CURRENT_TIMESTAMP, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getCreePar());
            ps.setString(2, m.getModifiePar());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : null;
            }
        }
    }

    private void updateEntiteTouch(Connection conn, Long idEntite, String modifiePar) throws SQLException {
        if (idEntite == null) return;
        String sql = """
            UPDATE entite
            SET date_derniere_modification = CURRENT_TIMESTAMP,
                modifie_par = ?
            WHERE id_entite = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, modifiePar);
            ps.setLong(2, idEntite);
            ps.executeUpdate();
        }
    }

    private void deleteEntite(Connection conn, Long idEntite) throws SQLException {
        String sql = "DELETE FROM entite WHERE id_entite=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idEntite);
            ps.executeUpdate();
        }
    }

    private Long extractRoleId(Role r) {
        // si Role a getIdRole()
        try {
            return (Long) r.getClass().getMethod("getIdRole").invoke(r);
        } catch (Exception ignored) {
            // fallback: BaseEntity.id
            return r.getId();
        }
    }
}
