package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.entities.users.Medecin;
import ma.dentalTech.repository.modules.users.api.MedecinRepository;
import ma.dentalTech.service.modules.agendas.api.AgendaMensuelService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class CreateAgendaMensuelDialog extends JDialog {

    private final MedecinRepository medecinRepo;
    private final AgendaMensuelService agendaService;

    private final JComboBox<MedecinItem> cbMedecin = new JComboBox<>();
    private final JSpinner spMois;
    private final JSpinner spAnnee;

    public CreateAgendaMensuelDialog(JFrame parent,
                                     MedecinRepository medecinRepo,
                                     AgendaMensuelService agendaService) {
        super(parent, "Créer agenda mensuel", true);
        this.medecinRepo = medecinRepo;
        this.agendaService = agendaService;

        LocalDate now = LocalDate.now();
        spMois = new JSpinner(new SpinnerNumberModel(now.getMonthValue(), 1, 12, 1));
        spAnnee = new JSpinner(new SpinnerNumberModel(now.getYear(), now.getYear(), now.getYear() + 10, 1));

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(520, 220);
        setLocationRelativeTo(parent);

        buildUi();
        loadMedecins();
    }

    private void buildUi() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 10, 6, 10);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        // Médecin
        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        form.add(new JLabel("Médecin:"), c);
        c.gridx = 1; c.gridy = 0; c.weightx = 1;
        form.add(cbMedecin, c);

        // Mois
        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        form.add(new JLabel("Mois:"), c);
        c.gridx = 1; c.gridy = 1; c.weightx = 1;
        form.add(spMois, c);

        // Année
        c.gridx = 0; c.gridy = 2; c.weightx = 0;
        form.add(new JLabel("Année:"), c);
        c.gridx = 1; c.gridy = 2; c.weightx = 1;
        form.add(spAnnee, c);

        JButton btnCreate = new JButton("Créer");
        JButton btnCancel = new JButton("Annuler");

        btnCreate.addActionListener(e -> onCreate());
        btnCancel.addActionListener(e -> dispose());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnCreate);
        actions.add(btnCancel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(actions, BorderLayout.SOUTH);
    }

    private void loadMedecins() {
        cbMedecin.removeAllItems();
        cbMedecin.addItem(new MedecinItem(null, "-- Choisir --"));

        for (Medecin m : medecinRepo.findAllOrderByNom()) {
            // IMPORTANT: utiliser l'idUser comme identifiant médecin (pas id_entite)
            Long idMedecin = m.getIdUser();
            String label = m.getNom() + " (id=" + idMedecin + ")";
            cbMedecin.addItem(new MedecinItem(idMedecin, label));
        }
        cbMedecin.setSelectedIndex(0);
    }

    private void onCreate() {
        MedecinItem sel = (MedecinItem) cbMedecin.getSelectedItem();
        if (sel == null || sel.idMedecin == null) {
            JOptionPane.showMessageDialog(this, "Veuillez choisir un médecin.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int mois = (int) spMois.getValue();
        int annee = (int) spAnnee.getValue();

        try {
            agendaService.planifierAgendaMensuel(sel.idMedecin, mois, annee);
            JOptionPane.showMessageDialog(this,
                    "✅ Agenda créé pour le médecin id=" + sel.idMedecin + " sur " + mois + "/" + annee,
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Petit objet pour le combo ---
    private static class MedecinItem {
        final Long idMedecin;
        final String label;

        MedecinItem(Long idMedecin, String label) {
            this.idMedecin = idMedecin;
            this.label = label;
        }

        @Override public String toString() { return label; }
    }
}
