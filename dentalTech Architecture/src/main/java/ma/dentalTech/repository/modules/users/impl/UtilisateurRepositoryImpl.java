package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilisateurRepositoryImpl implements UtilisateurRepository {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    private Connection cn() throws SQLException {
        return sessionFactory.getConnection();
    }

    // ---- CRUD ----

    @Override
    public List<Utilisateur> findAll() {
        String sql = """
            SELECT u.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM utilisateur u
            LEFT JOIN entite e ON e.id_entite = u.id_entite
            """;
        List<Utilisateur> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapUtilisateur(rs));
        } catch (SQLException ex) {
            throw new RuntimeException("findAll utilisateur failed", ex);
        }
        return out;
    }

    @Override
    public Utilisateur findById(Long id) {
        return findByIdOpt(id).orElse(null);
    }

    private Optional<Utilisateur> findByIdOpt(Long idUser) {
        String sql = """
            SELECT u.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM utilisateur u
            LEFT JOIN entite e ON e.id_entite = u.id_entite
            WHERE u.id_user = ?
            """;
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapUtilisateur(rs));
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("findById utilisateur failed", ex);
        }
    }

    @Override
    public void create(Utilisateur u) throws SQLException {
        // 1) créer entite si tu veux audit obligatoire
        Long idEntite = insertEntiteIfNeeded(u);

        // 2) insert utilisateur
        String sql = """
            INSERT INTO utilisateur (id_entite, id_role, nom, email, adresse, cin, telephone, sexe,
                                    login, mot_de_passe, dernier_login, date_naissance)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setObject(1, idEntite, Types.BIGINT);

            Long roleId = (u.getRole() != null) ? extractRoleId(u.getRole()) : null;
            if (roleId == null) throw new IllegalArgumentException("Utilisateur.id_role is required");
            ps.setLong(2, roleId);

            ps.setString(3, u.getNom());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getAdresse());
            ps.setString(6, u.getCin());
            ps.setString(7, u.getTel());
            ps.setString(8, u.getSexe() != null ? u.getSexe().name() : null);
            ps.setString(9, u.getLogin());
            ps.setString(10, u.getMotDePasse());
            ps.setDate(11, u.getLastLoginDate() != null ? Date.valueOf(u.getLastLoginDate()) : null);
            ps.setDate(12, u.getDateNaissance() != null ? Date.valueOf(u.getDateNaissance()) : null);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) u.setIdUser(keys.getLong(1));
            }

            u.setId(idEntite);
        } catch (SQLException ex) {
            throw new RuntimeException("create utilisateur failed", ex);
        }
    }

    @Override
    public void update(Utilisateur u) {
        if (u.getIdUser() == null) throw new IllegalArgumentException("Utilisateur.idUser is null");

        String sql = """
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
        try (PreparedStatement ps = cn().prepareStatement(sql)) {

            Long roleId = (u.getRole() != null) ? extractRoleId(u.getRole()) : null;
            if (roleId == null) throw new IllegalArgumentException("Utilisateur.id_role is required");

            ps.setLong(1, roleId);
            ps.setString(2, u.getNom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getAdresse());
            ps.setString(5, u.getCin());
            ps.setString(6, u.getTel());
            ps.setString(7, u.getSexe() != null ? u.getSexe().name() : null);
            ps.setString(8, u.getLogin());
            ps.setString(9, u.getMotDePasse());
            ps.setDate(10, u.getLastLoginDate() != null ? Date.valueOf(u.getLastLoginDate()) : null);
            ps.setDate(11, u.getDateNaissance() != null ? Date.valueOf(u.getDateNaissance()) : null);
            ps.setLong(12, u.getIdUser());

            ps.executeUpdate();

            // audit
            updateEntiteTouch(u.getId(), u.getModifiePar());

        } catch (SQLException ex) {
            throw new RuntimeException("update utilisateur failed", ex);
        }
    }

    @Override
    public void delete(Utilisateur u) {
        if (u == null || u.getIdUser() == null) return;
        deleteById(u.getIdUser());
    }

    @Override
    public void deleteById(Long idUser) {
        Utilisateur u = findById(idUser);
        String sql = "DELETE FROM utilisateur WHERE id_user=?";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idUser);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("delete utilisateur failed", ex);
        }

        // delete entite (optionnel)
        if (u != null && u.getId() != null) {
            deleteEntite(u.getId());
        }
    }

    // ---- extra methods ----

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        String sql = """
            SELECT u.*, e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM utilisateur u
            LEFT JOIN entite e ON e.id_entite = u.id_entite
            WHERE u.email = ?
            """;
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapUtilisateur(rs));
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("findByEmail failed", ex);
        }
    }

    @Override
    public Optional<Utilisateur> findByLogin(String login) {
        String sql = """
            SELECT u.*, e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM utilisateur u
            LEFT JOIN entite e ON e.id_entite = u.id_entite
            WHERE u.login = ?
            """;
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapUtilisateur(rs));
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("findByLogin failed", ex);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM utilisateur WHERE email=? LIMIT 1";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("existsByEmail failed", ex);
        }
    }

    @Override
    public boolean existsByLogin(String login) {
        String sql = "SELECT 1 FROM utilisateur WHERE login=? LIMIT 1";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("existsByLogin failed", ex);
        }
    }

    @Override
    public List<Utilisateur> searchByNom(String keyword) {
        String sql = """
            SELECT u.*, e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM utilisateur u
            LEFT JOIN entite e ON e.id_entite = u.id_entite
            WHERE u.nom LIKE ?
            ORDER BY u.nom
            """;
        List<Utilisateur> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapUtilisateur(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("searchByNom failed", ex);
        }
        return out;
    }

    @Override
    public List<Utilisateur> findPage(int limit, int offset) {
        String sql = """
            SELECT u.*, e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM utilisateur u
            LEFT JOIN entite e ON e.id_entite = u.id_entite
            ORDER BY u.id_user
            LIMIT ? OFFSET ?
            """;
        List<Utilisateur> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapUtilisateur(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("findPage failed", ex);
        }
        return out;
    }

    // ---- Roles (schema = 1 role per user) ----

    @Override
    public Optional<Role> findRoleByUserId(Long idUser) {
        String sql = """
        SELECT r.id_role, r.id_entite, r.libelle,
               e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
        FROM utilisateur u
        JOIN role r ON r.id_role = u.id_role
        LEFT JOIN entite e ON e.id_entite = r.id_entite
        WHERE u.id_user = ?
        """;

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapRole(rs));
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("findRoleByUserId failed", ex);
        }
    }


    @Override
    public void updateRole(Long idUser, Long idRole) {
        String sql = "UPDATE utilisateur SET id_role = ? WHERE id_user = ?";

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idRole);
            ps.setLong(2, idUser);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("updateRole failed", ex);
        }
    }

    // ---- entite helpers ----

    private Long insertEntiteIfNeeded(Utilisateur u) throws SQLException {
        if (u.getId() != null) return u.getId();

        String sql = """
            INSERT INTO entite (date_creation, date_derniere_modification, cree_par, modifie_par)
            VALUES (CURRENT_DATE, CURRENT_TIMESTAMP, ?, ?)
            """;
        try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getCreePar());
            ps.setString(2, u.getModifiePar());
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
        } catch (SQLException ex) {
            // optionnel: ignorer si FK bloque
        }
    }

    private Long extractRoleId(Role r) {
        // recommandé: Role possède un champ idRole
        try {
            // si tu as getIdRole()
            return (Long) r.getClass().getMethod("getIdRole").invoke(r);
        } catch (Exception ignored) {
            // fallback (moins fiable)
            return r.getId();
        }
    }

    @Override
    public void updatePassword(Long idUser, String hashedPassword) {
        String sql = "UPDATE utilisateur SET mot_de_passe = ? WHERE id_user = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, hashedPassword);
            ps.setLong(2, idUser);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
