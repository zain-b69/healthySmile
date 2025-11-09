package ma.dentalTech.service.modules.auth.baseImplementation;
import ma.dentalTech.entities.utilisateur.Utilisateur;
import ma.dentalTech.repository.modules.auth.api.UtilisateurRepository;
import ma.dentalTech.service.modules.auth.api.AuthService;
import ma.dentalTech.common.utilitaire.Crypto;

import java.sql.SQLException;

public class AuthServiceImpl implements AuthService {

    private final UtilisateurRepository utilisateurRepository;

    // Injection du Repository
    public AuthServiceImpl(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public Utilisateur authentifier(String login, String motDePasse) throws SecurityException {
        try {
            // 1. Récupérer l'utilisateur par login depuis la BD
            Utilisateur utilisateur = utilisateurRepository.findByLogin(login);

            if (utilisateur == null) {
                throw new SecurityException("Login incorrect.");
            }

            // 2. Logique Métier : Validation du mot de passe
            String motDePasseHashed = Crypto.hash(motDePasse); // Utiliser l'utilitaire de hachage

            if (!Crypto.matches(utilisateur.getPasswordHash(), motDePasseHashed)) {
                throw new SecurityException("Mot de passe incorrect.");
            }

            // 3. Mise à jour du dernier login (Logique Métier/Audit)
            // utilisateur.setLastLoginDate(LocalDate.now());
            // utilisateurRepository.update(utilisateur); // Nécessite une méthode update dans le Repository

            return utilisateur;

        } catch (SQLException e) {
            // Conversion de l'exception technique en exception métier
            throw new RuntimeException("Erreur de connexion à la base de données lors de l'authentification.", e);
        }
    }
}