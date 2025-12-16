package ma.dentalTech.entities.base;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

/**
 * Classe de base pour toutes les entités du système.
 * Contient les champs d'audit communs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {

    protected Long id;
    protected LocalDate dateCreation;
    protected LocalDateTime dateDerniereModification;
    protected String modifiePar;
    protected String creePar;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        BaseEntity that = (BaseEntity) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
