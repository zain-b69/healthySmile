package ma.dentalTech.mvc.ui.modules.dashboard;

import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.mvc.ui.modules.dashboard.ApplicationPages;

import java.util.EnumSet;
import java.util.Set;

public final class PageAccess {
    private PageAccess() {}

    public static Set<ApplicationPages> allowedPages(RoleType role) {
        if (role == null) return EnumSet.of(ApplicationPages.DASHBOARD);

        return switch (role) {
            case MEDECIN -> EnumSet.of(
                    ApplicationPages.DASHBOARD,
                    ApplicationPages.PATIENTS,
                    ApplicationPages.RENDEZ_VOUS,
                    ApplicationPages.AGENDA,
                    ApplicationPages.DOSSIERS_MEDICAUX,
                    ApplicationPages.ORDONNANCES,
                    ApplicationPages.CERTIFICATIONS,
                    ApplicationPages.CATALOGUE,
                    ApplicationPages.CAISSE,               // ou SITUATION_FINANCIERE
                    ApplicationPages.STATISTIQUES
            );

            case SECRETAIRE -> EnumSet.of(
                    ApplicationPages.DASHBOARD,
                    ApplicationPages.PATIENTS,
                    ApplicationPages.RENDEZ_VOUS,
                    ApplicationPages.AGENDA,
                    ApplicationPages.STATISTIQUES
            );

            case ADMIN -> EnumSet.allOf(ApplicationPages.class); // admin tout
        };
    }

    public static boolean canAccess(RoleType role, ApplicationPages page) {
        return allowedPages(role).contains(page);
    }
}
