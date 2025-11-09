package ma.dentalTech.mvc.ui.common;

import ma.dentalTech.entities.utilisateur.Utilisateur;
import ma.dentalTech.entities.enums.RoleLibelle; // Assurez-vous d'avoir cet Enum

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Cadre principal de l'application, affichant le dashboard après connexion.
 * Utilise CardLayout pour basculer entre les vues.
 */
public class MainAppFrame extends JFrame {

    // --- Constantes pour CardLayout ---
    private static final String DASHBOARD_PANEL = "Dashboard";
    private static final String PATIENT_PANEL = "Patients";
    private static final String RDV_PANEL = "Rendez-vous";
    private static final String FACTURE_PANEL = "Factures";

    private final Utilisateur utilisateurConnecte;
    private final CardLayout cardLayout;
    private final JPanel mainContentPanel;
    private final JPanel sideMenuPanel;

    public MainAppFrame(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        this.cardLayout = new CardLayout();
        this.mainContentPanel = new JPanel(cardLayout);
        this.sideMenuPanel = new JPanel();

        initializeFrameSettings();
        buildUI();
    }

    private void initializeFrameSettings() {
        setTitle("DentalSoft - Bienvenue " + utilisateurConnecte.getUsername() + " (" + utilisateurConnecte.getRole().getLibelle() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750); // Taille recommandée pour une application de bureau
        setLocationRelativeTo(null); // Centrer l'application
    }

    private void buildUI() {
        // --- 1. Panneau de Base (BorderLayout) ---
        setLayout(new BorderLayout());

        // --- 2. Création du Menu Latéral (WEST) ---
        setupSideMenu();
        add(sideMenuPanel, BorderLayout.WEST);

        // --- 3. Ajout des Vues au CardLayout ---

        // Simuler des panels pour les différents modules
        mainContentPanel.add(new JLabel("Tableau de bord de base", SwingConstants.CENTER), DASHBOARD_PANEL);
        mainContentPanel.add(new JPanel(), PATIENT_PANEL); // PatientPanel irait ici
        mainContentPanel.add(new JPanel(), RDV_PANEL);     // RdvPanel irait ici
        mainContentPanel.add(new JPanel(), FACTURE_PANEL); // FacturePanel irait ici

        add(mainContentPanel, BorderLayout.CENTER);
    }

    // -------------------------------------------------------------------------
    // Configuration du Menu Latéral (Sidebar)
    // -------------------------------------------------------------------------

    private void setupSideMenu() {
        sideMenuPanel.setLayout(new BoxLayout(sideMenuPanel, BoxLayout.Y_AXIS));
        sideMenuPanel.setPreferredSize(new Dimension(200, getHeight()));
        sideMenuPanel.setBackground(new Color(240, 240, 240)); // Couleur claire pour la barre

        // Affichage du nom de l'utilisateur
        JLabel welcomeLabel = new JLabel("Bonjour, " + utilisateurConnecte.getUsername());
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        sideMenuPanel.add(Box.createVerticalStrut(20));
        sideMenuPanel.add(welcomeLabel);
        sideMenuPanel.add(Box.createVerticalStrut(30));

        // Ajout des boutons basés sur le rôle
        String role = utilisateurConnecte.getRole().getLibelle().name();

        addButton("Tableau de bord", DASHBOARD_PANEL, "Dashboard");

        // Logique d'affichage des menus spécifiques aux rôles
        if (role.equals(RoleLibelle.MEDECIN.name())) {
            addButton("Patients", PATIENT_PANEL, "Patients");
            addButton("Rendez-vous", RDV_PANEL, "Rendez-vous");
            addButton("Ordonnances/Certificats", "Ordonnance", "Ordonnance");
        } else if (role.equals(RoleLibelle.SECRETAIRE.name())) {
            addButton("Patients", PATIENT_PANEL, "Patients");
            addButton("Rendez-vous", RDV_PANEL, "Rendez-vous");
            addButton("Factures", FACTURE_PANEL, "Factures");
        }

        // Bouton de déconnexion (toujours visible)
        sideMenuPanel.add(Box.createVerticalGlue()); // Pousse le bouton en bas
        addButton("Se Déconnecter", "Logout", "Se Deconnecter");
        sideMenuPanel.add(Box.createVerticalStrut(20));
    }

    private void addButton(String text, String cardName, String actionCommand) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setActionCommand(cardName);
        button.addActionListener(this::handleMenuAction);
        sideMenuPanel.add(button);
        sideMenuPanel.add(Box.createVerticalStrut(10));
    }

    private void handleMenuAction(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("Logout")) {
            // Logique de déconnexion : cacher la frame et afficher la LoginWindow
            dispose();
            // ApplicationContext.getLoginController().startAuthenticationFlow();
        } else {
            // Afficher le panneau correspondant
            cardLayout.show(mainContentPanel, command);
        }
    }

    // -------------------------------------------------------------------------
    // Méthodes de Redirection (appelées par AuthController)
    // -------------------------------------------------------------------------

    /** Affiche le dashboard par défaut du médecin. */
    public void showMedecinDashboard() {
        cardLayout.show(mainContentPanel, DASHBOARD_PANEL);
    }

    /** Affiche le dashboard par défaut de la secrétaire. */
    public void showSecretaireDashboard() {
        cardLayout.show(mainContentPanel, DASHBOARD_PANEL);
    }

    // ... Autres méthodes pour les autres rôles
}