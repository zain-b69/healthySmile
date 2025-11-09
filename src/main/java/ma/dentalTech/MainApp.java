package ma.dentalTech;

import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.mvc.controllers.modules.auth.api.AuthController;

import javax.swing.SwingUtilities;

/**
 * Point d'entrée principal de l'application DentalSoft.
 * Responsable de l'initialisation du contexte IoC et du lancement de l'UI.
 */
public class MainApp {

    public static void main(String[] args) {
        // 1. Charger et initialiser le contexte IoC (l'ApplicationContext)
        // Ce simple appel initialise toutes les couches (DAO, Service, Controller)
        // grâce au bloc statique de l'ApplicationContext.
        try {
            // Tentative d'accès à une classe du contexte pour garantir l'initialisation
            // Vous pouvez simplement appeler ApplicationContext.getBean(AuthController.class)
            // pour garantir que le bloc statique est exécuté.
            ApplicationContext.getBean(AuthController.class);

            System.out.println("✅ ApplicationContext initialisé avec succès. Composants prêts.");

        } catch (Exception e) {
            System.err.println("❌ Échec de l'initialisation du contexte de l'application. Vérifiez beans.properties et les constructeurs.");
            e.printStackTrace();
            return; // Arrêter l'application si l'initialisation échoue
        }

        // 2. Lancement de l'Interface Graphique (UI)
        // SwingUtilities.invokeLater assure que la création et la modification
        // des composants Swing se font sur le thread d'événements (EDT),
        // ce qui est essentiel pour la stabilité de Swing.
        SwingUtilities.invokeLater(() -> {
            try {
                // 3. Récupérer le contrôleur d'authentification depuis le contexte
                AuthController authController = ApplicationContext.getBean(AuthController.class);

                // 4. Lancer le flux d'authentification (affiche la LoginWindow)
                authController.startAuthenticationFlow();

            } catch (Exception e) {
                System.err.println("❌ Erreur lors du lancement du contrôleur d'authentification.");
                e.printStackTrace();
            }
        });
    }
}