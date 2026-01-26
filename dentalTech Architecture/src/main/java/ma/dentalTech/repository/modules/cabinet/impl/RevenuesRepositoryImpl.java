package ma.dentalTech.repository.modules.cabinet.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.cabinet.api.RevenuesRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RevenuesRepositoryImpl implements RevenuesRepository {

    // ========== AJOUTER CES 2 LIGNES ==========
    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    private Connection cn() throws SQLException {
        return sessionFactory.getConnection();
    }
    // ==========================================

    // -------- CRUD --------

    @Override
    public List<Revenues> findAll() {
        String sql = """
        SELECT 
            r.id_revenu,
            r.id_cabinet,
            r.id_entite,
            r.titre,
            r.description,
            r.montant,
            r.date_revenu,
            e.date_creation,
            e.date_derniere_modification,
            e.cree_par,
            e.modifie_par
        FROM revenu r
        LEFT JOIN entite e ON r.id_entite = e.id_entite
        ORDER BY r.date_revenu DESC
        """;

        List<Revenues> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(RowMappers.mapRevenues(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des revenus", e);
        }
        return out;
    }


    @Override
    public Revenues findById(Long id) {
        String sql = """
        SELECT 
            r.id_revenu,
            r.id_cabinet,
            r.id_entite,
            r.titre,
            r.description,
            r.montant,
            r.date_revenu,
            e.date_creation,
            e.date_derniere_modification,
            e.cree_par,
            e.modifie_par
        FROM revenu r
        LEFT JOIN entite e ON r.id_entite = e.id_entite
        WHERE r.id_revenu = ?
        """;

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapRevenues(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du revenu", e);
        }
    }


    @Override
    public Revenues create(Revenues revenue) {
        String sql = """
            INSERT INTO Revenu(titre, description, montant, date_revenu, id_cabinet)
            VALUES(?,?,?,?,?)
            """;

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, revenue.getTitre());
            ps.setString(2, revenue.getDescription());
            ps.setDouble(3, revenue.getMontant());
            ps.setTimestamp(4, Timestamp.valueOf(revenue.getDate()));
            ps.setLong(5, revenue.getIdCabinet());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) revenue.setIdRevenu(keys.getLong(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public Revenues update(Revenues revenue) {
        String sql = """
            UPDATE Revenu SET
              titre=?, description=?, montant=?, date_revenu=?, id_cabinet=?
            WHERE id_revenu=?
            """;

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {

            ps.setString(1, revenue.getTitre());
            ps.setString(2, revenue.getDescription());
            ps.setDouble(3, revenue.getMontant());
            ps.setTimestamp(4, Timestamp.valueOf(revenue.getDate()));
            ps.setLong(5, revenue.getIdCabinet());
            ps.setLong(6, revenue.getIdRevenu());

            ps.executeUpdate();

        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public void delete(Revenues revenue) {
        if (revenue != null) deleteById(revenue.getIdRevenu());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Revenu WHERE id_revenu=?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // -------- FINDERS --------

    @Override
    public List<Revenues> findByCabinet(Long cabinetId) {
        String sql = """
        SELECT 
            r.id_revenu,
            r.id_cabinet,
            r.id_entite,
            r.titre,
            r.description,
            r.montant,
            r.date_revenu,
            e.date_creation,
            e.date_derniere_modification,
            e.cree_par,
            e.modifie_par
        FROM revenu r
        LEFT JOIN entite e ON r.id_entite = e.id_entite
        WHERE r.id_cabinet=?
        ORDER BY r.date_revenu DESC
        """;

        List<Revenues> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRevenues(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Revenues> findByCabinetAndDateBetween(Long cabinetId, LocalDateTime start, LocalDateTime end) {
        String sql = """
        SELECT 
            r.id_revenu,
            r.id_cabinet,
            r.id_entite,
            r.titre,
            r.description,
            r.montant,
            r.date_revenu,
            e.date_creation,
            e.date_derniere_modification,
            e.cree_par,
            e.modifie_par
        FROM revenu r
        LEFT JOIN entite e ON r.id_entite = e.id_entite
        WHERE r.id_cabinet=? AND r.date_revenu BETWEEN ? AND ?
        ORDER BY r.date_revenu DESC
        """;

        List<Revenues> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            ps.setTimestamp(2, Timestamp.valueOf(start));
            ps.setTimestamp(3, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRevenues(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Revenues> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        String sql = """
        SELECT 
            r.id_revenu,
            r.id_cabinet,
            r.id_entite,
            r.titre,
            r.description,
            r.montant,
            r.date_revenu,
            e.date_creation,
            e.date_derniere_modification,
            e.cree_par,
            e.modifie_par
        FROM revenu r
        LEFT JOIN entite e ON r.id_entite = e.id_entite
        WHERE r.date_revenu BETWEEN ? AND ?
        ORDER BY r.date_revenu DESC
        """;

        List<Revenues> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRevenues(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM revenu WHERE id_revenu=?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM Revenu";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Revenues> findPage(int limit, int offset) {
        String sql = """
        SELECT 
            r.id_revenu,
            r.id_cabinet,
            r.id_entite,
            r.titre,
            r.description,
            r.montant,
            r.date_revenu,
            e.date_creation,
            e.date_derniere_modification,
            e.cree_par,
            e.modifie_par
        FROM revenu r
        LEFT JOIN entite e ON r.id_entite = e.id_entite
        ORDER BY r.date_revenu DESC LIMIT ? OFFSET ?
        """;

        List<Revenues> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRevenues(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    // -------- RELATION --------

    @Override
    public CabinetMedical getCabinetOfRevenue(Long revenueId) {
        String sql = """
            SELECT c.*
            FROM cabinet_medicale c
            JOIN Revenu r ON r.id_cabinet = c.id_cabinet
            WHERE r.id_revenu=?
            """;

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, revenueId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapCabinetMedical(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}