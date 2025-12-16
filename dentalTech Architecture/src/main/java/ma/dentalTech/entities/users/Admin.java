package ma.dentalTech.entities.users;

import java.time.LocalDate;
import lombok.*;
import ma.dentalTech.entities.cabinet.CabinetMedical;

/**
 * Entité représentant un administrateur du système.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends Staff {

    @Builder(builderMethodName = "adminBuilder")
    public Admin(String nom, String email, String adresse, String cin, String tel,
                 ma.dentalTech.entities.enums.Sexe sexe, String login, String motDePasse,
                 LocalDate lastLoginDate, LocalDate dateNaissance, Double salaire, Double prime,
                 LocalDate dateRecrutement, Integer soldeCongé, CabinetMedical cabinetMedical) {
        super(nom, email, adresse, cin, tel, sexe, login, motDePasse, lastLoginDate,
                dateNaissance, salaire, prime, dateRecrutement, soldeCongé, cabinetMedical);
    }

    @Override
    public String toString() {
        return """
            Admin {
                id = %d,
                nom = '%s',
                email = '%s'
            }
            """.formatted(getId(), getNom(), getEmail());
    }
}
