package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.entities.dossierMedical.Facture;
import ma.dentalTech.entities.enums.StatutFacture;

import java.time.LocalDateTime;
import java.util.List;

public interface CaisseService {

    Facture creerFacture(Long idConsultation, Long idSf, Double total);

    Facture encaisser(Long idFacture, Double montantEncaisse);

    Facture annuler(Long idFacture);

    Facture getById(Long idFacture);

    List<Facture> findAll();
    Facture findByConsultation(Long idConsultation);
    List<Facture> findBySf(Long idSf);
    List<Facture> findByStatut(StatutFacture statut);
    List<Facture> findByDateBetween(LocalDateTime start, LocalDateTime end);

    Facture creerFactureDepuisConsultation(Long idConsultation, Long idSituationFinanciere);
}
