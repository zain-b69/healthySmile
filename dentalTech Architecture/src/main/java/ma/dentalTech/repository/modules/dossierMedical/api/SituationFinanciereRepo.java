package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.SituationFinanciere;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

/**
 * DAO JDBC - SituationFinanciere.
 */
public interface SituationFinanciereRepo extends CrudRepository<SituationFinanciere, Long> {

    void create(SituationFinanciere sf);

    SituationFinanciere findByDossierMedical(Long idDm);

    List<SituationFinanciere> findByStatut(StatutSituationFinanciere statut);

    boolean existsById(Long id);

    long count();

    List<SituationFinanciere> findPage(int limit, int offset);

    Long createAndReturnId(SituationFinanciere sf);
}
