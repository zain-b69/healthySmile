package ma.dentalTech.repository.modules.agenda.api;


import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AgendaMensuelRepository extends CrudRepository<AgendaMensuel, Long> {

    Optional<AgendaMensuel> findByMedecinAndMoisAndAnnee(Long medecinId, String moisEnumName, int annee);
    List<AgendaMensuel> findByMedecin(Long medecinId);

    // Gestion jours non disponibles
    List<LocalDate> getJoursNonDisponibles(Long agendaId);
    void addJourNonDisponible(Long agendaId, LocalDate date);
    void removeJourNonDisponible(Long agendaId, LocalDate date);
    void clearJoursNonDisponibles(Long agendaId);
}
