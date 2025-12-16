package ma.dentalTech.entities.dossierMedical;

import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ordonnance extends BaseEntity {

    private Long idOrd;             // ordonnance.id_ord
    private Long idConsultation;    // ordonnance.id_consultation (FK)
    private LocalDate date;         // ordonnance.date
    private Long idDm;              // ordonnance.id_dm (FK)

    // N..1
    private DossierMedical dossierMedical;
    private Consultation consultation;

    // 1..N
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Prescription> prescriptions = new ArrayList<>();
}
