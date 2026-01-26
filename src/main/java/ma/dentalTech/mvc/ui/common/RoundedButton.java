package ma.dentalTech.mvc.ui.common;

import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {

    private int arc = 12; // small rounding

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false); // we paint the background ourselves
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    public void setArc(int arc) {
        this.arc = arc;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        g2.dispose();
        super.paintComponent(g); // draws the text/icon
    }
}
