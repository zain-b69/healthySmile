package ma.dentalTech.entities.cabinet;

import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;

import java.time.LocalDateTime;

/**
 * Entité représentant les revenus du cabinet.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Revenues extends BaseEntity {

    // PK
    private Long idRevenu;

    private String titre;
    private String description;
    private Double montant;
    private LocalDateTime date;

    // FK vers CabinetMedical
    private Long idCabinet;

    // Relation objet (facultative)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private CabinetMedical cabinet;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Revenues)) return false;
        Revenues that = (Revenues) o;
        return idRevenu != null && idRevenu.equals(that.idRevenu);
    }

    @Override
    public int hashCode() {
        return idRevenu != null ? idRevenu.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Revenues {
                idRevenu = %d,
                titre = '%s',
                montant = %.2f,
                date = %s
            }
            """.formatted(idRevenu, titre, montant, date);
    }
}
