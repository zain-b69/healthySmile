package ma.dentalTech.repository.modules.users.api;


import ma.dentalTech.entities.users.Secretaire;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SecretaireRepository extends CrudRepository<Secretaire, Long> {

    List<Secretaire> findAllOrderByNom();
    Optional<Secretaire> findByNumCNSS(String numCNSS);
    List<Secretaire> findByCommissionMin(Double minCommission);
}

