package ma.dentalTech.service.modules.auth.api;

import ma.dentalTech.service.modules.auth.dto.AuthRequest;

import java.util.Map;

public interface AuthValidatorService {
    Map<String, String> validate (AuthRequest authRequest);
}
