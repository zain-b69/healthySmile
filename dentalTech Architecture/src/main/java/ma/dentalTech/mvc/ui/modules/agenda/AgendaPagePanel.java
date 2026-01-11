package ma.dentalTech.mvc.ui.modules.agenda;

import ma.dentalTech.entities.users.Medecin;
import ma.dentalTech.repository.modules.users.api.MedecinRepository;
import ma.dentalTech.service.modules.agendas.api.AgendaMensuelService;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.repository.modules.agenda.api.RDVRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class AgendaPagePanel extends JPanel {

    private final MedecinRepository medecinRepo;
    private final AgendaMensuelService agendaService;
    private final RDVRepository rdvRepo;
    private Long currentAgendaId = null;

    private final JComboBox<MedecinItem> cbMedecin = new JComboBox<>();
    private final JSpinner spMois;
    private final JSpinner spAnnee;

    private final JButton btnCreate = new JButton("Créer agenda");
    private final JButton btnOpen = new JButton("Ouvrir agenda");
    private final JLabel lblInfo = new JLabel(" ");

    public AgendaPagePanel(MedecinRepository medecinRepo,
                           AgendaMensuelService agendaService,
                           RDVRepository rdvRepo) {
        this.medecinRepo = medecinRepo;
        this.agendaService = agendaService;
        this.rdvRepo = rdvRepo;

        setLayout(new BorderLayout());
        setOpaque(true);

        LocalDate now = LocalDate.now();
        spMois = new JSpinner(new SpinnerNumberModel(now.getMonthValue(), 1, 12, 1));
        spAnnee = new JSpinner(new SpinnerNumberModel(now.getYear(), now.getYear() - 1, now.getYear() + 10, 1));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        loadMedecins();
        wireActions();
    }


    private JComponent buildHeader() {
        JPanel top = new JPanel(new GridBagLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 8, 4, 8);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0; c.gridy = 0;
        top.add(new JLabel("Médecin:"), c);
        c.gridx = 1; c.gridy = 0; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1;
        top.add(cbMedecin, c);

        c.gridx = 2; c.gridy = 0; c.weightx = 0; c.fill = GridBagConstraints.NONE;
        top.add(new JLabel("Mois:"), c);
        c.gridx = 3; c.gridy = 0;
        top.add(spMois, c);

        c.gridx = 4; c.gridy = 0;
        top.add(new JLabel("Année:"), c);
        c.gridx = 5; c.gridy = 0;
        top.add(spAnnee, c);

        c.gridx = 6; c.gridy = 0;
        top.add(btnOpen, c);

        c.gridx = 7; c.gridy = 0;
        top.add(btnCreate, c);

        c.gridx = 0; c.gridy = 1; c.gridwidth = 8;
        lblInfo.setForeground(new Color(30, 90, 30));
        top.add(lblInfo, c);

        return top;
    }

    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel hint = new JLabel("""
                <html>
                <b>Astuce:</b> Crée/ouvre l’agenda mensuel du médecin avant d’ajouter des RDV.<br/>
                Sinon, l’INSERT RDV échoue car <code>agenda_mensuel_id</code> est obligatoire.
                </html>
                """);

        body.add(hint, BorderLayout.NORTH);
        body.add(new JScrollPane(table), BorderLayout.CENTER);

        // Ici tu peux plus tard intégrer ton écran de RDV (table + boutons)
        // une fois que l'agenda mensuel est chargé.
        return body;
    }

    private void loadMedecins() {
        cbMedecin.removeAllItems();
        cbMedecin.addItem(new MedecinItem(null, "-- Choisir --"));

        for (Medecin m : medecinRepo.findAllOrderByNom()) {
            // IMPORTANT: l'id à utiliser = idUser (pas BaseEntity.id)
            Long idMedecin = m.getIdUser();
            cbMedecin.addItem(new MedecinItem(idMedecin, m.getNom() + " (id=" + idMedecin + ")"));
        }
        cbMedecin.setSelectedIndex(0);
    }

    private void wireActions() {
        btnCreate.addActionListener(e -> {
            MedecinItem sel = (MedecinItem) cbMedecin.getSelectedItem();
            if (sel == null || sel.idMedecin == null) {
                JOptionPane.showMessageDialog(this, "Choisis un médecin.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int mois = (int) spMois.getValue();
            int annee = (int) spAnnee.getValue();

            try {
                agendaService.planifierAgendaMensuel(sel.idMedecin, mois, annee);
                lblInfo.setText("✅ Agenda créé pour médecin id=" + sel.idMedecin + " (" + mois + "/" + annee + ")");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnOpen.addActionListener(e -> {
            MedecinItem sel = (MedecinItem) cbMedecin.getSelectedItem();
            if (sel == null || sel.idMedecin == null) {
                JOptionPane.showMessageDialog(this, "Choisis un médecin.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int mois = (int) spMois.getValue();
            int annee = (int) spAnnee.getValue();

            try {
                // ⚠️ Tu dois avoir une méthode dans agendaService qui renvoie l'id d'agenda
                Long agendaId = agendaService.findAgendaId(sel.idMedecin, mois, annee);

                if (agendaId == null) {
                    currentAgendaId = null;
                    model.setRowCount(0);
                    JOptionPane.showMessageDialog(this,
                            "Aucun agenda pour ce mois. Clique sur 'Créer agenda'.",
                            "Info", JOptionPane.INFORMATION_MESSAGE);
                    lblInfo.setText("⚠️ Aucun agenda trouvé pour médecin id=" + sel.idMedecin + " (" + mois + "/" + annee + ")");
                    return;
                }

                currentAgendaId = agendaId;
                lblInfo.setText("✅ Agenda sélectionné (id=" + agendaId + ") pour médecin id=" + sel.idMedecin + " (" + mois + "/" + annee + ")");
                loadRdvsForAgenda(agendaId);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

    }

    private static class MedecinItem {
        final Long idMedecin;
        final String label;

        MedecinItem(Long idMedecin, String label) {
            this.idMedecin = idMedecin;
            this.label = label;
        }

        @Override public String toString() { return label; }
    }

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Date", "Heure", "Patient", "Statut", "Motif"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable table = new JTable(model);

    private void loadRdvsForAgenda(Long agendaId) {
        model.setRowCount(0);

        for (RDV r : rdvRepo.findByAgenda(agendaId)) {
            model.addRow(new Object[]{
                    r.getIdRdv(),
                    r.getDate(),
                    r.getHeure(),
                    r.getIdPatient(),   // si tu veux afficher nom patient => il faut le joindre ou appeler patientRepo
                    r.getStatut(),
                    r.getMotif()
            });
        }
    }


}
