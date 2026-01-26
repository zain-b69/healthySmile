package ma.dentalTech.mvc.controllers.modules.caisse.api;

import ma.dentalTech.entities.dossierMedical.Facture;
import ma.dentalTech.entities.enums.StatutFacture;

import java.time.LocalDateTime;
import java.util.List;

public interface CaisseController {

    // liste
    List<Facture> findAll();
    Facture getById(Long idFacture);

    // filtres
    Facture findByConsultation(Long idConsultation);
    List<Facture> findBySf(Long idSf);
    List<Facture> findByStatut(StatutFacture statut);
    List<Facture> findByDateBetween(LocalDateTime start, LocalDateTime end);

    // actions métier caisse
    Facture creerFacture(Long idConsultation, Long idSf, Double total);
    Facture encaisser(Long idFacture, Double montantEncaisse);
    Facture annuler(Long idFacture);
}
