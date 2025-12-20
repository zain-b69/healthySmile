package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.enums.TitreNotification;
import ma.dentalTech.entities.enums.TypeNotification;
import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepositoryImpl implements NotificationRepository {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    private Connection cn() throws SQLException {
        return sessionFactory.getConnection();
    }

    // -------------------- CRUD (CrudRepository) --------------------

    @Override
    public List<Notification> findAll() {
        String sql = """
            SELECT n.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM notification n
            LEFT JOIN entite e ON e.id_entite = n.id_entite
            ORDER BY n.date_notification DESC, n.id_notification DESC
            """;

        List<Notification> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapNotification(rs));
        } catch (SQLException ex) {
            throw new RuntimeException("Notification.findAll failed", ex);
        }
        return out;
    }

    @Override
    public Notification findById(Long id) {
        String sql = """
            SELECT n.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM notification n
            LEFT JOIN entite e ON e.id_entite = n.id_entite
            WHERE n.id_notification = ?
            """;

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapNotification(rs) : null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Notification.findById failed", ex);
        }
    }

    @Override
    public void create(Notification n) {
        if (n == null) return;

        Long userId = extractUtilisateurId(n.getUtilisateur());
        if (userId == null) throw new IllegalArgumentException("Notification.utilisateur.idUser is required");

        try {
            Long idEntite = insertEntiteIfNeeded(n);

            String sql = """
                INSERT INTO notification (id_entite, id_utilisateur, titre, message, type_notification, date_notification, lu)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

            try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setObject(1, idEntite, Types.BIGINT);
                ps.setLong(2, userId);
                ps.setString(3, n.getTitre() != null ? n.getTitre().name() : null);
                ps.setString(4, n.getMessage());
                ps.setString(5, n.getType() != null ? n.getType().name() : null);

                // date_notification NOT NULL => on met "maintenant" si date/time pas fournis
                Timestamp ts = toTimestampOrNow(n.getDate(), n.getTime());
                ps.setTimestamp(6, ts);

                ps.setBoolean(7, n.isLue());

                ps.executeUpdate();

                // Stocker la PK si ton entity a setIdNotification(Long)
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) setNotificationPkIfPresent(n, keys.getLong(1));
                }

                // BaseEntity.id = id_entite
                n.setId(idEntite);
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Notification.create failed", ex);
        }
    }

    @Override
    public void update(Notification n) {
        if (n == null) return;

        Long notificationId = extractNotificationPk(n);
        if (notificationId == null) {
            throw new IllegalArgumentException("Notification PK missing (add field idNotification + getter/setter OR ensure it's set)");
        }

        Long userId = extractUtilisateurId(n.getUtilisateur());
        if (userId == null) throw new IllegalArgumentException("Notification.utilisateur.idUser is required");

        String sql = """
            UPDATE notification
            SET id_utilisateur = ?,
                titre = ?,
                message = ?,
                type_notification = ?,
                date_notification = ?,
                lu = ?
            WHERE id_notification = ?
            """;

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, n.getTitre() != null ? n.getTitre().name() : null);
            ps.setString(3, n.getMessage());
            ps.setString(4, n.getType() != null ? n.getType().name() : null);
            ps.setTimestamp(5, toTimestampOrNow(n.getDate(), n.getTime()));
            ps.setBoolean(6, n.isLue());
            ps.setLong(7, notificationId);

            ps.executeUpdate();

            // audit (si entite existe)
            Long idEntite = (n.getId() != null) ? n.getId() : findEntiteIdByNotificationId(notificationId);
            updateEntiteTouch(idEntite, n.getModifiePar());

        } catch (SQLException ex) {
            throw new RuntimeException("Notification.update failed", ex);
        }
    }

    @Override
    public void delete(Notification n) {
        if (n == null) return;
        Long notificationId = extractNotificationPk(n);
        if (notificationId != null) deleteById(notificationId);
    }

    @Override
    public void deleteById(Long id) {
        Long idEntite = findEntiteIdByNotificationId(id);

        String sql = "DELETE FROM notification WHERE id_notification = ?";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Notification.deleteById failed", ex);
        }

        // optionnel: supprimer entite après suppression notification
        if (idEntite != null) deleteEntite(idEntite);
    }

    // -------------------- Méthodes spécifiques (NotificationRepository) --------------------

    @Override
    public List<Notification> findByUtilisateur(Long utilisateurId) {
        String sql = """
            SELECT n.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM notification n
            LEFT JOIN entite e ON e.id_entite = n.id_entite
            WHERE n.id_utilisateur = ?
            ORDER BY n.date_notification DESC, n.id_notification DESC
            """;

        return queryList(sql, ps -> ps.setLong(1, utilisateurId));
    }

    @Override
    public List<Notification> findUnreadByUtilisateur(Long utilisateurId) {
        String sql = """
            SELECT n.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM notification n
            LEFT JOIN entite e ON e.id_entite = n.id_entite
            WHERE n.id_utilisateur = ?
              AND n.lu = 0
            ORDER BY n.date_notification DESC, n.id_notification DESC
            """;

        return queryList(sql, ps -> ps.setLong(1, utilisateurId));
    }

    @Override
    public List<Notification> findByDate(Long utilisateurId, LocalDate date) {
        String sql = """
            SELECT n.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM notification n
            LEFT JOIN entite e ON e.id_entite = n.id_entite
            WHERE n.id_utilisateur = ?
              AND DATE(n.date_notification) = ?
            ORDER BY n.date_notification DESC, n.id_notification DESC
            """;

        return queryList(sql, ps -> {
            ps.setLong(1, utilisateurId);
            ps.setDate(2, Date.valueOf(date));
        });
    }

    @Override
    public List<Notification> findByType(Long utilisateurId, TypeNotification type) {
        String sql = """
            SELECT n.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM notification n
            LEFT JOIN entite e ON e.id_entite = n.id_entite
            WHERE n.id_utilisateur = ?
              AND n.type_notification = ?
            ORDER BY n.date_notification DESC, n.id_notification DESC
            """;

        return queryList(sql, ps -> {
            ps.setLong(1, utilisateurId);
            ps.setString(2, type != null ? type.name() : null);
        });
    }

    @Override
    public List<Notification> findByTitre(Long utilisateurId, TitreNotification titre) {
        String sql = """
            SELECT n.*,
                   e.date_creation, e.date_derniere_modification, e.cree_par, e.modifie_par
            FROM notification n
            LEFT JOIN entite e ON e.id_entite = n.id_entite
            WHERE n.id_utilisateur = ?
              AND n.titre = ?
            ORDER BY n.date_notification DESC, n.id_notification DESC
            """;

        return queryList(sql, ps -> {
            ps.setLong(1, utilisateurId);
            ps.setString(2, titre != null ? titre.name() : null);
        });
    }

    @Override
    public void markAsRead(Long notificationId) {
        String sql = "UPDATE notification SET lu = 1 WHERE id_notification = ?";

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, notificationId);
            ps.executeUpdate();

            // audit (optionnel)
            Long idEntite = findEntiteIdByNotificationId(notificationId);
            updateEntiteTouch(idEntite, "system");

        } catch (SQLException ex) {
            throw new RuntimeException("Notification.markAsRead failed", ex);
        }
    }

    @Override
    public void markAllAsReadForUser(Long utilisateurId) {
        String sql = "UPDATE notification SET lu = 1 WHERE id_utilisateur = ? AND lu = 0";

        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Notification.markAllAsReadForUser failed", ex);
        }
    }

    // -------------------- Helpers --------------------

    private interface Binder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    private List<Notification> queryList(String sql, Binder binder) {
        List<Notification> out = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapNotification(rs));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Notification query failed", ex);
        }
        return out;
    }

    private Long extractUtilisateurId(Utilisateur u) {
        if (u == null) return null;
        if (u.getIdUser() != null) return u.getIdUser();
        // fallback (si quelqu’un met BaseEntity.id à la place)
        return u.getId();
    }

    private Timestamp toTimestampOrNow(LocalDate d, LocalTime t) {
        LocalDateTime ldt;
        if (d != null && t != null) ldt = LocalDateTime.of(d, t);
        else if (d != null) ldt = d.atStartOfDay();
        else ldt = LocalDateTime.now();
        return Timestamp.valueOf(ldt);
    }

    private Long extractNotificationPk(Notification n) {
        try {
            // si Notification a getIdNotification()
            Object v = n.getClass().getMethod("getIdNotification").invoke(n);
            return (v instanceof Long) ? (Long) v : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private void setNotificationPkIfPresent(Notification n, long pk) {
        try {
            n.getClass().getMethod("setIdNotification", Long.class).invoke(n, pk);
        } catch (Exception ignored) {
            // si l'entity ne possède pas idNotification, on ignore (mais update/delete auront besoin de la PK)
        }
    }

    private Long findEntiteIdByNotificationId(Long notificationId) {
        if (notificationId == null) return null;

        String sql = "SELECT id_entite FROM notification WHERE id_notification = ?";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setLong(1, notificationId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getObject("id_entite", Long.class) : null;
            }
        } catch (SQLException ex) {
            return null;
        }
    }

    private Long insertEntiteIfNeeded(BaseEntity e) throws SQLException {
        if (e.getId() != null) return e.getId();

        String sql = """
            INSERT INTO entite (date_creation, date_derniere_modification, cree_par, modifie_par)
            VALUES (CURRENT_DATE, CURRENT_TIMESTAMP, ?, ?)
            """;

        try (PreparedStatement ps = cn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getCreePar());
            ps.setString(2, e.getModifiePar());
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
            // parfois FK bloque -> on ignore
        }
    }
}
