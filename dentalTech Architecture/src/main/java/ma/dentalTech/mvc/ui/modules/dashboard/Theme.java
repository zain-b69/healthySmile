package ma.dentalTech.mvc.ui.modules.dashboard;

import java.awt.*;

public final class Theme {
    private Theme(){}

    public static final Color NAV_BG = new Color(0x1E3A5F);      // bleu foncé (sidebar)
    public static final Color NAV_BTN = new Color(0x2D64B3);     // bleu bouton
    public static final Color NAV_BTN_HOVER = new Color(0x2A5AA0);
    public static final Color NAV_BTN_ACTIVE = new Color(0x234D8A);

    public static final Color BG = new Color(0xF6F8FC);          // fond clair
    public static final Color CARD = new Color(0x2D64B3);        // cartes bleues
    public static final Color CARD_TEXT = Color.WHITE;

    public static final Color PRIMARY = new Color(0x2D64B3);       // bleu
    public static final Color PRIMARY_HOVER = new Color(0x2A5AA0); // hover bleu
    public static final Color LOGOUT = new Color(0xD64545);      // rouge
    public static final Color LOGOUT_HOVER = new Color(0xC13E3E);

    public static Font h1() { return new Font("SansSerif", Font.BOLD, 28); }
    public static Font h2() { return new Font("SansSerif", Font.BOLD, 20); }
    public static Font base() { return new Font("SansSerif", Font.PLAIN, 14); }
}
