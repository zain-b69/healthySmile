package ma.dentalTech.entities.cabinet;

import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;

import java.time.LocalDateTime;

/**
 * Entité représentant les charges du cabinet.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Charges extends BaseEntity {

    // PK
    private Long idCharge;

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
        if (!(o instanceof Charges)) return false;
        Charges that = (Charges) o;
        return idCharge != null && idCharge.equals(that.idCharge);
    }

    @Override
    public int hashCode() {
        return idCharge != null ? idCharge.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Charges {
                idCharge = %d,
                titre = '%s',
                montant = %.2f,
                date = %s
            }
            """.formatted(idCharge, titre, montant, date);
    }
}
