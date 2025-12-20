package ma.dentalTech.service.modules.auth.dto;

import ma.dentalTech.entities.enums.RoleType;

import java.io.Serializable;
import java.security.Principal;
import java.util.Objects;

/**
 * Représente l'utilisateur authentifié (principal) dans l'application.
 * Sert à transporter l'identité + le rôle après un login réussi.
 */
public class UserPrincipal implements Principal, Serializable {

    private final Long idUser;      // utilisateur.id_user
    private final String login;     // utilisateur.login (ou email si tu veux)
    private final String email;     // utilisateur.email
    private final String nom;       // utilisateur.nom
    private final RoleType role;    // role.libelle (ADMIN/MEDECIN/SECRETAIRE/PATIENT)

    public UserPrincipal(Long idUser, String login, String email, String nom, RoleType role) {
        this.idUser = idUser;
        this.login = login;
        this.email = email;
        this.nom = nom;
        this.role = role;
    }

    public Long getIdUser() {
        return idUser;
    }

    @Override
    public String getName() {
        // Principal.getName() -> identifier (souvent login ou email)
        return (login != null && !login.isBlank()) ? login : email;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getNom() {
        return nom;
    }

    public RoleType getRole() {
        return role;
    }

    public boolean hasRole(RoleType r) {
        return role == r;
    }

    public boolean isAdmin() {
        return role == RoleType.ADMIN;
    }

    public boolean isMedecin() {
        return role == RoleType.MEDECIN;
    }

    public boolean isSecretaire() {
        return role == RoleType.SECRETAIRE;
    }

    @Override
    public String toString() {
        return "UserPrincipal{" +
                "idUser=" + idUser +
                ", login='" + login + '\'' +
                ", email='" + email + '\'' +
                ", nom='" + nom + '\'' +
                ", role=" + role +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPrincipal that)) return false;
        return Objects.equals(idUser, that.idUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser);
    }
}
