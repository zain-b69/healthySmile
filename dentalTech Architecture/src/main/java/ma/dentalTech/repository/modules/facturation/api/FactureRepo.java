package ma.dentalTech.repository.modules.facturation.api;

import ma.dentalTech.entities.dossierMedical.Facture;
import ma.dentalTech.entities.enums.StatutFacture;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FactureRepo {

    // CRUD
    Facture save(Facture f);
    Facture update(Facture f);
    Optional<Facture> findById(Long idFacture);
    List<Facture> findAll();
    boolean deleteById(Long idFacture);

    // Finders utiles (dashboard / métier)
    Optional<Facture> findByIdConsultation(Long idConsultation);
    List<Facture> findByIdSf(Long idSf);
    List<Facture> findByStatut(StatutFacture statut);

    List<Facture> findByDateFactureBetween(LocalDateTime start, LocalDateTime end);
}
