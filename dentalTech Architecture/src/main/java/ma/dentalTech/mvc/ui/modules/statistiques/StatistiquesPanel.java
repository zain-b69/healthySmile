package ma.dentalTech.mvc.ui.modules.statistiques;

import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.enums.CategorieStatistique;
import ma.dentalTech.mvc.controllers.modules.statistiques.api.StatistiquesController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class StatistiquesPanel extends JPanel {

    private final StatistiquesController controller;

    private final JTextField tfCabinet = new JTextField(8);
    private final JTextField tfDebut = new JTextField(10); // yyyy-MM-dd
    private final JTextField tfFin = new JTextField(10);   // yyyy-MM-dd
    private final JComboBox<CategorieStatistique> cbCat =
            new JComboBox<>(CategorieStatistique.values());

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Période", "Début", "Fin", "Patients", "RDV", "Consult", "Revenu", "Taux%", "Top Acte", "Cat"},
            0
    );
    private final JTable table = new JTable(model);

    public StatistiquesPanel(StatistiquesController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 15, 15, 15));

        add(buildTop(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);

        LocalDate now = LocalDate.now();
        tfDebut.setText(now.withDayOfMonth(1).toString());
        tfFin.setText(now.toString());
        cbCat.setSelectedItem(CategorieStatistique.REVENUS);
    }

    private JComponent buildTop() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

        p.add(new JLabel("Cabinet ID:"));
        p.add(tfCabinet);

        p.add(new JLabel("Début:"));
        p.add(tfDebut);

        p.add(new JLabel("Fin:"));
        p.add(tfFin);

        p.add(new JLabel("Catégorie:"));
        p.add(cbCat);

        JButton btnLister = new JButton("Lister");
        btnLister.addActionListener(e -> onLister());
        p.add(btnLister);

        JButton btnGenerer = new JButton("Générer snapshot");
        btnGenerer.addActionListener(e -> onGenerer());
        p.add(btnGenerer);

        JButton btnRapport = new JButton("Rapport performance");
        btnRapport.addActionListener(e -> onRapport());
        p.add(btnRapport);

        return p;
    }

    private JComponent buildBottom() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        JButton btnDelete = new JButton("Supprimer");
        btnDelete.addActionListener(e -> onDeleteSelected());
        p.add(btnDelete);

        return p;
    }

    private void onLister() {
        try {
            long idCab = parseCabinetId();
            CategorieStatistique cat = (CategorieStatistique) cbCat.getSelectedItem();

            List<Statistiques> list;
            if (cat != null) list = controller.findByCabinetAndCategorie(idCab, cat);
            else list = controller.findByCabinet(idCab);

            refresh(list);

        } catch (Exception ex) {
            showErr(ex);
        }
    }

    private void onGenerer() {
        try {
            long idCab = parseCabinetId();
            LocalDate debut = LocalDate.parse(tfDebut.getText().trim());
            LocalDate fin = LocalDate.parse(tfFin.getText().trim());
            CategorieStatistique cat = (CategorieStatistique) cbCat.getSelectedItem();

            Statistiques s = Statistiques.builder()
                    .idCabinet(idCab)
                    .periode(buildPeriodeLabel(debut, fin))
                    .dateDebut(debut)
                    .dateFin(fin)
                    .categorie(cat == null ? CategorieStatistique.REVENUS : cat)
                    // métriques: tu peux les saisir dans un dialog si tu veux.
                    // pour l’instant on laisse null / 0 selon ton service
                    .nbPatients(0)
                    .nbRdv(0)
                    .nbConsultation(0)
                    .revenuTotal(0.0)
                    .tauxAnnulation(0.0)
                    .actePlusEffectif(null)
                    .build();

            Statistiques created = controller.generer(s);
            JOptionPane.showMessageDialog(this,
                    "Snapshot créé: ID=" + created.getIdStatistique(),
                    "OK", JOptionPane.INFORMATION_MESSAGE);

            onLister();

        } catch (Exception ex) {
            showErr(ex);
        }
    }

    private void onRapport() {
        try {
            long idCab = parseCabinetId();
            LocalDate debut = LocalDate.parse(tfDebut.getText().trim());
            LocalDate fin = LocalDate.parse(tfFin.getText().trim());

            String rapport = controller.rapportPerformance(idCab, debut, fin);

            JTextArea ta = new JTextArea(rapport);
            ta.setEditable(false);
            ta.setLineWrap(true);
            ta.setWrapStyleWord(true);

            JScrollPane sp = new JScrollPane(ta);
            sp.setPreferredSize(new Dimension(700, 450));

            JOptionPane.showMessageDialog(this, sp, "Rapport Performance", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            showErr(ex);
        }
    }

    private void onDeleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionne une ligne.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Long id = (Long) model.getValueAt(row, 0);
        int res = JOptionPane.showConfirmDialog(this, "Supprimer statistiques ID=" + id + " ?", "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (res != JOptionPane.YES_OPTION) return;

        try {
            controller.deleteById(id);
            onLister();
        } catch (Exception ex) {
            showErr(ex);
        }
    }

    private void refresh(List<Statistiques> list) {
        model.setRowCount(0);
        for (Statistiques s : list) {
            model.addRow(new Object[]{
                    s.getIdStatistique(),
                    s.getPeriode(),
                    s.getDateDebut(),
                    s.getDateFin(),
                    s.getNbPatients(),
                    s.getNbRdv(),
                    s.getNbConsultation(),
                    s.getRevenuTotal(),
                    s.getTauxAnnulation(),
                    s.getActePlusEffectif(),
                    s.getCategorie()
            });
        }
    }

    private long parseCabinetId() {
        String v = tfCabinet.getText().trim();
        if (v.isEmpty()) throw new IllegalArgumentException("Cabinet ID requis");
        return Long.parseLong(v);
    }

    private String buildPeriodeLabel(LocalDate debut, LocalDate fin) {
        // simple: “DU yyyy-MM-dd AU yyyy-MM-dd”
        return "DU " + debut + " AU " + fin;
    }

    private void showErr(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
