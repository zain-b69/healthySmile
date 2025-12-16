package ma.dentalTech.repository.modules.users.api;


import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.entities.enums.TypeNotification;
import ma.dentalTech.entities.enums.TitreNotification;
import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification, Long> {

    List<Notification> findByUtilisateur(Long utilisateurId);
    List<Notification> findUnreadByUtilisateur(Long utilisateurId);
    List<Notification> findByDate(Long utilisateurId, LocalDate date);
    List<Notification> findByType(Long utilisateurId, TypeNotification type);
    List<Notification> findByTitre(Long utilisateurId, TitreNotification titre);
    List<Notification> findByPriorite(Long utilisateurId, PrioriteNotification priorite);

    void markAsRead(Long notificationId);
    void markAllAsReadForUser(Long utilisateurId);
}
