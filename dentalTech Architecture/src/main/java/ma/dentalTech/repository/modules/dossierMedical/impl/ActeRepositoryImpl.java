package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActeRepositoryImpl implements ActeRepository {

    private final DataSource dataSource;

    public ActeRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Acte save(Acte acte) {
        final String sql = "INSERT INTO acte (libelle, categorie, prix_de_base) VALUES (?, ?, ?)";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, acte.getLibelle());
            ps.setString(2, acte.getCategorie());
            if (acte.getPrixDeBase() == null) ps.setNull(3, Types.DOUBLE);
            else ps.setDouble(3, acte.getPrixDeBase());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    acte.setIdActe(rs.getLong(1));
                }
            }
            return acte;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur save Acte", e);
        }
    }

    @Override
    public Acte update(Acte acte) {
        if (acte.getIdActe() == null) {
            throw new IllegalArgumentException("idActe est requis pour update");
        }

        final String sql = "UPDATE acte SET libelle=?, categorie=?, prix_de_base=? WHERE id_acte=?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, acte.getLibelle());
            ps.setString(2, acte.getCategorie());
            if (acte.getPrixDeBase() == null) ps.setNull(3, Types.DOUBLE);
            else ps.setDouble(3, acte.getPrixDeBase());
            ps.setLong(4, acte.getIdActe());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("Acte introuvable: " + acte.getIdActe());
            }
            return acte;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update Acte", e);
        }
    }

    @Override
    public Optional<Acte> findById(Long idActe) {
        final String sql = "SELECT id_acte, libelle, categorie, prix_de_base FROM acte WHERE id_acte=?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, idActe);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById Acte", e);
        }
    }

    @Override
    public List<Acte> findAll() {
        final String sql = "SELECT id_acte, libelle, categorie, prix_de_base FROM acte";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Acte> list = new ArrayList<>();
            while (rs.next()) list.add(mapRow(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll Acte", e);
        }
    }

    @Override
    public boolean deleteById(Long idActe) {
        final String sql = "DELETE FROM acte WHERE id_acte=?";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setLong(1, idActe);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur deleteById Acte", e);
        }
    }

    @Override
    public List<Acte> findByCategorie(String categorie) {
        final String sql = "SELECT id_acte, libelle, categorie, prix_de_base FROM acte WHERE LOWER(categorie)=LOWER(?)";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, categorie);

            try (ResultSet rs = ps.executeQuery()) {
                List<Acte> list = new ArrayList<>();
                while (rs.next()) list.add(mapRow(rs));
                return list;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByCategorie Acte", e);
        }
    }

    @Override
    public List<Acte> findByLibelleContains(String keyword) {
        final String sql = "SELECT id_acte, libelle, categorie, prix_de_base FROM acte WHERE LOWER(libelle) LIKE LOWER(?)";

        try (Connection cn = dataSource.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                List<Acte> list = new ArrayList<>();
                while (rs.next()) list.add(mapRow(rs));
                return list;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByLibelleContains Acte", e);
        }
    }

    @Override
    public List<Acte> findByCategorieIgnoreCase(String categorie) {
        return List.of();
    }

    @Override
    public List<Acte> findByLibelleContainingIgnoreCase(String keyword) {
        return List.of();
    }

    private Acte mapRow(ResultSet rs) throws SQLException {
        Acte acte = new Acte();
        acte.setIdActe(rs.getLong("id_acte"));
        acte.setLibelle(rs.getString("libelle"));
        acte.setCategorie(rs.getString("categorie"));

        double prix = rs.getDouble("prix_de_base");
        acte.setPrixDeBase(rs.wasNull() ? null : prix);

        // interventions non chargées ici (Repository Acte = table acte uniquement)
        return acte;
    }
}
