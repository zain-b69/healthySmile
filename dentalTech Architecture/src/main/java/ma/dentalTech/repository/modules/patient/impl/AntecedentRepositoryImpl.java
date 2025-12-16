package ma.dentalTech.repository.modules.patient.impl;

import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.enums.*;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AntecedentRepositoryImpl implements AntecedentRepository {

    // -------- CRUD --------
    @Override
    public List<Antecedent> findAll() {
        String sql = "SELECT * FROM Antecedents ORDER BY categorie, niveauRisque, nom";
        List<Antecedent> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Antecedent findById(Long id) {
        String sql = "SELECT * FROM Antecedents WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapAntecedent(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(Antecedent a) {
        String sql = "INSERT INTO Antecedents(nom, categorie, niveauRisque) VALUES(?,?,?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getNom());
            ps.setString(2, a.getCategorie().name());
            ps.setString(3, a.getNiveauRisque().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setId(keys.getLong(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Antecedent a) {
        String sql = "UPDATE Antecedents SET nom=?, categorie=?, niveauRisque=? WHERE id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, a.getNom());
            ps.setString(2, a.getCategorie().name());
            ps.setString(3, a.getNiveauRisque().name());
            ps.setLong(4, a.getId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Antecedent a) { if (a != null) deleteById(a.getId()); }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Antecedents WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // -------- Extras --------
    @Override
    public Optional<Antecedent> findByNom(String nom) {
        String sql = "SELECT * FROM Antecedents WHERE nom = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapAntecedent(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Antecedent> findByCategorie(CategorieAntecedent categorie) {
        String sql = "SELECT * FROM Antecedents WHERE categorie = ? ORDER BY nom";
        List<Antecedent> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, categorie.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Antecedent> findByNiveauRisque(NiveauRisque niveau) {
        String sql = "SELECT * FROM Antecedents WHERE niveauRisque = ? ORDER BY nom";
        List<Antecedent> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, niveau.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM Antecedents WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public long count() {
        String sql = "SELECT COUNT(*) FROM Antecedents";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Antecedent> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Antecedents ORDER BY categorie, niveauRisque, nom LIMIT ? OFFSET ?";
        List<Antecedent> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Patient> getPatientsHavingAntecedent(Long antecedentId) {
        String sql = """
            SELECT p.* 
            FROM Patients p 
            JOIN Patient_Antecedents pa ON pa.patient_id = p.id
            WHERE pa.antecedent_id = ?
            ORDER BY p.nom, p.prenom
            """;
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, antecedentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }
}
