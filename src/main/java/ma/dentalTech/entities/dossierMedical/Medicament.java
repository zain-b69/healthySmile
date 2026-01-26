package ma.dentalTech.entities.dossierMedical;

import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicament extends BaseEntity {

    private Long idMct;           // medicament.id_mct
    private String nom;
    private String laboratoire;
    private String type;          // medicament.type
    private String forme;         // medicament.forme
    private Boolean remboursable; // medicament.remboursable
    private Double prixUnitaire;  // medicament.prix_unitaire
    private String description;   // medicament.description

    // N..N
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Prescription> prescriptions = new ArrayList<>();
}
