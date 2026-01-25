package ma.dentalTech.mvc.ui.modules.rdv;

import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.dossierMedical.Facture;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.users.Medecin;
import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.util.ConsultationWorkflowController;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.controllers.modules.agenda.api.RDVController;
import ma.dentalTech.mvc.ui.common.ButtonStyles;
import ma.dentalTech.mvc.ui.common.RoundedButton;
import ma.dentalTech.repository.modules.users.api.MedecinRepository;
import ma.dentalTech.service.modules.auth.dto.UserPrincipal;
import ma.dentalTech.service.modules.dossierMedicale.dto.InterventionInput;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

public class RendezVousPanel extends JPanel {

    private final UserPrincipal principal;
    private final RDVController rdvController;
    private final PatientController patientController;
    private final MedecinRepository medecinRepo;
    private final ConsultationWorkflowController workflowController;
    private final JComboBox<MedecinItem> cbMedecin = new JComboBox<>();
    private final JTextField tfDate = new JTextField(10);

    private final RoundedButton btnRefresh = new RoundedButton("Rafraîchir");
    private final RoundedButton btnAdd = new RoundedButton("Nouveau RDV");
    private final RoundedButton btnConfirm = new RoundedButton("Confirmer");
    private final RoundedButton btnMove = new RoundedButton("Déplacer");
    private final RoundedButton btnCancel = new RoundedButton("Annuler");
    private final RoundedButton btnOuvrirConsultation = new RoundedButton("Consultation");

    private final RdvTableModel model = new RdvTableModel();
    private final JTable table = new JTable(model);

    // cache labels
    private Map<Long, String> patientLabelById = new HashMap<>();
    private Map<Long, String> medecinLabelById = new HashMap<>();

    public RendezVousPanel(UserPrincipal principal,
                           RDVController rdvController,
                           PatientController patientController,
                           MedecinRepository medecinRepo,
                           ConsultationWorkflowController workflowController
) {
        this.principal = principal;
        this.rdvController = rdvController;
        this.patientController = patientController;
        this.medecinRepo = medecinRepo;
        this.workflowController = workflowController;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);

        initData();
        wireActions();
        refresh();
    }

    private JComponent buildTopBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));

        p.add(new JLabel("Médecin:"));
        p.add(cbMedecin);

        p.add(new JLabel("Date (yyyy-MM-dd):"));
        tfDate.setText(LocalDate.now().toString());
        p.add(tfDate);

        ButtonStyles.primary(btnRefresh);
        p.add(btnRefresh);
        return p;
    }

    private JComponent buildCenter() {
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Rendez-vous"));
        return sp;
    }

    private JComponent buildActions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));

        ButtonStyles.primary(btnAdd);
        p.add(btnAdd);
        ButtonStyles.primary(btnConfirm);
        p.add(btnConfirm);
        ButtonStyles.primary(btnMove);
        p.add(btnMove);
        ButtonStyles.primary(btnCancel);
        p.add(btnCancel);
        ButtonStyles.primary(btnOuvrirConsultation);
        p.add(btnOuvrirConsultation);

        return p;
    }

    private void initData() {
        // medecin list
        cbMedecin.removeAllItems();

        RoleType role = safeRole();
        if (role == RoleType.MEDECIN) {
            // medecin voit seulement lui-même
            Long id = principal.getIdUser();
            cbMedecin.addItem(new MedecinItem(id, "Moi (id=" + id + ")"));
            cbMedecin.setEnabled(false);
        } else {
            // secretaire / admin choisit
            cbMedecin.addItem(new MedecinItem(null, "— Choisir —"));
            for (Medecin m : medecinRepo.findAllOrderByNom()) {
                cbMedecin.addItem(new MedecinItem(m.getIdUser(), labelMedecin(m)));
            }
            cbMedecin.setEnabled(true);
        }

        // build cache labels
        medecinLabelById.clear();
        for (int i = 0; i < cbMedecin.getItemCount(); i++) {
            MedecinItem it = cbMedecin.getItemAt(i);
            if (it != null && it.id != null) medecinLabelById.put(it.id, it.label);
        }

        patientLabelById.clear();
        for (Patient p : patientController.findAll()) {
            patientLabelById.put(p.getIdPatient(), labelPatient(p));
        }

        // permissions buttons
        if (role == RoleType.ADMIN) {
            // admin peut, ou tu peux limiter si ton prof veut
        }
    }

    private void wireActions() {
        btnRefresh.addActionListener(e -> refresh());

        btnAdd.addActionListener(e -> onAdd());
        btnConfirm.addActionListener(e -> onConfirm());
        btnMove.addActionListener(e -> onMove());
        btnCancel.addActionListener(e -> onCancel());
        btnOuvrirConsultation.addActionListener(e -> creerConsultationCompleteDepuisRdv());
    }

    private void refresh() {
        LocalDate date = parseDate(tfDate.getText());
        if (date == null) return;

        MedecinItem m = (MedecinItem) cbMedecin.getSelectedItem();
        Long idMedecin = (m != null ? m.id : null);
        if (idMedecin == null) {
            model.setRows(Collections.emptyList(), patientLabelById, medecinLabelById);
            return;
        }

        List<RDV> rdvs = rdvController.consulterEmploiDuTemps(idMedecin, date);
        model.setRows(rdvs, patientLabelById, medecinLabelById);
    }

    private void onAdd() {
        MedecinItem med = (MedecinItem) cbMedecin.getSelectedItem();
        Long idMedecin = (med != null ? med.id : null);
        if (idMedecin == null) {
            JOptionPane.showMessageDialog(this, "Choisis un médecin.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        JComboBox<PatientItem> cbPatient = new JComboBox<>();
        cbPatient.addItem(new PatientItem(null, "— Choisir —"));
        for (Patient p : patientController.findAll()) {
            cbPatient.addItem(new PatientItem(p.getIdPatient(), labelPatient(p)));
        }

        JTextField fDate = new JTextField(LocalDate.now().toString());
        JTextField fHeure = new JTextField("09:00");
        JTextField fMotif = new JTextField();

        form.add(new JLabel("Patient:"));
        form.add(cbPatient);
        form.add(new JLabel("Date (yyyy-MM-dd):"));
        form.add(fDate);
        form.add(new JLabel("Heure (HH:mm):"));
        form.add(fHeure);
        form.add(new JLabel("Motif:"));
        form.add(fMotif);

        int ok = JOptionPane.showConfirmDialog(this, form, "Nouveau RDV", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        PatientItem pat = (PatientItem) cbPatient.getSelectedItem();
        Long idPatient = (pat != null ? pat.id : null);
        if (idPatient == null) {
            JOptionPane.showMessageDialog(this, "Choisis un patient.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate date = parseDate(fDate.getText());
        if (date == null) return;

        String heure = normalizeTime(fHeure.getText());
        if (heure == null) return;

        String motif = fMotif.getText() == null ? "" : fMotif.getText().trim();

        try {
            rdvController.prendreRendezVous(idPatient, idMedecin, date, heure, motif);
            tfDate.setText(date.toString());
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onConfirm() {
        RDV rdv = getSelected();
        if (rdv == null) return;

        try {
            rdvController.confirmerRendezVous(rdv.getIdRdv());
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onMove() {
        RDV rdv = getSelected();
        if (rdv == null) return;

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField fDate = new JTextField(rdv.getDate() != null ? rdv.getDate().toString() : LocalDate.now().toString());
        JTextField fHeure = new JTextField(rdv.getHeure() != null ? rdv.getHeure().substring(0, 5) : "10:00");
        form.add(new JLabel("Nouvelle date (yyyy-MM-dd):"));
        form.add(fDate);
        form.add(new JLabel("Nouvelle heure (HH:mm):"));
        form.add(fHeure);

        int ok = JOptionPane.showConfirmDialog(this, form, "Déplacer RDV", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        LocalDate nd = parseDate(fDate.getText());
        if (nd == null) return;

        String nh = normalizeTime(fHeure.getText());
        if (nh == null) return;

        try {
            rdvController.deplacerRendezVous(rdv.getIdRdv(), nd, nh);
            tfDate.setText(nd.toString());
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        RDV rdv = getSelected();
        if (rdv == null) return;

        String motif = JOptionPane.showInputDialog(this, "Motif d'annulation:", "Annuler RDV", JOptionPane.QUESTION_MESSAGE);
        if (motif == null) return;

        try {
            rdvController.annulerRendezVous(rdv.getIdRdv(), motif.trim());
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void creerConsultationCompleteDepuisRdv() {
        try {
            Long selectedRdvId = getSelected().getIdRdv();     // depuis la JTable
            Long connectedMedecinId = principal.getIdUser();// ou principal.getId()
            Long idDm = getSelected().getIdDm();               // soit depuis RDV, soit chargé avant
            Long idSituationFinanciere = getSelected().getIdDm();      // SF du patient (ou input)

            List<InterventionInput> list = new ArrayList<>();
            list.add(new InterventionInput(1L, 300.0,12));
            list.add(new InterventionInput(3L, 150.0,21));

            Facture f = workflowController.creerDepuisRdv(
                    selectedRdvId,
                    connectedMedecinId,
                    idDm,
                    idSituationFinanciere,
                    list
            );

            JOptionPane.showMessageDialog(this,
                    "Consultation + Facture créées. Facture #" + f.getIdFacture(),
                    "OK",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private RDV getSelected() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionne un RDV dans la table.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return model.getAt(r);
    }

    private LocalDate parseDate(String s) {
        try {
            return LocalDate.parse(s.trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Date invalide. Exemple: 2026-01-10", "Erreur", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private String normalizeTime(String s) {
        try {
            LocalTime t = LocalTime.parse(s.trim().length() == 5 ? s.trim() : s.trim()); // accept HH:mm or HH:mm:ss
            // stocker en HH:mm:ss pour MySQL TIME
            return String.format("%02d:%02d:%02d", t.getHour(), t.getMinute(), t.getSecond());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Heure invalide. Exemple: 09:30", "Erreur", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private RoleType safeRole() {
        try {
            return principal.getRole();
        } catch (Exception e) {
            return null;
        }
    }

    private String labelPatient(Patient p) {
        String nom = (p.getNom() != null ? p.getNom() : "Patient");
        return nom + " (id=" + p.getIdPatient() + ")";
    }

    private String labelMedecin(Medecin m) {
        String nom = (m.getNom() != null ? m.getNom() : "Médecin");
        return nom + " (id=" + m.getId() + ")";
    }

    // ---------- Combo items ----------
    private record PatientItem(Long id, String label) {
        @Override public String toString() { return label; }
    }

    private record MedecinItem(Long id, String label) {
        @Override public String toString() { return label; }
    }

    // ---------- Table model ----------
    private static class RdvTableModel extends AbstractTableModel {
        private final String[] cols = {"ID", "Date", "Heure", "Patient", "Médecin", "Statut", "Motif"};
        private List<RDV> rows = new ArrayList<>();
        private Map<Long, String> patientLabelById = Map.of();
        private Map<Long, String> medecinLabelById = Map.of();

        public void setRows(List<RDV> rows,
                            Map<Long, String> patientLabelById,
                            Map<Long, String> medecinLabelById) {
            this.rows = (rows == null ? new ArrayList<>() : new ArrayList<>(rows));
            this.patientLabelById = (patientLabelById == null ? Map.of() : patientLabelById);
            this.medecinLabelById = (medecinLabelById == null ? Map.of() : medecinLabelById);
            fireTableDataChanged();
        }

        public RDV getAt(int row) { return rows.get(row); }

        @Override public int getRowCount() { return rows.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int column) { return cols[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            RDV r = rows.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> r.getIdRdv();
                case 1 -> r.getDate();
                case 2 -> r.getHeure();
                case 3 -> patientLabelById.getOrDefault(r.getIdPatient(), "id=" + r.getIdPatient());
                case 4 -> medecinLabelById.getOrDefault(r.getIdMedecin(), "id=" + r.getIdMedecin());
                case 5 -> r.getStatut();
                case 6 -> r.getMotif();
                default -> "";
            };
        }
    }
}
