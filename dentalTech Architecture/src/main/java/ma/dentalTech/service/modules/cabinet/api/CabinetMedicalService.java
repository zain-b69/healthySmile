package ma.dentalTech.service.modules.cabinet.api;

import ma.dentalTech.entities.cabinet.CabinetMedical;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service métier du module Cabinet.
 */
public interface CabinetMedicalService {

    /**
     * Ouvre un nouveau cabinet médical.
     */
    CabinetMedical ouvrirCabinet(CabinetMedical cabinet) throws SQLException;

    /**
     * Met à jour les informations d'un cabinet.
     */
    CabinetMedical mettreAJourInformationsCabinet(CabinetMedical cabinet);

    /**
     * Ferme définitivement un cabinet.
     */
    void fermerCabinet(Long idCabinet);

    /**
     * Consulte les informations d'un cabinet.
     */
    Optional<CabinetMedical> consulterCabinet(Long idCabinet);

    /**
     * Liste tous les cabinets actifs.
     */
    List<CabinetMedical> listerCabinetsActifs();
}