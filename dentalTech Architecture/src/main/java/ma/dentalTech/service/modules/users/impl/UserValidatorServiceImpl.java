package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.service.modules.users.api.UserValidatorService;
import ma.dentalTech.service.modules.users.dto.*;

public class UserValidatorServiceImpl implements UserValidatorService {

    private static final int MIN_LOGIN_LEN = 3;
    private static final int MIN_PASSWORD_LEN = 6;

    @Override
    public void validateCreateAdmin(CreateAdminRequest r) {
        validateCommonCreate(r.nom(), r.email(), r.login(), r.motDePasse());
        // champs admin spécifiques (optionnel)
        // ex: salaire/prime/soldeConge...
    }

    @Override
    public void validateCreateMedecin(CreateMedecinRequest r) {
        validateCommonCreate(r.nom(), r.email(), r.login(), r.motDePasse());
        if (r.specialite() == null || r.specialite().isBlank()) {
            throw new IllegalArgumentException("specialite obligatoire");
        }
    }

    @Override
    public void validateCreateSecretaire(CreateSecretaireRequest r) {
        validateCommonCreate(r.nom(), r.email(), r.login(), r.motDePasse());
        if (r.numCNSS() == null || r.numCNSS().isBlank()) {
            throw new IllegalArgumentException("numCNSS obligatoire");
        }
        // commission optionnelle
    }

    @Override
    public void validateUpdateProfile(UpdateUserProfileRequest r) {
        if (r == null) throw new IllegalArgumentException("request obligatoire");
        if (r.id() == null) throw new IllegalArgumentException("id obligatoire");

        // pas besoin d'obliger tous les champs, c'est un update partiel
        if (r.email() != null && r.email().isBlank()) {
            throw new IllegalArgumentException("email invalide");
        }
        if (r.nom() != null && r.nom().isBlank()) {
            throw new IllegalArgumentException("nom invalide");
        }
    }

    // -------- helpers --------

    private static void validateCommonCreate(String nom, String email, String login, String pwd) {
        if (nom == null || nom.isBlank()) throw new IllegalArgumentException("nom obligatoire");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email obligatoire");

        // validation email simple
        if (!email.contains("@") || email.startsWith("@") || email.endsWith("@")) {
            throw new IllegalArgumentException("email invalide");
        }

        if (login == null || login.isBlank()) throw new IllegalArgumentException("login obligatoire");
        if (login.trim().length() < MIN_LOGIN_LEN) {
            throw new IllegalArgumentException("login trop court (min " + MIN_LOGIN_LEN + ")");
        }

        if (pwd == null || pwd.isBlank()) throw new IllegalArgumentException("motDePasse obligatoire");
        if (pwd.length() < MIN_PASSWORD_LEN) {
            throw new IllegalArgumentException("motDePasse trop court (min " + MIN_PASSWORD_LEN + ")");
        }
    }
}
