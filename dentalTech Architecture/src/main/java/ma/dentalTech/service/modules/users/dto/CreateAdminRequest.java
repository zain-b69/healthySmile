package ma.dentalTech.service.modules.users.dto;

import ma.dentalTech.entities.enums.Sexe;

import java.time.LocalDate;

public record CreateAdminRequest(
        String nom,
        String email,
        String adresse,
        String cin,
        String tel,
        Sexe sexe,
        String login,
        String motDePasse,
        LocalDate dateNaissance,
        Double salaire,
        Double prime,
        LocalDate dateRecrutement,
        Integer soldeConge
) {
    public CreateAdminRequest {
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("nom obligatoire");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email obligatoire");
        if (login == null || login.isBlank()) throw new IllegalArgumentException("login obligatoire");
        if (motDePasse == null || motDePasse.isBlank()) throw new IllegalArgumentException("motDePasse obligatoire");
    }
}
