package ma.dentalTech.service.modules.agendas.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.StatutRDV;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.users.Medecin;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.repository.modules.agenda.api.RDVRepository;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.repository.modules.users.api.MedecinRepository;
import ma.dentalTech.service.modules.agendas.api.RDVService;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RDVServiceImpl implements RDVService {

    private final RDVRepository rdvRepo;
    private final AgendaMensuelRepository agendaRepo;
    private final PatientRepository patientRepo;
    private final MedecinRepository medecinRepo;

    public RDVServiceImpl(RDVRepository rdvRepo,
                          AgendaMensuelRepository agendaRepo,
                          PatientRepository patientRepo,
                          MedecinRepository medRepo) {
        this.rdvRepo = rdvRepo;
        this.agendaRepo = agendaRepo;
        this.patientRepo = patientRepo;
        this.medecinRepo = medRepo;
    }

    private Connection cn() throws SQLException {
        return SessionFactory.getInstance().getConnection();
    }

    @Override
    public RDV prendreRendezVous(long idPatient, long idMedecin, LocalDate date, String heure, String motif) throws SQLException {

        if (date == null) throw new IllegalArgumentException("date est null");
        if (heure == null || heure.isBlank()) throw new IllegalArgumentException("heure invalide");
        if (motif == null || motif.isBlank()) throw new IllegalArgumentException("motif invalide");

        // 1) Patient existe ?
        Patient patient = patientRepo.findById(idPatient);
        if (patient == null) throw new IllegalArgumentException("Patient introuvable: " + idPatient);

        // 2) Médecin existe ?
        Medecin medecin = medecinRepo.findById(idMedecin);
        if (medecin == null) throw new IllegalArgumentException("Médecin introuvable: " + idMedecin);

        // 3) date/heure pas dans le passé
        LocalTime h;
        try { h = LocalTime.parse(heure); }
        catch (Exception e) { throw new IllegalArgumentException("Format heure invalide: " + heure); }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dtRdv = LocalDateTime.of(date, h);
        if (dtRdv.isBefore(now)) throw new IllegalArgumentException("RDV dans le passé interdit");

        // 4) Agenda mensuel (obligatoire NOT NULL)
        AgendaMensuel agenda = getOrCreateAgendaMensuel(idMedecin, date);

        // 5) Jour non disponible ?
        if (isJourNonDisponible(agenda.getIdAgenda(), date)) {
            throw new IllegalStateException("Jour non disponible pour ce médecin: " + date);
        }

        // 6) Dossier médical (id_dm obligatoire NOT NULL)
        Long idDm = ensureDossierMedical(idPatient);

        // 7) Créneau libre ?
        if (!verifierDisponibiliteCreneau(idMedecin, date, heure)) {
            throw new IllegalStateException("Créneau non disponible: " + date + " " + heure);
        }

        // 8) Création RDV
        RDV rdv = RDV.builder()
                .idPatient(idPatient)
                .idMedecin(idMedecin)
                .date(date)
                .heure(heure)
                .motif(motif)
                .statut(StatutRDV.EN_ATTENTE)
                .idDm(idDm)
                .agendaMensuelId(agenda.getIdAgenda())
                .build();

        rdvRepo.create(rdv);
        return rdv;
    }

    @Override
    public void annulerRendezVous(long idRdv, String motifAnnulation) {
        RDV rdv = rdvRepo.findById(idRdv);
        if (rdv == null) throw new IllegalArgumentException("RDV introuvable: " + idRdv);

        if (rdv.getStatut() == StatutRDV.ANNULE || rdv.getStatut() == StatutRDV.TERMINE) {
            throw new IllegalStateException("RDV déjà " + rdv.getStatut());
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dt = LocalDateTime.of(rdv.getDate(), LocalTime.parse(rdv.getHeure()));
        if (dt.minusHours(1).isBefore(now)) {
            throw new IllegalStateException("Annulation interdite < 1h avant");
        }

        rdv.setStatut(StatutRDV.ANNULE);
        if (motifAnnulation != null && !motifAnnulation.isBlank()) {
            String old = rdv.getNoteMedecin();
            rdv.setNoteMedecin((old == null ? "" : old + "\n") + "Annulé: " + motifAnnulation);
        }
        rdvRepo.annuler(idRdv);
    }

    @Override
    public void deplacerRendezVous(long idRdv, LocalDate nouvelleDate, String nouvelleHeure) throws SQLException {
        RDV rdv = rdvRepo.findById(idRdv);
        if (rdv == null) throw new IllegalArgumentException("RDV introuvable: " + idRdv);

        if (rdv.getStatut() == StatutRDV.ANNULE || rdv.getStatut() == StatutRDV.TERMINE) {
            throw new IllegalStateException("Impossible de déplacer un RDV " + rdv.getStatut());
        }

        LocalTime h;
        try { h = LocalTime.parse(nouvelleHeure); }
        catch (Exception e) { throw new IllegalArgumentException("Format heure invalide: " + nouvelleHeure); }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dt = LocalDateTime.of(nouvelleDate, h);
        if (dt.isBefore(now)) throw new IllegalArgumentException("Déplacement vers le passé interdit");

        // agenda (si mois/année changent, il faut recalculer agenda_mensuel_id)
        AgendaMensuel agenda = getOrCreateAgendaMensuel(rdv.getIdMedecin(), nouvelleDate);

        if (isJourNonDisponible(agenda.getIdAgenda(), nouvelleDate)) {
            throw new IllegalStateException("Jour non disponible: " + nouvelleDate);
        }

        if (!verifierDisponibiliteCreneau(rdv.getIdMedecin(), nouvelleDate, nouvelleHeure)) {
            throw new IllegalStateException("Créneau non disponible");
        }

        rdv.setDate(nouvelleDate);
        rdv.setHeure(nouvelleHeure);
        rdv.setAgendaMensuelId(agenda.getIdAgenda());
        rdv.setStatut(StatutRDV.EN_ATTENTE);

        rdvRepo.update(rdv);
    }

    @Override
    public void confirmerRendezVous(long idRdv) {
        RDV rdv = rdvRepo.findById(idRdv);
        if (rdv == null) throw new IllegalArgumentException("RDV introuvable: " + idRdv);

        if (rdv.getStatut() != StatutRDV.EN_ATTENTE && rdv.getStatut() != StatutRDV.PLANIFIE) {
            throw new IllegalStateException("Seuls EN_ATTENTE/PLANIFIE peuvent être confirmés");
        }

        rdv.setStatut(StatutRDV.CONFIRME);
        rdvRepo.confirmer(idRdv);
    }

    @Override
    public List<RDV> consulterEmploiDuTemps(long idMedecin, LocalDate date) {
        Medecin medecin = medecinRepo.findById(idMedecin);
        if (medecin == null) throw new IllegalArgumentException("Médecin introuvable: " + idMedecin);
        return rdvRepo.findByMedecinAndDate(idMedecin, date);
    }

    @Override
    public boolean verifierDisponibiliteCreneau(long idMedecin, LocalDate date, String heure) throws SQLException {
        // médecin existe ?
        Medecin medecin = medecinRepo.findById(idMedecin);
        if (medecin == null) throw new IllegalArgumentException("Médecin introuvable: " + idMedecin);

        // heure valide ?
        LocalTime.parse(heure);

        // agenda/jour dispo ?
        AgendaMensuel agenda = getOrCreateAgendaMensuel(idMedecin, date);
        if (isJourNonDisponible(agenda.getIdAgenda(), date)) return false;

        // créneau déjà pris ?
        List<RDV> rdvs = rdvRepo.findByMedecinAndDate(idMedecin, date);
        for (RDV r : rdvs) {
            if (heure.equals(r.getHeure()) && r.getStatut() != StatutRDV.ANNULE) return false;
        }
        return true;
    }

    @Override
    public void changerStatutRendezVous(long idRdv, StatutRDV nouveauStatut) {
        RDV rdv = rdvRepo.findById(idRdv);
        if (rdv == null) throw new IllegalArgumentException("RDV introuvable: " + idRdv);

        StatutRDV old = rdv.getStatut();
        if (old == StatutRDV.ANNULE && nouveauStatut != StatutRDV.ANNULE)
            throw new IllegalStateException("RDV annulé ne change plus");
        if (old == StatutRDV.TERMINE && nouveauStatut != StatutRDV.TERMINE)
            throw new IllegalStateException("RDV terminé ne change plus");

        rdv.setStatut(nouveauStatut);
        rdvRepo.update(rdv);
    }

    @Override
    public List<RDV> rechercherRendezVousFuturs(long idPatient) {
        Patient patient = patientRepo.findById(idPatient);
        if (patient == null) throw new IllegalArgumentException("Patient introuvable: " + idPatient);

        List<RDV> all = rdvRepo.findByPatient(idPatient);
        List<RDV> out = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (RDV r : all) {
            if ((r.getDate().isAfter(today) || r.getDate().isEqual(today))
                    && r.getStatut() != StatutRDV.ANNULE
                    && r.getStatut() != StatutRDV.TERMINE) {
                out.add(r);
            }
        }
        out.sort((a, b) -> {
            int c = a.getDate().compareTo(b.getDate());
            if (c != 0) return c;
            return LocalTime.parse(a.getHeure()).compareTo(LocalTime.parse(b.getHeure()));
        });
        return out;
    }

    // ----------------- Helpers BD -----------------

    private AgendaMensuel getOrCreateAgendaMensuel(long idMedecin, LocalDate date) throws SQLException {
        int mois = date.getMonthValue();
        int annee = date.getYear();

        // 1) chercher
        var opt = agendaRepo.findByMedecinAndMoisAndAnnee(idMedecin, mois, annee);
        if (opt.isPresent()) return opt.get();

        // 2) créer
        AgendaMensuel a = AgendaMensuel.builder()
                .idMedecin(idMedecin)
                .mois(mois)
                .annee(annee)
                .build();

        agendaRepo.create(a);

        // 3) recharger pour récupérer l'id
        return agendaRepo.findByMedecinAndMoisAndAnnee(idMedecin, mois, annee)
                .orElseThrow(() -> new IllegalStateException("Agenda créé mais introuvable"));
    }


    private boolean isJourNonDisponible(Long agendaId, LocalDate date) {
        if (agendaId == null) return false;
        List<LocalDate> jours = agendaRepo.getJoursNonDisponibles(agendaId);
        return jours != null && jours.contains(date);
    }

    private Long ensureDossierMedical(long idPatient) throws SQLException {
        // 1) chercher
        String sel = "SELECT id_dm FROM dossier_medicale WHERE id_patient=? LIMIT 1";
        try (Connection c = cn(); PreparedStatement ps = c.prepareStatement(sel)) {
            ps.setLong(1, idPatient);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }

        // 2) créer si absent
        String ins = "INSERT INTO dossier_medicale (id_patient, date_de_creation) VALUES (?, CURRENT_DATE)";
        try (Connection c = cn(); PreparedStatement ps = c.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, idPatient);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        throw new SQLException("Impossible de créer dossier_medicale pour patient=" + idPatient);
    }


}
