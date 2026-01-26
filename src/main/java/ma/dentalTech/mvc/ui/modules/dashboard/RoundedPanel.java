package ma.dentalTech.mvc.ui.modules.dashboard;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private final int radius;

    public RoundedPanel(int radius) {
        this.radius = radius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // shadow (simple)
        g2.setColor(new Color(0,0,0,30));
        g2.fillRoundRect(6, 6, getWidth()-12, getHeight()-12, radius, radius);

        // main
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth()-12, getHeight()-12, radius, radius);

        g2.dispose();
    }
}
