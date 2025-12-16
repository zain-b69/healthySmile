package ma.dentalTech.repository.modules.users.api;


import ma.dentalTech.entities.users.Medecin;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;

public interface MedecinRepository extends CrudRepository<Medecin, Long> {

    List<Medecin> findAllOrderByNom();
    List<Medecin> findBySpecialite(String specialiteLike);
}

