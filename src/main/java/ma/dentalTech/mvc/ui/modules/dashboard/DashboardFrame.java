package ma.dentalTech.mvc.ui.modules.dashboard;

import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;
import ma.dentalTech.service.modules.auth.dto.UserPrincipal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardFrame extends JFrame {

    private final DashboardController controller;
    private final UserPrincipal principal;

    private final JPanel center = new JPanel(new BorderLayout());
    private final Map<ApplicationPages, JButton> navButtons = new EnumMap<>(ApplicationPages.class);

    public DashboardFrame(DashboardController controller, UserPrincipal principal) {
        this.controller = controller;
        this.principal = principal;

        setTitle("HealthySmile");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BG);

        getContentPane().add(buildSidebar(), BorderLayout.WEST);
        getContentPane().add(buildTopAndCenter(), BorderLayout.CENTER);

        // default page
        navigate(ApplicationPages.DASHBOARD);
    }

    private JComponent buildTopAndCenter() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(true);
        wrap.setBackground(Theme.BG);

        wrap.add(buildTopBar(), BorderLayout.NORTH);

        center.setOpaque(true);
        center.setBackground(Theme.BG);
        center.setBorder(new EmptyBorder(20, 20, 20, 20));
        wrap.add(center, BorderLayout.CENTER);

        return wrap;
    }

    private JComponent buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(true);
        top.setBackground(Theme.BG);
        top.setBorder(new EmptyBorder(18, 22, 10, 22));

        JLabel title = new JLabel("HealthySmile");
        title.setFont(Theme.h1());

        RoundedButton logout = new RoundedButton("Se Deconnecter");
        ButtonStyles.danger(logout);
        logout.addActionListener(e -> controller.onLogoutRequested());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(logout);

        top.add(title, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        return top;
    }

    private JComponent buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setOpaque(true);
        side.setBackground(Theme.NAV_BG);
        side.setPreferredSize(new Dimension(260, 0));
        side.setBorder(new EmptyBorder(18, 16, 16, 16));

        // LOGO (on l'ajoute après)
        side.add(buildLogo());
        side.add(Box.createVerticalStrut(14));

        RoleType role = (principal != null) ? principal.getRole() : null;

        Map<ApplicationPages, String> menu = new LinkedHashMap<>();
        menu.put(ApplicationPages.DASHBOARD, "Home");
        menu.put(ApplicationPages.RENDEZ_VOUS, "Rendez-vous");
        menu.put(ApplicationPages.AGENDA, "Agenda");
        menu.put(ApplicationPages.PATIENTS, "Patients");
        menu.put(ApplicationPages.DOSSIERS_MEDICAUX, "Dossiers Medicaux");
        menu.put(ApplicationPages.ORDONNANCES, "Ordonnances");
        menu.put(ApplicationPages.CERTIFICATIONS, "Certifications");
        menu.put(ApplicationPages.SITUATIONS_FINANCIERES,"Situations Financières");
        menu.put(ApplicationPages.CATALOGUE, "Catalogue");
        menu.put(ApplicationPages.CAISSE, "Caisse");
        menu.put(ApplicationPages.STATISTIQUES, "Statistiques");
        menu.put(ApplicationPages.USERS, "Utilisateurs");

        for (var entry : menu.entrySet()) {
            ApplicationPages page = entry.getKey();
            if (!PageAccess.canAccess(role, page)) continue; // ✅ filtre rôle

            side.add(navBtn(entry.getValue(), page));
            side.add(Box.createVerticalStrut(10));
        }

        side.add(Box.createVerticalGlue());

        side.add(Box.createVerticalStrut(10));

        RoundedButton exit = new RoundedButton("Quitter");
        ButtonStyles.danger(exit);
        exit.addActionListener(e -> controller.onExitRequested());
        side.add(exit);

        return side;
    }

    private JButton navBtn(String text, ApplicationPages page) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        styleNavButton(b);

        b.addActionListener(e -> navigate(page));
        navButtons.put(page, b);
        return b;
    }

    private void navigate(ApplicationPages page) {
        // active style
        for (var entry : navButtons.entrySet()) {
            JButton btn = entry.getValue();
            btn.setBackground(entry.getKey() == page ? Theme.NAV_BTN_ACTIVE : Theme.NAV_BTN);
        }

        JComponent panel = controller.onNavigateRequested(page);
        setCenter(panel);
    }

    public void setCenter(JComponent component) {
        center.removeAll();
        center.add(component, BorderLayout.CENTER);
        center.revalidate();
        center.repaint();
    }

    // -------- styles ----------
    private static void styleNavButton(JButton b) {
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setBackground(Theme.NAV_BTN);
        b.setFont(Theme.base());
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (b.getBackground().equals(Theme.NAV_BTN_ACTIVE)) return;
                b.setBackground(Theme.NAV_BTN_HOVER);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (b.getBackground().equals(Theme.NAV_BTN_ACTIVE)) return;
                b.setBackground(Theme.NAV_BTN);
            }
        });
    }

    private static void styleLogoutButton(JButton b) {
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setBackground(Theme.LOGOUT);
        b.setFont(Theme.base());
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(150, 36));

        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(Theme.LOGOUT_HOVER); }
            @Override public void mouseExited(MouseEvent e) { b.setBackground(Theme.LOGOUT); }
        });
    }

    private JComponent buildLogo() {
        JLabel logo = new JLabel();
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        ImageIcon icon = loadScaledSvgIcon("/assets/logo.svg", 160, 60);
        if (icon != null) logo.setIcon(icon);
        else {
            logo.setText("HealthySmile");
            logo.setForeground(Color.WHITE);
            logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        }

        logo.setBorder(new EmptyBorder(4, 6, 8, 6));
        return logo;
    }

    private ImageIcon loadScaledSvgIcon(String resourcePath, int w, int h) {
        try (java.io.InputStream in = getClass().getResourceAsStream(resourcePath)) {
            if (in == null) return null;

            org.apache.batik.transcoder.image.PNGTranscoder t = new org.apache.batik.transcoder.image.PNGTranscoder();
            t.addTranscodingHint(org.apache.batik.transcoder.image.PNGTranscoder.KEY_WIDTH, (float) w);
            t.addTranscodingHint(org.apache.batik.transcoder.image.PNGTranscoder.KEY_HEIGHT, (float) h);

            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            org.apache.batik.transcoder.TranscoderInput input = new org.apache.batik.transcoder.TranscoderInput(in);
            org.apache.batik.transcoder.TranscoderOutput output = new org.apache.batik.transcoder.TranscoderOutput(out);
            t.transcode(input, output);

            byte[] pngBytes = out.toByteArray();
            return new ImageIcon(javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(pngBytes)));
        } catch (Exception e) {
            return null;
        }
    }

}
