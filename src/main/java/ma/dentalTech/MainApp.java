package ma.dentalTech;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.auth.api.AuthController;

public class MainApp
{

        public static void main(String[] args) {
            AuthController authController = ApplicationContext.getBean(AuthController.class);
            authController.showLogin();
        }
}
