package ma.dentalTech.entities.dossierMedical;

import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SituationFinanciere extends BaseEntity {

    private Long idSf;                 // situation_financiere.id_sf
    private Double totaleDesActes;     // situation_financiere.totale_des_actes
    private Double totalePaye;         // situation_financiere.totale_paye
    private Double credit;             // situation_financiere.credit

    private StatutSituationFinanciere statut;
    private Boolean enPromo;           // OUI / NON

    private Long idDm;                 // situation_financiere.id_dm (FK)

    // 1..1
    private DossierMedical dossierMedical;

    // 1..N
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Facture> factures = new ArrayList<>();
}
