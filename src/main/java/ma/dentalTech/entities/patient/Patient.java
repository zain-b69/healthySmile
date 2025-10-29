package ma.dentalTech.entities.patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class Patient {

    private Long id;
    private String nom;
    private String prenom;
    private String adresse;
    private String telephone;
    private String email;
    private LocalDate dateNaissance;
    private LocalDateTime dateCreation;
    private Sexe sexe;
    private Assurance assurance;


}
