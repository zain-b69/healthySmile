package ma.dentalTech.mvc.controllers.modules.auth.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.auth.api.AuthController;

public class TestAuthController {
    public static void main(String[] args) {
        AuthController controller = ApplicationContext.getBean(AuthController.class);
        controller.showLogin();
    }
}
