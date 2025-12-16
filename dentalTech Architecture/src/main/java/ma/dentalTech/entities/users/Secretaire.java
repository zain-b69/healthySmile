package ma.dentalTech.entities.users;

import java.time.LocalDate;
import lombok.*;
import ma.dentalTech.entities.cabinet.CabinetMedical;

/**
 * Entité représentant une secrétaire du cabinet.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Secretaire extends Staff {

    private String numCNSS;
    private Double commission;

    @Builder(builderMethodName = "secretaireBuilder")
    public Secretaire(String nom, String email, String adresse, String cin, String tel,
                      ma.dentalTech.entities.enums.Sexe sexe, String login, String motDePasse,
                      LocalDate lastLoginDate, LocalDate dateNaissance, Double salaire, Double prime,
                      LocalDate dateRecrutement, Integer soldeCongé, String numCNSS, Double commission, CabinetMedical cabinetMedical) {
        super( nom, email, adresse, cin, tel, sexe, login, motDePasse, lastLoginDate,
                dateNaissance, salaire, prime, dateRecrutement, soldeCongé, cabinetMedical);
        this.numCNSS = numCNSS;
        this.commission = commission;
    }

    @Override
    public String toString() {
        return """
            Secretaire {
                id = %d,
                nom = '%s',
                numCNSS = '%s',
                commission = %.2f
            }
            """.formatted(getId(), getNom(), numCNSS, commission != null ? commission : 0.0);
    }
}
