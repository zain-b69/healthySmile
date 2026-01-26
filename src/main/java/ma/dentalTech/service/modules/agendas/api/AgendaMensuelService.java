package ma.dentalTech.service.modules.agendas.api;

import ma.dentalTech.entities.agenda.AgendaMensuel;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des agendas mensuels des médecins.
 */
public interface AgendaMensuelService {

    /**
     * Planifie un nouvel agenda mensuel pour un médecin.
     */
    AgendaMensuel planifierAgendaMensuel(long idMedecin, int mois, int annee) throws SQLException;

    /**
     * Consulte l'agenda d'un médecin pour une période donnée.
     */
    Optional<AgendaMensuel> consulterAgendaMensuel(long idAgenda);

    /**
     * Vérifie si un médecin est disponible à une date spécifique.
     */
    boolean verifierDisponibiliteMedecin(long idMedecin, LocalDate date);

    /**
     * Déclare un jour comme non disponible pour un médecin.
     */
    void declarerJourIndisponible(long idMedecin, LocalDate date, String motif);

    /**
     * Annule la déclaration d'indisponibilité pour un jour donné.
     */
    void annulerJourIndisponible(long idMedecin, LocalDate date);

    /**
     * Liste tous les jours non disponibles d'un médecin.
     */
    List<LocalDate> listerJoursIndisponibles(long idMedecin, int mois, int annee);

    /**
     * Recherche l'agenda actif d'un médecin.
     */
    Optional<AgendaMensuel> rechercherAgendaActif(long idMedecin);

    Long findAgendaId(long idMedecin, int mois, int annee) throws SQLException;
}