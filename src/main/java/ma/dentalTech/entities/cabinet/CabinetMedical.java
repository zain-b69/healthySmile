package ma.dentalTech.entities.cabinet;

import lombok.*;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.users.Staff;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant le cabinet médical dentaire.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CabinetMedical extends BaseEntity {

    private Long idCabinet;     // PK
    private Long idUser;        // FK vers utilisateur

    private String nom;
    private String email;
    private String logo;
    private String adresse;
    private String cin;
    private String tel1;
    private String tel2;
    private String siteWeb;     // ancien site_web
    private String instagram;
    private String facebook;
    private String description;

    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Staff> staff = new ArrayList<>();

    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Charges> charges = new ArrayList<>();

    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Revenues> revenues = new ArrayList<>();

    @ToString.Exclude @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Statistiques> statistiques = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CabinetMedical)) return false;
        CabinetMedical that = (CabinetMedical) o;
        return idCabinet != null && idCabinet.equals(that.idCabinet);
    }

    @Override
    public int hashCode() {
        return idCabinet != null ? idCabinet.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            CabinetMedical {
                idCabinet = %d,
                idUser = %d,
                nom = '%s',
                email = '%s',
                adresse = '%s',
                tel1 = '%s'
            }
            """.formatted(idCabinet, idUser, nom, email, adresse, tel1);
    }
}
