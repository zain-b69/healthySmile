package ma.dentalTech.entities.dossierMedical;

import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Acte extends BaseEntity {

    private Long idActe;          // acte.id_acte
    private String libelle;
    private String categorie;
    private Double prixDeBase;    // acte.prix_de_base

    // 1..N : un acte peut être utilisé dans plusieurs interventions
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<InterventionMedecin> interventions = new ArrayList<>();
}
