package ma.dentalTech.repository.modules.agenda.api;

import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RDVRepository extends CrudRepository<RDV, Long> {

    List<RDV> findByMedecinAndDate(Long idMedecin, LocalDate date);

    List<RDV> findByMedecinAndDateRange(Long idMedecin, LocalDate start, LocalDate end);

    List<RDV> findByPatient(Long idPatient);

    List<RDV> findByAgenda(Long agendaMensuelId);

    List<RDV> findByStatut(Enum statut);

    List<RDV> findByCreneau(Long idCreneau);

    Optional<RDV> findByCreneauUnique(Long idCreneau);

    boolean existsByCreneau(Long idCreneau);

    long count();

    List<RDV> findPage(int limit, int offset);
}
