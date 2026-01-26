package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.repository.modules.dossierMedical.api.CertificatRepo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CertificatRepoImpl implements CertificatRepo {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    private Connection cn() throws SQLException {
        return sessionFactory.getConnection();
    }

    // =========================
    // CRUD
    // =========================

    @Override
    public Certificat save(Certificat c) {
        String sql = """
            INSERT INTO certificat
            (id_consultation, date_debut, date_fin, duree, note_medecin, id_dm)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps =
                     cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
            throw new RuntimeException("Certificat.save failed", e);
        }
    }

    @Override
    public Certificat update(Certificat c) {
        if (c == null || c.getIdCertif() == null)
            throw new IllegalArgumentException("idCertif is required for update");

        String sql = """
            UPDATE certificat
            SET id_consultation = ?,
                date_debut = ?,
                date_fin = ?,
                duree = ?,
                note_medecin = ?,
                id_dm = ?
            WHERE id_certif = ?
            """;

        try (PreparedStatement ps = cn().prepareStatement(sql)) {

            setLongOrNull(ps, 1, c.getIdConsultation());
            setDateOrNull(ps, 2, c.getDateDebut());
            setDateOrNull(ps, 3, c.getDateFin());
            setIntOrNull(ps, 4, c.getDuree());
            ps.setString(5, c.getNoteMedecin());
            setLongOrNull(ps, 6, c.getIdDm());
            ps.setLong(7, c.getIdCertif());

            if (ps.executeUpdate() == 0)
                throw new RuntimeException("Certificat not found: " + c.getIdCertif());

            return c;

        } catch (SQLException e) {
            throw new RuntimeException("Certificat.update failed", e);
        }
    }

    @Override
    public Optional<Certificat> findById(Long idCertif) {
        if (idCertif == null) return Optional.empty();

        String sql = "SELECT * FROM certificat WHERE id_certif = ?";

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idCertif);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(mapRow(rs))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Certificat.findById failed", e);
        }
    }

    @Override
    public List<Certificat> findAll() {
        String sql = "SELECT * FROM certificat ORDER BY id_certif";

        List<Certificat> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(mapRow(rs));
            return out;

        } catch (SQLException e) {
            throw new RuntimeException("Certificat.findAll failed", e);
        }
    }

    @Override
    public boolean deleteById(Long idCertif) {
        if (idCertif == null) return false;

        String sql = "DELETE FROM certificat WHERE id_certif = ?";

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idCertif);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Certificat.deleteById failed", e);
        }
    }

    // =========================
    // Queries métier
    // =========================

    @Override
    public List<Certificat> findByIdConsultation(Long idConsultation) {
        return findListByOneParam("id_consultation", idConsultation);
    }

    @Override
    public List<Certificat> findByIdDm(Long idDm) {
        return findListByOneParam("id_dm", idDm);
    }

    @Override
    public List<Certificat> findByDateDebutBetween(LocalDate start, LocalDate end) {
        String sql = """
            SELECT *
            FROM certificat
            WHERE date_debut BETWEEN ? AND ?
            """;

        List<Certificat> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {

            setDateOrNull(ps, 1, start);
            setDateOrNull(ps, 2, end);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Certificat.findByDateDebutBetween failed", e);
        }
        return out;
    }

    // =========================
    // Helpers
    // =========================

    private List<Certificat> findListByOneParam(String column, Long value) {
        String sql = "SELECT * FROM certificat WHERE " + column + " = ?";

        List<Certificat> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Certificat.findBy " + column + " failed", e);
        }
        return out;
    }

    private Certificat mapRow(ResultSet rs) throws SQLException {
        Certificat c = new Certificat();

        c.setIdCertif(rs.getLong("id_certif"));
        c.setIdConsultation(rs.getObject("id_consultation", Long.class));
        c.setDateDebut(rs.getObject("date_debut", LocalDate.class));
        c.setDateFin(rs.getObject("date_fin", LocalDate.class));
        c.setDuree(rs.getObject("duree", Integer.class));
        c.setNoteMedecin(rs.getString("note_medecin"));
        c.setIdDm(rs.getObject("id_dm", Long.class));

        // relations non chargées ici
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
