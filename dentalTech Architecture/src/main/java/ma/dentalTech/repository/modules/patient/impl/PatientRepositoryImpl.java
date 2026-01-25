package ma.dentalTech.repository.modules.patient.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientRepositoryImpl implements PatientRepository {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    private Connection cn() throws SQLException {
        return sessionFactory.getConnection();
    }

    // =========================
    // CRUD
    // =========================

    @Override
    public List<Patient> findAll() {
        String sql = """
            SELECT p.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM patient p
            LEFT JOIN entite e ON e.id_entite = p.id_entite
            ORDER BY p.id_patient
            """;

        List<Patient> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapPatient(rs));
        } catch (SQLException ex) {
            throw new RuntimeException("Patient.findAll failed", ex);
        }
        return out;
    }

    @Override
    public Patient findById(Long id) {
        if (id == null) return null;

        String sql = """
            SELECT p.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM patient p
            LEFT JOIN entite e ON e.id_entite = p.id_entite
            WHERE p.id_patient = ?
            """;

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapPatient(rs) : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Patient.findById failed", ex);
        }
    }

    @Override
    public void create(Patient p) {
        System.out.println(">>> PatientRepositoryImpl.CREATE RUNNING (v2) " + this.getClass().getName());
        if (p == null) throw new IllegalArgumentException("Patient is null");

        try {
            Connection c = cn(); // ⚠️ ne pas fermer (singleton)

            Long idEntite = insertEntiteIfNeeded(p);
            if (idEntite == null || idEntite == 0L) {
                throw new IllegalStateException("idEntite is null/0 after insertEntiteIfNeeded");
            }

            String sql = """
            INSERT INTO patient (id_entite, nom, date_de_naissance, sexe, adresse, telephone, assurance)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, idEntite);
                ps.setString(2, p.getNom());
                ps.setDate(3, Date.valueOf(p.getDateNaissance())); // NOT NULL en DB
                ps.setString(4, p.getSexe() != null ? p.getSexe().name() : null);
                ps.setString(5, p.getAdresse());
                ps.setString(6, p.getTelephone());
                ps.setString(7, p.getAssurance() != null ? p.getAssurance().name() : null);

                ps.executeUpdate();
            }

            Long idPatient = null;
            try (PreparedStatement ps2 = c.prepareStatement("SELECT LAST_INSERT_ID()");
                 ResultSet rs = ps2.executeQuery()) {
                if (rs.next()) idPatient = rs.getLong(1);
            }

            // fallback final par id_entite (au cas où)
            if (idPatient == null || idPatient == 0L) {
                try (PreparedStatement ps3 = c.prepareStatement(
                        "SELECT id_patient FROM patient WHERE id_entite = ? LIMIT 1"
                )) {
                    ps3.setLong(1, idEntite);
                    try (ResultSet rs = ps3.executeQuery()) {
                        if (rs.next()) idPatient = rs.getLong(1);
                    }
                }
            }

            if (idPatient == null || idPatient == 0L) {
                throw new IllegalStateException("Insert patient OK but cannot retrieve id_patient");
            }

            p.setIdPatient(idPatient);
            p.setId(idEntite);

            // DEBUG (temporaire)
            System.out.println("DEBUG repo.create: idEntite=" + p.getId() + " idPatient=" + p.getIdPatient());

        } catch (SQLException ex) {
            throw new RuntimeException("Patient.create failed", ex);
        }
    }

    @Override
    public void update(Patient p) {
        if (p == null || p.getIdPatient() == null)
            throw new IllegalArgumentException("Patient.idPatient is null");

        String sql = """
            UPDATE patient
            SET nom = ?,
                date_de_naissance = ?,
                sexe = ?,
                adresse = ?,
                telephone = ?,
                assurance = ?
            WHERE id_patient = ?
            """;

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setDate(2, p.getDateNaissance() != null ? Date.valueOf(p.getDateNaissance()) : null);
            ps.setString(3, p.getSexe() != null ? p.getSexe().name() : null);
            ps.setString(4, p.getAdresse());
            ps.setString(5, p.getTelephone());
            ps.setString(6, p.getAssurance() != null ? p.getAssurance().name() : null);
            ps.setLong(7, p.getIdPatient());

            ps.executeUpdate();

            updateEntiteTouch(p.getId(), p.getModifiePar());

        } catch (SQLException ex) {
            throw new RuntimeException("Patient.update failed", ex);
        }
    }

    @Override
    public void delete(Patient p) {
        if (p == null || p.getIdPatient() == null) return;
        deleteById(p.getIdPatient());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        Patient p = findById(id);

        // d'abord supprimer les liaisons many-to-many (si FK)
        removeAllAntecedentsFromPatient(id);

        String sql = "DELETE FROM patient WHERE id_patient = ?";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Patient.deleteById failed", ex);
        }

        // optionnel : supprimer entite
        if (p != null && p.getId() != null) deleteEntite(p.getId());
    }

    // =========================
    // Extra methods
    // =========================

    @Override
    public Optional<Patient> findByEmail(String email) {
        // ⚠️ Ta table patient n’a pas de colonne email.
        // Option 1: ajouter email à la table
        // Option 2: supprimer cette méthode de l'interface
        throw new UnsupportedOperationException("patient.email n'existe pas dans la table (ajoute la colonne ou supprime la méthode).");
    }

    @Override
    public Optional<Patient> findByTelephone(String telephone) {
        String sql = """
            SELECT p.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM patient p
            LEFT JOIN entite e ON e.id_entite = p.id_entite
            WHERE p.telephone = ?
            LIMIT 1
            """;

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, telephone);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapPatient(rs)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Patient.findByTelephone failed", ex);
        }
    }

    @Override
    public List<Patient> searchByNomPrenom(String keyword) {
        // ⚠️ pas de prenom en DB => recherche sur nom seulement
        String sql = """
            SELECT p.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM patient p
            LEFT JOIN entite e ON e.id_entite = p.id_entite
            WHERE p.nom LIKE ?
            ORDER BY p.nom
            """;

        List<Patient> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Patient.searchByNomPrenom failed", ex);
        }
        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM patient WHERE id_patient = ? LIMIT 1";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Patient.existsById failed", ex);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM patient";
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException ex) {
            throw new RuntimeException("Patient.count failed", ex);
        }
    }

    @Override
    public List<Patient> findPage(int limit, int offset) {
        String sql = """
            SELECT p.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM patient p
            LEFT JOIN entite e ON e.id_entite = p.id_entite
            ORDER BY p.id_patient
            LIMIT ? OFFSET ?
            """;

        List<Patient> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Patient.findPage failed", ex);
        }
        return out;
    }

    // =========================
    // Many-to-Many: patient <-> antecedent
    // =========================

    @Override
    public void addAntecedentToPatient(Long patientId, Long antecedentId) {
        String sql = "INSERT INTO patient_antecedent (id_patient, id_antecedent) VALUES (?, ?)";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ps.setLong(2, antecedentId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Patient.addAntecedentToPatient failed", ex);
        }
    }

    @Override
    public void removeAntecedentFromPatient(Long patientId, Long antecedentId) {
        String sql = "DELETE FROM patient_antecedent WHERE id_patient = ? AND id_antecedent = ?";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ps.setLong(2, antecedentId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Patient.removeAntecedentFromPatient failed", ex);
        }
    }

    @Override
    public void removeAllAntecedentsFromPatient(Long patientId) {
        String sql = "DELETE FROM patient_antecedent WHERE id_patient = ?";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            // si la table n'existe pas encore, tu verras l'erreur -> crée-la
            throw new RuntimeException("Patient.removeAllAntecedentsFromPatient failed", ex);
        }
    }

    @Override
    public List<Antecedent> getAntecedentsOfPatient(Long patientId) {
        String sql = """
            SELECT a.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM antecedents a
            JOIN patient_antecedent pa ON pa.id_antecedent = a.id_antecedent
            LEFT JOIN entite e ON e.id_entite = a.id_entite
            WHERE pa.id_patient = ?
            ORDER BY a.nom
            """;

        List<Antecedent> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Patient.getAntecedentsOfPatient failed", ex);
        }
        return out;
    }

    @Override
    public List<Patient> getPatientsByAntecedent(Long antecedentId) {
        String sql = """
            SELECT p.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM patient p
            JOIN patient_antecedent pa ON pa.id_patient = p.id_patient
            LEFT JOIN entite e ON e.id_entite = p.id_entite
            WHERE pa.id_antecedent = ?
            ORDER BY p.nom
            """;

        List<Patient> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, antecedentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Patient.getPatientsByAntecedent failed", ex);
        }
        return out;
    }

    // =========================
    // entite helpers
    // =========================

    private Long insertEntiteIfNeeded(Patient p) throws SQLException {
        System.out.println(">>> insertEntiteIfNeeded called, creePar=" + p.getCreePar());
        if (p.getId() != null) return p.getId();

        Connection c = cn(); // ⚠️ ne pas fermer (singleton)

        String sql = """
        INSERT INTO entite (date_creation, date_derniere_modification, cree_par, modifie_par)
        VALUES (CURRENT_DATE, CURRENT_TIMESTAMP, ?, ?)
        """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getCreePar());
            ps.setString(2, p.getModifiePar());
            ps.executeUpdate();
        }

        Long idEntite = null;
        try (PreparedStatement ps2 = c.prepareStatement("SELECT LAST_INSERT_ID()");
             ResultSet rs = ps2.executeQuery()) {
            if (rs.next()) idEntite = rs.getLong(1);
        }

        if (idEntite == null || idEntite == 0L) {
            throw new IllegalStateException("Insert entite OK but LAST_INSERT_ID() returned null/0");
        }

        p.setId(idEntite);
        return idEntite;
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
