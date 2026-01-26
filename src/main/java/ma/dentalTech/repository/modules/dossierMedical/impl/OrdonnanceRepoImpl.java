package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Impl JDBC pour Ordonnance.
 *
 * Hypothèses DB (alignées au reste du projet):
 * - table ordonnance(id_ord PK, id_entite FK entite, id_consultation FK, id_dm FK, date)
 * - table entite(id_entite PK, date_creation, date_derniere_modification, cree_par, modifie_par)
 */
public class OrdonnanceRepoImpl implements OrdonnanceRepo {

    private Connection cn() throws SQLException {
        return SessionFactory.getInstance().getConnection();
    }

    // =========================
    // CRUD
    // =========================

    @Override
    public List<Ordonnance> findAll() {
        String sql = """
            SELECT o.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM ordonnance o
            LEFT JOIN entite e ON e.id_entite = o.id_entite
            ORDER BY o.id_ord
            """;

        List<Ordonnance> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapOrdonnance(rs));
        } catch (SQLException ex) {
            throw new RuntimeException("Ordonnance.findAll failed", ex);
        }
        return out;
    }

    @Override
    public Ordonnance findById(Long id) {
        if (id == null) return null;

        String sql = """
            SELECT o.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM ordonnance o
            LEFT JOIN entite e ON e.id_entite = o.id_entite
            WHERE o.id_ord = ?
            """;

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapOrdonnance(rs) : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Ordonnance.findById failed", ex);
        }
    }

    @Override
    public Ordonnance create(Ordonnance o) {
        if (o == null) throw new IllegalArgumentException("Ordonnance is null");

        try {
            Long idEntite = insertEntiteIfNeeded(o.getCreePar(), o.getModifiePar(), o.getId());

            String sql = """
                INSERT INTO ordonnance (id_entite, id_consultation, date, id_dm)
                VALUES (?, ?, ?, ?)
                """;

            try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setObject(1, idEntite, Types.BIGINT);
                ps.setObject(2, o.getIdConsultation(), Types.BIGINT);
                ps.setDate(3, o.getDate() != null ? Date.valueOf(o.getDate()) : null);
                ps.setObject(4, o.getIdDm(), Types.BIGINT);

                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) o.setIdOrd(keys.getLong(1));
                }

                o.setId(idEntite);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Ordonnance.create failed", ex);
        }
        return null;
    }

    @Override
    public Ordonnance update(Ordonnance o) {
        if (o == null || o.getIdOrd() == null)
            throw new IllegalArgumentException("Ordonnance.idOrd is null");

        String sql = """
            UPDATE ordonnance
            SET id_consultation = ?,
                date = ?,
                id_dm = ?
            WHERE id_ord = ?
            """;

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setObject(1, o.getIdConsultation(), Types.BIGINT);
            ps.setDate(2, o.getDate() != null ? Date.valueOf(o.getDate()) : null);
            ps.setObject(3, o.getIdDm(), Types.BIGINT);
            ps.setLong(4, o.getIdOrd());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Aucune ordonnance mise à jour, id=" + o.getIdOrd());
            }

            updateEntiteTouch(o.getId(), o.getModifiePar());
            return o;
        } catch (SQLException ex) {
            throw new RuntimeException("Ordonnance.update failed", ex);
        }
    }

    @Override
    public void delete(Ordonnance o) {
        if (o == null) return;
        deleteById(o.getIdOrd());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        Ordonnance o = findById(id);
        // d'abord supprimer prescriptions (si FK)
        removeAllPrescriptions(id);

        String sql = "DELETE FROM ordonnance WHERE id_ord = ?";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Ordonnance.deleteById failed", ex);
        }

        if (o != null && o.getId() != null) deleteEntite(o.getId());
    }

    // =========================
    // Finders
    // =========================

    @Override
    public List<Ordonnance> findByDossierMedical(Long idDm) {
        String sql = """
            SELECT o.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM ordonnance o
            LEFT JOIN entite e ON e.id_entite = o.id_entite
            WHERE o.id_dm = ?
            ORDER BY o.date DESC, o.id_ord DESC
            """;
        List<Ordonnance> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setObject(1, idDm, Types.BIGINT);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapOrdonnance(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Ordonnance.findByDossierMedical failed", ex);
        }
        return out;
    }

    @Override
    public List<Ordonnance> findByConsultation(Long idConsultation) {
        String sql = """
            SELECT o.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM ordonnance o
            LEFT JOIN entite e ON e.id_entite = o.id_entite
            WHERE o.id_consultation = ?
            ORDER BY o.id_ord DESC
            """;
        List<Ordonnance> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setObject(1, idConsultation, Types.BIGINT);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapOrdonnance(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Ordonnance.findByConsultation failed", ex);
        }
        return out;
    }

    @Override
    public List<Prescription> getPrescriptions(Long idOrd) {
        String sql = """
            SELECT p.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM prescription p
            LEFT JOIN entite e ON e.id_entite = p.id_entite
            WHERE p.id_ordonnance = ?
            ORDER BY p.id_pr
            """;
        List<Prescription> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setObject(1, idOrd, Types.BIGINT);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPrescription(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Ordonnance.getPrescriptions failed", ex);
        }
        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM ordonnance WHERE id_ord = ? LIMIT 1";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Ordonnance.existsById failed", ex);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM ordonnance";
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException ex) {
            throw new RuntimeException("Ordonnance.count failed", ex);
        }
    }

    @Override
    public List<Ordonnance> findPage(int limit, int offset) {
        String sql = """
            SELECT o.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM ordonnance o
            LEFT JOIN entite e ON e.id_entite = o.id_entite
            ORDER BY o.id_ord
            LIMIT ? OFFSET ?
            """;
        List<Ordonnance> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapOrdonnance(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Ordonnance.findPage failed", ex);
        }
        return out;
    }

    // =========================
    // Helpers
    // =========================

    private void removeAllPrescriptions(Long idOrd) {
        String sql = "DELETE FROM prescription WHERE id_ordonnance = ?";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idOrd);
            ps.executeUpdate();
        } catch (SQLException ex) {
            // si la table n'existe pas encore : tu verras l'erreur -> crée-la
            throw new RuntimeException("Ordonnance.removeAllPrescriptions failed", ex);
        }
    }

    private Long insertEntiteIfNeeded(String creePar, String modifiePar, Long existingIdEntite) throws SQLException {
        if (existingIdEntite != null) return existingIdEntite;

        String sql = """
            INSERT INTO entite (date_creation, date_derniere_modification, cree_par, modifie_par)
            VALUES (CURRENT_DATE, CURRENT_TIMESTAMP, ?, ?)
            """;
        try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, creePar);
            ps.setString(2, modifiePar);
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

    private void deleteEntite(Long idEntite) {
        String sql = "DELETE FROM entite WHERE id_entite = ?";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idEntite);
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}