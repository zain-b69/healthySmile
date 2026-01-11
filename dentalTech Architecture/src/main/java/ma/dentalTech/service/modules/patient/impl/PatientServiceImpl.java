package ma.dentalTech.service.modules.patient.impl;

import ma.dentalTech.entities.patient.Antecedent;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.service.modules.patient.api.PatientService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PatientServiceImpl implements PatientService {

    private final PatientRepository repo;

    public PatientServiceImpl(PatientRepository repo) {
        this.repo = repo;
    }

    // ---- CRUD ----

    @Override
    public List<Patient> findAll() {
        return repo.findAll();
    }

    @Override
    public Patient findById(Long id) {
        if (id == null) throw new IllegalArgumentException("id is null");
        return repo.findById(id);
    }

    @Override
    public void create(Patient patient) throws SQLException {
        repo.create(patient);
    }

    @Override
    public void update(Patient patient) {
        repo.update(patient);
    }

    @Override
    public void delete(Patient patient) {
        repo.delete(patient);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public Optional<Patient> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    @Override
    public Optional<Patient> findByTelephone(String telephone) {
        return repo.findByTelephone(telephone);
    }

    @Override
    public List<Patient> searchByNomPrenom(String keyword) {
        return repo.searchByNomPrenom(keyword);
    }

    @Override
    public boolean existsById(Long id) {
        return repo.existsById(id);
    }

    @Override
    public long count() {
        return  repo.count();
    }

    @Override
    public List<Patient> findPage(int limit, int offset) {
        return repo.findPage(limit, offset);
    }

    @Override
    public void addAntecedentToPatient(Long patientId, Long antecedentId) {
        if (patientId == null) throw new IllegalArgumentException("patientId is null");
        if (antecedentId == null) throw new IllegalArgumentException("antecedentId is null");
        repo.addAntecedentToPatient(patientId, antecedentId);
    }

    @Override
    public void removeAntecedentFromPatient(Long patientId, Long antecedentId) {
        if (patientId == null) throw new IllegalArgumentException("patientId is null");
        if (antecedentId == null) throw new IllegalArgumentException("antecedentId is null");
        repo.removeAntecedentFromPatient(patientId, antecedentId);
    }

    @Override
    public void removeAllAntecedentsFromPatient(Long patientId) {
        if (patientId == null) throw new IllegalArgumentException("patientId is null");
        repo.removeAllAntecedentsFromPatient(patientId);
    }

    @Override
    public List<Antecedent> getAntecedentsOfPatient(Long patientId) {
        if (patientId == null) throw new IllegalArgumentException("patientId is null");
        return repo.getAntecedentsOfPatient(patientId);
    }

    @Override
    public List<Patient> getPatientsByAntecedent(Long antecedentId) {
        if (antecedentId == null) throw new IllegalArgumentException("antecedentId is null");
        return repo.getPatientsByAntecedent(antecedentId);
    }
}
