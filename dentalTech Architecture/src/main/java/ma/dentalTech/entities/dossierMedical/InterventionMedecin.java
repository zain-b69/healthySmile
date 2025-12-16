package ma.dentalTech.entities.dossierMedical;

import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterventionMedecin extends BaseEntity {

    private Long idIm;             // intervention_medecin.id_im
    private Long idConsultation;   // intervention_medecin.id_consultation (FK)
    private Long idActe;           // intervention_medecin.id_acte (FK)

    private Double prixDePatient;  // intervention_medecin.prix_de_patient
    private Integer numDent;       // intervention_medecin.num_dent

    // N..1
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private Consultation consultation;

    // N..1
    private Acte acte;
}
