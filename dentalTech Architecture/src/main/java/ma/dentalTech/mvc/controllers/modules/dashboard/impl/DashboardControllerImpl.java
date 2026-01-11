package ma.dentalTech.mvc.controllers.modules.dashboard.impl;

import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaMensuelController;
import ma.dentalTech.mvc.controllers.modules.agenda.api.RDVController;
import ma.dentalTech.mvc.controllers.modules.auth.api.AuthController;
import ma.dentalTech.mvc.controllers.modules.auth.impl.AuthSession;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.ui.modules.agenda.AgendaPagePanel;
import ma.dentalTech.mvc.ui.modules.dashboard.*;

import ma.dentalTech.mvc.ui.modules.rdv.RendezVousPanel;
import ma.dentalTech.repository.modules.agenda.api.RDVRepository;
import ma.dentalTech.repository.modules.users.api.MedecinRepository;
import ma.dentalTech.service.modules.agendas.api.AgendaMensuelService;
import ma.dentalTech.service.modules.auth.api.AuthorizationService;
import ma.dentalTech.service.modules.auth.dto.UserPrincipal;

import javax.swing.*;
import java.awt.*;

public class DashboardControllerImpl implements DashboardController {

    private final AuthorizationService authorizationService;
    private final AuthController authController;
    private final PatientController patientController;
    private final RDVRepository rdvRepository;
    private final MedecinRepository medecinRepository;
    private final RDVController rdvController;
    private final AgendaMensuelService agendaMensuelService;
    private final AgendaMensuelController agendaMensuelController;

    private DashboardFrame view;
    private UserPrincipal principal;

    public DashboardControllerImpl(AuthorizationService authorizationService,
                                   AuthController authController,
                                   PatientController patientController, RDVRepository rdvRepository,
                                   RDVController rdvController,
                                   MedecinRepository medecinRepository,
                                   AgendaMensuelController agendaMensuelController, AgendaMensuelService agendaMensuelService) {
        this.authorizationService = authorizationService;
        this.authController = authController;
        this.patientController = patientController;
        this.rdvRepository = rdvRepository;
        this.rdvController = rdvController;
        this.medecinRepository = medecinRepository;
        this.agendaMensuelService = agendaMensuelService;
        this.agendaMensuelController = agendaMensuelController;
    }


    @Override
    public void showDashboard(UserPrincipal principal) {
        this.principal = principal;
        AuthSession.setCurrentUser(principal);

        Runnable show = () -> {
            if (view == null) {
                view = new DashboardFrame(this, principal);
            }
            view.setVisible(true);
            view.toFront();
        };

        if (SwingUtilities.isEventDispatchThread()) show.run();
        else SwingUtilities.invokeLater(show);
    }

    @Override
    public JComponent onNavigateRequested(ApplicationPages page) {
        if (principal == null) return message("Session invalide. Veuillez vous reconnecter.");

        if (!canAccess(page)) {
            return forbidden("Accès refusé pour: " + page);
        }
        return switch (page) {
            case DASHBOARD -> dashboardPanel();
            case PATIENTS -> new PatiensListPanel(patientController);

            // placeholders (tu implémenteras après)
            case RENDEZ_VOUS -> new RendezVousPanel(principal, rdvController, patientController, medecinRepository);
            case AGENDA -> new AgendaPagePanel(medecinRepository, agendaMensuelService, rdvRepository);
            case DOSSIERS_MEDICAUX -> placeholder("Dossiers médicaux");
            case ORDONNANCES -> placeholder("Ordonnances");
            case CERTIFICATIONS -> placeholder("Certifications");
            case CATALOGUE_ACTES -> placeholder("Catalogue des actes");
            case CAISSE -> placeholder("Caisse");
            case STATISTIQUES -> placeholder("Statistiques");
            case USERS -> placeholder("Gestion des utilisateurs");
        };
    }

    private JComponent placeholder(String title) {
        JPanel p = new JPanel(new java.awt.GridBagLayout());
        JLabel lbl = new JLabel(title + " - en cours...");
        lbl.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 18));
        p.add(lbl);
        return p;
    }


    @Override
    public void onLogoutRequested() {
        if (view != null) view.dispose();
        view = null;

        AuthSession.logout();

        // retour login
        SwingUtilities.invokeLater(authController::showLogin);
    }

    @Override
    public void onExitRequested() {
        int res = JOptionPane.showConfirmDialog(
                view,
                "Quitter l'application ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
        );
        if (res == JOptionPane.YES_OPTION) System.exit(0);
    }

    // --------- helpers UI ---------

    private JComponent dashboardPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        String nom = (principal != null && principal.getNom() != null) ? principal.getNom() : "Utilisateur";
        JLabel welcome = new JLabel("Welcom Back, " + nom);
        welcome.setFont(Theme.base());
        welcome.setBorder(new javax.swing.border.EmptyBorder(0,0,15,0));

        JPanel cards = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        cards.setOpaque(false);

        int totalPatients = 0;
        try { totalPatients = patientController.findAll().size(); } catch (Exception ignored) {}

        cards.add(new StatCard("Total Patient", String.valueOf(totalPatients)));
        cards.add(new StatCard("Patient Visit", "150"));     // placeholder (à lier aux consultations)
        cards.add(new StatCard("Total Rendez-vous", "150")); // placeholder (à lier aux RDV)

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(welcome, BorderLayout.NORTH);
        top.add(cards, BorderLayout.CENTER);

        p.add(top, BorderLayout.NORTH);
        return p;
    }

    private JComponent forbidden(String msg) {
        JPanel p = new JPanel(new GridBagLayout());
        JLabel lbl = new JLabel(msg);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        p.add(lbl);
        return p;
    }

    private JComponent message(String msg) {
        JPanel p = new JPanel(new GridBagLayout());
        JLabel lbl = new JLabel(msg);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 16));
        p.add(lbl);
        return p;
    }

    private boolean canAccess(ApplicationPages page) {
        return PageAccess.canAccess(principal.getRole(), page);
    }

}
