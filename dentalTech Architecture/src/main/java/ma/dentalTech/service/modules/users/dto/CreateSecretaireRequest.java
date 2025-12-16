package ma.dentalTech.service.modules.users.dto;

import java.time.LocalDate;
import ma.dentalTech.entities.enums.Sexe;


public record CreateSecretaireRequest(
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
        Integer soldeConge,
        String numCNSS,
        Double commission
) {}
