package ma.dentalTech.repository.modules.agenda.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AgendaMensuelRepositoryImpl implements AgendaMensuelRepository {

    // ========== AJOUTER CES 2 LIGNES ==========
    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    private Connection cn() throws SQLException {
        return sessionFactory.getConnection();
    }
    // ==========================================

    // -------- CRUD --------
    @Override
    public List<AgendaMensuel> findAll() {
        String sql = "SELECT * FROM agenda ORDER BY annee, mois, id_medecin";
        List<AgendaMensuel> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapAgendaMensuel(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public AgendaMensuel findById(Long id) {
        String sql = "SELECT * FROM agenda WHERE id_agenda = ?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapAgendaMensuel(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AgendaMensuel create(AgendaMensuel agenda) {
        String sql = "INSERT INTO agenda(mois, annee, id_medecin) VALUES (?, ?, ?)";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, agenda.getMois());
            ps.setInt(2, agenda.getAnnee());
            ps.setLong(3, agenda.getIdMedecin());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) agenda.setIdAgenda(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public AgendaMensuel update(AgendaMensuel agenda) {
        String sql = "UPDATE agenda SET mois = ?, annee = ?, id_medecin = ? WHERE id_agenda = ?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setInt(1, agenda.getMois());
            ps.setInt(2, agenda.getAnnee());
            ps.setLong(3, agenda.getIdMedecin());
            ps.setLong(4, agenda.getIdAgenda());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void delete(AgendaMensuel agenda) {
        if (agenda != null) deleteById(agenda.getIdAgenda());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM agenda WHERE id_agenda = ?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // -------- Extras --------
    @Override
    public Optional<AgendaMensuel> findByMedecinAndMoisAndAnnee(Long idMedecin, Integer mois, Integer annee) {
        String sql = "SELECT * FROM agenda WHERE id_medecin = ? AND mois = ? AND annee = ?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idMedecin);
            ps.setInt(2, mois);
            ps.setInt(3, annee);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapAgendaMensuel(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AgendaMensuel> findByMedecin(Long idMedecin) {
        String sql = "SELECT * FROM agenda WHERE id_medecin = ?";
        List<AgendaMensuel> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idMedecin);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAgendaMensuel(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM agenda WHERE id_agenda = ?";

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
        String sql = "SELECT COUNT(*) FROM agenda";

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
    public List<AgendaMensuel> findPage(int limit, int offset) {
        String sql = "SELECT * FROM agenda ORDER BY annee, mois LIMIT ? OFFSET ?";
        List<AgendaMensuel> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAgendaMensuel(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    // -------- Gestion des jours non disponibles --------
    @Override
    public List<LocalDate> getJoursNonDisponibles(Long agendaId) {
        String sql = "SELECT date_non_disponible FROM agenda_jours_non_disponibles WHERE id_agenda = ?";
        List<LocalDate> out = new ArrayList<>();

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(rs.getDate("date_non_disponible").toLocalDate());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public void addJourNonDisponible(Long agendaId, LocalDate date) {
        String sql = "INSERT INTO agenda_jours_non_disponibles(id_agenda, date_non_disponible) VALUES(?, ?)";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            ps.setDate(2, Date.valueOf(date));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeJourNonDisponible(Long agendaId, LocalDate date) {
        String sql = "DELETE FROM agenda_jours_non_disponibles WHERE id_agenda = ? AND date_non_disponible = ?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            ps.setDate(2, Date.valueOf(date));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearJoursNonDisponibles(Long agendaId) {
        String sql = "DELETE FROM agenda_jours_non_disponibles WHERE id_agenda = ?";

        // MODIFIER : enlever Connection du try
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}