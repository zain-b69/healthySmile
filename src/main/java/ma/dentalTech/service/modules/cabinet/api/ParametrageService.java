package ma.dentalTech.service.modules.cabinet.api;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des paramètres du cabinet.
 * Définit les règles métier pour les horaires, durées, et configurations.
 */
public interface ParametrageService {

    /**
     * Configure les horaires d'ouverture du cabinet pour un jour de la semaine.
     */
    void configurerHorairesCabinet(String jour, LocalTime heureOuverture, LocalTime heureFermeture);

    /**
     * Rétablit les horaires par défaut pour un jour spécifique.
     */
    void reinitialiserHorairesJour(String jour);

    /**
     * Consulte les horaires configurés pour un jour.
     */
    Optional<HorairesJour> consulterHorairesJour(String jour);

    /**
     * Vérifie si le cabinet est ouvert à un moment donné (jour + heure).
     */
    boolean verifierCabinetOuvert(String jour, LocalTime heure);

    /**
     * Configure la durée standard d'un rendez-vous.
     */
    void definirDureeStandardRendezVous(int dureeMinutes);

    /**
     * Consulte la durée standard d'un rendez-vous.
     */
    int consulterDureeStandardRendezVous();

    /**
     * Définit le nombre maximum de rendez-vous par jour pour un médecin.
     */
    void definirLimiteRendezVousQuotidienne(int limite);

    /**
     * Consulte la limite de rendez-vous par jour.
     */
    int consulterLimiteRendezVousQuotidienne();

    /**
     * Active ou désactive la prise de rendez-vous en ligne.
     */
    void configurerPriseRDVEnLigne(boolean active);

    /**
     * Vérifie si la prise de rendez-vous en ligne est active.
     */
    boolean verifierPriseRDVEnLigneActive();

    /**
     * Définit la période de rappel pour les rendez-vous (en heures).
     */
    void configurerDelaiRappelRDV(int heuresAvant);

    /**
     * Consulte le délai de rappel des rendez-vous.
     */
    int consulterDelaiRappelRDV();

    /**
     * Valide la configuration des horaires (cohérence entre les jours).
     */
    boolean validerConfigurationHoraires();

    /**
     * Exporte la configuration complète du cabinet.
     */
    ConfigurationCabinet exporterConfiguration();

    /**
     * Importe une configuration à partir d'un modèle.
     */
    void importerConfiguration(ConfigurationCabinet configuration);

    /**
     * Représentation des horaires d'un jour.
     */
    class HorairesJour {
        private final String jour;
        private final LocalTime heureOuverture;
        private final LocalTime heureFermeture;

        public HorairesJour(String jour, LocalTime heureOuverture, LocalTime heureFermeture) {
            this.jour = jour;
            this.heureOuverture = heureOuverture;
            this.heureFermeture = heureFermeture;
        }

        public String getJour() {
            return jour;
        }

        public LocalTime getHeureOuverture() {
            return heureOuverture;
        }

        public LocalTime getHeureFermeture() {
            return heureFermeture;
        }
    }

    /**
     * Représentation de la configuration complète du cabinet.
     */
    class ConfigurationCabinet {
        private List<HorairesJour> horaires;
        private int dureeStandardRDV;
        private int limiteRDVQuotidienne;
        private boolean priseRDVEnLigne;
        private int delaiRappelRDV;

        // Getters and setters
        public List<HorairesJour> getHoraires() { return horaires; }
        public void setHoraires(List<HorairesJour> horaires) { this.horaires = horaires; }

        public int getDureeStandardRDV() { return dureeStandardRDV; }
        public void setDureeStandardRDV(int dureeStandardRDV) { this.dureeStandardRDV = dureeStandardRDV; }

        public int getLimiteRDVQuotidienne() { return limiteRDVQuotidienne; }
        public void setLimiteRDVQuotidienne(int limiteRDVQuotidienne) { this.limiteRDVQuotidienne = limiteRDVQuotidienne; }

        public boolean isPriseRDVEnLigne() { return priseRDVEnLigne; }
        public void setPriseRDVEnLigne(boolean priseRDVEnLigne) { this.priseRDVEnLigne = priseRDVEnLigne; }

        public int getDelaiRappelRDV() { return delaiRappelRDV; }
        public void setDelaiRappelRDV(int delaiRappelRDV) { this.delaiRappelRDV = delaiRappelRDV; }
    }
}