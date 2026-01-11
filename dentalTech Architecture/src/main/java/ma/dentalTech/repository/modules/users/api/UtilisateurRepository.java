package ma.dentalTech.repository.modules.users.api;

import ma.dentalTech.entities.users.Role;
import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends CrudRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findByLogin(String login);
    boolean existsByEmail(String email);
    boolean existsByLogin(String login);

    List<Utilisateur> searchByNom(String keyword); // LIKE %keyword%
    List<Utilisateur> findPage(int limit, int offset);

    // utilisateur.id_role (1 seul rôle)
    Optional<Role> findRoleByUserId(Long idUser);
    void updateRole(Long idUser, Long idRole);
    void updatePassword(Long idUser, String hashedPassword);

}
