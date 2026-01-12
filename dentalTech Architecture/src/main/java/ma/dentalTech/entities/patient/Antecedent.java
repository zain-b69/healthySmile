package ma.dentalTech.entities.patient;

import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.service.modules.patient.api.AntecedentService;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Antecedent extends BaseEntity {

    @EqualsAndHashCode.Include
    private Long idAntecedent;   // antecedents.id_antecedent (PK)

    private String nom;          // antecedents.nom
    private CategorieAntecedent categorie;    // antecedents.categorie
    private NiveauRisque niveauDeRisque; // antecedents.niveau_de_risque
}
