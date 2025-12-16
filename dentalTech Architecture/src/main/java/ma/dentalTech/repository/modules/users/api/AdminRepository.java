package ma.dentalTech.repository.modules.users.api;

import ma.dentalTech.entities.users.Admin;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends CrudRepository<Admin, Long> {

    List<Admin> findAllOrderByNom();
    Optional<Admin> findByEmail(String email);
}
