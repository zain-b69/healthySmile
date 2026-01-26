package ma.dentalTech.service.modules.users.dto;

import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.enums.Sexe;

import java.time.LocalDate;
import java.util.Set;

public record UserAccountDto(
        Long id,
        String nom,
        String email,
        String login,
        Sexe sexe,
        LocalDate dateNaissance,
        Set<RoleType> roles,
        Set<String> privileges
) {}

