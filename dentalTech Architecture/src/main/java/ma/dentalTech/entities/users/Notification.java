package ma.dentalTech.entities.users;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.enums.TitreNotification;
import ma.dentalTech.entities.enums.TypeNotification;

/**
 * Entité représentant une notification dans le système.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Notification extends BaseEntity {

    private Long idNotification; // PK notification.id_notification
    private TitreNotification titre;
    private String message;
    private LocalDate date;
    private LocalTime time;
    private TypeNotification type;
    private boolean lue;

    private Utilisateur utilisateur;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        Notification that = (Notification) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Notification {
                id = %d,
                titre = %s,
                message = '%s',
                date = %s,
                lue = %b
            }
            """.formatted(id, titre, message, date, lue);
    }
}
