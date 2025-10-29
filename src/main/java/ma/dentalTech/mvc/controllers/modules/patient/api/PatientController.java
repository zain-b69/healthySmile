package ma.dentalTech.mvc.controllers.modules.patient.api;

public interface PatientController
{

    /**
     * Méthode qui affiche les patients ajoutés aujourd'hui au système
     * trié par date d'ajout au système
     * Chaque patient est affiché seulement avec son nom complet, son âge et sa date d'ajout formatée (selon un PatientDTO)
     */
    void showRecentPatients();

}
