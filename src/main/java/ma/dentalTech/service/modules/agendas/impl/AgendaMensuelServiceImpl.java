package ma.dentalTech.service.modules.agendas.impl;

import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.users.Medecin;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.repository.modules.users.api.MedecinRepository;
import ma.dentalTech.service.modules.agendas.api.AgendaMensuelService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AgendaMensuelServiceImpl implements AgendaMensuelService {

    private final AgendaMensuelRepository agendaRepo;
    private final MedecinRepository medecinRepo;

    public AgendaMensuelServiceImpl(
            AgendaMensuelRepository agendaRepo,
            MedecinRepository medecinRepo) {
        this.agendaRepo = agendaRepo;
        this.medecinRepo = medecinRepo;
    }

    @Override
    public AgendaMensuel planifierAgendaMensuel(long idMedecin, int mois, int annee) throws SQLException {
        // 1. Vérifier si le médecin existe
        Medecin medecin = medecinRepo.findById(idMedecin);
        if (medecin == null) {
            throw new IllegalArgumentException("Médecin non trouvé avec l'ID: " + idMedecin);
        }

        // 2. Vérifier qu'il n'existe pas déjà d'agenda pour cette période
        Optional<AgendaMensuel> agendaExistant = agendaRepo.findByMedecinAndMoisAndAnnee(idMedecin, mois, annee);

        if (agendaExistant.isPresent()) {
            throw new IllegalStateException("Un agenda existe déjà pour ce médecin sur la période " + mois + "/" + annee);
        }

        // 3. Valider la période
        LocalDate maintenant = LocalDate.now();
        if (annee < maintenant.getYear() || (annee == maintenant.getYear() && mois < maintenant.getMonthValue())) {
            throw new IllegalArgumentException("Impossible de planifier un agenda pour une période passée");
        }

        // 4. Créer l'agenda avec les champs disponibles
        AgendaMensuel agenda = AgendaMensuel.builder()
                .mois(mois)
                .annee(annee)
                .idMedecin(idMedecin)
                .joursNonDisponibles(new ArrayList<>())
                .rendezVous(new ArrayList<>())
                .build();

        agendaRepo.create(agenda);
        return agenda;
    }

    @Override
    public Optional<AgendaMensuel> consulterAgendaMensuel(long idAgenda) {
        return Optional.ofNullable(agendaRepo.findById(idAgenda));
    }

    @Override
    public boolean verifierDisponibiliteMedecin(long idMedecin, LocalDate date) {
        // 1. Vérifier si le médecin existe
        Medecin medecin = medecinRepo.findById(idMedecin);
        if (medecin == null) {
            throw new IllegalArgumentException("Médecin non trouvé avec l'ID: " + idMedecin);
        }

        // 2. Vérifier si le médecin a un agenda pour ce mois
        int mois = date.getMonthValue();
        int annee = date.getYear();
        Optional<AgendaMensuel> agendaOpt = agendaRepo.findByMedecinAndMoisAndAnnee(idMedecin, mois, annee);

        if (agendaOpt.isEmpty()) {
            return false; // Pas d'agenda pour cette période
        }

        AgendaMensuel agenda = agendaOpt.get();

        // 3. Vérifier si la date est dans la liste des jours non disponibles
        // Note: Nous utilisons la méthode isJourDisponible() de l'entité
        return agenda.isJourDisponible(date);
    }

    @Override
    public void declarerJourIndisponible(long idMedecin, LocalDate date, String motif) {
        // 1. Vérifier si le médecin existe
        Medecin medecin = medecinRepo.findById(idMedecin);
        if (medecin == null) {
            throw new IllegalArgumentException("Médecin non trouvé avec l'ID: " + idMedecin);
        }

        // 2. Vérifier si c'est une date future
        LocalDate aujourdhui = LocalDate.now();
        if (date.isBefore(aujourdhui)) {
            throw new IllegalArgumentException("Impossible de déclarer un jour passé comme indisponible");
        }

        // 3. Récupérer l'agenda du médecin pour ce mois
        int mois = date.getMonthValue();
        int annee = date.getYear();
        Optional<AgendaMensuel> agendaOpt = agendaRepo.findByMedecinAndMoisAndAnnee(idMedecin, mois, annee);

        if (agendaOpt.isEmpty()) {
            throw new IllegalStateException("Le médecin n'a pas d'agenda planifié pour " + mois + "/" + annee);
        }

        AgendaMensuel agenda = agendaOpt.get();

        // 4. Vérifier si le jour n'est pas déjà déclaré indisponible
        if (!agenda.isJourDisponible(date)) {
            throw new IllegalStateException("Ce jour est déjà déclaré indisponible pour ce médecin");
        }

        // 5. Ajouter le jour non disponible
        agendaRepo.addJourNonDisponible(agenda.getIdAgenda(), date);

        // Note: Le motif n'est pas sauvegardé dans la structure actuelle
        // Vous pourriez ajouter cette fonctionnalité plus tard
    }

    @Override
    public void annulerJourIndisponible(long idMedecin, LocalDate date) {
        // 1. Vérifier si le médecin existe
        Medecin medecin = medecinRepo.findById(idMedecin);
        if (medecin == null) {
            throw new IllegalArgumentException("Médecin non trouvé avec l'ID: " + idMedecin);
        }

        // 2. Récupérer l'agenda du médecin pour ce mois
        int mois = date.getMonthValue();
        int annee = date.getYear();
        Optional<AgendaMensuel> agendaOpt = agendaRepo.findByMedecinAndMoisAndAnnee(idMedecin, mois, annee);

        if (agendaOpt.isEmpty()) {
            throw new IllegalStateException("Le médecin n'a pas d'agenda pour " + mois + "/" + annee);
        }

        AgendaMensuel agenda = agendaOpt.get();

        // 3. Vérifier que la date n'est pas passée
        LocalDate aujourdhui = LocalDate.now();
        if (date.isBefore(aujourdhui)) {
            throw new IllegalStateException("Impossible d'annuler une indisponibilité passée");
        }

        // 4. Vérifier si le jour est dans la liste des jours non disponibles
        List<LocalDate> joursIndispos = agendaRepo.getJoursNonDisponibles(agenda.getIdAgenda());
        if (!joursIndispos.contains(date)) {
            throw new IllegalStateException("Ce jour n'est pas déclaré comme indisponible");
        }
        // 5. Supprimer le jour non disponible
        agendaRepo.removeJourNonDisponible(agenda.getIdAgenda(), date);
    }

    @Override
    public List<LocalDate> listerJoursIndisponibles(long idMedecin, int mois, int annee) {
        // 1. Vérifier si le médecin existe
        Medecin medecin = medecinRepo.findById(idMedecin);
        if (medecin == null) {
            throw new IllegalArgumentException("Médecin non trouvé avec l'ID: " + idMedecin);
        }

        List<LocalDate> resultats = new ArrayList<>();

        if (mois == 0 && annee == 0) {
            // Récupérer tous les agendas du médecin
            List<AgendaMensuel> agendas = agendaRepo.findByMedecin(idMedecin);
            for (AgendaMensuel agenda : agendas) {
                resultats.addAll(agendaRepo.getJoursNonDisponibles(agenda.getIdAgenda()));
            }
        } else {
            // Récupérer l'agenda spécifique
            Optional<AgendaMensuel> agendaOpt = agendaRepo.findByMedecinAndMoisAndAnnee(idMedecin, mois, annee);
            if (agendaOpt.isPresent()) {
                resultats = agendaRepo.getJoursNonDisponibles(agendaOpt.get().getIdAgenda());
            }
        }

        return resultats;
    }

    @Override
    public Optional<AgendaMensuel> rechercherAgendaActif(long idMedecin) {
        // 1. Vérifier si le médecin existe
        Medecin medecin = medecinRepo.findById(idMedecin);
        if (medecin == null) {
            throw new IllegalArgumentException("Médecin non trouvé avec l'ID: " + idMedecin);
        }

        // 2. Récupérer tous les agendas du médecin
        List<AgendaMensuel> agendas = agendaRepo.findByMedecin(idMedecin);

        // 3. Chercher l'agenda du mois courant
        LocalDate aujourdhui = LocalDate.now();
        int moisCourant = aujourdhui.getMonthValue();
        int anneeCourante = aujourdhui.getYear();

        for (AgendaMensuel agenda : agendas) {
            if (agenda.getMois() == moisCourant && agenda.getAnnee() == anneeCourante) {
                return Optional.of(agenda);
            }
        }

        return Optional.empty();
    }

    @Override
    public Long findAgendaId(long idMedecin, int mois, int annee) throws SQLException {
        var opt = agendaRepo.findByMedecinAndMoisAndAnnee(idMedecin, mois, annee);
        return opt.map(a -> a.getIdAgenda()).orElse(null);
    }

}