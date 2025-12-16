package ma.dentalTech.entities.dossierMedical;

import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.patient.Patient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DossierMedical extends BaseEntity {

    private Long idDm;                 // dossier_medicale.id_dm
    private Long idPatient;            // dossier_medicale.id_patient (FK)
    private LocalDate dateDeCreation;  // dossier_medicale.date_de_creation

    // 1..1
    private Patient patient;

    // 1..1
    private SituationFinanciere situationFinanciere;

    // 1..N
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Consultation> consultations = new ArrayList<>();

    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<RDV> rdvs = new ArrayList<>();

    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Ordonnance> ordonnances = new ArrayList<>();

    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Certificat> certificats = new ArrayList<>();
}
