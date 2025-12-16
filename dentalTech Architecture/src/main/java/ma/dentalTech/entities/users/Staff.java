package ma.dentalTech.entities.users;

import java.time.LocalDate;
import lombok.*;
import ma.dentalTech.entities.cabinet.CabinetMedical;

/**
 * Entité représentant un membre du personnel (staff).
 * Classe parent pour Admin, Médecin et Secrétaire.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Staff extends Utilisateur {

    private Double salaire;
    private Double prime;
    private LocalDate dateRecrutement;
    private Integer soldeCongé;
    private CabinetMedical cabinetMedical;

    @Builder(builderMethodName = "staffBuilder")
    public Staff(String nom, String email, String adresse, String cin, String tel,
                 ma.dentalTech.entities.enums.Sexe sexe, String login, String motDePasse,
                 LocalDate lastLoginDate, LocalDate dateNaissance, Double salaire, Double prime,
                 LocalDate dateRecrutement, Integer soldeCongé, CabinetMedical cabinetMedical) {
        super(nom, email, adresse, cin, tel, sexe, login, motDePasse, lastLoginDate,
                dateNaissance, null, null);
        this.salaire = salaire;
        this.prime = prime;
        this.dateRecrutement = dateRecrutement;
        this.soldeCongé = soldeCongé;
        this.cabinetMedical = cabinetMedical;
    }

    @Override
    public String toString() {
        return """
            Staff {
                id = %d,
                nom = '%s',
                salaire = %.2f,
                dateRecrutement = %s
                cabinetMedical = %s
            }
            """.formatted(getId(), getNom(), salaire, dateRecrutement, cabinetMedical);
    }
}
