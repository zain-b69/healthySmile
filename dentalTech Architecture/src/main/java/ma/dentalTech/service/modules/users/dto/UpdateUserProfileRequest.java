package ma.dentalTech.service.modules.users.dto;

import java.time.LocalDate;
import ma.dentalTech.entities.enums.Sexe;

public record UpdateUserProfileRequest(
        Long id,
        String nom,
        String email,
        String adresse,
        String tel,
        Sexe sexe,
        LocalDate dateNaissance
) {}
