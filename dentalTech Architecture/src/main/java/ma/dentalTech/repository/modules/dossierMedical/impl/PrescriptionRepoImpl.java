package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.entities.dossierMedical.PrescriptionMedicament;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Impl JDBC pour Prescription.
 */
public class PrescriptionRepoImpl implements PrescriptionRepo {

    private Connection cn() throws SQLException {
        return SessionFactory.getInstance().getConnection();
    }

    // =========================
    // CRUD
    // =========================

    @Override
    public List<Prescription> findAll() {
        String sql = """
            SELECT p.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM prescription p
            LEFT JOIN entite e ON e.id_entite = p.id_entite
            ORDER BY p.id_pr
            """;

        List<Prescription> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapPrescription(rs));
        } catch (SQLException ex) {
            throw new RuntimeException("Prescription.findAll failed", ex);
        }
        return out;
    }

    @Override
    public Prescription findById(Long id) {
        if (id == null) return null;
        String sql = """
            SELECT p.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM prescription p
            LEFT JOIN entite e ON e.id_entite = p.id_entite
            WHERE p.id_pr = ?
            """;
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapPrescription(rs) : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Prescription.findById failed", ex);
        }
    }

    @Override
    public void create(Prescription p) {
        if (p == null) throw new IllegalArgumentException("Prescription is null");
        try {
            Long idEntite = insertEntiteIfNeeded(p.getCreePar(), p.getModifiePar(), p.getId());

            String sql = """
                INSERT INTO prescription (id_entite, id_ordonnance, quantite, categorie, frequence, duree_en_jours)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

            try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setObject(1, idEntite, Types.BIGINT);
                ps.setObject(2, p.getIdOrdonnance(), Types.BIGINT);
                ps.setObject(3, p.getQuantite(), Types.INTEGER);
                ps.setString(4, p.getCategorie());
                ps.setString(5, p.getFrequence());
                ps.setObject(6, p.getDureeEnJours(), Types.INTEGER);

                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) p.setIdPr(keys.getLong(1));
                }
                p.setId(idEntite);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Prescription.create failed", ex);
        }
    }

    @Override
    public void update(Prescription p) {
        if (p == null || p.getIdPr() == null)
            throw new IllegalArgumentException("Prescription.idPr is null");

        String sql = """
            UPDATE prescription
            SET id_ordonnance = ?,
                quantite = ?,
                categorie = ?,
                frequence = ?,
                duree_en_jours = ?
            WHERE id_pr = ?
            """;

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setObject(1, p.getIdOrdonnance(), Types.BIGINT);
            ps.setObject(2, p.getQuantite(), Types.INTEGER);
            ps.setString(3, p.getCategorie());
            ps.setString(4, p.getFrequence());
            ps.setObject(5, p.getDureeEnJours(), Types.INTEGER);
            ps.setLong(6, p.getIdPr());
            ps.executeUpdate();

            updateEntiteTouch(p.getId(), p.getModifiePar());
        } catch (SQLException ex) {
            throw new RuntimeException("Prescription.update failed", ex);
        }
    }

    @Override
    public void delete(Prescription p) {
        if (p == null) return;
        deleteById(p.getIdPr());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        Prescription p = findById(id);
        removeAllMedicamentsFromPrescription(id);

        String sql = "DELETE FROM prescription WHERE id_pr = ?";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Prescription.deleteById failed", ex);
        }

        if (p != null && p.getId() != null) deleteEntite(p.getId());
    }

    // =========================
    // Finders
    // =========================

    @Override
    public List<Prescription> findByOrdonnance(Long idOrd) {
        String sql = """
            SELECT p.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM prescription p
            LEFT JOIN entite e ON e.id_entite = p.id_entite
            WHERE p.id_ordonnance = ?
            ORDER BY p.id_pr
            """;
        List<Prescription> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setObject(1, idOrd, Types.BIGINT);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPrescription(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Prescription.findByOrdonnance failed", ex);
        }
        return out;
    }

    // =========================
    // Pivot: prescription_medicament
    // =========================

    @Override
    public void addMedicamentToPrescription(Long idPr, Long idMct, Integer quantite) {
        String sql = """
            INSERT INTO prescription_medicament (id_pr, id_mct, quantite)
            VALUES (?, ?, ?)
            """;
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idPr);
            ps.setLong(2, idMct);
            if (quantite == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, quantite);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Prescription.addMedicamentToPrescription failed", ex);
        }
    }

    @Override
    public void removeMedicamentFromPrescription(Long idPr, Long idMct) {
        String sql = "DELETE FROM prescription_medicament WHERE id_pr = ? AND id_mct = ?";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idPr);
            ps.setLong(2, idMct);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Prescription.removeMedicamentFromPrescription failed", ex);
        }
    }

    @Override
    public void removeAllMedicamentsFromPrescription(Long idPr) {
        String sql = "DELETE FROM prescription_medicament WHERE id_pr = ?";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idPr);
            ps.executeUpdate();
        } catch (SQLException ex) {
            // si la table n'existe pas encore : tu verras l'erreur -> crée-la
            throw new RuntimeException("Prescription.removeAllMedicamentsFromPrescription failed", ex);
        }
    }

    @Override
    public List<Medicament> getMedicamentsOfPrescription(Long idPr) {
        String sql = """
            SELECT m.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM medicament m
            JOIN prescription_medicament pm ON pm.id_mct = m.id_mct
            LEFT JOIN entite e ON e.id_entite = m.id_entite
            WHERE pm.id_pr = ?
            ORDER BY m.nom
            """;
        List<Medicament> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idPr);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapMedicament(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Prescription.getMedicamentsOfPrescription failed", ex);
        }
        return out;
    }

    @Override
    public List<PrescriptionMedicament> getLignes(Long idPr) {
        String sql = """
            SELECT pm.id_pr, pm.id_mct, pm.quantite
            FROM prescription_medicament pm
            WHERE pm.id_pr = ?
            ORDER BY pm.id_mct
            """;
        List<PrescriptionMedicament> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idPr);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPrescriptionMedicament(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Prescription.getLignes failed", ex);
        }
        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM prescription WHERE id_pr = ? LIMIT 1";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Prescription.existsById failed", ex);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM prescription";
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException ex) {
            throw new RuntimeException("Prescription.count failed", ex);
        }
    }

    @Override
    public List<Prescription> findPage(int limit, int offset) {
        String sql = """
            SELECT p.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM prescription p
            LEFT JOIN entite e ON e.id_entite = p.id_entite
            ORDER BY p.id_pr
            LIMIT ? OFFSET ?
            """;
        List<Prescription> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPrescription(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Prescription.findPage failed", ex);
        }
        return out;
    }

    // =========================
    // Helpers entite
    // =========================

    private Long insertEntiteIfNeeded(String creePar, String modifiePar, Long existingIdEntite) throws SQLException {
        if (existingIdEntite != null) return existingIdEntite;

        String sql = """
            INSERT INTO entite (date_creation, date_derniere_modification, cree_par, modifie_par)
            VALUES (CURRENT_DATE, CURRENT_TIMESTAMP, ?, ?)
            """;
        try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, creePar);
            ps.setString(2, modifiePar);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : null;
            }
        }
    }

    private void updateEntiteTouch(Long idEntite, String modifiePar) throws SQLException {
        if (idEntite == null) return;

        String sql = """
            UPDATE entite
            SET date_derniere_modification = CURRENT_TIMESTAMP,
                modifie_par = ?
            WHERE id_entite = ?
            """;
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, modifiePar);
            ps.setLong(2, idEntite);
            ps.executeUpdate();
        }
    }

    private void deleteEntite(Long idEntite) {
        String sql = "DELETE FROM entite WHERE id_entite = ?";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, idEntite);
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}
