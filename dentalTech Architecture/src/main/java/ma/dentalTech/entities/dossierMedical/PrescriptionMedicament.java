package ma.dentalTech.entities.dossierMedical;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionMedicament {
    private Long idPr;
    private Long idMct;
    private Integer quantite;
    private Prescription prescription;
    private Medicament medicament;
}
