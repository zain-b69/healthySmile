package ma.dentalTech.entities.users;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.Sexe;

/**
 * Entité représentant un utilisateur du système.
 * Classe parent pour Admin, Médecin et Secrétaire.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur extends BaseEntity {

    private Long idUser;
    private String nom;
    private String adresse;
    private String email;
    private String cin;
    private String tel;
    private Sexe sexe;
    private String login;
    private String motDePasse;
    private LocalDate lastLoginDate;
    private LocalDate dateNaissance;
    private Role role;

    private List<Notification> notifications = new ArrayList<>();

    public Utilisateur(String nom, String email, String adresse, String cin, String tel, Sexe sexe, String login, String motDePasse, LocalDate lastLoginDate, LocalDate dateNaissance, Object o, Object o1) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilisateur)) return false;
        Utilisateur that = (Utilisateur) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Utilisateur {
                id = %d,
                nom = '%s',
                email = '%s',
                login = '%s'
            }
            """.formatted(id, nom, email, login);
    }
}
