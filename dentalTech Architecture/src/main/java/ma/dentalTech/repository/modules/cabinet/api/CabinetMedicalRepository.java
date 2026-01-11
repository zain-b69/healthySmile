package ma.dentalTech.repository.modules.cabinet.api;

import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.users.Staff;
import ma.dentalTech.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CabinetMedicalRepository extends CrudRepository<CabinetMedical, Long> {

    Optional<CabinetMedical> findByNom(String nom);

    Optional<CabinetMedical> findByEmail(String email);

    List<CabinetMedical> searchByNomOrAdresse(String keyword);

    boolean existsById(Long id);

    long count();

    List<CabinetMedical> findPage(int limit, int offset);

    // ---- Relations Many-to-Many avec Staff ----
    void addStaffToCabinet(Long cabinetId, Long staffId);

    void removeStaffFromCabinet(Long cabinetId, Long staffId);

    void removeAllStaffFromCabinet(Long cabinetId);

    List<Staff> getStaffOfCabinet(Long cabinetId);

    List<CabinetMedical> getCabinetsOfStaff(Long staffId);


}
