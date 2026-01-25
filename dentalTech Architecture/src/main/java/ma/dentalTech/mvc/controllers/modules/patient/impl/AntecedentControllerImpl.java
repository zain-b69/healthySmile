package ma.dentalTech.mvc.controllers.modules.patient.impl;

import ma.dentalTech.entities.enums.CategorieAntecedent;
import ma.dentalTech.entities.enums.NiveauRisque;
import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentController;
import ma.dentalTech.service.modules.patient.api.AntecedentService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AntecedentControllerImpl implements AntecedentController {

    private final AntecedentService service;

    public AntecedentControllerImpl(AntecedentService service) {
        this.service = service;
    }

    @Override public List<Antecedent> findAll() { return service.findAll(); }
    @Override public Antecedent findById(Long id) { return service.findById(id); }

    @Override public void create(Antecedent a) throws SQLException { service.create(a); }
    @Override public void update(Antecedent a) { service.update(a); }
    @Override public void deleteById(Long id) { service.deleteById(id); }

    @Override public Optional<Antecedent> findByNom(String nom) { return service.findByNom(nom); }
    @Override public List<Antecedent> findByCategorie(CategorieAntecedent categorie) { return service.findByCategorie(categorie); }
    @Override public List<Antecedent> findByNiveauRisque(NiveauRisque niveau) { return service.findByNiveauRisque(niveau); }

    @Override public List<Patient> getPatientsHavingAntecedent(Long antecedentId) {
        return service.getPatientsHavingAntecedent(antecedentId);
    }
}
