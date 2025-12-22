package ma.dentalTech.service.modules.auth.dto;

import ma.dentalTech.entities.enums.RoleType;

import java.io.Serializable;
import java.security.Principal;
import java.util.Objects;

public class UserPrincipal implements Principal, Serializable {

    private final Long idUser;
    private final String login;
    private final String email;
    private final String nom;
    private final RoleType role;

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
