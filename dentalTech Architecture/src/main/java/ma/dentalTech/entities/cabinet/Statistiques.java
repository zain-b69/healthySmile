package ma.dentalTech.entities.cabinet;

import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;

import java.time.LocalDate;

/**
 * Entité représentant les statistiques du cabinet.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statistiques extends BaseEntity {

    // PK
    private Long idStatistique;

    // FK vers entite (si tu la gardes)
    private Long idEntite;

    // FK vers cabinet
    private Long idCabinet;

    private String periode;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    private Integer nbPatients;
    private Integer nbRdv;
    private Integer nbConsultation;
    private Double revenuTotal;
    private Double tauxAnnulation;
    private String actePlusEffectif;

    // Relation objet facultative
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private CabinetMedical cabinet;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Statistiques)) return false;
        Statistiques that = (Statistiques) o;
        return idStatistique != null && idStatistique.equals(that.idStatistique);
    }

    @Override
    public int hashCode() {
        return idStatistique != null ? idStatistique.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Statistiques {
                idStatistique = %d,
                idCabinet = %d,
                periode = '%s',
                dateDebut = %s,
                dateFin = %s,
                nbPatients = %d,
                nbRdv = %d,
                nbConsultation = %d,
                revenuTotal = %.2f,
                tauxAnnulation = %.2f,
                actePlusEffectif = '%s'
            }
            """.formatted(idStatistique, idCabinet, periode, dateDebut, dateFin,
                nbPatients, nbRdv, nbConsultation, revenuTotal, tauxAnnulation, actePlusEffectif);
    }
}
