package ma.dentalTech.mvc.ui.common;

import ma.dentalTech.mvc.ui.modules.dashboard.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class ButtonStyles {
    private ButtonStyles(){}

    public static void primary(JButton b) {
        prepare(b);
        b.setForeground(Color.WHITE);
        b.setBackground(Theme.PRIMARY);
        installHover(b, Theme.PRIMARY, Theme.PRIMARY_HOVER);
    }

    /* ================= SECONDARY ================= */
    public static void secondary(JButton b) {
        prepare(b);
        applyFilled(b, Theme.SECONDARY, Theme.SECONDARY_HOVER, Theme.TEXT_DARK);
    }

    public static void danger(JButton b) {
        prepare(b);
        b.setForeground(Color.WHITE);
        b.setBackground(Theme.LOGOUT);
        installHover(b, Theme.LOGOUT, Theme.LOGOUT_HOVER);
    }

    private static void prepare(JButton b) {
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setFont(Theme.base());
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(150, 36));

        // ✅ IMPORTANT: RoundedButton paints its own background
        if (b instanceof RoundedButton) {
            b.setContentAreaFilled(false);
            b.setOpaque(false);
        } else {
            b.setContentAreaFilled(true);
            b.setOpaque(true);
        }
    }

    /* ================= STYLE APPLIER ================= */
    private static void applyFilled(JButton b, Color normal, Color hover, Color text) {
        b.setBackground(normal);
        b.setForeground(text);
        installHover(b, normal, hover);
    }

    private static void installHover(JButton b, Color normal, Color hover) {
        // Avoid stacking multiple MouseListeners if called many times
        for (var ml : b.getMouseListeners()) {
            if (ml instanceof HoverListener) {
                b.removeMouseListener(ml);
            }
        }
        b.addMouseListener(new HoverListener(b, normal, hover));
    }

    private static final class HoverListener extends MouseAdapter {
        private final JButton b;
        private final Color normal;
        private final Color hover;

        private HoverListener(JButton b, Color normal, Color hover) {
            this.b = b;
            this.normal = normal;
            this.hover = hover;
        }

        @Override public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
        @Override public void mouseExited(MouseEvent e) { b.setBackground(normal); }
    }
}
