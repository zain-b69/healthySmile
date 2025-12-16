package ma.dentalTech.service.modules.users.api;

import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.users.Role;

import java.util.List;

public interface RoleManagementService {

    Role createRole(Role role);

    Role updateRole(Role role);

    void deleteRole(Long roleId);

    Role getRoleById(Long id);

    Role getRoleByType(RoleType type);

    List<Role> getAllRoles();

    Role updateRolePrivileges(Long roleId, List<String> privileges);
}
