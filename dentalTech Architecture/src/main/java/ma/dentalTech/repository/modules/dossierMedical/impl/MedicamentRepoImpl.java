package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepo;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MedicamentRepoImpl implements MedicamentRepo {

    private DataSource dataSource; // optionnel
    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    public MedicamentRepoImpl() {
    }

    public MedicamentRepoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection cn() throws SQLException {
        if (dataSource != null) return dataSource.getConnection();
        return sessionFactory.getConnection();
    }

    @Override
    public Medicament save(Medicament m) {
        final String sql =
                "INSERT INTO medicament (nom, laboratoire, type, forme, remboursable, prix_unitaire, description) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection cn = cn();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getNom());
            ps.setString(2, m.getLaboratoire());
            ps.setString(3, m.getType());
            ps.setString(4, m.getForme());

            if (m.getRemboursable() == null) ps.setNull(5, Types.BOOLEAN);
            else ps.setBoolean(5, m.getRemboursable());

            setDoubleOrNull(ps, 6, m.getPrixUnitaire());
            ps.setString(7, m.getDescription());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) m.setIdMct(rs.getLong(1));
            }
            return m;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur save Medicament", e);
        }
    }

    @Override
    public Medicament update(Medicament m) {
        if (m.getIdMct() == null) {
            throw new IllegalArgumentException("idMct est requis pour update");
        }

        final String sql =
                "UPDATE medicament SET nom=?, laboratoire=?, type=?, forme=?, remboursable=?, prix_unitaire=?, description=? " +
                        "WHERE id_mct=?";

        try (Connection cn = cn();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, m.getNom());
            ps.setString(2, m.getLaboratoire());
            ps.setString(3, m.getType());
            ps.setString(4, m.getForme());

            if (m.getRemboursable() == null) ps.setNull(5, Types.BOOLEAN);
            else ps.setBoolean(5, m.getRemboursable());

            setDoubleOrNull(ps, 6, m.getPrixUnitaire());
            ps.setString(7, m.getDescription());
            ps.setLong(8, m.getIdMct());

            int updated = ps.executeUpdate();
            if (updated == 0) throw new RuntimeException("Medicament introuvable: " + m.getIdMct());

            return m;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update Medicament", e);
        }
    }

    @Override
    public Optional<Medicament> findById(Long idMct) {
        final String sql =
                "SELECT id_mct, nom, laboratoire, type, forme, remboursable, prix_unitaire, description " +
                        "FROM medicament WHERE id_mct=?";

        return queryOne(sql, ps -> ps.setLong(1, idMct));
    }

    @Override
    public List<Medicament> findAll() {
        final String sql =
                "SELECT id_mct, nom, laboratoire, type, forme, remboursable, prix_unitaire, description " +
                        "FROM medicament";

        return queryList(sql, ps -> {});
    }

    @Override
    public boolean deleteById(Long idMct) {
        final String sql = "DELETE FROM medicament WHERE id_mct=?";

        try (Connection cn = cn();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, idMct);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById Medicament", e);
        }
    }

    @Override
    public List<Medicament> findByNomContains(String keyword) {
        final String sql =
                "SELECT id_mct, nom, laboratoire, type, forme, remboursable, prix_unitaire, description " +
                        "FROM medicament WHERE LOWER(nom) LIKE LOWER(?)";

        return queryList(sql, ps -> ps.setString(1, "%" + keyword + "%"));
    }

    @Override
    public List<Medicament> findByLaboratoire(String laboratoire) {
        final String sql =
                "SELECT id_mct, nom, laboratoire, type, forme, remboursable, prix_unitaire, description " +
                        "FROM medicament WHERE LOWER(laboratoire)=LOWER(?)";

        return queryList(sql, ps -> ps.setString(1, laboratoire));
    }

    @Override
    public List<Medicament> findByType(String type) {
        final String sql =
                "SELECT id_mct, nom, laboratoire, type, forme, remboursable, prix_unitaire, description " +
                        "FROM medicament WHERE LOWER(type)=LOWER(?)";

        return queryList(sql, ps -> ps.setString(1, type));
    }

    @Override
    public List<Medicament> findByRemboursable(boolean remboursable) {
        final String sql =
                "SELECT id_mct, nom, laboratoire, type, forme, remboursable, prix_unitaire, description " +
                        "FROM medicament WHERE remboursable=?";

        return queryList(sql, ps -> ps.setBoolean(1, remboursable));
    }

    // -------------- Helpers --------------

    private interface StatementBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    private Optional<Medicament> queryOne(String sql, StatementBinder binder) {
        try (Connection cn = cn();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            binder.bind(ps);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur queryOne Medicament", e);
        }
    }

    private List<Medicament> queryList(String sql, StatementBinder binder) {
        try (Connection cn = cn();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            binder.bind(ps);

            try (ResultSet rs = ps.executeQuery()) {
                List<Medicament> list = new ArrayList<>();
                while (rs.next()) list.add(mapRow(rs));
                return list;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur queryList Medicament", e);
        }
    }

    private Medicament mapRow(ResultSet rs) throws SQLException {
        Medicament m = new Medicament();

        m.setIdMct(rs.getLong("id_mct"));
        m.setNom(rs.getString("nom"));
        m.setLaboratoire(rs.getString("laboratoire"));
        m.setType(rs.getString("type"));
        m.setForme(rs.getString("forme"));

        boolean remb = rs.getBoolean("remboursable");
        m.setRemboursable(rs.wasNull() ? null : remb);

        double prix = rs.getDouble("prix_unitaire");
        m.setPrixUnitaire(rs.wasNull() ? null : prix);

        m.setDescription(rs.getString("description"));
        m.setPrescriptions(new ArrayList<>());

        return m;
    }

    private void setDoubleOrNull(PreparedStatement ps, int idx, Double value) throws SQLException {
        if (value == null) ps.setNull(idx, Types.DOUBLE);
        else ps.setDouble(idx, value);
    }
}
