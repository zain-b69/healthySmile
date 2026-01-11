package ma.dentalTech.mvc.ui.modules.dashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatCard extends RoundedPanel {

    public StatCard(String title, String value) {
        super(20);
        setBackground(Theme.CARD);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(18, 18, 18, 18));
        setPreferredSize(new Dimension(220, 110));

        JLabel t = new JLabel("<html><div style='text-align:center;'>" + title + "</div></html>", SwingConstants.CENTER);
        t.setForeground(Theme.CARD_TEXT);
        t.setFont(Theme.base());

        JLabel v = new JLabel(value, SwingConstants.CENTER);
        v.setForeground(Theme.CARD_TEXT);
        v.setFont(Theme.h2());

        add(t, BorderLayout.CENTER);
        add(v, BorderLayout.SOUTH);
    }
}
