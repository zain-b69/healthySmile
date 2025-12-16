package ma.dentalTech.service.modules.auth.api;

public interface PasswordEncoder {

    String encode(CharSequence rawPassword);

    boolean matches(CharSequence rawPassword, String encodedPassword);
}
