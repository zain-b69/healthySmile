package ma.dentalTech.repository.modules.patient.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AntecedentRepositoryImpl implements AntecedentRepository {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    private Connection cn() throws SQLException {
        return sessionFactory.getConnection();
    }

    private static String toDbNiveau(NiveauRisque n) {
        if (n == null) return null;
        return switch (n) {
            case FAIBLE -> "FAIBLE";
            case MOYEN -> "MOYEN";      // ✅ si DB a MOYEN
            case ELEVE -> "ELEVE";
            case CRITIQUE -> "CRITIQUE"; // ✅ si DB a CRITIQUE, sinon map vers ELEVE
        };
    }

    // =========================
    // CRUD
    // =========================

    @Override
    public List<Antecedent> findAll() {
        String sql = """
            SELECT a.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM antecedents a
            LEFT JOIN entite e ON e.id_entite = a.id_entite
            ORDER BY a.id_antecedent
            """;

        List<Antecedent> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
        } catch (SQLException ex) {
            throw new RuntimeException("Antecedent.findAll failed", ex);
        }
        return out;
    }

    @Override
    public Antecedent findById(Long id) {
        if (id == null) return null;

        String sql = """
            SELECT a.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM antecedents a
            LEFT JOIN entite e ON e.id_entite = a.id_entite
            WHERE a.id_antecedent = ?
            """;

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapAntecedent(rs);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Antecedent.findById failed", ex);
        }
    }

    @Override
    public void create(Antecedent a) {
        if (a == null) throw new IllegalArgumentException("Antecedent is null");

        try {
            Long idEntite = insertEntiteIfNeeded(a);

            String sql = """
                INSERT INTO antecedents (id_entite, nom, categorie, niveau_de_risque)
                VALUES (?, ?, ?, ?)
                """;

            try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setObject(1, idEntite, Types.BIGINT);
                ps.setString(2, a.getNom());

                // categorie = varchar(100)
                Object cat = a.getCategorie(); // peut être String ou Enum selon ton entity
                ps.setString(3, cat != null ? cat.toString() : null);

                // niveau_de_risque = enum(...)
                ps.setString(4, toDbNiveau(a.getNiveauDeRisque()));


                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) a.setIdAntecedent(keys.getLong(1));
                }

                a.setId(idEntite); // BaseEntity.id = id_entite
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Antecedent.create failed", ex);
        }
    }

    @Override
    public void update(Antecedent a) {
        if (a == null || a.getIdAntecedent() == null)
            throw new IllegalArgumentException("Antecedent.idAntecedent is null");

        String sql = """
            UPDATE antecedents
            SET nom = ?,
                categorie = ?,
                niveau_de_risque = ?
            WHERE id_antecedent = ?
            """;

        try (PreparedStatement ps = cn().prepareStatement(sql)) {

            ps.setString(1, a.getNom());

            Object cat = a.getCategorie();
            ps.setString(2, cat != null ? cat.toString() : null);

            NiveauRisque nr = a.getNiveauDeRisque();
            ps.setString(3, nr != null ? nr.name() : null);

            ps.setLong(4, a.getIdAntecedent());

            ps.executeUpdate();

            // audit touch (si entite existe)
            updateEntiteTouch(a.getId(), a.getModifiePar());

        } catch (SQLException ex) {
            throw new RuntimeException("Antecedent.update failed", ex);
        }
    }

    @Override
    public void delete(Antecedent a) {
        if (a == null || a.getIdAntecedent() == null) return;
        deleteById(a.getIdAntecedent());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) return;

        Antecedent a = findById(id);

        String sql = "DELETE FROM antecedents WHERE id_antecedent = ?";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Antecedent.deleteById failed", ex);
        }

        // optionnel : supprimer entite si tu veux
        if (a != null && a.getId() != null) {
            deleteEntite(a.getId());
        }
    }

    // =========================
    // Extra methods (API)
    // =========================

    @Override
    public Optional<Antecedent> findByNom(String nom) {
        String sql = """
            SELECT a.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM antecedents a
            LEFT JOIN entite e ON e.id_entite = a.id_entite
            WHERE a.nom = ?
            LIMIT 1
            """;
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapAntecedent(rs));
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Antecedent.findByNom failed", ex);
        }
    }

    @Override
    public List<Antecedent> findByCategorie(CategorieAntecedent categorie) {
        String sql = """
            SELECT a.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM antecedents a
            LEFT JOIN entite e ON e.id_entite = a.id_entite
            WHERE a.categorie = ?
            ORDER BY a.nom
            """;
        List<Antecedent> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, categorie != null ? categorie.name() : null);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Antecedent.findByCategorie failed", ex);
        }
        return out;
    }

    @Override
    public List<Antecedent> findByNiveauRisque(NiveauRisque niveau) {
        String sql = """
            SELECT a.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM antecedents a
            LEFT JOIN entite e ON e.id_entite = a.id_entite
            WHERE a.niveau_de_risque = ?
            ORDER BY a.nom
            """;
        List<Antecedent> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setString(1, niveau != null ? niveau.name() : null);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Antecedent.findByNiveauRisque failed", ex);
        }
        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM antecedents WHERE id_antecedent = ? LIMIT 1";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Antecedent.existsById failed", ex);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM antecedents";
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException ex) {
            throw new RuntimeException("Antecedent.count failed", ex);
        }
    }

    @Override
    public List<Antecedent> findPage(int limit, int offset) {
        String sql = """
            SELECT a.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM antecedents a
            LEFT JOIN entite e ON e.id_entite = a.id_entite
            ORDER BY a.id_antecedent
            LIMIT ? OFFSET ?
            """;
        List<Antecedent> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Antecedent.findPage failed", ex);
        }
        return out;
    }

    // ---- Navigation inverse ----
    @Override
    public List<Patient> getPatientsHavingAntecedent(Long antecedentId) {
        // ⚠️ adapte le nom de la table d'association si besoin
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
            throw new RuntimeException("Antecedent.getPatientsHavingAntecedent failed", ex);
        }
        return out;
    }

    // =========================
    // entite helpers
    // =========================

    private Long insertEntiteIfNeeded(Antecedent a) throws SQLException {
        if (a.getId() != null) return a.getId();

        String sql = """
            INSERT INTO entite (date_creation, date_derniere_modification, cree_par, modifie_par)
            VALUES (CURRENT_DATE, CURRENT_TIMESTAMP, ?, ?)
            """;
        try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getCreePar());
            ps.setString(2, a.getModifiePar());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                return null;
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
            // ignore si FK bloque, ou tu peux logguer
        }
    }
}
