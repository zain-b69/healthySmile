package ma.dentalTech.repository.modules.auth.api;
import ma.dentalTech.entities.utilisateur.Utilisateur;
import java.sql.SQLException;

public interface UtilisateurRepository {

    // Méthode spécifique à l'authentification
    Utilisateur findByLogin(String login) throws SQLException;

    // ... Autres méthodes CRUD pour l'administration des utilisateurs (si nécessaire)
}