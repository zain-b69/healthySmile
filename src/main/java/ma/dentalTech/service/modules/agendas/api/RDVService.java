package ma.dentalTech.service.modules.agendas.api;

import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.StatutRDV;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface RDVService {
    RDV prendreRendezVous(long idPatient, long idMedecin, LocalDate date, String heure, String motif) throws SQLException;

    void annulerRendezVous(long idRdv, String motifAnnulation);

    void deplacerRendezVous(long idRdv, LocalDate nouvelleDate, String nouvelleHeure) throws SQLException;

    void confirmerRendezVous(long idRdv);

    List<RDV> consulterEmploiDuTemps(long idMedecin, LocalDate date);

    boolean verifierDisponibiliteCreneau(long idMedecin, LocalDate date, String heure) throws SQLException;

    void changerStatutRendezVous(long idRdv, StatutRDV nouveauStatut);

    List<RDV> rechercherRendezVousFuturs(long idPatient);
}
