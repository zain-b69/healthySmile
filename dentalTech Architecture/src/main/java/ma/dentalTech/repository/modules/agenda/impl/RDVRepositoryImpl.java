package ma.dentalTech.repository.modules.agenda.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.agenda.api.RDVRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RDVRepositoryImpl implements RDVRepository {

    // -------- CRUD --------
    @Override
    public List<RDV> findAll() {
        String sql = "SELECT * FROM rdv ORDER BY date, heure";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapRDV(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public RDV findById(Long id) {
        String sql = "SELECT * FROM rdv WHERE id_rdv = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapRDV(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(RDV rdv) {
        String sql = "INSERT INTO rdv(id_entite, id_patient, id_medecin, date, heure, motif, statut, note_medecin, id_creneau, id_dm, agenda_mensuel_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
    }

    @Override
    public void update(RDV rdv) {
        String sql = "UPDATE rdv SET id_entite=?, id_patient=?, id_medecin=?, date=?, heure=?, motif=?, statut=?, note_medecin=?, id_creneau=?, id_dm=?, agenda_mensuel_id=? WHERE id_rdv=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
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
    }

    @Override
    public void delete(RDV rdv) {
        if (rdv != null) deleteById(rdv.getIdRdv());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM rdv WHERE id_rdv = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // -------- Recherche spécifique --------
    @Override
    public List<RDV> findByMedecinAndDate(Long idMedecin, LocalDate date) {
        String sql = "SELECT * FROM rdv WHERE id_medecin = ? AND date = ?";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idMedecin);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRDV(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<RDV> findByMedecinAndDateRange(Long idMedecin, LocalDate start, LocalDate end) {
        String sql = "SELECT * FROM rdv WHERE id_medecin = ? AND date BETWEEN ? AND ?";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idMedecin);
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRDV(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<RDV> findByPatient(Long idPatient) {
        String sql = "SELECT * FROM rdv WHERE id_patient = ?";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idPatient);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRDV(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<RDV> findByAgenda(Long agendaId) {
        String sql = "SELECT * FROM rdv WHERE agenda_mensuel_id = ?";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRDV(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<RDV> findByStatut(Enum statut) {
        String sql = "SELECT * FROM rdv WHERE statut = ?";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRDV(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<RDV> findByCreneau(Long idCreneau) {
        String sql = "SELECT * FROM rdv WHERE id_creneau = ?";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idCreneau);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRDV(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Optional<RDV> findByCreneauUnique(Long idCreneau) {
        String sql = "SELECT * FROM rdv WHERE id_creneau = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idCreneau);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapRDV(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean existsByCreneau(Long idCreneau) {
        String sql = "SELECT 1 FROM rdv WHERE id_creneau = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idCreneau);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM rdv";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<RDV> findPage(int limit, int offset) {
        String sql = "SELECT * FROM rdv ORDER BY date, heure LIMIT ? OFFSET ?";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRDV(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }
}
