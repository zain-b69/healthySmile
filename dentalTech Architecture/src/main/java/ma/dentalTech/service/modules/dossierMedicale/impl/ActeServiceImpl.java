package ma.dentalTech.service.modules.dossierMedicale.impl;

import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository;
import ma.dentalTech.service.modules.dossierMedicale.api.ActeService;

import java.util.List;

public class ActeServiceImpl implements ActeService {

    private final ActeRepository acteRepository;

    public ActeServiceImpl(ActeRepository acteRepository) {
        this.acteRepository = acteRepository;
    }

    @Override
    public Acte create(Acte acte) {
        if (acte == null) throw new IllegalArgumentException("Acte null");
        acte.setIdActe(null);
        return acteRepository.save(acte);
    }

    @Override
    public Acte update(Acte acte) {
        if (acte == null || acte.getIdActe() == null) throw new IllegalArgumentException("idActe requis");
        // vérifier existence
        getById(acte.getIdActe());
        return acteRepository.update(acte);
    }

    @Override
    public Acte getById(Long idActe) {
        return acteRepository.findById(idActe)
                .orElseThrow(() -> new RuntimeException("Acte introuvable: " + idActe));
    }

    @Override
    public List<Acte> getAll() {
        return acteRepository.findAll();
    }

    @Override
    public boolean delete(Long idActe) {
        return acteRepository.deleteById(idActe);
    }

    @Override
    public List<Acte> searchByCategorie(String categorie) {
        return acteRepository.findByCategorieIgnoreCase(categorie);
    }

    @Override
    public List<Acte> searchByLibelle(String keyword) {
        return acteRepository.findByLibelleContainingIgnoreCase(keyword);
    }
}
