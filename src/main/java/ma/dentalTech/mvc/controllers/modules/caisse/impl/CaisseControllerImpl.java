package ma.dentalTech.mvc.controllers.modules.caisse.impl;

import ma.dentalTech.entities.dossierMedical.Facture;
import ma.dentalTech.entities.enums.StatutFacture;
import ma.dentalTech.mvc.controllers.modules.caisse.api.CaisseController;
import ma.dentalTech.service.modules.caisse.api.CaisseService;

import java.time.LocalDateTime;
import java.util.List;

public class CaisseControllerImpl implements CaisseController {

    private final CaisseService caisseService;

    public CaisseControllerImpl(CaisseService caisseService) {
        if (caisseService == null) throw new IllegalArgumentException("CaisseService requis");
        this.caisseService = caisseService;
    }

    // -------------------------
    // liste / détails
    // -------------------------

    @Override
    public List<Facture> findAll() {
        return caisseService.findAll();
    }

    @Override
    public Facture getById(Long idFacture) {
        return caisseService.getById(idFacture);
    }

    // -------------------------
    // filtres
    // -------------------------

    @Override
    public Facture findByConsultation(Long idConsultation) {
        return caisseService.findByConsultation(idConsultation);
    }

    @Override
    public List<Facture> findBySf(Long idSf) {
        return caisseService.findBySf(idSf);
    }

    @Override
    public List<Facture> findByStatut(StatutFacture statut) {
        return caisseService.findByStatut(statut);
    }

    @Override
    public List<Facture> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        return caisseService.findByDateBetween(start, end);
    }

    // -------------------------
    // actions caisse
    // -------------------------

    @Override
    public Facture creerFacture(Long idConsultation, Long idSf, Double total) {
        return caisseService.creerFacture(idConsultation, idSf, total);
    }

    @Override
    public Facture encaisser(Long idFacture, Double montantEncaisse) {
        return caisseService.encaisser(idFacture, montantEncaisse);
    }

    @Override
    public Facture annuler(Long idFacture) {
        return caisseService.annuler(idFacture);
    }
}
