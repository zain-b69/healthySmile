package ma.dentalTech.repository.modules.cabinet.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.enums.CategorieStatistique;
import ma.dentalTech.entities.users.Staff;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.cabinet.api.CabinetMedicalRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CabinetMedicalRepositoryImpl implements CabinetMedicalRepository {

    // -------- CRUD --------

    @Override
    public List<CabinetMedical> findAll() {
        String sql = "SELECT * FROM cabinet_medicale ORDER BY nom";
        List<CabinetMedical> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapCabinetMedical(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public CabinetMedical findById(Long id) {
        String sql = "SELECT * FROM cabinet_medicale WHERE id_cabinet = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
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
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

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
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // -------- FINDERS --------

    @Override
    public Optional<CabinetMedical> findByNom(String nom) {
        String sql = "SELECT * FROM cabinet_medicale WHERE nom=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
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
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
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
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
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
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM cabinet_medicale";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<CabinetMedical> findPage(int limit, int offset) {
        String sql = "SELECT * FROM cabinet_medicale ORDER BY nom LIMIT ? OFFSET ?";
        List<CabinetMedical> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
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
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setLong(2, staffId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void removeStaffFromCabinet(Long cabinetId, Long staffId) {
        String sql = "DELETE FROM Cabinet_Staff WHERE cabinet_id=? AND staff_id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setLong(2, staffId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void removeAllStaffFromCabinet(Long cabinetId) {
        String sql = "DELETE FROM Cabinet_Staff WHERE cabinet_id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
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
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
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
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, staffId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCabinetMedical(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Charges> getChargesOfCabinet(Long cabinetId) {
        List<Charges> charges = new ArrayList<>();
        String sql = "SELECT * FROM charge WHERE id_cabinet = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            var rs = ps.executeQuery();

            while (rs.next()) {
                Charges charge = new Charges();
                charge.setIdCharge(rs.getLong("id_charge"));
                charge.setTitre(rs.getString("titre"));
                charge.setDescription(rs.getString("description"));
                charge.setMontant(rs.getDouble("montant"));
                charge.setDate(rs.getTimestamp("date_charge").toLocalDateTime()); // assuming DB column is date_charge
                charge.setIdCabinet(rs.getLong("id_cabinet"));

                charges.add(charge);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des charges du cabinet", e);
        }

        return charges;
    }

    @Override
    public List<Revenues> getRevenuesOfCabinet(Long cabinetId) {
        List<Revenues> revenues = new ArrayList<>();
        String sql = "SELECT * FROM revenu WHERE id_cabinet = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            var rs = ps.executeQuery();

            while (rs.next()) {
                Revenues revenue = new Revenues();
                revenue.setIdRevenu(rs.getLong("id_revenu"));
                revenue.setTitre(rs.getString("titre"));
                revenue.setDescription(rs.getString("description"));
                revenue.setMontant(rs.getDouble("montant"));
                revenue.setDate(rs.getTimestamp("date_revenu").toLocalDateTime()); // assuming DB column is date_revenu
                revenue.setIdCabinet(rs.getLong("id_cabinet"));

                revenues.add(revenue);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des revenus du cabinet", e);
        }

        return revenues;
    }

    @Override
    public List<Statistiques> getStatistiquesOfCabinet(Long cabinetId) {
        List<Statistiques> stats = new ArrayList<>();
        String sql = "SELECT * FROM statistiques WHERE id_cabinet = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            var rs = ps.executeQuery();

            while (rs.next()) {
                Statistiques stat = new Statistiques();
                stat.setIdStatistique(rs.getLong("id_statistique"));
                stat.setIdCabinet(rs.getLong("id_cabinet"));
                stat.setPeriode(rs.getString("periode"));
                stat.setDateDebut(rs.getDate("date_debut").toLocalDate());
                stat.setDateFin(rs.getDate("date_fin").toLocalDate());
                stat.setNbPatients(rs.getInt("nb_patients"));
                stat.setNbRdv(rs.getInt("nb_rdv"));
                stat.setNbConsultation(rs.getInt("nb_consultation"));
                stat.setRevenuTotal(rs.getDouble("revenu_total"));
                stat.setTauxAnnulation(rs.getDouble("taux_annulation"));
                stat.setActePlusEffectif(rs.getString("acte_plus_effectif"));
                stat.setCategorie(CategorieStatistique.valueOf(rs.getString("categorie"))); // assuming stored as String

                stats.add(stat);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des statistiques du cabinet", e);
        }

        return stats;
    }
}
