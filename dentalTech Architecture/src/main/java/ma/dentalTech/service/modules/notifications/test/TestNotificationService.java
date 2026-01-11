package ma.dentalTech.service.modules.notifications.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.TitreNotification;
import ma.dentalTech.entities.enums.TypeNotification;
import ma.dentalTech.service.modules.notifications.api.NotificationService;

public class TestNotificationService {
    public static void main(String[] args) throws Exception {

        NotificationService svc = ApplicationContext.getBean(NotificationService.class);

        // ⚠️ mets un userId qui existe chez toi (ex: admin id = 6)
        long userId = 6L;

        var n = svc.sendToUser(
                userId,
                TitreNotification.INFO,          // adapte si enum différent
                "Bienvenue dans HealthySmile",
                TypeNotification.MESSAGE_SYSTEME          // adapte si enum différent
        );

        System.out.println("Notification created: " + n);

        System.out.println("Unread count: " + svc.getUnreadUserNotifications(userId).size());
        svc.markAllAsReadForUser(userId);
        System.out.println("Unread count after markAll: " + svc.getUnreadUserNotifications(userId).size());
    }
}
