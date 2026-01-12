package ma.dentalTech.mvc.controllers.modules.patient.impl;

import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.service.modules.patient.api.PatientService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PatientControllerImpl implements PatientController {

    private final PatientService service;

    public PatientControllerImpl(PatientService service) {
        this.service = service;
    }

    @Override
    public List<Patient> findAll() {
        return service.findAll();
    }

    @Override
    public Patient findById(Long id) {
        return service.findById(id);
    }

    @Override
    public void create(Patient patient) throws SQLException {
        service.create(patient);
    }

    @Override
    public void update(Patient patient) {
        service.update(patient);
    }

    @Override
    public void deleteById(Long id) {
        service.deleteById(id);
    }

    @Override
    public Optional<Patient> findByEmail(String email) {
        return service.findByEmail(email);
    }

    @Override
    public Optional<Patient> findByTelephone(String telephone) {
        return service.findByTelephone(telephone);
    }

    @Override
    public List<Patient> searchByNomPrenom(String keyword) {
        return service.searchByNomPrenom(keyword);
    }
}
