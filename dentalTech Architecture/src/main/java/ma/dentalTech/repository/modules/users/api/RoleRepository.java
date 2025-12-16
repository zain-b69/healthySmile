package ma.dentalTech.repository.modules.users.api;

import ma.dentalTech.entities.users.Role;
import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByLibelle(String libelle);
    //List<Role> findByType(RoleType type);
    Optional<Role> findByType(RoleType type);
    List<String> getPrivileges(Long roleId);
    void addPrivilege(Long roleId, String privilege);
    void removePrivilege(Long roleId, String privilege);
    boolean existsByLibelle(String libelle);

    List<Role> findRolesByUtilisateurId(Long utilisateurId);
    // à ajouter dans RoleRepository (repository layer)
    void assignRoleToUser(Long utilisateurId, Long roleId);
    void removeRoleFromUser(Long utilisateurId, Long roleId);
}
