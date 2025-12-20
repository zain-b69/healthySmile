package ma.dentalTech.repository.modules.cabinet.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.enums.CategorieStatistique;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.cabinet.api.StatistiquesRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StatistiquesRepositoryImpl implements StatistiquesRepository {

    // ---------- CRUD ----------

    @Override
    public List<Statistiques> findAll() {
        String sql = "SELECT * FROM statistiques ORDER BY date_fin DESC";
        List<Statistiques> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapStatistiques(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public Statistiques findById(Long id) {
        String sql = "SELECT * FROM statistiques WHERE id_statistique=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapStatistiques(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(Statistiques s) {
        String sql = """
            INSERT INTO statistiques(
                id_entite, id_cabinet, periode, date_debut, date_fin,
                nb_patients, nb_rdv, nb_consultation,
                revenu_total, taux_annulation, acte_plus_effectif, categorie
            ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setObject(1, s.getIdEntite());
            ps.setLong(2, s.getIdCabinet());
            ps.setString(3, s.getPeriode());
            ps.setDate(4, Date.valueOf(s.getDateDebut()));
            ps.setDate(5, Date.valueOf(s.getDateFin()));
            ps.setObject(6, s.getNbPatients());
            ps.setObject(7, s.getNbRdv());
            ps.setObject(8, s.getNbConsultation());
            ps.setObject(9, s.getRevenuTotal());
            ps.setObject(10, s.getTauxAnnulation());
            ps.setString(11, s.getActePlusEffectif());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) s.setIdStatistique(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Statistiques s) {
        String sql = """
            UPDATE statistiques SET
                id_entite=?, id_cabinet=?, periode=?,
                date_debut=?, date_fin=?,
                nb_patients=?, nb_rdv=?, nb_consultation=?,
                revenu_total=?, taux_annulation=?,
                acte_plus_effectif=?, categorie=?
            WHERE id_statistique=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setObject(1, s.getIdEntite());
            ps.setLong(2, s.getIdCabinet());
            ps.setString(3, s.getPeriode());
            ps.setDate(4, Date.valueOf(s.getDateDebut()));
            ps.setDate(5, Date.valueOf(s.getDateFin()));
            ps.setObject(6, s.getNbPatients());
            ps.setObject(7, s.getNbRdv());
            ps.setObject(8, s.getNbConsultation());
            ps.setObject(9, s.getRevenuTotal());
            ps.setObject(10, s.getTauxAnnulation());
            ps.setString(11, s.getActePlusEffectif());
            ps.setLong(12, s.getIdStatistique());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Statistiques s) {
        if (s != null) deleteById(s.getIdStatistique());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM statistiques WHERE id_statistique=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ---------- FINDERS ----------

    @Override
    public List<Statistiques> findByCabinet(Long cabinetId) {
        String sql = "SELECT * FROM Statistique WHERE id_cabinet=?";
        return findList(sql, cabinetId);
    }

    @Override
    public List<Statistiques> findByCategorie(CategorieStatistique categorie) {
        String sql = "SELECT * FROM Statistique WHERE categorie=?";
        return findList(sql, categorie.name());
    }

    @Override
    public List<Statistiques> findByCabinetAndCategorie(Long cabinetId, CategorieStatistique categorie) {
        String sql = "SELECT * FROM Statistique WHERE id_cabinet=? AND categorie=?";
        return findList(sql, cabinetId, categorie.name());
    }

    @Override
    public List<Statistiques> findByDate(LocalDate dateCalcul) {
        String sql = "SELECT * FROM Statistique WHERE date_fin=?";
        return findList(sql, Date.valueOf(dateCalcul));
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM statistiques WHERE id_statistique=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM statistiques";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Statistiques> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Statistique ORDER BY date_fin DESC LIMIT ? OFFSET ?";
        return findList(sql, limit, offset);
    }

    // ---------- UTILS ----------

    private List<Statistiques> findList(String sql, Object... params) {
        List<Statistiques> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStatistiques(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }
}