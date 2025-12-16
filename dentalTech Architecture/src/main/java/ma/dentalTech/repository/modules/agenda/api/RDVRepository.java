package ma.dentalTech.repository.modules.agenda.api;


import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface RDVRepository extends CrudRepository<RDV, Long> {

    List<RDV> findByMedecinAndDate(Long medecinId, LocalDate date);
    List<RDV> findByMedecinAndDateRange(Long medecinId, LocalDate start, LocalDate end);
    List<RDV> findByPatient(Long patientId);
    List<RDV> findByAgenda(Long agendaId);
}
