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
        return repo.findById(id);
    }

    @Override
    public void create(Patient patient) throws SQLException {
        if (patient == null) throw new IllegalArgumentException("patient is null");
        repo.create(patient);
    }

    @Override
    public void update(Patient patient) {
        if (patient == null) throw new IllegalArgumentException("patient is null");
        if (patient.getIdPatient() == null) throw new IllegalArgumentException("patient.idPatient is null");
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

    // ---- extra ----

    @Override
    public Optional<Patient> findByEmail(String email) {
        if (email == null || email.isBlank()) return Optional.empty();
        return repo.findByEmail(email);
    }

    @Override
    public Optional<Patient> findByTelephone(String telephone) {
        if (telephone == null || telephone.isBlank()) return Optional.empty();
        return repo.findByTelephone(telephone);
    }

    @Override
    public List<Patient> searchByNomPrenom(String keyword) {
        if (keyword == null) keyword = "";
        return repo.searchByNomPrenom(keyword);
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;
        return repo.existsById(id);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public List<Patient> findPage(int limit, int offset) {
        return repo.findPage(limit, offset);
    }

    // ---- Many-to-Many ----

    @Override
    public void addAntecedentToPatient(Long patientId, Long antecedentId) {
        repo.addAntecedentToPatient(patientId, antecedentId);
    }

    @Override
    public void removeAntecedentFromPatient(Long patientId, Long antecedentId) {
        repo.removeAntecedentFromPatient(patientId, antecedentId);
    }

    @Override
    public void removeAllAntecedentsFromPatient(Long patientId) {
        repo.removeAllAntecedentsFromPatient(patientId);
    }

    @Override
    public List<Antecedent> getAntecedentsOfPatient(Long patientId) {
        return repo.getAntecedentsOfPatient(patientId);
    }

    @Override
    public List<Patient> getPatientsByAntecedent(Long antecedentId) {
        return repo.getPatientsByAntecedent(antecedentId);
    }
}
