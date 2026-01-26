package ma.dentalTech.repository.modules.users.api;

import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByLibelle(RoleType libelle);
    boolean existsByLibelle(RoleType libelle);
}
