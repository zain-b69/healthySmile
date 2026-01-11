package ma.dentalTech.repository.modules.cabinet.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.cabinet.api.ChargesRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChargesRepositoryImpl implements ChargesRepository {

    // ========== AJOUTER CES 2 LIGNES ==========
    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    private Connection cn() throws SQLException {
        return sessionFactory.getConnection();
    }
    // ==========================================

    // -------- CRUD --------

    @Override
    public List<Charges> findAll() {
        String sql = """
        SELECT 
            c.id_charge,
            c.id_cabinet,
            c.id_entite,
            c.titre,
            c.description,
            c.montant,
            c.date_charge,
            e.date_creation,
            e.date_derniere_modification,
            e.cree_par,
            e.modifie_par
        FROM charge c
        LEFT JOIN entite e ON c.id_entite = e.id_entite
        ORDER BY c.date_charge DESC
        """;

        List<Charges> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(RowMappers.mapCharges(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des charges", e);
        }
        return out;
    }

    @Override
    public Charges findById(Long id) {
        String sql = """
        SELECT 
            c.id_charge,
            c.id_cabinet,
            c.id_entite,
            c.titre,
            c.description,
            c.montant,
            c.date_charge,
            e.date_creation,
            e.date_derniere_modification,
            e.cree_par,
            e.modifie_par
        FROM charge c
        LEFT JOIN entite e ON c.id_entite = e.id_entite
        WHERE c.id_charge = ?
        """;

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapCharges(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la charge", e);
        }
    }

    @Override
    public void create(Charges charge) {
        String sql = """
            INSERT INTO charge(titre, description, montant, date_charge, id_cabinet)
            VALUES(?,?,?,?,?)
            """;

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, charge.getTitre());
            ps.setString(2, charge.getDescription());
            ps.setDouble(3, charge.getMontant());
            ps.setTimestamp(4, Timestamp.valueOf(charge.getDate()));
            ps.setLong(5, charge.getIdCabinet());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) charge.setIdCharge(keys.getLong(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Charges charge) {
        String sql = """
            UPDATE charge SET
              titre=?, description=?, montant=?, date_charge=?, id_cabinet=?
            WHERE id_charge=?
            """;

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, charge.getTitre());
            ps.setString(2, charge.getDescription());
            ps.setDouble(3, charge.getMontant());
            ps.setTimestamp(4, Timestamp.valueOf(charge.getDate()));
            ps.setLong(5, charge.getIdCabinet());
            ps.setLong(6, charge.getIdCharge());

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Charges charge) {
        if (charge != null) deleteById(charge.getIdCharge());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM charge WHERE id_charge=?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // -------- FINDERS --------

    @Override
    public List<Charges> findByCabinet(Long cabinetId) {
        String sql = """
        SELECT 
            c.id_charge,
            c.id_cabinet,
            c.id_entite,
            c.titre,
            c.description,
            c.montant,
            c.date_charge,
            e.date_creation,
            e.date_derniere_modification,
            e.cree_par,
            e.modifie_par
        FROM charge c
        LEFT JOIN entite e ON c.id_entite = e.id_entite
        WHERE c.id_cabinet=?
        ORDER BY c.date_charge DESC
        """;

        List<Charges> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCharges(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Charges> findByCabinetAndDateBetween(Long cabinetId, LocalDateTime start, LocalDateTime end) {
        String sql = """
        SELECT 
            c.id_charge,
            c.id_cabinet,
            c.id_entite,
            c.titre,
            c.description,
            c.montant,
            c.date_charge,
            e.date_creation,
            e.date_derniere_modification,
            e.cree_par,
            e.modifie_par
        FROM charge c
        LEFT JOIN entite e ON c.id_entite = e.id_entite
        WHERE c.id_cabinet=? AND c.date_charge BETWEEN ? AND ?
        ORDER BY c.date_charge DESC
        """;

        List<Charges> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setTimestamp(2, Timestamp.valueOf(start));
            ps.setTimestamp(3, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCharges(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Charges> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        String sql = """
        SELECT 
            c.id_charge,
            c.id_cabinet,
            c.id_entite,
            c.titre,
            c.description,
            c.montant,
            c.date_charge,
            e.date_creation,
            e.date_derniere_modification,
            e.cree_par,
            e.modifie_par
        FROM charge c
        LEFT JOIN entite e ON c.id_entite = e.id_entite
        WHERE c.date_charge BETWEEN ? AND ?
        ORDER BY c.date_charge DESC
        """;

        List<Charges> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCharges(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM charge WHERE id_charge=?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM charge";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Charges> findPage(int limit, int offset) {
        String sql = """
        SELECT 
            c.id_charge,
            c.id_cabinet,
            c.id_entite,
            c.titre,
            c.description,
            c.montant,
            c.date_charge,
            e.date_creation,
            e.date_derniere_modification,
            e.cree_par,
            e.modifie_par
        FROM charge c
        LEFT JOIN entite e ON c.id_entite = e.id_entite
        ORDER BY c.date_charge DESC LIMIT ? OFFSET ?
        """;

        List<Charges> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCharges(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    // -------- RELATION --------

    @Override
    public CabinetMedical getCabinetOfCharge(Long chargeId) {
        String sql = """
            SELECT c.*
            FROM cabinet_medicale c
            JOIN charge ch ON ch.id_cabinet = c.id_cabinet
            WHERE ch.id_charge=?
            """;

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, chargeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapCabinetMedical(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}