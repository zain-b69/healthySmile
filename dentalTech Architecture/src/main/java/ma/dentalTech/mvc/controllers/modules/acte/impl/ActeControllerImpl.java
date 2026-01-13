package ma.dentalTech.mvc.controllers.modules.acte.impl;

import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.mvc.controllers.modules.acte.api.ActeController;
import ma.dentalTech.service.modules.dossierMedicale.api.ActeService;

import java.util.List;

public class ActeControllerImpl implements ActeController {

    private final ActeService acteService;

    public ActeControllerImpl(ActeService acteService) {
        if (acteService == null) throw new IllegalArgumentException("ActeService requis");
        this.acteService = acteService;
    }

    @Override public List<Acte> findAll() { return acteService.getAll(); }

    @Override public List<Acte> search(String keyword, String categorie) {
        return acteService.rechercher(keyword, categorie);
    }

    @Override public Acte create(String libelle, String categorie, Double prixDeBase) {
        return acteService.creerActeCatalogue(libelle, categorie, prixDeBase);
    }

    @Override public Acte update(Long idActe, String libelle, String categorie, Double prixDeBase) {
        return acteService.modifierActeCatalogue(idActe, libelle, categorie, prixDeBase);
    }

    @Override public Acte updatePrix(Long idActe, Double nouveauPrix) {
        return acteService.changerPrixDeBase(idActe, nouveauPrix);
    }

    @Override public boolean delete(Long idActe) { return acteService.supprimer(idActe); }

    @Override public Acte getById(Long idActe) { return acteService.getById(idActe); }
}
