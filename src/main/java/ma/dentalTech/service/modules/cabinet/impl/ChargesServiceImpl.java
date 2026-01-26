package ma.dentalTech.service.modules.cabinet.impl;

import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.repository.modules.cabinet.api.CabinetMedicalRepository;
import ma.dentalTech.repository.modules.cabinet.api.ChargesRepository;
import ma.dentalTech.service.modules.cabinet.api.ChargesService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ChargesServiceImpl implements ChargesService {

    private final ChargesRepository chargesRepository;
    private final CabinetMedicalRepository cabinetMedicalRepository;

    public ChargesServiceImpl(ChargesRepository chargesRepository,
                              CabinetMedicalRepository cabinetMedicalRepository) {
        this.chargesRepository = chargesRepository;
        this.cabinetMedicalRepository = cabinetMedicalRepository;
    }

    @Override
    public Charges enregistrerCharge(Charges charge) throws SQLException {
        // 1. Validation des données
        if (charge == null) {
            throw new IllegalArgumentException("La charge ne peut pas être nulle");
        }

        if (charge.getTitre() == null || charge.getTitre().trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre de la charge est requis");
        }

        if (charge.getMontant() == null || charge.getMontant() <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }

        if (charge.getDate() == null) {
            charge.setDate(LocalDateTime.now());
        }

        if (charge.getIdCabinet() == null) {
            throw new IllegalArgumentException("L'ID du cabinet est requis");
        }

        // 2. Vérifier que le cabinet existe
        if (!cabinetMedicalRepository.existsById(charge.getIdCabinet())) {
            throw new IllegalArgumentException("Cabinet introuvable avec l'ID: " + charge.getIdCabinet());
        }

        // 3. Vérifier que la date n'est pas dans le futur (pour une charge)
        if (charge.getDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date d'une charge ne peut pas être dans le futur");
        }

        try {
            // 4. Enregistrer la charge
            chargesRepository.create(charge);

            // 5. Récupérer la charge fraîchement créée
            return chargesRepository.findById(charge.getIdCharge());
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof SQLException) {
                throw (SQLException) cause;
            }
            throw new SQLException("Erreur lors de l'enregistrement de la charge", e);
        }
    }

    @Override
    public Charges modifierCharge(Charges charge) {
        // 1. Validation des données
        if (charge == null || charge.getIdCharge() == null) {
            throw new IllegalArgumentException("La charge et son ID ne peuvent pas être nuls");
        }

        // 2. Vérifier que la charge existe
        Charges existingCharge = chargesRepository.findById(charge.getIdCharge());
        if (existingCharge == null) {
            throw new IllegalArgumentException("Charge introuvable avec l'ID: " + charge.getIdCharge());
        }

        // 3. Validation des nouvelles données
        if (charge.getTitre() == null || charge.getTitre().trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre de la charge est requis");
        }

        if (charge.getMontant() == null || charge.getMontant() <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }

        if (charge.getIdCabinet() == null) {
            throw new IllegalArgumentException("L'ID du cabinet est requis");
        }

        // 4. Vérifier que le cabinet existe
        if (!cabinetMedicalRepository.existsById(charge.getIdCabinet())) {
            throw new IllegalArgumentException("Cabinet introuvable avec l'ID: " + charge.getIdCabinet());
        }

        // 5. Vérifier que la date n'est pas dans le futur
        if (charge.getDate() != null && charge.getDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date d'une charge ne peut pas être dans le futur");
        }

        // 6. Si la date n'est pas fournie, garder la date originale
        if (charge.getDate() == null) {
            charge.setDate(existingCharge.getDate());
        }

        // 7. Mettre à jour la charge
        chargesRepository.update(charge);

        // 8. Récupérer la charge mise à jour
        return chargesRepository.findById(charge.getIdCharge());
    }

    @Override
    public void supprimerCharge(long idCharge) {
        // 1. Vérifier que la charge existe
        Charges charge = chargesRepository.findById(idCharge);
        if (charge == null) {
            throw new IllegalArgumentException("Charge introuvable avec l'ID: " + idCharge);
        }

        // 2. Vérifier que la charge n'est pas trop ancienne (politique de rétention)
        LocalDateTime sixMois = LocalDateTime.now().minusMonths(6);
        if (charge.getDate().isBefore(sixMois)) {
            throw new IllegalStateException("Impossible de supprimer une charge de plus de 6 mois");
        }

        // 3. Supprimer la charge
        chargesRepository.deleteById(idCharge);
    }

    @Override
    public List<Charges> consulterChargesCabinet(long idCabinet, LocalDateTime debut, LocalDateTime fin) {
        // 1. Validation des paramètres
        if (idCabinet <= 0) {
            throw new IllegalArgumentException("L'ID du cabinet doit être positif");
        }

        if (debut == null) {
            debut = LocalDateTime.now().minusMonths(1); // Par défaut: dernier mois
        }

        if (fin == null) {
            fin = LocalDateTime.now();
        }

        // 2. Vérifier que le cabinet existe
        if (!cabinetMedicalRepository.existsById(idCabinet)) {
            throw new IllegalArgumentException("Cabinet introuvable avec l'ID: " + idCabinet);
        }

        // 3. Vérifier que la période est valide
        if (debut.isAfter(fin)) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }

        // 4. Vérifier que la période ne dépasse pas 1 an (pour des raisons de performance)
        if (debut.isBefore(LocalDateTime.now().minusYears(1))) {
            throw new IllegalArgumentException("La période ne peut pas dépasser 1 an");
        }

        // 5. Récupérer les charges
        return chargesRepository.findByCabinetAndDateBetween(idCabinet, debut, fin);
    }

    @Override
    public double calculerTotalCharges(long idCabinet, LocalDateTime debut, LocalDateTime fin) {
        // 1. Récupérer les charges pour la période
        List<Charges> charges = consulterChargesCabinet(idCabinet, debut, fin);

        // 2. Calculer le total
        double total = 0.0;
        for (Charges charge : charges) {
            total += charge.getMontant();
        }

        return total;
    }

    @Override
    public List<Charges> consulterToutesChargesCabinet(long idCabinet) {
        if (idCabinet <= 0) {
            throw new IllegalArgumentException("L'ID du cabinet doit être positif");
        }

        if (!cabinetMedicalRepository.existsById(idCabinet)) {
            throw new IllegalArgumentException("Cabinet introuvable avec l'ID: " + idCabinet);
        }

        return chargesRepository.findByCabinet(idCabinet);
    }

    @Override
    public double calculerTotalToutesCharges(long idCabinet) {
        List<Charges> toutesCharges = consulterToutesChargesCabinet(idCabinet);

        double total = 0.0;
        for (Charges charge : toutesCharges) {
            total += charge.getMontant();
        }

        return total;
    }

    @Override
    public Charges consulterChargeParId(long idCharge) {
        Charges charge = chargesRepository.findById(idCharge);
        if (charge == null) {
            throw new IllegalArgumentException("Charge introuvable avec l'ID: " + idCharge);
        }
        return charge;
    }

    @Override
    public boolean existeCharge(long idCharge) {
        return chargesRepository.existsById(idCharge);
    }

    @Override
    public List<Charges> consulterDernieresCharges(long idCabinet, int nombre) {
        if (nombre <= 0 || nombre > 100) {
            throw new IllegalArgumentException("Le nombre doit être entre 1 et 100");
        }

        if (idCabinet <= 0) {
            throw new IllegalArgumentException("L'ID du cabinet doit être positif");
        }

        if (!cabinetMedicalRepository.existsById(idCabinet)) {
            throw new IllegalArgumentException("Cabinet introuvable avec l'ID: " + idCabinet);
        }

        List<Charges> toutesCharges = consulterToutesChargesCabinet(idCabinet);

        // Trier par date décroissante et prendre les N premières
        toutesCharges.sort((c1, c2) -> c2.getDate().compareTo(c1.getDate()));

        return toutesCharges.subList(0, Math.min(nombre, toutesCharges.size()));
    }

    // Méthodes supplémentaires non déclarées dans l'interface (optionnelles)

    public List<Charges> consulterChargesParPeriode(LocalDateTime debut, LocalDateTime fin) {
        if (debut == null) {
            debut = LocalDateTime.now().minusMonths(1);
        }

        if (fin == null) {
            fin = LocalDateTime.now();
        }

        if (debut.isAfter(fin)) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }

        return chargesRepository.findByDateBetween(debut, fin);
    }

    public long nombreTotalChargesCabinet(long idCabinet) {
        List<Charges> charges = consulterToutesChargesCabinet(idCabinet);
        return charges.size();
    }
}