package ma.dentalTech.service.modules.notifications.api;

import ma.dentalTech.entities.enums.TitreNotification;
import ma.dentalTech.entities.enums.TypeNotification;
import ma.dentalTech.entities.users.Notification;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface NotificationService {

    List<Notification> getUserNotifications(Long utilisateurId);

    List<Notification> getUnreadUserNotifications(Long utilisateurId);

    List<Notification> getUserNotificationsByDate(Long utilisateurId, LocalDate date);

    List<Notification> getUserNotificationsByType(Long utilisateurId, TypeNotification type);

    List<Notification> getUserNotificationsByTitre(Long utilisateurId, TitreNotification titre);

    Notification sendToUser(Long utilisateurId,
                            TitreNotification titre,
                            String message,
                            TypeNotification type) throws SQLException;

    void markAsRead(Long notificationId);

    void markAllAsReadForUser(Long utilisateurId);

    void deleteNotification(Long notificationId);
}
