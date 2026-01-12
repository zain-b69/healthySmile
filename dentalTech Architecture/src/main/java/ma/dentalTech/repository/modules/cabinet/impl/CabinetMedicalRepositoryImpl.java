package ma.dentalTech.repository.modules.cabinet.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.enums.CategorieStatistique;
import ma.dentalTech.entities.users.Staff;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.cabinet.api.CabinetMedicalRepository;
import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.entities.cabinet.Statistiques;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CabinetMedicalRepositoryImpl implements CabinetMedicalRepository {

    // ========== AJOUTER CES 2 LIGNES ==========
    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    private Connection cn() throws SQLException {
        return sessionFactory.getConnection();
    }
    // ==========================================

    // -------- CRUD --------

    @Override
    public List<CabinetMedical> findAll() {
        String sql = "SELECT * FROM cabinet_medicale ORDER BY nom";
        List<CabinetMedical> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapCabinetMedical(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public CabinetMedical findById(Long id) {
        String sql = "SELECT * FROM cabinet_medicale WHERE id_cabinet = ?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapCabinetMedical(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(CabinetMedical cabinet) {
        String sql = """
            INSERT INTO cabinet_medicale
            (id_user, nom, email, logo, adresse, cin, tel1, tel2, site_web,
             instagram, facebook, description)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
            """;

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, cabinet.getIdUser());
            ps.setString(2, cabinet.getNom());
            ps.setString(3, cabinet.getEmail());
            ps.setString(4, cabinet.getLogo());
            ps.setString(5, cabinet.getAdresse());
            ps.setString(6, cabinet.getCin());
            ps.setString(7, cabinet.getTel1());
            ps.setString(8, cabinet.getTel2());
            ps.setString(9, cabinet.getSiteWeb());
            ps.setString(10, cabinet.getInstagram());
            ps.setString(11, cabinet.getFacebook());
            ps.setString(12, cabinet.getDescription());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) cabinet.setIdCabinet(keys.getLong(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(CabinetMedical cabinet) {
        String sql = """
            UPDATE cabinet_medicale SET
              nom=?, email=?, logo=?, adresse=?, cin=?, tel1=?, tel2=?,
              site_web=?, instagram=?, facebook=?, description=?
            WHERE id_cabinet=?
            """;

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, cabinet.getNom());
            ps.setString(2, cabinet.getEmail());
            ps.setString(3, cabinet.getLogo());
            ps.setString(4, cabinet.getAdresse());
            ps.setString(5, cabinet.getCin());
            ps.setString(6, cabinet.getTel1());
            ps.setString(7, cabinet.getTel2());
            ps.setString(8, cabinet.getSiteWeb());
            ps.setString(9, cabinet.getInstagram());
            ps.setString(10, cabinet.getFacebook());
            ps.setString(11, cabinet.getDescription());
            ps.setLong(12, cabinet.getIdCabinet());

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(CabinetMedical cabinet) {
        if (cabinet != null) deleteById(cabinet.getIdCabinet());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM cabinet_medicale WHERE id_cabinet=?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // -------- FINDERS --------

    @Override
    public Optional<CabinetMedical> findByNom(String nom) {
        String sql = "SELECT * FROM cabinet_medicale WHERE nom=?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapCabinetMedical(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<CabinetMedical> findByEmail(String email) {
        String sql = "SELECT * FROM cabinet_medicale WHERE email=?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapCabinetMedical(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<CabinetMedical> searchByNomOrAdresse(String keyword) {
        String sql = "SELECT * FROM cabinet_medicale WHERE nom LIKE ? OR adresse LIKE ?";
        List<CabinetMedical> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCabinetMedical(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM cabinet_medicale WHERE id_cabinet=?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM cabinet_medicale";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<CabinetMedical> findPage(int limit, int offset) {
        String sql = "SELECT * FROM cabinet_medicale ORDER BY nom LIMIT ? OFFSET ?";
        List<CabinetMedical> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCabinetMedical(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    // -------- RELATION CABINET ↔ STAFF (Many-to-Many) --------

    @Override
    public void addStaffToCabinet(Long cabinetId, Long staffId) {
        String sql = "INSERT INTO Cabinet_Staff(cabinet_id, staff_id) VALUES(?,?)";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setLong(2, staffId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void removeStaffFromCabinet(Long cabinetId, Long staffId) {
        String sql = "DELETE FROM Cabinet_Staff WHERE cabinet_id=? AND staff_id=?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setLong(2, staffId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void removeAllStaffFromCabinet(Long cabinetId) {
        String sql = "DELETE FROM Cabinet_Staff WHERE cabinet_id=?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Staff> getStaffOfCabinet(Long cabinetId) {
        String sql = """
            SELECT s.*
            FROM Staff s
            JOIN Cabinet_Staff cs ON cs.staff_id = s.id_staff
            WHERE cs.cabinet_id=?
            """;
        List<Staff> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStaff(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<CabinetMedical> getCabinetsOfStaff(Long staffId) {
        String sql = """
            SELECT c.*
            FROM cabinet_medicale c
            JOIN Cabinet_Staff cs ON cs.cabinet_id = c.id_cabinet
            WHERE cs.staff_id=?
            """;
        List<CabinetMedical> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, staffId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCabinetMedical(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }
}