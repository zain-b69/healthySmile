package ma.dentalTech.entities.users;

import java.time.LocalDate;
import lombok.*;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.cabinet.CabinetMedical;

/**
 * Entité représentant un médecin dentiste.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Medecin extends Staff {

    private String specialite;
    private AgendaMensuel agendaMensuel;

    @Builder(builderMethodName = "medecinBuilder")
    public Medecin(String nom, String email, String adresse, String cin, String tel,
                   ma.dentalTech.entities.enums.Sexe sexe, String login, String motDePasse,
                   LocalDate lastLoginDate, LocalDate dateNaissance, Double salaire, Double prime,
                   LocalDate dateRecrutement, Integer soldeCongé, String specialite,
                   AgendaMensuel agendaMensuel, CabinetMedical cabinetMedical) {
        super(nom, email, adresse, cin, tel, sexe, login, motDePasse, lastLoginDate,
                dateNaissance, salaire, prime, dateRecrutement, soldeCongé,cabinetMedical);
        this.specialite = specialite;
        this.agendaMensuel = agendaMensuel;
    }

    @Override
    public String toString() {
        return """
            Medecin {
                id = %d,
                nom = '%s',
                specialite = '%s'
            }
            """.formatted(getId(), getNom(), specialite);
    }
}
