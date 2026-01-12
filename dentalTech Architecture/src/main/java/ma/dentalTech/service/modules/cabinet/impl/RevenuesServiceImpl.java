package ma.dentalTech.service.modules.cabinet.impl;

import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.repository.modules.cabinet.api.CabinetMedicalRepository;
import ma.dentalTech.repository.modules.cabinet.api.RevenuesRepository;
import ma.dentalTech.service.modules.cabinet.api.RevenuesService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;

public class RevenuesServiceImpl implements RevenuesService {

    private final RevenuesRepository revenuesRepository;
    private final CabinetMedicalRepository cabinetMedicalRepository;

    public RevenuesServiceImpl(RevenuesRepository revenuesRepository,
                               CabinetMedicalRepository cabinetMedicalRepository) {
        this.revenuesRepository = revenuesRepository;
        this.cabinetMedicalRepository = cabinetMedicalRepository;
    }

    @Override
    public Revenues enregistrerRevenue(Revenues revenue) throws SQLException {
        // 1. Validation des données
        if (revenue == null) {
            throw new IllegalArgumentException("Le revenu ne peut pas être nul");
        }

        if (revenue.getTitre() == null || revenue.getTitre().trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre du revenu est requis");
        }

        if (revenue.getMontant() == null || revenue.getMontant() <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }

        if (revenue.getDate() == null) {
            revenue.setDate(LocalDateTime.now());
        }

        if (revenue.getIdCabinet() == null) {
            throw new IllegalArgumentException("L'ID du cabinet est requis");
        }

        // 2. Vérifier que le cabinet existe
        if (!cabinetMedicalRepository.existsById(revenue.getIdCabinet())) {
            throw new IllegalArgumentException("Cabinet introuvable avec l'ID: " + revenue.getIdCabinet());
        }

        // 3. Vérifier que la date n'est pas dans le futur (pour un revenu)
        if (revenue.getDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date d'un revenu ne peut pas être dans le futur");
        }

        // 4. Validation supplémentaire : montant maximum raisonnable
        if (revenue.getMontant() > 1000000) { // 1 million
            throw new IllegalArgumentException("Le montant semble anormalement élevé. Veuillez vérifier.");
        }

        // 5. Enregistrer le revenu
        revenuesRepository.create(revenue);

        // 6. Récupérer le revenu fraîchement créé
        return revenuesRepository.findById(revenue.getIdRevenu());
    }

    @Override
    public Revenues modifierRevenue(Revenues revenue) {
        // 1. Validation des données
        if (revenue == null || revenue.getIdRevenu() == null) {
            throw new IllegalArgumentException("Le revenu et son ID ne peuvent pas être nuls");
        }

        // 2. Vérifier que le revenu existe
        Revenues existingRevenue = revenuesRepository.findById(revenue.getIdRevenu());
        if (existingRevenue == null) {
            throw new IllegalArgumentException("Revenu introuvable avec l'ID: " + revenue.getIdRevenu());
        }

        // 3. Validation des nouvelles données
        if (revenue.getTitre() == null || revenue.getTitre().trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre du revenu est requis");
        }

        if (revenue.getMontant() == null || revenue.getMontant() <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }

        if (revenue.getIdCabinet() == null) {
            throw new IllegalArgumentException("L'ID du cabinet est requis");
        }

        // 4. Vérifier que le cabinet existe
        if (!cabinetMedicalRepository.existsById(revenue.getIdCabinet())) {
            throw new IllegalArgumentException("Cabinet introuvable avec l'ID: " + revenue.getIdCabinet());
        }

        // 5. Vérifier que la date n'est pas dans le futur
        if (revenue.getDate() != null && revenue.getDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date d'un revenu ne peut pas être dans le futur");
        }

        // 6. Validation du montant (vérification d'anomalie)
        if (revenue.getMontant() > 1000000) {
            throw new IllegalArgumentException("Le montant semble anormalement élevé. Veuillez vérifier.");
        }

        // 7. Si la date n'est pas fournie, garder la date originale
        if (revenue.getDate() == null) {
            revenue.setDate(existingRevenue.getDate());
        }

        // 8. Mettre à jour le revenu
        revenuesRepository.update(revenue);

        // 9. Récupérer le revenu mis à jour
        return revenuesRepository.findById(revenue.getIdRevenu());
    }

    @Override
    public void supprimerRevenue(long idRevenu) {
        // 1. Vérifier que le revenu existe
        Revenues revenue = revenuesRepository.findById(idRevenu);
        if (revenue == null) {
            throw new IllegalArgumentException("Revenu introuvable avec l'ID: " + idRevenu);
        }

        // 2. Vérifier que le revenu n'est pas trop ancien (politique de rétention)
        LocalDateTime unAn = LocalDateTime.now().minusYears(1);
        if (revenue.getDate().isBefore(unAn)) {
            throw new IllegalStateException("Impossible de supprimer un revenu de plus de 1 an");
        }

        // 3. Vérification supplémentaire : revenu important
        if (revenue.getMontant() > 10000) {
            // Pour les revenus importants, on pourrait vouloir une validation supplémentaire
            // Par exemple, nécessiter une autorisation
            System.out.println("Attention: Suppression d'un revenu important: " + revenue.getMontant() + " - " + revenue.getTitre());
        }

        // 4. Supprimer le revenu
        revenuesRepository.deleteById(idRevenu);
    }

    @Override
    public List<Revenues> consulterRevenuesCabinet(long idCabinet, LocalDateTime debut, LocalDateTime fin) {
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

        // 4. Vérifier que la période ne dépasse pas 2 ans (pour des raisons de performance)
        if (debut.isBefore(LocalDateTime.now().minusYears(2))) {
            throw new IllegalArgumentException("La période ne peut pas dépasser 2 ans");
        }

        // 5. Récupérer les revenus
        return revenuesRepository.findByCabinetAndDateBetween(idCabinet, debut, fin);
    }

    @Override
    public double calculerTotalRevenues(long idCabinet, LocalDateTime debut, LocalDateTime fin) {
        // 1. Récupérer les revenus pour la période
        List<Revenues> revenues = consulterRevenuesCabinet(idCabinet, debut, fin);

        // 2. Calculer le total
        double total = 0.0;
        for (Revenues revenue : revenues) {
            total += revenue.getMontant();
        }

        return total;
    }

    @Override
    public Optional<Revenues> consulterRevenue(long idRevenu) {
        // 1. Validation
        if (idRevenu <= 0) {
            return Optional.empty();
        }

        // 2. Récupérer le revenu
        Revenues revenue = revenuesRepository.findById(idRevenu);

        // 3. Retourner en Optional
        return Optional.ofNullable(revenue);
    }

    // Méthodes supplémentaires pour une gestion plus complète

    public List<Revenues> consulterTousRevenuesCabinet(long idCabinet) {
        if (idCabinet <= 0) {
            throw new IllegalArgumentException("L'ID du cabinet doit être positif");
        }

        if (!cabinetMedicalRepository.existsById(idCabinet)) {
            throw new IllegalArgumentException("Cabinet introuvable avec l'ID: " + idCabinet);
        }

        return revenuesRepository.findByCabinet(idCabinet);
    }

    public double calculerTotalTousRevenuesCabinet(long idCabinet) {
        List<Revenues> tousRevenues = consulterTousRevenuesCabinet(idCabinet);

        double total = 0.0;
        for (Revenues revenue : tousRevenues) {
            total += revenue.getMontant();
        }

        return total;
    }

    public List<Revenues> consulterRevenuesParPeriode(LocalDateTime debut, LocalDateTime fin) {
        if (debut == null) {
            debut = LocalDateTime.now().minusMonths(1);
        }

        if (fin == null) {
            fin = LocalDateTime.now();
        }

        if (debut.isAfter(fin)) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }

        return revenuesRepository.findByDateBetween(debut, fin);
    }

    public List<Revenues> consulterDerniersRevenues(long idCabinet, int nombre) {
        if (nombre <= 0 || nombre > 100) {
            throw new IllegalArgumentException("Le nombre doit être entre 1 et 100");
        }

        if (idCabinet <= 0) {
            throw new IllegalArgumentException("L'ID du cabinet doit être positif");
        }

        if (!cabinetMedicalRepository.existsById(idCabinet)) {
            throw new IllegalArgumentException("Cabinet introuvable avec l'ID: " + idCabinet);
        }

        List<Revenues> tousRevenues = consulterTousRevenuesCabinet(idCabinet);

        // Trier par date décroissante et prendre les N premiers
        tousRevenues.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));

        return tousRevenues.subList(0, Math.min(nombre, tousRevenues.size()));
    }

    public List<Revenues> rechercherRevenuesParMotCle(long idCabinet, String motCle) {
        if (idCabinet <= 0) {
            throw new IllegalArgumentException("L'ID du cabinet doit être positif");
        }

        if (motCle == null || motCle.trim().isEmpty()) {
            return consulterTousRevenuesCabinet(idCabinet);
        }

        List<Revenues> tousRevenues = consulterTousRevenuesCabinet(idCabinet);
        String recherche = motCle.trim().toLowerCase();

        // Filtrer par titre ou description
        List<Revenues> resultats = new java.util.ArrayList<>();
        for (Revenues revenue : tousRevenues) {
            boolean correspond = false;

            if (revenue.getTitre() != null &&
                    revenue.getTitre().toLowerCase().contains(recherche)) {
                correspond = true;
            }

            if (!correspond && revenue.getDescription() != null &&
                    revenue.getDescription().toLowerCase().contains(recherche)) {
                correspond = true;
            }

            if (correspond) {
                resultats.add(revenue);
            }
        }

        return resultats;
    }

    public double calculerMoyenneMensuelleRevenues(long idCabinet, int nombreMois) {
        if (nombreMois <= 0 || nombreMois > 24) {
            throw new IllegalArgumentException("Le nombre de mois doit être entre 1 et 24");
        }

        LocalDateTime fin = LocalDateTime.now();
        LocalDateTime debut = fin.minusMonths(nombreMois);

        double total = calculerTotalRevenues(idCabinet, debut, fin);

        return total / nombreMois;
    }

    public Revenues getPlusGrosRevenu(long idCabinet, LocalDateTime debut, LocalDateTime fin) {
        List<Revenues> revenues = consulterRevenuesCabinet(idCabinet, debut, fin);

        if (revenues.isEmpty()) {
            return null;
        }

        Revenues plusGros = revenues.get(0);
        for (Revenues revenue : revenues) {
            if (revenue.getMontant() > plusGros.getMontant()) {
                plusGros = revenue;
            }
        }

        return plusGros;
    }

    public long nombreTotalRevenuesCabinet(long idCabinet) {
        List<Revenues> revenues = consulterTousRevenuesCabinet(idCabinet);
        return revenues.size();
    }


}