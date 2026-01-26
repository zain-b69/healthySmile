package ma.dentalTech.entities.agenda;

import java.time.LocalDate;
import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.entities.enums.StatutRDV;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.users.Medecin;

/**
 * Entité représentant un rendez-vous.
 * Compatible avec la table `rdv` de la base MySQL.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RDV extends BaseEntity {

    // ID propre à RDV (correspond à id_rdv dans la BD)
    private Long idRdv;

    // Colonnes de la table rdv
    private Long idEntite;       // id_entite nullable
    private Long idPatient;      // id_patient NOT NULL
    private Long idMedecin;      // id_medecin NOT NULL
    private LocalDate date;      // date NOT NULL
    private String heure;        // varchar(20)
    private String motif;        // varchar(255)
    private StatutRDV statut;    // enum('EN_ATTENTE','EN_COURS','TERMINE','ANNULE')
    private String noteMedecin;  // varchar(255)
    private Long idCreneau;      // id_creneau nullable
    private Long idDm;           // id_dm NOT NULL

    // Relation optionnelle avec AgendaMensuel (pas dans la table mais utile côté métier)
    private Long agendaMensuelId;

    // Objets pour la couche métier
    private Patient patient;
    private Medecin medecin;
    private DossierMedical dossierMedical;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RDV)) return false;
        RDV that = (RDV) o;
        return idRdv != null && idRdv.equals(that.idRdv);
    }

    @Override
    public int hashCode() {
        return idRdv != null ? idRdv.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            RDV {
                idRdv = %d,
                idEntite = %s,
                date = %s,
                heure = '%s',
                motif = '%s',
                statut = %s,
                noteMedecin = '%s',
                idPatient = %d,
                idMedecin = %d,
                idDm = %d,
                idCreneau = %s,
                agendaMensuelId = %s
            }
            """.formatted(
                idRdv,
                idEntite != null ? idEntite.toString() : "null",
                date,
                heure,
                motif,
                statut,
                noteMedecin,
                idPatient,
                idMedecin,
                idDm,
                idCreneau != null ? idCreneau.toString() : "null",
                agendaMensuelId != null ? agendaMensuelId.toString() : "null"
        );
    }
}
