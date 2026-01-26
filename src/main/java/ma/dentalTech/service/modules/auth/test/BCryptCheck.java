package ma.dentalTech.service.modules.auth.test;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptCheck {
    public static void main(String[] args) {
        String plain = "admin123"; // le mot de passe que tu penses avoir hashé
        String hash  = "$2a$10$w1gqYyqkq7K8ZbKxQzJw7uQk1yYp9l0J7c0l2v4uJ0wqkXWc7b9eG";

        System.out.println(BCrypt.checkpw(plain, hash)); // true ou false
    }
}
