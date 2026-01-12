package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.SituationFinanciere;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.SituationFinanciereRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Impl JDBC pour SituationFinanciere (compatible DB sans colonne en_promo).
 */
public class SituationFinanciereRepoImpl implements SituationFinanciereRepo {

    private Connection cn() throws SQLException {
        return SessionFactory.getInstance().getConnection();
    }

    // =========================
    // CRUD
    // =========================

    @Override
    public List<SituationFinanciere> findAll() {
        String sql = """
            SELECT sf.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM situation_financiere sf
            LEFT JOIN entite e ON e.id_entite = sf.id_entite
            ORDER BY sf.id_sf
            """;

        List<SituationFinanciere> out = new ArrayList<>();
        try (Connection c = cn();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapSituationFinanciere(rs));
        } catch (SQLException ex) {
            throw new RuntimeException("SituationFinanciere.findAll failed", ex);
        }
        return out;
    }

    @Override
    public SituationFinanciere findById(Long id) {
        if (id == null) return null;

        String sql = """
            SELECT sf.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM situation_financiere sf
            LEFT JOIN entite e ON e.id_entite = sf.id_entite
            WHERE sf.id_sf = ?
            """;

        try (Connection c = cn();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapSituationFinanciere(rs) : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SituationFinanciere.findById failed", ex);
        }
    }

    @Override
    public void create(SituationFinanciere sf) {
        if (sf == null) throw new IllegalArgumentException("SituationFinanciere is null");

        String sql = """
            INSERT INTO situation_financiere (
                id_entite, totale_des_actes, totale_paye, credit, statut, id_dm
            ) VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection c = cn()) {
            Long idEntite = insertEntiteIfNeeded(c, sf.getCreePar(), sf.getModifiePar(), sf.getId());

            // DB sans en_promo > on persiste uniquement statut
            StatutSituationFinanciere statutToPersist = deriveStatutToPersist(sf);
            sf.setStatut(statutToPersist);
            sf.setEnPromo(statutToPersist == StatutSituationFinanciere.EN_PROMO);

            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setObject(1, idEntite, Types.BIGINT);
                ps.setObject(2, sf.getTotaleDesActes(), Types.DOUBLE);
                ps.setObject(3, sf.getTotalePaye(), Types.DOUBLE);
                ps.setObject(4, sf.getCredit(), Types.DOUBLE);
                ps.setString(5, statutToPersist != null ? statutToPersist.name() : null);
                ps.setObject(6, sf.getIdDm(), Types.BIGINT);

                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) sf.setIdSf(keys.getLong(1));
                }

                // BaseEntity.id = id_entite
                sf.setId(idEntite);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SituationFinanciere.create failed", ex);
        }
    }

    @Override
    public void update(SituationFinanciere sf) {
        if (sf == null || sf.getIdSf() == null)
            throw new IllegalArgumentException("SituationFinanciere.idSf is null");

        String sql = """
            UPDATE situation_financiere
            SET totale_des_actes = ?,
                totale_paye = ?,
                credit = ?,
                statut = ?,
                id_dm = ?
            WHERE id_sf = ?
            """;

        // DB sans en_promo => statut seulement
        StatutSituationFinanciere statutToPersist = deriveStatutToPersist(sf);
        sf.setStatut(statutToPersist);
        sf.setEnPromo(statutToPersist == StatutSituationFinanciere.EN_PROMO);

        try (Connection c = cn();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setObject(1, sf.getTotaleDesActes(), Types.DOUBLE);
            ps.setObject(2, sf.getTotalePaye(), Types.DOUBLE);
            ps.setObject(3, sf.getCredit(), Types.DOUBLE);
            ps.setString(4, statutToPersist != null ? statutToPersist.name() : null);
            ps.setObject(5, sf.getIdDm(), Types.BIGINT);
            ps.setLong(6, sf.getIdSf());

            ps.executeUpdate();
            updateEntiteTouch(c, sf.getId(), sf.getModifiePar());

        } catch (SQLException ex) {
            throw new RuntimeException("SituationFinanciere.update failed", ex);
        }
    }

    @Override
    public void delete(SituationFinanciere sf) {
        if (sf == null) return;
        deleteById(sf.getIdSf());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        SituationFinanciere sf = findById(id);

        String sql = "DELETE FROM situation_financiere WHERE id_sf = ?";
        try (Connection c = cn();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("SituationFinanciere.deleteById failed", ex);
        }

        if (sf != null && sf.getId() != null) {
            deleteEntite(sf.getId());
        }
    }

    // =========================
    // Finders
    // =========================

    @Override
    public SituationFinanciere findByDossierMedical(Long idDm) {
        String sql = """
            SELECT sf.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM situation_financiere sf
            LEFT JOIN entite e ON e.id_entite = sf.id_entite
            WHERE sf.id_dm = ?
            LIMIT 1
            """;

        try (Connection c = cn();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setObject(1, idDm, Types.BIGINT);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapSituationFinanciere(rs) : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SituationFinanciere.findByDossierMedical failed", ex);
        }
    }

    @Override
    public List<SituationFinanciere> findByStatut(StatutSituationFinanciere statut) {
        String sql = """
            SELECT sf.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM situation_financiere sf
            LEFT JOIN entite e ON e.id_entite = sf.id_entite
            WHERE sf.statut = ?
            ORDER BY sf.id_sf DESC
            """;

        List<SituationFinanciere> out = new ArrayList<>();
        try (Connection c = cn();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, statut != null ? statut.name() : null);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapSituationFinanciere(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SituationFinanciere.findByStatut failed", ex);
        }
        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM situation_financiere WHERE id_sf = ? LIMIT 1";
        try (Connection c = cn();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SituationFinanciere.existsById failed", ex);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM situation_financiere";
        try (Connection c = cn();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getLong(1);
        } catch (SQLException ex) {
            throw new RuntimeException("SituationFinanciere.count failed", ex);
        }
    }

    @Override
    public List<SituationFinanciere> findPage(int limit, int offset) {
        String sql = """
            SELECT sf.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM situation_financiere sf
            LEFT JOIN entite e ON e.id_entite = sf.id_entite
            ORDER BY sf.id_sf
            LIMIT ? OFFSET ?
            """;

        List<SituationFinanciere> out = new ArrayList<>();
        try (Connection c = cn();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapSituationFinanciere(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("SituationFinanciere.findPage failed", ex);
        }
        return out;
    }

    // =========================
    // Helpers
    // =========================

    /**
     * DB n'a pas en_promo > promo est représentée par statut = EN_PROMO.
     * On dérive un statut cohérent si null.
     */
    private StatutSituationFinanciere deriveStatutToPersist(SituationFinanciere sf) {
        if (sf == null) return null;

        if (sf.getStatut() != null) return sf.getStatut();

        // si l'objet possède un flag enPromo, on le convertit en statut
        if (Boolean.TRUE.equals(sf.getEnPromo())) return StatutSituationFinanciere.EN_PROMO;

        Double credit = sf.getCredit();
        if (credit != null && credit > 0.000001) return StatutSituationFinanciere.EN_CREDIT;

        return StatutSituationFinanciere.EN_REGLE;
    }

    private Long insertEntiteIfNeeded(Connection c, String creePar, String modifiePar, Long existingIdEntite) throws SQLException {
        if (existingIdEntite != null) return existingIdEntite;

        String sql = """
            INSERT INTO entite (date_creation, date_derniere_modification, cree_par, modifie_par)
            VALUES (CURRENT_DATE, CURRENT_TIMESTAMP, ?, ?)
            """;

        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, creePar);
            ps.setString(2, modifiePar);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : null;
            }
        }
    }

    private void updateEntiteTouch(Connection c, Long idEntite, String modifiePar) throws SQLException {
        if (idEntite == null) return;

        String sql = """
            UPDATE entite
            SET date_derniere_modification = CURRENT_TIMESTAMP,
                modifie_par = ?
            WHERE id_entite = ?
            """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, modifiePar);
            ps.setLong(2, idEntite);
            ps.executeUpdate();
        }
    }

    private void deleteEntite(Long idEntite) {
        String sql = "DELETE FROM entite WHERE id_entite = ?";
        try (Connection c = cn();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, idEntite);
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}
