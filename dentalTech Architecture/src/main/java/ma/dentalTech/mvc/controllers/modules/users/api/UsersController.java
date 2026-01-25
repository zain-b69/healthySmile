package ma.dentalTech.mvc.controllers.modules.users.api;

import ma.dentalTech.service.modules.auth.dto.UserPrincipal;

import javax.swing.*;

public interface UsersController {
    JPanel getView(UserPrincipal principal);
}
