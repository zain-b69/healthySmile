package ma.dentalTech.conf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Singleton qui gère la configuration et l'établissement des connexions JDBC.
 * Il lit le fichier db.properties pour obtenir les informations de connexion.
 */
public class Db {

    // L'unique instance de la classe (Singleton)
    private static Db instance;

    // Propriétés de connexion lues depuis le fichier
    private final Properties properties = new Properties();

    private Db() {
        // Le constructeur est privé pour empêcher l'instanciation externe (Singleton)

        // 1. Charger le fichier db.properties
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config/db.properties")) {

            if (input == null) {
                // IMPORTANT : S'assurer que le fichier est dans src/main/resources/config/
                throw new RuntimeException("Erreur: Le fichier de configuration 'db.properties' est introuvable.");
            }

            properties.load(input);

            // 2. Charger le Driver JDBC (Souvent optionnel avec les versions modernes de Java)
            // Class.forName(properties.getProperty("db.driver"));

        } catch (IOException | RuntimeException e) {
            System.err.println("Erreur fatale lors du chargement de la configuration de la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Méthode statique pour obtenir l'unique instance de la classe Db.
     * @return L'instance Singleton de Db.
     */
    public static Db getInstance() {
        if (instance == null) {
            instance = new Db();
        }
        return instance;
    }

    /**
     * Crée et retourne un nouvel objet Connection à la base de données.
     * @return Une connexion JDBC.
     * @throws SQLException Si une erreur survient lors de l'établissement de la connexion.
     */
    public Connection getConnection() throws SQLException {
        // Utilise les propriétés lues pour établir la connexion
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        if (url == null || user == null) {
            throw new SQLException("Paramètres de connexion manquants dans db.properties.");
        }

        // Retourne la connexion
        return DriverManager.getConnection(url, user, password);
    }
}