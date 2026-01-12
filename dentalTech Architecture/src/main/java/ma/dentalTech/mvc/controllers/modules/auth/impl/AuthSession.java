package ma.dentalTech.mvc.controllers.modules.auth.impl;

import lombok.Getter;
import lombok.Setter;
import ma.dentalTech.service.modules.auth.dto.UserPrincipal;

public final class AuthSession {

    @Getter
    @Setter
    private static UserPrincipal currentUser;

    private AuthSession() {}

    public static boolean isAuthenticated() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }
}
