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

    // ========== AJOUTER CES 2 LIGNES ==========
    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    private Connection cn() throws SQLException {
        return sessionFactory.getConnection();
    }
    // ==========================================

    // ---------- BASE SELECT QUERY ----------
    private static final String BASE_SELECT = """
        SELECT 
            s.id_statistique,
            s.id_cabinet,
            s.id_entite,
            s.periode,
            s.date_debut,
            s.date_fin,
            s.nb_patients,
            s.nb_rdv,
            s.nb_consultation,
            s.revenu_total,
            s.taux_annulation,
            s.acte_plus_effectif,
            s.categorie,
            e.date_creation,
            e.date_derniere_modification,
            e.cree_par,
            e.modifie_par
        FROM statistiques s
        LEFT JOIN entite e ON s.id_entite = e.id_entite
    """;

    // ---------- CRUD ----------

    @Override
    public List<Statistiques> findAll() {
        String sql = BASE_SELECT + " ORDER BY s.date_fin DESC";

        List<Statistiques> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(RowMappers.mapStatistiques(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des statistiques", e);
        }
        return out;
    }

    @Override
    public Statistiques findById(Long id) {
        String sql = BASE_SELECT + " WHERE s.id_statistique = ?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapStatistiques(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la statistique", e);
        }
    }

    @Override
    public void create(Statistiques s) {
        String sql = """
        INSERT INTO statistiques(
            id_cabinet, periode, date_debut, date_fin,
            nb_patients, nb_rdv, nb_consultation,
            revenu_total, taux_annulation, acte_plus_effectif, categorie
        ) VALUES (?,?,?,?,?,?,?,?,?,?,?)
        """;

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, s.getIdCabinet());
            ps.setString(2, s.getPeriode());
            ps.setDate(3, Date.valueOf(s.getDateDebut()));
            ps.setDate(4, Date.valueOf(s.getDateFin()));
            ps.setObject(5, s.getNbPatients());
            ps.setObject(6, s.getNbRdv());
            ps.setObject(7, s.getNbConsultation());
            ps.setObject(8, s.getRevenuTotal());
            ps.setObject(9, s.getTauxAnnulation());
            ps.setString(10, s.getActePlusEffectif());
            ps.setString(11, s.getCategorie().name());

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
                id_cabinet=?, periode=?,
                date_debut=?, date_fin=?,
                nb_patients=?, nb_rdv=?, nb_consultation=?,
                revenu_total=?, taux_annulation=?,
                acte_plus_effectif=?, categorie=?
            WHERE id_statistique=?
            """;

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {

            // Removed id_entite parameter
            ps.setLong(1, s.getIdCabinet());
            ps.setString(2, s.getPeriode());
            ps.setDate(3, Date.valueOf(s.getDateDebut()));
            ps.setDate(4, Date.valueOf(s.getDateFin()));
            ps.setObject(5, s.getNbPatients());
            ps.setObject(6, s.getNbRdv());
            ps.setObject(7, s.getNbConsultation());
            ps.setObject(8, s.getRevenuTotal());
            ps.setObject(9, s.getTauxAnnulation());
            ps.setString(10, s.getActePlusEffectif());
            ps.setString(11, s.getCategorie().name());
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

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ---------- FINDERS ----------

    @Override
    public List<Statistiques> findByCabinet(Long cabinetId) {
        String sql = BASE_SELECT + " WHERE s.id_cabinet = ? ORDER BY s.date_fin DESC";
        return findList(sql, cabinetId);
    }

    @Override
    public List<Statistiques> findByCategorie(CategorieStatistique categorie) {
        String sql = BASE_SELECT + " WHERE s.categorie = ? ORDER BY s.date_fin DESC";
        return findList(sql, categorie.name());
    }

    @Override
    public List<Statistiques> findByCabinetAndCategorie(Long cabinetId, CategorieStatistique categorie) {
        String sql = BASE_SELECT + " WHERE s.id_cabinet = ? AND s.categorie = ? ORDER BY s.date_fin DESC";
        return findList(sql, cabinetId, categorie.name());
    }

    @Override
    public List<Statistiques> findByDate(LocalDate dateCalcul) {
        String sql = BASE_SELECT + " WHERE s.date_fin = ? ORDER BY s.date_fin DESC";
        return findList(sql, Date.valueOf(dateCalcul));
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM statistiques WHERE id_statistique=?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {

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

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Statistiques> findPage(int limit, int offset) {
        String sql = BASE_SELECT + " ORDER BY s.date_fin DESC LIMIT ? OFFSET ?";
        return findList(sql, limit, offset);
    }

    // ---------- UTILS ----------

    private List<Statistiques> findList(String sql, Object... params) {
        List<Statistiques> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {

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