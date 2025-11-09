package ma.dentalTech.mvc.controllers.modules.auth.api;

/**
 * Contrat pour la gestion des actions d'authentification (connexion et déconnexion).
 */
public interface AuthController {

    /** Lance le processus d'authentification (affichage du formulaire ou saisie console). */
    void startAuthenticationFlow();

    /** Gère la tentative de connexion avec les identifiants fournis. */
    void handleLoginAttempt(String login, String password);

    /** Gère la déconnexion d'un utilisateur. */
    void handleLogout();
}