package ma.dentalTech.entities.dossierMedical;

import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription extends BaseEntity {

    private Long idPr;             // prescription.id_pr
    private Long idOrdonnance;     // prescription.id_ordonnance (FK)

    private Integer quantite;      // prescription.quantite (optionnel)
    private String categorie;      // prescription.categorie
    private String frequence;      // prescription.frequence
    private Integer dureeEnJours;  // prescription.duree_en_jours

    // N..1
    private Ordonnance ordonnance;

    // N..N (via table pivot prescription_medicament)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Medicament> medicaments = new ArrayList<>();

    // Optionnel (si tu veux gérer la quantité par médicament)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<PrescriptionMedicament> lignes = new ArrayList<>();

    public void setPrescriptionMedicaments(List<PrescriptionMedicament> lignesByPrescription) {
    }

    public List<PrescriptionMedicament> getPrescriptionMedicaments() {
        return new ArrayList<>();
    }
}
