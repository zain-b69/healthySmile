package ma.dentalTech.repository.modules.cabinet.api;

import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RevenuesRepository extends CrudRepository<Revenues, Long> {

    List<Revenues> findByCabinet(Long cabinetId);

    List<Revenues> findByCabinetAndDateBetween(Long cabinetId, LocalDateTime start, LocalDateTime end);

    List<Revenues> findByDateBetween(LocalDateTime start, LocalDateTime end);

    boolean existsById(Long id);

    long count();

    List<Revenues> findPage(int limit, int offset);
}
