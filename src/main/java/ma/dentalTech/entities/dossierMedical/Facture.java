package ma.dentalTech.entities.dossierMedical;

import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.StatutFacture;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facture extends BaseEntity {

    private Long idFacture;         // facture.id_facture
    private Double totaleFacture;   // facture.totale_facture
    private Double reste;           // facture.reste
    private StatutFacture statut;   // facture.statut
    private LocalDateTime dateFacture;

    private Long idConsultation;    // facture.id_consultation (FK)
    private Long idSf;              // facture.id_sf (FK)

    // N..1
    private Consultation consultation;
    private SituationFinanciere situationFinanciere;
}
