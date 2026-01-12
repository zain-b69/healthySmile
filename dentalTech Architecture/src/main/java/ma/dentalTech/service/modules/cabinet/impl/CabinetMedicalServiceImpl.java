package ma.dentalTech.service.modules.cabinet.impl;

import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.repository.modules.cabinet.api.CabinetMedicalRepository;
import ma.dentalTech.service.modules.cabinet.api.CabinetMedicalService;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CabinetMedicalServiceImpl implements CabinetMedicalService {

    private final CabinetMedicalRepository cabinetMedicalRepository;

    public CabinetMedicalServiceImpl(CabinetMedicalRepository cabinetMedicalRepository) {
        this.cabinetMedicalRepository = cabinetMedicalRepository;
    }

    @Override
    public CabinetMedical ouvrirCabinet(CabinetMedical cabinet) throws SQLException {
        // Validation des données du cabinet
        if (cabinet == null) {
            throw new IllegalArgumentException("Les informations du cabinet ne peuvent pas être nulles");
        }

        if (cabinet.getNom() == null || cabinet.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du cabinet est requis");
        }

        if (cabinet.getEmail() == null || cabinet.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'email du cabinet est requis");
        }

        // Vérifier si un cabinet avec le même nom ou email existe déjà
        Optional<CabinetMedical> existingByName = cabinetMedicalRepository.findByNom(cabinet.getNom());
        if (existingByName.isPresent()) {
            throw new IllegalStateException("Un cabinet avec le nom '" + cabinet.getNom() + "' existe déjà");
        }

        Optional<CabinetMedical> existingByEmail = cabinetMedicalRepository.findByEmail(cabinet.getEmail());
        if (existingByEmail.isPresent()) {
            throw new IllegalStateException("Un cabinet avec l'email '" + cabinet.getEmail() + "' existe déjà");
        }

        try {
            // Sauvegarder le cabinet
            cabinetMedicalRepository.create(cabinet);

            // Récupérer le cabinet fraîchement créé avec son ID généré
            return cabinetMedicalRepository.findById(cabinet.getIdCabinet());
        } catch (RuntimeException e) {
            // Si une SQLException est encapsulée dans RuntimeException
            Throwable cause = e.getCause();
            if (cause instanceof SQLException) {
                throw (SQLException) cause;
            }
            throw new SQLException("Erreur lors de l'ouverture du cabinet", e);
        }
    }

    @Override
    public CabinetMedical mettreAJourInformationsCabinet(CabinetMedical cabinet) {
        // Validation
        if (cabinet == null || cabinet.getIdCabinet() == null) {
            throw new IllegalArgumentException("Le cabinet ou son ID ne peut pas être nul");
        }

        // Vérifier si le cabinet existe
        CabinetMedical existingCabinet = cabinetMedicalRepository.findById(cabinet.getIdCabinet());
        if (existingCabinet == null) {
            throw new IllegalArgumentException("Cabinet introuvable avec l'ID : " + cabinet.getIdCabinet());
        }

        // Vérifier les doublons de nom (si le nom a changé)
        if (!existingCabinet.getNom().equals(cabinet.getNom())) {
            Optional<CabinetMedical> duplicateName = cabinetMedicalRepository.findByNom(cabinet.getNom());
            if (duplicateName.isPresent() && !duplicateName.get().getIdCabinet().equals(cabinet.getIdCabinet())) {
                throw new IllegalStateException("Un autre cabinet avec le nom '" + cabinet.getNom() + "' existe déjà");
            }
        }

        // Vérifier les doublons d'email (si l'email a changé)
        if (!existingCabinet.getEmail().equals(cabinet.getEmail())) {
            Optional<CabinetMedical> duplicateEmail = cabinetMedicalRepository.findByEmail(cabinet.getEmail());
            if (duplicateEmail.isPresent() && !duplicateEmail.get().getIdCabinet().equals(cabinet.getIdCabinet())) {
                throw new IllegalStateException("Un autre cabinet avec l'email '" + cabinet.getEmail() + "' existe déjà");
            }
        }

        // Mettre à jour le cabinet
        cabinetMedicalRepository.update(cabinet);

        // Retourner le cabinet mis à jour
        return cabinetMedicalRepository.findById(cabinet.getIdCabinet());
    }

    @Override
    public void fermerCabinet(Long idCabinet) {
        // Validation
        if (idCabinet == null) {
            throw new IllegalArgumentException("L'ID du cabinet ne peut pas être nul");
        }

        // Vérifier si le cabinet existe
        CabinetMedical cabinet = cabinetMedicalRepository.findById(idCabinet);
        if (cabinet == null) {
            throw new IllegalArgumentException("Cabinet introuvable avec l'ID : " + idCabinet);
        }

        // Supprimer le cabinet
        cabinetMedicalRepository.delete(cabinet);
    }

    @Override
    public Optional<CabinetMedical> consulterCabinet(Long idCabinet) {
        // Validation
        if (idCabinet == null) {
            throw new IllegalArgumentException("L'ID du cabinet ne peut pas être nul");
        }

        // Récupérer le cabinet
        CabinetMedical cabinet = cabinetMedicalRepository.findById(idCabinet);

        // Retourner en Optional
        return Optional.ofNullable(cabinet);
    }

    @Override
    public List<CabinetMedical> listerCabinetsActifs() {
        // Récupérer tous les cabinets (dans un système réel, on aurait un champ "actif")
        return cabinetMedicalRepository.findAll();
    }

    // Méthodes supplémentaires pour une gestion complète (optionnelles)

    public boolean existeCabinet(Long idCabinet) {
        return idCabinet != null && cabinetMedicalRepository.existsById(idCabinet);
    }

    public long nombreTotalCabinets() {
        return cabinetMedicalRepository.count();
    }

    public List<CabinetMedical> rechercherParNomOuAdresse(String motCle) {
        if (motCle == null || motCle.trim().isEmpty()) {
            return cabinetMedicalRepository.findAll();
        }
        return cabinetMedicalRepository.searchByNomOrAdresse(motCle.trim());
    }

    public List<CabinetMedical> listerParPage(int page, int taillePage) {
        if (page < 0 || taillePage <= 0) {
            throw new IllegalArgumentException("Page et taillePage doivent être positifs");
        }
        int offset = page * taillePage;
        return cabinetMedicalRepository.findPage(taillePage, offset);
    }
}