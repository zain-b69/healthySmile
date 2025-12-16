package ma.dentalTech.entities.dossierMedical;

import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.users.Medecin;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consultation extends BaseEntity {

    private Long idConsultation;     // consultation.id_consultation
    private Long idMedecin;          // consultation.id_medecin (FK -> utilisateur)
    private LocalDate date;          // consultation.date
    private StatutConsultation statut;
    private String observationMedecin;

    private Long idDm;               // consultation.id_dm (FK)
    private Long idRdv;              // consultation.id_rdv (FK, unique)

    // N..1
    private DossierMedical dossierMedical;
    private Medecin medecin;

    // 0..1
    private RDV rdv;

    // 1..N
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<InterventionMedecin> interventions = new ArrayList<>();

    // 0..1
    private Ordonnance ordonnance;
    private Certificat certificat;
    private Facture facture;
}
