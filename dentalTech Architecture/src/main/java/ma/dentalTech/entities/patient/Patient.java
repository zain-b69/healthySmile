package ma.dentalTech.entities.patient;

import java.time.LocalDate;
import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Patient extends BaseEntity {

    @EqualsAndHashCode.Include
    private Long idPatient; // patient.id_patient (PK)

    private String nom;
    private LocalDate dateNaissance;
    private Sexe sexe;
    private String adresse;
    private String telephone;
    private Assurance assurance;
}
