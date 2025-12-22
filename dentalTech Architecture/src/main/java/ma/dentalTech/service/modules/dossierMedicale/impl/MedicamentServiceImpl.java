package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.MedicamentService;

import java.util.List;

public class MedicamentServiceImpl implements MedicamentService {

    private final MedicamentRepo medicamentRepo;

    public MedicamentServiceImpl(MedicamentRepo medicamentRepo) {
        this.medicamentRepo = medicamentRepo;
    }

    @Override
    public Medicament create(Medicament m) {
        if (m == null) throw new IllegalArgumentException("Medicament null");
        m.setIdMct(null);
        return medicamentRepo.save(m);
    }

    @Override
    public Medicament update(Medicament m) {
        if (m == null || m.getIdMct() == null) throw new IllegalArgumentException("idMct requis");
        getById(m.getIdMct());
        return medicamentRepo.update(m);
    }

    @Override
    public Medicament getById(Long idMct) {
        return medicamentRepo.findById(idMct)
                .orElseThrow(() -> new RuntimeException("Medicament introuvable: " + idMct));
    }

    @Override
    public List<Medicament> getAll() {
        return medicamentRepo.findAll();
    }

    @Override
    public boolean delete(Long idMct) {
        return medicamentRepo.deleteById(idMct);
    }

    @Override
    public List<Medicament> searchByNom(String keyword) {
        return medicamentRepo.findByNomContains(keyword);
    }

    @Override
    public List<Medicament> getByLaboratoire(String laboratoire) {
        return medicamentRepo.findByLaboratoire(laboratoire);
    }

    @Override
    public List<Medicament> getByType(String type) {
        return medicamentRepo.findByType(type);
    }

    @Override
    public List<Medicament> getByRemboursable(boolean remboursable) {
        return medicamentRepo.findByRemboursable(remboursable);
    }
}
