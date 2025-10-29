package ma.dentalTech.common.validation;

import java.util.regex.Pattern;
import ma.dentalTech.common.exceptions.ValidationException;

public final class Validators {
    private static final Pattern EMAIL = Pattern.compile("^[^\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+\\-\\s]{6,20}$");

    private Validators() {
    }

    public static void notBlank(String v, String field) throws ValidationException {
        if (v == null || v.trim().isEmpty()) throw new ValidationException(field + " est obligatoire");
    }

    public static void email(String v) throws ValidationException {
        if (v != null && !EMAIL.matcher(v).matches()) throw new ValidationException("Email invalide");
    }

    public static void phone(String v) throws ValidationException {
        if (v != null && !PHONE.matcher(v).matches()) throw new ValidationException("Téléphone invalide");
    }

    public static void minLen(String v, int n, String field) throws ValidationException {
        if (v == null || v.length() < n)
            throw new ValidationException(field + " doit contenir au moins " + n + " caractères");
    }
}
