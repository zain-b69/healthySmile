package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

/**
 * DAO JDBC - Ordonnance.
 * Convention DB utilisée (alignée sur les autres repos du projet):
 * - table ordonnance (id_ord PK, id_entite FK entite)
 * - audit dans table entite (date_creation, date_derniere_modification, cree_par, modifie_par)
 */
public interface OrdonnanceRepo extends CrudRepository<Ordonnance, Long> {

    List<Ordonnance> findByDossierMedical(Long idDm);

    List<Ordonnance> findByConsultation(Long idConsultation);

    List<Prescription> getPrescriptions(Long idOrd);

    boolean existsById(Long id);

    long count();

    List<Ordonnance> findPage(int limit, int offset);
}
