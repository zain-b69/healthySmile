package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepo;
import ma.dentalTech.configuration.SessionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InterventionMedecinRepoImpl implements InterventionMedecinRepo {

    private final SessionFactory sessionFactory;

    public InterventionMedecinRepoImpl() {
        this.sessionFactory = SessionFactory.getInstance();
    }

    @Override
    public InterventionMedecin save(InterventionMedecin im) {
        final String sql =
                "INSERT INTO intervention_medecin (id_consultation, id_acte, prix_de_patient, num_dent) " +
                        "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = sessionFactory.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setLongOrNull(ps, 1, im.getIdConsultation());
            setLongOrNull(ps, 2, im.getIdActe());
            setDoubleOrNull(ps, 3, im.getPrixDePatient());
            setIntOrNull(ps, 4, im.getNumDent());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) im.setIdIm(rs.getLong(1));
            }
            return im;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur save InterventionMedecin", e);
        }
    }

    @Override
    public InterventionMedecin update(InterventionMedecin im) {
        if (im.getIdIm() == null) {
            throw new IllegalArgumentException("idIm est requis pour update");
        }

        final String sql =
                "UPDATE intervention_medecin " +
                        "SET id_consultation=?, id_acte=?, prix_de_patient=?, num_dent=? " +
                        "WHERE id_im=?";

        try (PreparedStatement ps = sessionFactory.getConnection().prepareStatement(sql)) {

            setLongOrNull(ps, 1, im.getIdConsultation());
            setLongOrNull(ps, 2, im.getIdActe());
            setDoubleOrNull(ps, 3, im.getPrixDePatient());
            setIntOrNull(ps, 4, im.getNumDent());
            ps.setLong(5, im.getIdIm());

            int updated = ps.executeUpdate();
            if (updated == 0) throw new RuntimeException("Intervention introuvable: " + im.getIdIm());

            return im;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update InterventionMedecin", e);
        }
    }

    @Override
    public Optional<InterventionMedecin> findById(Long idIm) {
        final String sql =
                "SELECT id_im, id_consultation, id_acte, prix_de_patient, num_dent " +
                        "FROM intervention_medecin WHERE id_im=?";

        return queryOne(sql, ps -> ps.setLong(1, idIm));
    }

    @Override
    public List<InterventionMedecin> findAll() {
        final String sql =
                "SELECT id_im, id_consultation, id_acte, prix_de_patient, num_dent " +
                        "FROM intervention_medecin";

        return queryList(sql, ps -> {});
    }

    @Override
    public boolean deleteById(Long idIm) {
        final String sql = "DELETE FROM intervention_medecin WHERE id_im=?";

        try (PreparedStatement ps = sessionFactory.getConnection().prepareStatement(sql)) {
            ps.setLong(1, idIm);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById InterventionMedecin", e);
        }
    }

    @Override
    public List<InterventionMedecin> findByIdConsultation(Long idConsultation) {
        final String sql =
                "SELECT id_im, id_consultation, id_acte, prix_de_patient, num_dent " +
                        "FROM intervention_medecin WHERE id_consultation=?";

        return queryList(sql, ps -> ps.setLong(1, idConsultation));
    }

    @Override
    public List<InterventionMedecin> findByIdActe(Long idActe) {
        final String sql =
                "SELECT id_im, id_consultation, id_acte, prix_de_patient, num_dent " +
                        "FROM intervention_medecin WHERE id_acte=?";

        return queryList(sql, ps -> ps.setLong(1, idActe));
    }

    @Override
    public List<InterventionMedecin> findByIdConsultationAndNumDent(Long idConsultation, Integer numDent) {
        final String sql =
                "SELECT id_im, id_consultation, id_acte, prix_de_patient, num_dent " +
                        "FROM intervention_medecin WHERE id_consultation=? AND num_dent=?";

        return queryList(sql, ps -> {
            ps.setLong(1, idConsultation);
            if (numDent == null) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, numDent);
        });
    }

    // --------------- Helpers ---------------

    private interface StatementBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    private Optional<InterventionMedecin> queryOne(String sql, StatementBinder binder) {
        try (PreparedStatement ps = sessionFactory.getConnection().prepareStatement(sql)) {

            binder.bind(ps);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur queryOne InterventionMedecin", e);
        }
    }

    private List<InterventionMedecin> queryList(String sql, StatementBinder binder) {
        try (PreparedStatement ps = sessionFactory.getConnection().prepareStatement(sql)) {

            binder.bind(ps);

            try (ResultSet rs = ps.executeQuery()) {
                List<InterventionMedecin> list = new ArrayList<>();
                while (rs.next()) list.add(mapRow(rs));
                return list;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur queryList InterventionMedecin", e);
        }
    }

    private InterventionMedecin mapRow(ResultSet rs) throws SQLException {
        InterventionMedecin im = new InterventionMedecin();

        im.setIdIm(rs.getLong("id_im"));

        long idCons = rs.getLong("id_consultation");
        im.setIdConsultation(rs.wasNull() ? null : idCons);

        long idActe = rs.getLong("id_acte");
        im.setIdActe(rs.wasNull() ? null : idActe);

        double prix = rs.getDouble("prix_de_patient");
        im.setPrixDePatient(rs.wasNull() ? null : prix);

        int dent = rs.getInt("num_dent");
        im.setNumDent(rs.wasNull() ? null : dent);

        // relations non chargées ici
        im.setConsultation(null);
        im.setActe(null);

        return im;
    }

    private void setLongOrNull(PreparedStatement ps, int idx, Long value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.BIGINT);
        else ps.setLong(idx, value);
    }

    private void setIntOrNull(PreparedStatement ps, int idx, Integer value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.INTEGER);
        else ps.setInt(idx, value);
    }

    private void setDoubleOrNull(PreparedStatement ps, int idx, Double value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.DOUBLE);
        else ps.setDouble(idx, value);
    }
}
