package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.service.modules.auth.api.AuthValidatorService;
import ma.dentalTech.service.modules.auth.dto.AuthRequest;

import java.util.HashMap;
import java.util.Map;

public class AuthValidatorServiceImpl implements AuthValidatorService {
    @Override
    public Map<String, String> validate(AuthRequest authRequest) {

        Map<String, String> result = new HashMap<>();

        if (authRequest == null) {
            result.put("_global", "Formulaire invalide");
            return result;
        }

        String password = authRequest.getPassword() != null ? authRequest.getPassword() : "";
        String login = authRequest.getLogin() != null ? authRequest.getLogin().trim() : "" ;
        return Map.of();
    }
}
