package ma.dentalTech.repository.modules.users.api;

import ma.dentalTech.entities.users.Staff;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface StaffRepository extends CrudRepository<Staff, Long> {

    List<Staff> findAllOrderByNom();
    List<Staff> findBySalaireBetween(Double min, Double max);
    List<Staff> findByDateRecrutementAfter(LocalDate date);
}
