package ma.dentalTech.entities.dossierMedical;

import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificat extends BaseEntity {

    private Long idCertif;          // certificat.id_certif
    private Long idConsultation;    // certificat.id_consultation (FK)
    private LocalDate dateDebut;    // certificat.date_debut
    private LocalDate dateFin;      // certificat.date_fin
    private Integer duree;
    private String noteMedecin;     // certificat.note_medecin
    private Long idDm;              // certificat.id_dm (FK)

    // N..1
    private DossierMedical dossierMedical;
    private Consultation consultation;
}
