package ma.dentalTech.service.modules.users.api;

import ma.dentalTech.service.modules.users.dto.CreateAdminRequest;
import ma.dentalTech.service.modules.users.dto.CreateMedecinRequest;
import ma.dentalTech.service.modules.users.dto.CreateSecretaireRequest;
import ma.dentalTech.service.modules.users.dto.UpdateUserProfileRequest;

public interface UserValidatorService {

    void validateCreateAdmin(CreateAdminRequest request);

    void validateCreateMedecin(CreateMedecinRequest request);

    void validateCreateSecretaire(CreateSecretaireRequest request);

    void validateUpdateProfile(UpdateUserProfileRequest request);
}
