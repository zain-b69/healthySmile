package ma.dentalTech.entities.agenda;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

/**
 * Entité représentant l'agenda mensuel d'un médecin.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendaMensuel {

    private Long idAgenda; // correspond à id_agenda dans la BD
    private Integer mois;
    private Integer annee;
    private Long idMedecin; // correspond à id_medecin dans la BD

    // Pour que ce soit compatible avec la BD, stocker en JSON ou gérer via table séparée
    private List<LocalDate> joursNonDisponibles = new ArrayList<>();

    // Pour que ce soit compatible avec la BD, ce champ doit correspondre à une table RDV avec idAgenda comme FK
    private List<RDV> rendezVous = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgendaMensuel)) return false;
        AgendaMensuel that = (AgendaMensuel) o;
        return idAgenda != null && idAgenda.equals(that.idAgenda);
    }

    @Override
    public int hashCode() {
        return idAgenda != null ? idAgenda.hashCode() : 0;
    }

    public boolean isJourDisponible(LocalDate date) {
        return joursNonDisponibles == null || !joursNonDisponibles.contains(date);
    }

    @Override
    public String toString() {
        return """
            AgendaMensuel {
                idAgenda = %d,
                mois = %d,
                annee = %d,
                joursNonDisponiblesCount = %d,
                rdvCount = %d,
                idMedecin = %d
            }
            """.formatted(idAgenda, mois, annee,
                joursNonDisponibles == null ? 0 : joursNonDisponibles.size(),
                rendezVous == null ? 0 : rendezVous.size(),
                idMedecin);
    }
}
