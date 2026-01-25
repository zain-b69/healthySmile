package ma.dentalTech.mvc.controllers.modules.users.impl;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.mvc.controllers.modules.users.api.UsersController;
import ma.dentalTech.mvc.ui.modules.users.UsersPanel;
import ma.dentalTech.service.modules.auth.dto.UserPrincipal;
import ma.dentalTech.service.modules.users.api.PasswordManagementService;
import ma.dentalTech.service.modules.users.api.UserManagementService;

import javax.swing.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UsersControllerImpl implements UsersController {

    private final UserManagementService userManagementService;
    private final PasswordManagementService passwordManagementService;

    private JPanel cached;

    @Override
    public JPanel getView(UserPrincipal principal) {
        if (cached == null) {
            cached = new UsersPanel(principal, userManagementService, passwordManagementService);
        }
        return cached;
    }
}
