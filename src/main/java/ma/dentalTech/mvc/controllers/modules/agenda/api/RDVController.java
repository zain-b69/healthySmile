package ma.dentalTech.mvc.controllers.modules.agenda.api;

import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.StatutRDV;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface RDVController {
    RDV prendreRendezVous(long idPatient, long idMedecin, LocalDate date, String heure, String motif) throws SQLException;

    void annulerRendezVous(long idRdv, String motifAnnulation);

    void deplacerRendezVous(long idRdv, LocalDate nouvelleDate, String nouvelleHeure) throws SQLException;

    void confirmerRendezVous(long idRdv);

    void changerStatutRendezVous(long idRdv, StatutRDV statut);

    List<RDV> emploiDuTemps(long idMedecin, LocalDate date);

    List<RDV> rdvFutursPatient(long idPatient);

    List<RDV> consulterEmploiDuTemps(long idMedecin, LocalDate date);
}
