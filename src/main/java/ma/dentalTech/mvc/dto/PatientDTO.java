package ma.dentalTech.mvc.dto;

import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class PatientDTO {
    private String nomComplet;
    private int age;
    private String dateCreationFormatee;

}
