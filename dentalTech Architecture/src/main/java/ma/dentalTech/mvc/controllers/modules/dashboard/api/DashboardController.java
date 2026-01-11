package ma.dentalTech.mvc.controllers.modules.dashboard.api;

import ma.dentalTech.mvc.ui.modules.dashboard.ApplicationPages;
import ma.dentalTech.service.modules.auth.dto.UserPrincipal;

import javax.swing.*;

public interface DashboardController {

    void showDashboard(UserPrincipal principal);

    // callback venant de la vue (navigation)
    JComponent onNavigateRequested(ApplicationPages page);

    void onLogoutRequested();
    void onExitRequested();
}
