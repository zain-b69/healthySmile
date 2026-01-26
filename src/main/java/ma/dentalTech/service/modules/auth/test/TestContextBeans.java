package ma.dentalTech.service.modules.auth.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.service.modules.auth.api.AuthService;

public class TestContextBeans {
    public static void main(String[] args) {
        AuthService authService = ApplicationContext.getBean(AuthService.class);
        System.out.println("authService = " + authService);
    }
}
