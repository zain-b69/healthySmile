package ma.dentalTech.service.modules.cabinet.impl;

import ma.dentalTech.service.modules.cabinet.api.ParametrageService;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ParametrageServiceImpl implements ParametrageService {

    // Configuration par défaut
    private static final int DUREE_STANDARD_RDV_DEFAUT = 30; // 30 minutes
    private static final int LIMITE_RDV_QUOTIDIENNE_DEFAUT = 20;
    private static final int DELAI_RAPPEL_RDV_DEFAUT = 24; // 24 heures avant

    // Jours de la semaine
    private static final List<String> JOURS_SEMAINE = Arrays.asList(
            "LUNDI", "MARDI", "MERCREDI", "JEUDI", "Vendredi", "Samedi", "DIMANCHE"
    );

    // Horaires par défaut (8h-18h du lundi au vendredi, 9h-13h samedi, fermé dimanche)
    private static final Map<String, HorairesJour> HORAIRES_DEFAUT = new HashMap<>();
    static {
        HORAIRES_DEFAUT.put("LUNDI", new HorairesJour("LUNDI", LocalTime.of(8, 0), LocalTime.of(18, 0)));
        HORAIRES_DEFAUT.put("MARDI", new HorairesJour("MARDI", LocalTime.of(8, 0), LocalTime.of(18, 0)));
        HORAIRES_DEFAUT.put("MERCREDI", new HorairesJour("MERCREDI", LocalTime.of(8, 0), LocalTime.of(18, 0)));
        HORAIRES_DEFAUT.put("JEUDI", new HorairesJour("JEUDI", LocalTime.of(8, 0), LocalTime.of(18, 0)));
        HORAIRES_DEFAUT.put("Vendredi", new HorairesJour("Vendredi", LocalTime.of(8, 0), LocalTime.of(18, 0)));
        HORAIRES_DEFAUT.put("Samedi", new HorairesJour("Samedi", LocalTime.of(9, 0), LocalTime.of(13, 0)));
        HORAIRES_DEFAUT.put("DIMANCHE", null); // Fermé
    }

    // Stockage des configurations (en mémoire pour cet exemple)
    private final Map<String, HorairesJour> horairesConfigures = new ConcurrentHashMap<>();
    private int dureeStandardRDV = DUREE_STANDARD_RDV_DEFAUT;
    private int limiteRDVQuotidienne = LIMITE_RDV_QUOTIDIENNE_DEFAUT;
    private boolean priseRDVEnLigne = true;
    private int delaiRappelRDV = DELAI_RAPPEL_RDV_DEFAUT;

    public ParametrageServiceImpl() {
        // Initialiser avec les horaires par défaut
        reinitialiserTousLesHoraires();
    }

    @Override
    public void configurerHorairesCabinet(String jour, LocalTime heureOuverture, LocalTime heureFermeture) {
        // 1. Validation du jour
        String jourNormalise = normaliserJour(jour);
        if (!JOURS_SEMAINE.contains(jourNormalise)) {
            throw new IllegalArgumentException("Jour invalide: " + jour + ". Jours valides: " + JOURS_SEMAINE);
        }

        // 2. Validation des heures
        if (heureOuverture == null || heureFermeture == null) {
            throw new IllegalArgumentException("Les heures d'ouverture et fermeture sont requises");
        }

        if (heureOuverture.isAfter(heureFermeture) || heureOuverture.equals(heureFermeture)) {
            throw new IllegalArgumentException("L'heure d'ouverture doit être avant l'heure de fermeture");
        }

        // 3. Validation des plages horaires raisonnables
        if (heureOuverture.isBefore(LocalTime.of(5, 0))) {
            throw new IllegalArgumentException("L'heure d'ouverture ne peut pas être avant 5h00");
        }

        if (heureFermeture.isAfter(LocalTime.of(23, 0))) {
            throw new IllegalArgumentException("L'heure de fermeture ne peut pas être après 23h00");
        }

        // 4. Validation de la durée d'ouverture
        long dureeMinutes = java.time.Duration.between(heureOuverture, heureFermeture).toMinutes();
        if (dureeMinutes < 60) {
            throw new IllegalArgumentException("Le cabinet doit être ouvert au moins 1 heure");
        }

        if (dureeMinutes > 720) {
            throw new IllegalArgumentException("Le cabinet ne peut pas être ouvert plus de 12 heures");
        }

        // 5. Configurer les horaires
        horairesConfigures.put(jourNormalise, new HorairesJour(jourNormalise, heureOuverture, heureFermeture));
    }

    @Override
    public void reinitialiserHorairesJour(String jour) {
        String jourNormalise = normaliserJour(jour);

        if (!JOURS_SEMAINE.contains(jourNormalise)) {
            throw new IllegalArgumentException("Jour invalide: " + jour);
        }

        // Rétablir les horaires par défaut pour ce jour
        horairesConfigures.put(jourNormalise, HORAIRES_DEFAUT.get(jourNormalise));
    }

    @Override
    public Optional<HorairesJour> consulterHorairesJour(String jour) {
        String jourNormalise = normaliserJour(jour);

        if (!JOURS_SEMAINE.contains(jourNormalise)) {
            return Optional.empty();
        }

        return Optional.ofNullable(horairesConfigures.get(jourNormalise));
    }

    @Override
    public boolean verifierCabinetOuvert(String jour, LocalTime heure) {
        // 1. Normaliser le jour
        String jourNormalise = normaliserJour(jour);

        // 2. Récupérer les horaires du jour
        HorairesJour horaires = horairesConfigures.get(jourNormalise);

        // 3. Vérifier si fermé (horaires == null)
        if (horaires == null) {
            return false;
        }

        // 4. Vérifier si l'heure est dans la plage horaire
        return !heure.isBefore(horaires.getHeureOuverture()) &&
                !heure.isAfter(horaires.getHeureFermeture());
    }

    @Override
    public void definirDureeStandardRendezVous(int dureeMinutes) {
        // Validation
        if (dureeMinutes < 15) {
            throw new IllegalArgumentException("La durée minimum d'un RDV est de 15 minutes");
        }

        if (dureeMinutes > 120) {
            throw new IllegalArgumentException("La durée maximum d'un RDV est de 120 minutes");
        }

        // La durée doit être un multiple de 5 minutes pour faciliter la planification
        if (dureeMinutes % 5 != 0) {
            throw new IllegalArgumentException("La durée doit être un multiple de 5 minutes");
        }

        this.dureeStandardRDV = dureeMinutes;
    }

    @Override
    public int consulterDureeStandardRendezVous() {
        return dureeStandardRDV;
    }

    @Override
    public void definirLimiteRendezVousQuotidienne(int limite) {
        // Validation
        if (limite < 1) {
            throw new IllegalArgumentException("La limite doit être au moins de 1 RDV par jour");
        }

        if (limite > 50) {
            throw new IllegalArgumentException("La limite maximum est de 50 RDV par jour");
        }

        this.limiteRDVQuotidienne = limite;
    }

    @Override
    public int consulterLimiteRendezVousQuotidienne() {
        return limiteRDVQuotidienne;
    }

    @Override
    public void configurerPriseRDVEnLigne(boolean active) {
        this.priseRDVEnLigne = active;
    }

    @Override
    public boolean verifierPriseRDVEnLigneActive() {
        return priseRDVEnLigne;
    }

    @Override
    public void configurerDelaiRappelRDV(int heuresAvant) {
        // Validation
        if (heuresAvant < 1) {
            throw new IllegalArgumentException("Le délai minimum de rappel est de 1 heure");
        }

        if (heuresAvant > 168) { // 1 semaine
            throw new IllegalArgumentException("Le délai maximum de rappel est de 168 heures (1 semaine)");
        }

        this.delaiRappelRDV = heuresAvant;
    }

    @Override
    public int consulterDelaiRappelRDV() {
        return delaiRappelRDV;
    }

    @Override
    public boolean validerConfigurationHoraires() {
        // 1. Vérifier que tous les jours ont des horaires configurés
        for (String jour : JOURS_SEMAINE) {
            if (!horairesConfigures.containsKey(jour)) {
                return false;
            }
        }

        // 2. Vérifier la cohérence des plages horaires
        for (HorairesJour horaires : horairesConfigures.values()) {
            if (horaires != null) {
                // Vérifier que l'heure de fermeture est après l'heure d'ouverture
                if (!horaires.getHeureFermeture().isAfter(horaires.getHeureOuverture())) {
                    return false;
                }

                // Vérifier que la durée est raisonnable (au moins 1 heure, max 12 heures)
                long dureeMinutes = java.time.Duration.between(
                        horaires.getHeureOuverture(), horaires.getHeureFermeture()
                ).toMinutes();

                if (dureeMinutes < 60 || dureeMinutes > 720) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ConfigurationCabinet exporterConfiguration() {
        ConfigurationCabinet config = new ConfigurationCabinet();

        // Récupérer tous les horaires
        List<HorairesJour> horairesList = new ArrayList<>();
        for (String jour : JOURS_SEMAINE) {
            HorairesJour horaires = horairesConfigures.get(jour);
            if (horaires != null) {
                horairesList.add(horaires);
            }
        }
        config.setHoraires(horairesList);

        // Autres paramètres
        config.setDureeStandardRDV(dureeStandardRDV);
        config.setLimiteRDVQuotidienne(limiteRDVQuotidienne);
        config.setPriseRDVEnLigne(priseRDVEnLigne);
        config.setDelaiRappelRDV(delaiRappelRDV);

        return config;
    }

    @Override
    public void importerConfiguration(ConfigurationCabinet configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("La configuration ne peut pas être nulle");
        }

        // 1. Importer les horaires
        if (configuration.getHoraires() != null) {
            for (HorairesJour horaires : configuration.getHoraires()) {
                configurerHorairesCabinet(horaires.getJour(),
                        horaires.getHeureOuverture(),
                        horaires.getHeureFermeture());
            }
        }

        // 2. Importer les autres paramètres
        if (configuration.getDureeStandardRDV() > 0) {
            definirDureeStandardRendezVous(configuration.getDureeStandardRDV());
        }

        if (configuration.getLimiteRDVQuotidienne() > 0) {
            definirLimiteRendezVousQuotidienne(configuration.getLimiteRDVQuotidienne());
        }

        configurerPriseRDVEnLigne(configuration.isPriseRDVEnLigne());

        if (configuration.getDelaiRappelRDV() > 0) {
            configurerDelaiRappelRDV(configuration.getDelaiRappelRDV());
        }
    }

    // Méthodes utilitaires supplémentaires

    public void reinitialiserTousLesHoraires() {
        horairesConfigures.clear();
        horairesConfigures.putAll(HORAIRES_DEFAUT);
    }

    public void reinitialiserTousLesParametres() {
        reinitialiserTousLesHoraires();
        dureeStandardRDV = DUREE_STANDARD_RDV_DEFAUT;
        limiteRDVQuotidienne = LIMITE_RDV_QUOTIDIENNE_DEFAUT;
        priseRDVEnLigne = true;
        delaiRappelRDV = DELAI_RAPPEL_RDV_DEFAUT;
    }

    public Map<String, HorairesJour> consulterTousLesHoraires() {
        return new HashMap<>(horairesConfigures);
    }

    public List<String> getJoursOuverture() {
        List<String> joursOuverts = new ArrayList<>();
        for (String jour : JOURS_SEMAINE) {
            if (horairesConfigures.get(jour) != null) {
                joursOuverts.add(jour);
            }
        }
        return joursOuverts;
    }

    public boolean isFerme(String jour) {
        String jourNormalise = normaliserJour(jour);
        return horairesConfigures.get(jourNormalise) == null;
    }

    public LocalTime getHeurePremierRdv(String jour) {
        String jourNormalise = normaliserJour(jour);
        HorairesJour horaires = horairesConfigures.get(jourNormalise);

        if (horaires == null) {
            throw new IllegalStateException("Le cabinet est fermé le " + jour);
        }

        // Premier RDV possible 30 minutes après l'ouverture
        return horaires.getHeureOuverture().plusMinutes(30);
    }

    public LocalTime getHeureDernierRdv(String jour) {
        String jourNormalise = normaliserJour(jour);
        HorairesJour horaires = horairesConfigures.get(jourNormalise);

        if (horaires == null) {
            throw new IllegalStateException("Le cabinet est fermé le " + jour);
        }

        // Dernier RDV possible 1 heure avant la fermeture
        return horaires.getHeureFermeture().minusHours(1);
    }

    // Méthode utilitaire pour normaliser les noms de jours
    private String normaliserJour(String jour) {
        if (jour == null) {
            throw new IllegalArgumentException("Le jour ne peut pas être nul");
        }

        String jourNormalise = jour.trim().toUpperCase();

        // Gérer les variations de casse
        Map<String, String> corrections = new HashMap<>();
        corrections.put("VENDREDI", "Vendredi");
        corrections.put("SAMEDI", "Samedi");

        return corrections.getOrDefault(jourNormalise, jourNormalise);
    }
}