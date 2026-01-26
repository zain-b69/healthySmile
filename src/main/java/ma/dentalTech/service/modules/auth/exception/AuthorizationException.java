package ma.dentalTech.service.modules.auth.exception;

/**
 * Exception levée quand un utilisateur n'a pas le rôle / privilège requis.
 */
public class AuthorizationException extends RuntimeException {

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
