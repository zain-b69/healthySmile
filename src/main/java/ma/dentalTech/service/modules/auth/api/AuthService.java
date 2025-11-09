package ma.dentalTech.service.modules.auth.api;
import ma.dentalTech.entities.utilisateur.Utilisateur;

public interface AuthService {

    /**
     * Tente d'authentifier un utilisateur.
     * @param login Le login (ou email) de l'utilisateur.
     * @param motDePasse Le mot de passe non-crypté.
     * @return L'objet Utilisateur si l'authentification est réussie.
     * @throws SecurityException Si l'authentification échoue.
     */
    Utilisateur authentifier(String login, String motDePasse) throws SecurityException;
}