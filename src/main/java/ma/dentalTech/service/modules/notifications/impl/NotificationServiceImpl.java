package ma.dentalTech.service.modules.notifications.impl;

import ma.dentalTech.entities.enums.TitreNotification;
import ma.dentalTech.entities.enums.TypeNotification;
import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import ma.dentalTech.service.modules.notifications.api.NotificationService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UtilisateurRepository utilisateurRepository) {
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // ------------------- queries -------------------

    @Override
    public List<Notification> getUserNotifications(Long utilisateurId) {
        if (utilisateurId == null) throw new IllegalArgumentException("utilisateurId obligatoire");
        ensureUserExists(utilisateurId);
        return notificationRepository.findByUtilisateur(utilisateurId);
    }

    @Override
    public List<Notification> getUnreadUserNotifications(Long utilisateurId) {
        if (utilisateurId == null) throw new IllegalArgumentException("utilisateurId obligatoire");
        ensureUserExists(utilisateurId);
        return notificationRepository.findUnreadByUtilisateur(utilisateurId);
    }

    @Override
    public List<Notification> getUserNotificationsByDate(Long utilisateurId, LocalDate date) {
        if (utilisateurId == null) throw new IllegalArgumentException("utilisateurId obligatoire");
        if (date == null) throw new IllegalArgumentException("date obligatoire");
        ensureUserExists(utilisateurId);
        return notificationRepository.findByDate(utilisateurId, date);
    }

    @Override
    public List<Notification> getUserNotificationsByType(Long utilisateurId, TypeNotification type) {
        if (utilisateurId == null) throw new IllegalArgumentException("utilisateurId obligatoire");
        if (type == null) throw new IllegalArgumentException("type obligatoire");
        ensureUserExists(utilisateurId);
        return notificationRepository.findByType(utilisateurId, type);
    }

    @Override
    public List<Notification> getUserNotificationsByTitre(Long utilisateurId, TitreNotification titre) {
        if (utilisateurId == null) throw new IllegalArgumentException("utilisateurId obligatoire");
        if (titre == null) throw new IllegalArgumentException("titre obligatoire");
        ensureUserExists(utilisateurId);
        return notificationRepository.findByTitre(utilisateurId, titre);
    }

    // ------------------- commands -------------------

    @Override
    public Notification sendToUser(Long utilisateurId,
                                   TitreNotification titre,
                                   String message,
                                   TypeNotification type) throws SQLException {
        if (utilisateurId == null) throw new IllegalArgumentException("utilisateurId obligatoire");
        if (titre == null) throw new IllegalArgumentException("titre obligatoire");
        if (type == null) throw new IllegalArgumentException("type obligatoire");
        if (message == null || message.isBlank()) throw new IllegalArgumentException("message obligatoire");

        Utilisateur u = utilisateurRepository.findById(utilisateurId);
        if (u == null) throw new IllegalArgumentException("USER_NOT_FOUND");

        Notification n = Notification.builder()
                .titre(titre)
                .message(message)
                .type(type)
                .lue(false)
                .utilisateur(u)
                .build();

        n.setCreePar("system");
        n.setModifiePar("system");
        notificationRepository.create(n);
        return n;
    }

    @Override
    public void markAsRead(Long notificationId) {
        if (notificationId == null) throw new IllegalArgumentException("notificationId obligatoire");
        // optionnel : vérifier existence
        Notification n = notificationRepository.findById(notificationId);
        if (n == null) throw new IllegalArgumentException("NOTIFICATION_NOT_FOUND");

        notificationRepository.markAsRead(notificationId);
    }

    @Override
    public void markAllAsReadForUser(Long utilisateurId) {
        if (utilisateurId == null) throw new IllegalArgumentException("utilisateurId obligatoire");
        ensureUserExists(utilisateurId);
        notificationRepository.markAllAsReadForUser(utilisateurId);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        if (notificationId == null) throw new IllegalArgumentException("notificationId obligatoire");
        notificationRepository.deleteById(notificationId);
    }

    // ------------------- helpers -------------------

    private void ensureUserExists(Long utilisateurId) {
        if (utilisateurRepository.findById(utilisateurId) == null) {
            throw new IllegalArgumentException("USER_NOT_FOUND");
        }
    }
}
