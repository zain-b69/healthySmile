package ma.dentalTech.repository.modules.cabinet.api;

import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ChargesRepository extends CrudRepository<Charges, Long> {

    List<Charges> findByCabinet(Long cabinetId);

    List<Charges> findByCabinetAndDateBetween(Long cabinetId, LocalDateTime start, LocalDateTime end);

    List<Charges> findByDateBetween(LocalDateTime start, LocalDateTime end);

    boolean existsById(Long id);

    long count();

    List<Charges> findPage(int limit, int offset);
}
