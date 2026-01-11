package ma.dentalTech.mvc.controllers.modules.agenda.impl;

import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.StatutRDV;
import ma.dentalTech.mvc.controllers.modules.agenda.api.RDVController;
import ma.dentalTech.service.modules.agendas.api.RDVService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RDVControllerImpl implements RDVController {

    private final RDVService service;

    public RDVControllerImpl(RDVService service) {
        this.service = service;
    }

    @Override
    public RDV prendreRendezVous(long idPatient, long idMedecin, LocalDate date, String heure, String motif) throws SQLException {
        return service.prendreRendezVous(idPatient, idMedecin, date, heure, motif);
    }

    @Override
    public void annulerRendezVous(long idRdv, String motifAnnulation) {
        service.annulerRendezVous(idRdv, motifAnnulation);
    }

    @Override
    public void deplacerRendezVous(long idRdv, LocalDate nouvelleDate, String nouvelleHeure) throws SQLException {
        service.deplacerRendezVous(idRdv, nouvelleDate, nouvelleHeure);
    }

    @Override
    public void confirmerRendezVous(long idRdv) {
        service.confirmerRendezVous(idRdv);
    }

    @Override
    public void changerStatutRendezVous(long idRdv, StatutRDV statut) {
        service.changerStatutRendezVous(idRdv, statut);
    }

    @Override
    public List<RDV> emploiDuTemps(long idMedecin, LocalDate date) {
        return service.consulterEmploiDuTemps(idMedecin, date);
    }

    @Override
    public List<RDV> rdvFutursPatient(long idPatient) {
        return service.rechercherRendezVousFuturs(idPatient);
    }

    @Override
    public List<RDV> consulterEmploiDuTemps(long idMedecin, LocalDate date) {
        return service.consulterEmploiDuTemps(idMedecin, date);
    }

}
