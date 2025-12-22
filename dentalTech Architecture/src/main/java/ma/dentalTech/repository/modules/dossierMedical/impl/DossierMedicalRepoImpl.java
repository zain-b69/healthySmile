package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepo;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DossierMedicalRepoImpl implements DossierMedicalRepo {

    private final DataSource dataSource;

    public DossierMedicalRepoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DossierMedical save(DossierMedical dm) {
        final String sql =
                "INSERT INTO dossier_medicale (id_patient, date_de_creation) VALUES (?, ?)";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setLongOrNull(ps, 1, dm.getIdPatient());
            setDateOrNull(ps, 2, dm.getDateDeCreation());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) dm.setIdDm(rs.getLong(1));
            }
            return dm;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur save DossierMedical", e);
        }
    }

    @Override
    public DossierMedical update(DossierMedical dm) {
        if (dm.getIdDm() == null) {
            throw new IllegalArgumentException("idDm est requis pour update");
        }

        final String sql =
                "UPDATE dossier_medicale SET id_patient=?, date_de_creation=? WHERE id_dm=?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            setLongOrNull(ps, 1, dm.getIdPatient());
            setDateOrNull(ps, 2, dm.getDateDeCreation());
            ps.setLong(3, dm.getIdDm());

            int updated = ps.executeUpdate();
            if (updated == 0) throw new RuntimeException("DossierMedical introuvable: " + dm.getIdDm());

            return dm;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update DossierMedical", e);
        }
    }

    @Override
    public Optional<DossierMedical> findById(Long idDm) {
        final String sql =
                "SELECT id_dm, id_patient, date_de_creation FROM dossier_medicale WHERE id_dm=?";

        return queryOne(sql, ps -> ps.setLong(1, idDm));
    }

    @Override
    public List<DossierMedical> findAll() {
        final String sql =
                "SELECT id_dm, id_patient, date_de_creation FROM dossier_medicale";

        return queryList(sql, ps -> {});
    }

    @Override
    public boolean deleteById(Long idDm) {
        final String sql = "DELETE FROM dossier_medicale WHERE id_dm=?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, idDm);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById DossierMedical", e);
        }
    }

    @Override
    public Optional<DossierMedical> findByIdPatient(Long idPatient) {
        final String sql =
                "SELECT id_dm, id_patient, date_de_creation FROM dossier_medicale WHERE id_patient=?";

        return queryOne(sql, ps -> ps.setLong(1, idPatient));
    }

    @Override
    public List<DossierMedical> findByDateCreationBetween(LocalDate start, LocalDate end) {
        final String sql =
                "SELECT id_dm, id_patient, date_de_creation FROM dossier_medicale " +
                        "WHERE date_de_creation BETWEEN ? AND ?";

        return queryList(sql, ps -> {
            setDateOrNull(ps, 1, start);
            setDateOrNull(ps, 2, end);
        });
    }

    // ---------------- Helpers ----------------

    private interface StatementBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    private Optional<DossierMedical> queryOne(String sql, StatementBinder binder) {
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            binder.bind(ps);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur queryOne DossierMedical", e);
        }
    }

    private List<DossierMedical> queryList(String sql, StatementBinder binder) {
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            binder.bind(ps);

            try (ResultSet rs = ps.executeQuery()) {
                List<DossierMedical> list = new ArrayList<>();
                while (rs.next()) list.add(mapRow(rs));
                return list;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur queryList DossierMedical", e);
        }
    }

    private DossierMedical mapRow(ResultSet rs) throws SQLException {
        DossierMedical dm = new DossierMedical();

        dm.setIdDm(rs.getLong("id_dm"));

        long idPatient = rs.getLong("id_patient");
        dm.setIdPatient(rs.wasNull() ? null : idPatient);

        Date dc = rs.getDate("date_de_creation");
        dm.setDateDeCreation(dc == null ? null : dc.toLocalDate());

        // objets & listes non chargés ici
        dm.setPatient(null);
        dm.setSituationFinanciere(null);
        dm.setConsultations(new ArrayList<>());
        dm.setRdvs(new ArrayList<>());
        dm.setOrdonnances(new ArrayList<>());
        dm.setCertificats(new ArrayList<>());

        return dm;
    }

    private void setLongOrNull(PreparedStatement ps, int idx, Long value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.BIGINT);
        else ps.setLong(idx, value);
    }

    private void setDateOrNull(PreparedStatement ps, int idx, LocalDate value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.DATE);
        else ps.setDate(idx, Date.valueOf(value));
    }
}
