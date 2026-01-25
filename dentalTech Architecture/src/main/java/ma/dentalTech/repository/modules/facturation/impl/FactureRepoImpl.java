package ma.dentalTech.repository.modules.facturation.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Facture;
import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.repository.modules.facturation.api.FactureRepo;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FactureRepoImpl implements FactureRepo {

    // ✅ no-arg constructor
    public FactureRepoImpl() {}

    // ------------------------------------------------------------
    // IMPORTANT : adapte cette méthode si ton SessionFactory diffère
    // ------------------------------------------------------------
    private Connection cn() throws SQLException {
        return SessionFactory.getInstance().getConnection();
    }

    @Override
    public Long create(Long idConsultation, Long idSituationFinanciere, double total, double reste) {
        String sql = """
            INSERT INTO facture (id_entite, totale_facture, reste, statut, date_facture, id_consultation, id_sf)
            VALUES (NULL, ?, ?, 'EN_ATTENTE', NOW(), ?, ?)
            """;

        try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, total);
            ps.setDouble(2, reste);
            ps.setLong(3, idConsultation);
            ps.setLong(4, idSituationFinanciere);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
            throw new IllegalStateException("INSERT facture OK mais id_facture non récupéré");
        } catch (SQLException e) {
            e.printStackTrace(); // TEMP: pour voir la cause dans console
            throw new RuntimeException("Facture.create failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Facture save(Facture f) {
        final String sql =
                "INSERT INTO facture (totale_facture, reste, statut, date_facture, id_consultation, id_sf) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection cn = cn();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setDoubleOrNull(ps, 1, f.getTotaleFacture());
            setDoubleOrNull(ps, 2, f.getReste());
            setStringOrNull(ps, 3, f.getStatut() == null ? null : f.getStatut().name());
            setTimestampOrNull(ps, 4, f.getDateFacture());
            setLongOrNull(ps, 5, f.getIdConsultation());
            setLongOrNull(ps, 6, f.getIdSf());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) f.setIdFacture(rs.getLong(1));
            }
            return f;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur save Facture", e);
        }
    }

    @Override
    public Facture update(Facture f) {
        if (f.getIdFacture() == null) throw new IllegalArgumentException("idFacture requis pour update");

        final String sql =
                "UPDATE facture SET totale_facture=?, reste=?, statut=?, date_facture=?, id_consultation=?, id_sf=? " +
                        "WHERE id_facture=?";

        try (Connection cn = cn();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            setDoubleOrNull(ps, 1, f.getTotaleFacture());
            setDoubleOrNull(ps, 2, f.getReste());
            setStringOrNull(ps, 3, f.getStatut() == null ? null : f.getStatut().name());
            setTimestampOrNull(ps, 4, f.getDateFacture());
            setLongOrNull(ps, 5, f.getIdConsultation());
            setLongOrNull(ps, 6, f.getIdSf());
            ps.setLong(7, f.getIdFacture());

            int updated = ps.executeUpdate();
            if (updated == 0) throw new RuntimeException("Facture introuvable: " + f.getIdFacture());

            return f;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update Facture", e);
        }
    }

    // -------------------------
    // ✅ Updates ciblés (IMPORTANT)
    // -------------------------

    @Override
    public boolean updateReste(Long idFacture, Double reste) {
        if (idFacture == null) throw new IllegalArgumentException("idFacture requis");

        final String sql = "UPDATE facture SET reste=? WHERE id_facture=?";

        try (Connection cn = cn();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            setDoubleOrNull(ps, 1, reste);
            ps.setLong(2, idFacture);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur updateReste Facture", e);
        }
    }

    @Override
    public boolean updateStatut(Long idFacture, StatutFacture statut) {
        if (idFacture == null) throw new IllegalArgumentException("idFacture requis");
        if (statut == null) throw new IllegalArgumentException("statut requis");

        final String sql = "UPDATE facture SET statut=? WHERE id_facture=?";

        try (Connection cn = cn();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, statut.name());
            ps.setLong(2, idFacture);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur updateStatut Facture", e);
        }
    }

    @Override
    public boolean updateResteAndStatut(Long idFacture, Double reste, StatutFacture statut) {
        if (idFacture == null) throw new IllegalArgumentException("idFacture requis");
        if (statut == null) throw new IllegalArgumentException("statut requis");

        final String sql = "UPDATE facture SET reste=?, statut=? WHERE id_facture=?";

        try (Connection cn = cn();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            setDoubleOrNull(ps, 1, reste);
            ps.setString(2, statut.name());
            ps.setLong(3, idFacture);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur updateResteAndStatut Facture", e);
        }
    }

    @Override
    public void updateTotaux(Long idFacture, double total, double reste) {
        String sql = "UPDATE facture SET totale_facture=?, reste=? WHERE id_facture=?";

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setDouble(1, total);
            ps.setDouble(2, reste);
            ps.setLong(3, idFacture);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Facture.updateTotaux failed", e);
        }
    }

    // -------------------------
    // Finders
    // -------------------------

    @Override
    public Optional<Facture> findById(Long idFacture) {
        final String sql =
                "SELECT id_facture, totale_facture, reste, statut, date_facture, id_consultation, id_sf " +
                        "FROM facture WHERE id_facture=?";

        return queryOne(sql, ps -> ps.setLong(1, idFacture));
    }

    @Override
    public List<Facture> findAll() {
        final String sql =
                "SELECT id_facture, totale_facture, reste, statut, date_facture, id_consultation, id_sf " +
                        "FROM facture";

        return queryList(sql, ps -> {});
    }

    @Override
    public boolean deleteById(Long idFacture) {
        final String sql = "DELETE FROM facture WHERE id_facture=?";

        try (Connection cn = cn();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, idFacture);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById Facture", e);
        }
    }

    @Override
    public Optional<Facture> findByIdConsultation(Long idConsultation) {
        final String sql =
                "SELECT id_facture, totale_facture, reste, statut, date_facture, id_consultation, id_sf " +
                        "FROM facture WHERE id_consultation=?";

        return queryOne(sql, ps -> ps.setLong(1, idConsultation));
    }

    @Override
    public List<Facture> findByIdSf(Long idSf) {
        final String sql =
                "SELECT id_facture, totale_facture, reste, statut, date_facture, id_consultation, id_sf " +
                        "FROM facture WHERE id_sf=?";

        return queryList(sql, ps -> ps.setLong(1, idSf));
    }

    @Override
    public List<Facture> findByStatut(StatutFacture statut) {
        final String sql =
                "SELECT id_facture, totale_facture, reste, statut, date_facture, id_consultation, id_sf " +
                        "FROM facture WHERE statut=?";

        return queryList(sql, ps -> ps.setString(1, statut == null ? null : statut.name()));
    }

    @Override
    public List<Facture> findByDateFactureBetween(LocalDateTime start, LocalDateTime end) {
        final String sql =
                "SELECT id_facture, totale_facture, reste, statut, date_facture, id_consultation, id_sf " +
                        "FROM facture WHERE date_facture BETWEEN ? AND ?";

        return queryList(sql, ps -> {
            setTimestampOrNull(ps, 1, start);
            setTimestampOrNull(ps, 2, end);
        });
    }

    // ---------------- Helpers ----------------

    private interface StatementBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    private Optional<Facture> queryOne(String sql, StatementBinder binder) {
        try (Connection cn = cn();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            binder.bind(ps);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur queryOne Facture", e);
        }
    }

    private List<Facture> queryList(String sql, StatementBinder binder) {
        try (Connection cn = cn();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            binder.bind(ps);

            try (ResultSet rs = ps.executeQuery()) {
                List<Facture> list = new ArrayList<>();
                while (rs.next()) list.add(mapRow(rs));
                return list;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur queryList Facture", e);
        }
    }

    private Facture mapRow(ResultSet rs) throws SQLException {
        Facture f = new Facture();

        f.setIdFacture(rs.getLong("id_facture"));

        double totale = rs.getDouble("totale_facture");
        f.setTotaleFacture(rs.wasNull() ? null : totale);

        double reste = rs.getDouble("reste");
        f.setReste(rs.wasNull() ? null : reste);

        String st = rs.getString("statut");
        f.setStatut(st == null ? null : StatutFacture.valueOf(st));

        Timestamp ts = rs.getTimestamp("date_facture");
        f.setDateFacture(ts == null ? null : ts.toLocalDateTime());

        long idCons = rs.getLong("id_consultation");
        f.setIdConsultation(rs.wasNull() ? null : idCons);

        long idSf = rs.getLong("id_sf");
        f.setIdSf(rs.wasNull() ? null : idSf);

        f.setConsultation(null);
        f.setSituationFinanciere(null);

        return f;
    }

    private void setLongOrNull(PreparedStatement ps, int idx, Long value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.BIGINT);
        else ps.setLong(idx, value);
    }

    private void setDoubleOrNull(PreparedStatement ps, int idx, Double value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.DOUBLE);
        else ps.setDouble(idx, value);
    }

    private void setStringOrNull(PreparedStatement ps, int idx, String value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.VARCHAR);
        else ps.setString(idx, value);
    }

    private void setTimestampOrNull(PreparedStatement ps, int idx, LocalDateTime value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.TIMESTAMP);
        else ps.setTimestamp(idx, Timestamp.valueOf(value));
    }
}
