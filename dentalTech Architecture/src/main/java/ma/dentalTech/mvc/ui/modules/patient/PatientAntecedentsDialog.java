package ma.dentalTech.mvc.ui.modules.patient;

import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentController;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PatientAntecedentsDialog extends JDialog {

    private final PatientController patientController;
    private final AntecedentController antecedentController;
    private final Patient patient;

    private final DefaultListModel<Antecedent> linkedModel = new DefaultListModel<>();
    private final DefaultListModel<Antecedent> availableModel = new DefaultListModel<>();

    private final JList<Antecedent> linkedList = new JList<>(linkedModel);
    private final JList<Antecedent> availableList = new JList<>(availableModel);

    public PatientAntecedentsDialog(Window owner,
                                    PatientController patientController,
                                    AntecedentController antecedentController,
                                    Patient patient) {
        super(owner, "Antécédents : " + patient.getNom(), ModalityType.APPLICATION_MODAL);
        this.patientController = patientController;
        this.antecedentController = antecedentController;
        this.patient = patient;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(760, 420);
        setLocationRelativeTo(owner);

        linkedList.setCellRenderer(new AntecedentRenderer());
        availableList.setCellRenderer(new AntecedentRenderer());

        setContentPane(build());
        reload();
    }

    private JPanel build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Gestion des antécédents");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        root.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.BOTH;
        g.weightx = 1;
        g.weighty = 1;

        // Col 0: linked
        g.gridx = 0; g.gridy = 0;
        center.add(wrap("Antécédents liés au patient", linkedList), g);

        // Col 1: actions
        g.gridx = 1; g.gridy = 0;
        g.weightx = 0;
        center.add(actionsPanel(), g);
        g.weightx = 1;

        // Col 2: available
        g.gridx = 2; g.gridy = 0;
        center.add(wrap("Antécédents disponibles", availableList), g);

        root.add(center, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = new JButton("Fermer");
        close.addActionListener(e -> dispose());
        south.add(close);
        root.add(south, BorderLayout.SOUTH);

        return root;
    }

    private JPanel actionsPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JButton add = new JButton("Ajouter →");
        JButton remove = new JButton("← Retirer");
        JButton refresh = new JButton("Rafraîchir");

        add.setAlignmentX(Component.CENTER_ALIGNMENT);
        remove.setAlignmentX(Component.CENTER_ALIGNMENT);
        refresh.setAlignmentX(Component.CENTER_ALIGNMENT);

        add.addActionListener(e -> onAdd());
        remove.addActionListener(e -> onRemove());
        refresh.addActionListener(e -> reload());

        p.add(Box.createVerticalGlue());
        p.add(add);
        p.add(Box.createVerticalStrut(10));
        p.add(remove);
        p.add(Box.createVerticalStrut(18));
        p.add(refresh);
        p.add(Box.createVerticalGlue());

        return p;
    }

    private JPanel wrap(String label, JList<Antecedent> list) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        p.add(l, BorderLayout.NORTH);
        p.add(new JScrollPane(list), BorderLayout.CENTER);
        return p;
    }

    private void reload() {
        linkedModel.clear();
        availableModel.clear();

        try {
            List<Antecedent> linked = patientController.getAntecedentsOfPatient(patient.getIdPatient());
            List<Antecedent> all = antecedentController.findAll();

            Map<Long, Antecedent> linkedById = new HashMap<>();
            for (Antecedent a : linked) {
                if (a != null && a.getIdAntecedent() != null) linkedById.put(a.getIdAntecedent(), a);
            }

            for (Antecedent a : linked) linkedModel.addElement(a);

            for (Antecedent a : all) {
                if (a == null || a.getIdAntecedent() == null) continue;
                if (!linkedById.containsKey(a.getIdAntecedent())) {
                    availableModel.addElement(a);
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur chargement antécédents: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdd() {
        Antecedent selected = availableList.getSelectedValue();
        if (selected == null) return;

        try {
            patientController.addAntecedentToPatient(patient.getIdPatient(), selected.getIdAntecedent());
            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur ajout: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRemove() {
        Antecedent selected = linkedList.getSelectedValue();
        if (selected == null) return;

        try {
            patientController.removeAntecedentFromPatient(patient.getIdPatient(), selected.getIdAntecedent());
            reload();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur retrait: " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Renderer pour afficher nom + categorie + risque ---
    private static class AntecedentRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Antecedent a) {
                setText(a.getNom());
            }
            return this;
        }
    }
}
