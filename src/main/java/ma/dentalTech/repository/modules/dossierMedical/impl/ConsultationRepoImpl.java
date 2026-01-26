package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConsultationRepoImpl implements ConsultationRepo {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    public ConsultationRepoImpl() {
        // no-arg pour ApplicationContext.newNoArg()
    }

    private Connection cn() throws SQLException {
        return sessionFactory.getConnection(); // ⚠️ ne pas fermer si ton SessionFactory gère un singleton
    }

    @Override
    public Consultation save(Consultation c) {
        final String sql =
                "INSERT INTO consultation (id_medecin, date, statut, observation_medecin, id_dm, id_rdv) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            Connection cn = cn(); // ⚠️ comme PatientRepositoryImpl : ne pas fermer ici

            try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setLongOrNull(ps, 1, c.getIdMedecin());
                setDateOrNull(ps, 2, c.getDate());
                setStringOrNull(ps, 3, c.getStatut() == null ? null : c.getStatut().name());
                ps.setString(4, c.getObservationMedecin());
                setLongOrNull(ps, 5, c.getIdDm());
                setLongOrNull(ps, 6, c.getIdRdv());

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) c.setIdConsultation(rs.getLong(1));
                }
                return c;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur save Consultation", e);
        }
    }

    @Override
    public Consultation update(Consultation c) {
        if (c.getIdConsultation() == null) {
            throw new IllegalArgumentException("idConsultation est requis pour update");
        }

        final String sql =
                "UPDATE consultation SET id_medecin=?, date=?, statut=?, observation_medecin=?, id_dm=?, id_rdv=? " +
                        "WHERE id_consultation=?";

        try {
            Connection cn = cn();

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                setLongOrNull(ps, 1, c.getIdMedecin());
                setDateOrNull(ps, 2, c.getDate());
                setStringOrNull(ps, 3, c.getStatut() == null ? null : c.getStatut().name());
                ps.setString(4, c.getObservationMedecin());
                setLongOrNull(ps, 5, c.getIdDm());
                setLongOrNull(ps, 6, c.getIdRdv());
                ps.setLong(7, c.getIdConsultation());

                int updated = ps.executeUpdate();
                if (updated == 0) throw new RuntimeException("Consultation introuvable: " + c.getIdConsultation());

                return c;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update Consultation", e);
        }
    }

    @Override
    public Optional<Consultation> findById(Long idConsultation) {
        final String sql =
                "SELECT id_consultation, id_medecin, date, statut, observation_medecin, id_dm, id_rdv " +
                        "FROM consultation WHERE id_consultation=?";

        return queryOne(sql, ps -> ps.setLong(1, idConsultation));
    }

    @Override
    public List<Consultation> findAll() {
        final String sql =
                "SELECT id_consultation, id_medecin, date, statut, observation_medecin, id_dm, id_rdv " +
                        "FROM consultation";

        return queryList(sql, ps -> {});
    }

    @Override
    public boolean deleteById(Long idConsultation) {
        final String sql = "DELETE FROM consultation WHERE id_consultation=?";

        try {
            Connection cn = cn();

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setLong(1, idConsultation);
                return ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById Consultation", e);
        }
    }

    @Override
    public List<Consultation> findByIdDm(Long idDm) {
        final String sql =
                "SELECT id_consultation, id_medecin, date, statut, observation_medecin, id_dm, id_rdv " +
                        "FROM consultation WHERE id_dm=?";

        return queryList(sql, ps -> ps.setLong(1, idDm));
    }

    @Override
    public List<Consultation> findByIdMedecin(Long idMedecin) {
        final String sql =
                "SELECT id_consultation, id_medecin, date, statut, observation_medecin, id_dm, id_rdv " +
                        "FROM consultation WHERE id_medecin=?";

        return queryList(sql, ps -> ps.setLong(1, idMedecin));
    }

    @Override
    public Optional<Consultation> findByIdRdv(Long idRdv) {
        final String sql =
                "SELECT id_consultation, id_medecin, date, statut, observation_medecin, id_dm, id_rdv " +
                        "FROM consultation WHERE id_rdv=?";

        return queryOne(sql, ps -> ps.setLong(1, idRdv));
    }

    @Override
    public List<Consultation> findByDateBetween(LocalDate start, LocalDate end) {
        final String sql =
                "SELECT id_consultation, id_medecin, date, statut, observation_medecin, id_dm, id_rdv " +
                        "FROM consultation WHERE date BETWEEN ? AND ?";

        return queryList(sql, ps -> {
            setDateOrNull(ps, 1, start);
            setDateOrNull(ps, 2, end);
        });
    }

    @Override
    public List<Consultation> findByStatut(StatutConsultation statut) {
        final String sql =
                "SELECT id_consultation, id_medecin, date, statut, observation_medecin, id_dm, id_rdv " +
                        "FROM consultation WHERE statut=?";

        return queryList(sql, ps -> ps.setString(1, statut == null ? null : statut.name()));
    }

    // ---------------- Helpers ----------------

    private interface StatementBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    private Optional<Consultation> queryOne(String sql, StatementBinder binder) {
        try {
            Connection cn = cn();

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                binder.bind(ps);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return Optional.of(mapRow(rs));
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur queryOne Consultation", e);
        }
    }

    private List<Consultation> queryList(String sql, StatementBinder binder) {
        try {
            Connection cn = cn();

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                binder.bind(ps);

                try (ResultSet rs = ps.executeQuery()) {
                    List<Consultation> list = new ArrayList<>();
                    while (rs.next()) list.add(mapRow(rs));
                    return list;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur queryList Consultation", e);
        }
    }

    private Consultation mapRow(ResultSet rs) throws SQLException {
        Consultation c = new Consultation();

        c.setIdConsultation(rs.getLong("id_consultation"));

        long idMed = rs.getLong("id_medecin");
        c.setIdMedecin(rs.wasNull() ? null : idMed);

        Date d = rs.getDate("date");
        c.setDate(d == null ? null : d.toLocalDate());

        String st = rs.getString("statut");
        c.setStatut(st == null ? null : StatutConsultation.valueOf(st));

        c.setObservationMedecin(rs.getString("observation_medecin"));

        long idDm = rs.getLong("id_dm");
        c.setIdDm(rs.wasNull() ? null : idDm);

        long idRdv = rs.getLong("id_rdv");
        c.setIdRdv(rs.wasNull() ? null : idRdv);

        // objets liés non chargés ici
        c.setDossierMedical(null);
        c.setMedecin(null);
        c.setRdv(null);
        c.setInterventions(new ArrayList<>());
        c.setOrdonnance(null);
        c.setCertificat(null);
        c.setFacture(null);

        return c;
    }

    private void setLongOrNull(PreparedStatement ps, int idx, Long value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.BIGINT);
        else ps.setLong(idx, value);
    }

    private void setDateOrNull(PreparedStatement ps, int idx, LocalDate value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.DATE);
        else ps.setDate(idx, Date.valueOf(value));
    }

    private void setStringOrNull(PreparedStatement ps, int idx, String value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.VARCHAR);
        else ps.setString(idx, value);
    }
}
