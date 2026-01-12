package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.repository.modules.dossierMedical.api.CertificatRepo;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CertificatRepoImpl implements CertificatRepo {

    private final DataSource dataSource;

    public CertificatRepoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Certificat save(Certificat c) {
        final String sql =
                "INSERT INTO certificat (id_consultation, date_debut, date_fin, duree, note_medecin, id_dm) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setLongOrNull(ps, 1, c.getIdConsultation());
            setDateOrNull(ps, 2, c.getDateDebut());
            setDateOrNull(ps, 3, c.getDateFin());
            setIntOrNull(ps, 4, c.getDuree());
            ps.setString(5, c.getNoteMedecin());
            setLongOrNull(ps, 6, c.getIdDm());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setIdCertif(rs.getLong(1));
            }
            return c;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur save Certificat", e);
        }
    }

    @Override
    public Certificat update(Certificat c) {
        if (c.getIdCertif() == null) {
            throw new IllegalArgumentException("idCertif est requis pour update");
        }

        final String sql =
                "UPDATE certificat SET id_consultation=?, date_debut=?, date_fin=?, duree=?, note_medecin=?, id_dm=? " +
                        "WHERE id_certif=?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            setLongOrNull(ps, 1, c.getIdConsultation());
            setDateOrNull(ps, 2, c.getDateDebut());
            setDateOrNull(ps, 3, c.getDateFin());
            setIntOrNull(ps, 4, c.getDuree());
            ps.setString(5, c.getNoteMedecin());
            setLongOrNull(ps, 6, c.getIdDm());
            ps.setLong(7, c.getIdCertif());

            int updated = ps.executeUpdate();
            if (updated == 0) throw new RuntimeException("Certificat introuvable: " + c.getIdCertif());

            return c;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update Certificat", e);
        }
    }

    @Override
    public Optional<Certificat> findById(Long idCertif) {
        final String sql =
                "SELECT id_certif, id_consultation, date_debut, date_fin, duree, note_medecin, id_dm " +
                        "FROM certificat WHERE id_certif=?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, idCertif);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById Certificat", e);
        }
    }

    @Override
    public List<Certificat> findAll() {
        final String sql =
                "SELECT id_certif, id_consultation, date_debut, date_fin, duree, note_medecin, id_dm FROM certificat";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Certificat> list = new ArrayList<>();
            while (rs.next()) list.add(mapRow(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll Certificat", e);
        }
    }

    @Override
    public boolean deleteById(Long idCertif) {
        final String sql = "DELETE FROM certificat WHERE id_certif=?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, idCertif);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById Certificat", e);
        }
    }

    @Override
    public List<Certificat> findByIdConsultation(Long idConsultation) {
        final String sql =
                "SELECT id_certif, id_consultation, date_debut, date_fin, duree, note_medecin, id_dm " +
                        "FROM certificat WHERE id_consultation=?";

        return queryList(sql, ps -> ps.setLong(1, idConsultation));
    }

    @Override
    public List<Certificat> findByIdDm(Long idDm) {
        final String sql =
                "SELECT id_certif, id_consultation, date_debut, date_fin, duree, note_medecin, id_dm " +
                        "FROM certificat WHERE id_dm=?";

        return queryList(sql, ps -> ps.setLong(1, idDm));
    }

    @Override
    public List<Certificat> findByDateDebutBetween(LocalDate start, LocalDate end) {
        final String sql =
                "SELECT id_certif, id_consultation, date_debut, date_fin, duree, note_medecin, id_dm " +
                        "FROM certificat WHERE date_debut BETWEEN ? AND ?";

        return queryList(sql, ps -> {
            setDateOrNull(ps, 1, start);
            setDateOrNull(ps, 2, end);
        });
    }

    // ----------------- Helpers -----------------

    private interface StatementBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    private List<Certificat> queryList(String sql, StatementBinder binder) {
        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            binder.bind(ps);

            try (ResultSet rs = ps.executeQuery()) {
                List<Certificat> list = new ArrayList<>();
                while (rs.next()) list.add(mapRow(rs));
                return list;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur query Certificat", e);
        }
    }

    private Certificat mapRow(ResultSet rs) throws SQLException {
        Certificat c = new Certificat();
        c.setIdCertif(rs.getLong("id_certif"));

        long idCons = rs.getLong("id_consultation");
        c.setIdConsultation(rs.wasNull() ? null : idCons);

        Date d1 = rs.getDate("date_debut");
        c.setDateDebut(d1 == null ? null : d1.toLocalDate());

        Date d2 = rs.getDate("date_fin");
        c.setDateFin(d2 == null ? null : d2.toLocalDate());

        int duree = rs.getInt("duree");
        c.setDuree(rs.wasNull() ? null : duree);

        c.setNoteMedecin(rs.getString("note_medecin"));

        long idDm = rs.getLong("id_dm");
        c.setIdDm(rs.wasNull() ? null : idDm);

        // relations non chargées ici (à faire via autre repo/service si besoin)
        c.setDossierMedical(null);
        c.setConsultation(null);

        return c;
    }

    private void setLongOrNull(PreparedStatement ps, int idx, Long value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.BIGINT);
        else ps.setLong(idx, value);
    }

    private void setIntOrNull(PreparedStatement ps, int idx, Integer value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.INTEGER);
        else ps.setInt(idx, value);
    }

    private void setDateOrNull(PreparedStatement ps, int idx, LocalDate value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.DATE);
        else ps.setDate(idx, Date.valueOf(value));
    }
}
