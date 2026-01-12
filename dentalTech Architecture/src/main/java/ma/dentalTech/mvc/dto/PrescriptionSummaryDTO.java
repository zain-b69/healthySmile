package ma.dentalTech.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrescriptionSummaryDTO {
    private Long idPr;
    private Long idOrdonnance;

    private String categorie;
    private String frequence;
    private Integer dureeEnJours;

    private int nbMedicaments;
    private int quantiteTotale;
}
