package ma.dentalTech.repository.modules.patient.impl;

import lombok.Data;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.*;


@Data
public class PatientRepositoryImpl implements PatientRepository {



    public static void main(String[] args) throws Exception {

    }
    // -------- CRUD --------
    @Override
    public List<Patient> findAll() {
        String sql = "SELECT * FROM Patients ORDER BY id";
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapPatient(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Patient findById(Long id) {
        String sql = "SELECT * FROM Patients WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapPatient(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }


    @Override
    public void create(Patient p) {
        String sql = """
        INSERT INTO Patients(
            nom, prenom, adresse, telephone, email,
            dateNaissance, dateCreation, sexe, assurance
        )
        VALUES (?,?,?,?,?,?,?,?,?)
        """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setString(3, p.getAdresse());
            ps.setString(4, p.getTelephone());
            ps.setString(5, p.getEmail());

            // dateNaissance (LocalDate -> java.sql.Date)
            if (p.getDateNaissance() != null) {
                ps.setDate(6, Date.valueOf(p.getDateNaissance()));
            } else {
                ps.setNull(6, Types.DATE);
            }

            // dateCreation (LocalDate)
            LocalDate dateCreation = p.getDateCreation() != null
                    ? p.getDateCreation()
                    : LocalDate.now();
            ps.setDate(7, Date.valueOf(dateCreation));
            // on met à jour l'objet en mémoire
            p.setDateCreation(dateCreation);

            ps.setString(8, p.getSexe().name());
            ps.setString(9, p.getAssurance().name());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    p.setId(keys.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void update(Patient p) {
        String sql = """
            UPDATE Patients SET nom=?, prenom=?, adresse=?, telephone=?, email=?, 
                   dateNaissance=?, dateCreation=? , dateDerniereModification = ?,sexe=?, assurance=? WHERE id=?
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setString(3, p.getAdresse());
            ps.setString(4, p.getTelephone());
            ps.setString(5, p.getEmail());

            if (p.getDateNaissance() != null) ps.setDate(6, Date.valueOf(p.getDateNaissance()));
            else ps.setNull(6, Types.DATE);

            // dateCreation (LocalDate)
            LocalDate dateCreation = p.getDateCreation() != null
                    ? p.getDateCreation()
                    : LocalDate.now();
            ps.setDate(7, Date.valueOf(dateCreation));
            // on met à jour l'objet en mémoire
            p.setDateCreation(dateCreation);


            // dateCreation (LocalDate)
            LocalDateTime dateModif = LocalDateTime.now();
            ps.setTimestamp(8, Timestamp.valueOf(dateModif));
            // on met à jour l'objet en mémoire
            p.setDateDerniereModification(dateModif);

            ps.setString(9, p.getSexe().name());
            ps.setString(10, p.getAssurance().name());
            ps.setLong(11, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Patient p) { if (p != null) deleteById(p.getId()); }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Patients WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // -------- Extras --------
    @Override
    public Optional<Patient> findByEmail(String email) {
        String sql = "SELECT * FROM Patients WHERE email = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapPatient(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Patient> findByTelephone(String telephone) {
        String sql = "SELECT * FROM Patients WHERE telephone = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, telephone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapPatient(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Patient> searchByNomPrenom(String keyword) {
        String sql = "SELECT * FROM Patients WHERE nom LIKE ? OR prenom LIKE ? ORDER BY nom, prenom";
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM Patients WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public long count() {
        String sql = "SELECT COUNT(*) FROM Patients";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Patient> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Patients ORDER BY id LIMIT ? OFFSET ?";
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    // -------- Relation Many-to-Many --------
    @Override
    public void addAntecedentToPatient(Long patientId, Long antecedentId) {
        String sql = "INSERT IGNORE INTO Patient_Antecedents(patient_id, antecedent_id) VALUES (?,?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ps.setLong(2, antecedentId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void removeAntecedentFromPatient(Long patientId, Long antecedentId) {
        String sql = "DELETE FROM Patient_Antecedents WHERE patient_id=? AND antecedent_id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ps.setLong(2, antecedentId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void removeAllAntecedentsFromPatient(Long patientId) {
        String sql = "DELETE FROM Patient_Antecedents WHERE patient_id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Antecedent> getAntecedentsOfPatient(Long patientId) {
        String sql = """
            SELECT a.* 
            FROM Antecedents a 
            JOIN Patient_Antecedents pa ON pa.antecedent_id = a.id
            WHERE pa.patient_id = ?
            ORDER BY a.categorie, a.niveauRisque, a.nom
            """;
        List<Antecedent> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Patient> getPatientsByAntecedent(Long antecedentId) {
        String sql = """
            SELECT p.* 
            FROM Patients p 
            JOIN Patient_Antecedents pa ON pa.patient_id = p.id
            WHERE pa.antecedent_id = ?
            ORDER BY p.nom, p.prenom
            """;
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, antecedentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    /**
     * Trouver tous les patients ayant une assurance donnée.
     */
    public List<Patient> findByAssurance(Assurance assurance) {
        String sql = "SELECT * FROM Patients WHERE assurance = ? ORDER BY nom, prenom";
        List<Patient> result = new ArrayList<>();

        try  (Connection c = SessionFactory.getInstance().getConnection();
              PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, assurance.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(RowMappers.mapPatient(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // à remplacer par logger si tu as un logger
        }

        return result;
    }

    /**
     * Trouver tous les patients d'un sexe donné.
     */
    public List<Patient> findBySexe(Sexe sexe) {
        String sql = "SELECT * FROM Patients WHERE sexe = ? ORDER BY nom, prenom";
        List<Patient> result = new ArrayList<>();

        try  (Connection c = SessionFactory.getInstance().getConnection();
              PreparedStatement ps = c.prepareStatement(sql))  {

            ps.setString(1, sexe.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(RowMappers.mapPatient(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Recherche par préfixe (ou fragment) de téléphone.
     * Exemple : prefix = "06" → LIKE '06%'.
     */
    public List<Patient> searchByTelephoneLike(String prefix) {
        String sql = "SELECT * FROM Patients WHERE telephone LIKE ? ORDER BY nom, prenom";
        List<Patient> result = new ArrayList<>();
        String like = prefix + "%";

        try  (Connection c = SessionFactory.getInstance().getConnection();
              PreparedStatement ps = c.prepareStatement(sql))  {

            ps.setString(1, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(RowMappers.mapPatient(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Synchronise les antécédents d'un patient avec la table de jointure :
     * - supprime toutes les liaisons existantes
     * - réinsère celles présentes dans patient.getAntecedents()
     *
     * → à appeler typiquement après un update de Patient coté service.
     */
    public void syncAntecedentsForPatient(Patient patient) {
        if (patient == null || patient.getId() == null) {
            throw new IllegalArgumentException("Patient ou id patient null dans syncAntecedentsForPatient");
        }

        Long patientId = patient.getId();

        String deleteSql = "DELETE FROM Patient_Antecedents WHERE patient_id = ?";
        String insertSql = """
            INSERT INTO Patient_Antecedents (patient_id, antecedent_id)
            VALUES (?, ?)
            """;

        try  (Connection conn = SessionFactory.getInstance().getConnection();
             ) {
            conn.setAutoCommit(false);

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

                // 1) On supprime toutes les anciennes liaisons
                deleteStmt.setLong(1, patientId);
                deleteStmt.executeUpdate();

                // 2) On réinsère en batch les nouvelles liaisons
                if (patient.getAntecedents() != null) {
                    for (Antecedent ant : patient.getAntecedents()) {
                        if (ant == null || ant.getId() == null) continue;

                        insertStmt.setLong(1, patientId);
                        insertStmt.setLong(2, ant.getId());
                        insertStmt.addBatch();
                    }
                    insertStmt.executeBatch();
                }

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
