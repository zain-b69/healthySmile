package ma.dentalTech.repository.modules.agenda.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.agenda.api.RDVRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RDVRepositoryImpl implements RDVRepository {

    // ========== AJOUTER CES 2 LIGNES ==========
    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    private Connection cn() throws SQLException {
        return sessionFactory.getConnection();
    }
    // ==========================================

    // ===================== BASE SELECT =====================
    private static final String BASE_SELECT = """
        SELECT
            r.*,
            e.id_entite,
            e.date_creation,
            e.date_derniere_modification,
            e.cree_par,
            e.modifie_par
        FROM rdv r
        LEFT JOIN entite e ON e.id_entite = r.id_entite
    """;

    // -------- CRUD --------
    @Override
    public List<RDV> findAll() {
        String sql = BASE_SELECT + " ORDER BY r.date, r.heure";
        List<RDV> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapRDV(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public RDV findById(Long id) {
        String sql = BASE_SELECT + " WHERE r.id_rdv = ?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapRDV(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RDV create(RDV rdv) {
        String sql = """
            INSERT INTO rdv (
                id_entite,
                id_patient,
                id_medecin,
                date,
                heure,
                motif,
                statut,
                note_medecin,
                id_creneau,
                id_dm,
                agenda_mensuel_id
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setObject(1, rdv.getIdEntite(), Types.BIGINT);
            ps.setLong(2, rdv.getIdPatient());
            ps.setLong(3, rdv.getIdMedecin());
            ps.setDate(4, Date.valueOf(rdv.getDate()));
            ps.setString(5, rdv.getHeure());
            ps.setString(6, rdv.getMotif());
            ps.setString(7, rdv.getStatut().name());
            ps.setString(8, rdv.getNoteMedecin());
            ps.setObject(9, rdv.getIdCreneau(), Types.BIGINT);
            ps.setLong(10, rdv.getIdDm());
            ps.setObject(11, rdv.getAgendaMensuelId(), Types.BIGINT);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) rdv.setIdRdv(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public RDV update(RDV rdv) {
        String sql = """
            UPDATE rdv SET
                id_entite = ?,
                id_patient = ?,
                id_medecin = ?,
                date = ?,
                heure = ?,
                motif = ?,
                statut = ?,
                note_medecin = ?,
                id_creneau = ?,
                id_dm = ?,
                agenda_mensuel_id = ?
            WHERE id_rdv = ?
        """;

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setObject(1, rdv.getIdEntite(), Types.BIGINT);
            ps.setLong(2, rdv.getIdPatient());
            ps.setLong(3, rdv.getIdMedecin());
            ps.setDate(4, Date.valueOf(rdv.getDate()));
            ps.setString(5, rdv.getHeure());
            ps.setString(6, rdv.getMotif());
            ps.setString(7, rdv.getStatut().name());
            ps.setString(8, rdv.getNoteMedecin());
            ps.setObject(9, rdv.getIdCreneau(), Types.BIGINT);
            ps.setLong(10, rdv.getIdDm());
            ps.setObject(11, rdv.getAgendaMensuelId(), Types.BIGINT);
            ps.setLong(12, rdv.getIdRdv());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void delete(RDV rdv) {
        if (rdv != null) deleteById(rdv.getIdRdv());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM rdv WHERE id_rdv = ?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // -------- Recherche spécifique --------

    @Override
    public List<RDV> findByMedecinAndDate(Long idMedecin, LocalDate date) {
        String sql = BASE_SELECT + " WHERE r.id_medecin = ? AND r.date = ?";
        return queryList(sql, ps -> {
            ps.setLong(1, idMedecin);
            ps.setDate(2, Date.valueOf(date));
        });
    }

    @Override
    public List<RDV> findByMedecinAndDateRange(Long idMedecin, LocalDate start, LocalDate end) {
        String sql = BASE_SELECT + " WHERE r.id_medecin = ? AND r.date BETWEEN ? AND ?";
        return queryList(sql, ps -> {
            ps.setLong(1, idMedecin);
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));
        });
    }

    @Override
    public List<RDV> findByPatient(Long idPatient) {
        String sql = BASE_SELECT + " WHERE r.id_patient = ?";
        return queryList(sql, ps -> ps.setLong(1, idPatient));
    }

    @Override
    public List<RDV> findByAgenda(Long agendaId) {
        String sql = BASE_SELECT + " WHERE r.agenda_mensuel_id = ?";
        return queryList(sql, ps -> ps.setLong(1, agendaId));
    }

    @Override
    public List<RDV> findByStatut(Enum statut) {
        String sql = BASE_SELECT + " WHERE r.statut = ?";
        return queryList(sql, ps -> ps.setString(1, statut.name()));
    }

    @Override
    public List<RDV> findByCreneau(Long idCreneau) {
        String sql = BASE_SELECT + " WHERE r.id_creneau = ?";
        return queryList(sql, ps -> ps.setLong(1, idCreneau));
    }

    @Override
    public Optional<RDV> findByCreneauUnique(Long idCreneau) {
        String sql = BASE_SELECT + " WHERE r.id_creneau = ?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idCreneau);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapRDV(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsByCreneau(Long idCreneau) {
        String sql = "SELECT 1 FROM rdv WHERE id_creneau = ?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idCreneau);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM rdv";

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
    public List<RDV> findPage(int limit, int offset) {
        String sql = BASE_SELECT + " ORDER BY r.date, r.heure LIMIT ? OFFSET ?";
        return queryList(sql, ps -> {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
        });
    }

    // -------- Helper --------
    private List<RDV> queryList(String sql, SQLConsumer<PreparedStatement> binder) {
        List<RDV> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            binder.accept(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRDV(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public void confirmer(Long idRdv) {
        String sql = "UPDATE rdv SET statut = ? WHERE id_rdv = ?";

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, "CONFIRME");
            ps.setLong(2, idRdv);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void annuler(Long idRdv) {
        String sql = "UPDATE rdv SET statut = ? WHERE id_rdv = ?";

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, "ANNULE"); // adapte si ton enum est ANNULÉ / ANNULEE / CANCELED etc.
            ps.setLong(2, idRdv);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @FunctionalInterface
    private interface SQLConsumer<T> {
        void accept(T t) throws SQLException;
    }

}