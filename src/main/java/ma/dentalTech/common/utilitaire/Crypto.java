package ma.dentalTech.common.utilitaire;

import org.mindrot.jbcrypt.BCrypt;

public final class Crypto {
    private Crypto() {
    }

    public static String hash(String raw) {
        return BCrypt.hashpw(raw, BCrypt.gensalt(10));
    }

    public static boolean matches(String raw, String hash) {
        return BCrypt.checkpw(raw, hash);
    }
}
