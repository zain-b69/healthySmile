package ma.dentalTech.service.modules.cabinet.impl;

import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.enums.CategorieStatistique;
import ma.dentalTech.repository.modules.cabinet.api.CabinetMedicalRepository;
import ma.dentalTech.repository.modules.cabinet.api.StatistiquesRepository;
import ma.dentalTech.service.modules.cabinet.api.StatistiquesService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatistiquesServiceImpl implements StatistiquesService {

    private final StatistiquesRepository statistiquesRepository;
    private final CabinetMedicalRepository cabinetMedicalRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public StatistiquesServiceImpl(StatistiquesRepository statistiquesRepository,
                                   CabinetMedicalRepository cabinetMedicalRepository) {
        this.statistiquesRepository = statistiquesRepository;
        this.cabinetMedicalRepository = cabinetMedicalRepository;
    }

    @Override
    public Statistiques genererStatistiques(Statistiques statistique) throws SQLException {
        // 1. Validation des données
        if (statistique == null) {
            throw new IllegalArgumentException("Les statistiques ne peuvent pas être nulles");
        }

        if (statistique.getIdCabinet() == null) {
            throw new IllegalArgumentException("L'ID du cabinet est requis");
        }

        if (statistique.getDateDebut() == null || statistique.getDateFin() == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont requises");
        }

        if (statistique.getPeriode() == null || statistique.getPeriode().trim().isEmpty()) {
            throw new IllegalArgumentException("La période est requise");
        }

        if (statistique.getCategorie() == null) {
            throw new IllegalArgumentException("La catégorie de statistique est requise");
        }

        // 2. Vérifier que le cabinet existe
        if (!cabinetMedicalRepository.existsById(statistique.getIdCabinet())) {
            throw new IllegalArgumentException("Cabinet introuvable avec l'ID: " + statistique.getIdCabinet());
        }

        // 3. Validation des dates
        if (statistique.getDateDebut().isAfter(statistique.getDateFin())) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }

        // 4. Vérifier que la période ne dépasse pas 1 an
        Period periode = Period.between(statistique.getDateDebut(), statistique.getDateFin());
        if (periode.getYears() > 0 || (periode.getYears() == 0 && periode.getMonths() > 12)) {
            throw new IllegalArgumentException("La période ne peut pas dépasser 1 an");
        }

        // 5. Validation des valeurs numériques
        if (statistique.getNbPatients() != null && statistique.getNbPatients() < 0) {
            throw new IllegalArgumentException("Le nombre de patients ne peut pas être négatif");
        }

        if (statistique.getNbRdv() != null && statistique.getNbRdv() < 0) {
            throw new IllegalArgumentException("Le nombre de RDV ne peut pas être négatif");
        }

        if (statistique.getNbConsultation() != null && statistique.getNbConsultation() < 0) {
            throw new IllegalArgumentException("Le nombre de consultations ne peut pas être négatif");
        }

        if (statistique.getRevenuTotal() != null && statistique.getRevenuTotal() < 0) {
            throw new IllegalArgumentException("Le revenu total ne peut pas être négatif");
        }

        if (statistique.getTauxAnnulation() != null &&
                (statistique.getTauxAnnulation() < 0 || statistique.getTauxAnnulation() > 100)) {
            throw new IllegalArgumentException("Le taux d'annulation doit être entre 0 et 100");
        }

        // 6. Calculer des indicateurs si nécessaire
        calculerIndicateursManquants(statistique);

        try {
            // 7. Générer la statistique
            statistiquesRepository.create(statistique);

            // 8. Récupérer la statistique générée
            return statistiquesRepository.findById(statistique.getIdStatistique());
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof SQLException) {
                throw (SQLException) cause;
            }
            throw new SQLException("Erreur lors de la génération des statistiques", e);
        }
    }

    @Override
    public Statistiques mettreAJourStatistiques(Statistiques statistique) {
        // 1. Validation des données
        if (statistique == null || statistique.getIdStatistique() == null) {
            throw new IllegalArgumentException("Les statistiques et leur ID ne peuvent pas être nuls");
        }

        // 2. Vérifier que les statistiques existent
        Statistiques existingStats = statistiquesRepository.findById(statistique.getIdStatistique());
        if (existingStats == null) {
            throw new IllegalArgumentException("Statistiques introuvables avec l'ID: " + statistique.getIdStatistique());
        }

        // 3. Validation des nouvelles données
        if (statistique.getIdCabinet() == null) {
            throw new IllegalArgumentException("L'ID du cabinet est requis");
        }

        if (statistique.getDateDebut() == null || statistique.getDateFin() == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont requises");
        }

        if (statistique.getPeriode() == null || statistique.getPeriode().trim().isEmpty()) {
            throw new IllegalArgumentException("La période est requise");
        }

        if (statistique.getCategorie() == null) {
            throw new IllegalArgumentException("La catégorie de statistique est requise");
        }

        // 4. Vérifier que le cabinet existe
        if (!cabinetMedicalRepository.existsById(statistique.getIdCabinet())) {
            throw new IllegalArgumentException("Cabinet introuvable avec l'ID: " + statistique.getIdCabinet());
        }

        // 5. Validation des dates
        if (statistique.getDateDebut().isAfter(statistique.getDateFin())) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }

        // 6. Mettre à jour les statistiques
        statistiquesRepository.update(statistique);

        // 7. Récupérer les statistiques mises à jour
        return statistiquesRepository.findById(statistique.getIdStatistique());
    }

    @Override
    public void supprimerStatistiques(long idStatistique) {
        // 1. Vérifier que les statistiques existent
        Statistiques statistique = statistiquesRepository.findById(idStatistique);
        if (statistique == null) {
            throw new IllegalArgumentException("Statistiques introuvables avec l'ID: " + idStatistique);
        }

        // 2. Vérifier que les statistiques ne sont pas trop récentes
        LocalDate uneSemaine = LocalDate.now().minusWeeks(1);
        if (statistique.getDateFin().isAfter(uneSemaine)) {
            throw new IllegalStateException("Impossible de supprimer des statistiques de moins d'une semaine");
        }

        // 3. Supprimer les statistiques
        statistiquesRepository.deleteById(idStatistique);
    }

    @Override
    public List<Statistiques> consulterStatistiquesCabinet(long idCabinet, LocalDate debut, LocalDate fin) {
        // 1. Validation des paramètres
        if (idCabinet <= 0) {
            throw new IllegalArgumentException("L'ID du cabinet doit être positif");
        }

        // 2. Vérifier que le cabinet existe
        if (!cabinetMedicalRepository.existsById(idCabinet)) {
            throw new IllegalArgumentException("Cabinet introuvable avec l'ID: " + idCabinet);
        }

        // 3. Définir les dates par défaut si non fournies
        if (debut == null) {
            debut = LocalDate.now().minusMonths(6); // Derniers 6 mois par défaut
        }

        if (fin == null) {
            fin = LocalDate.now();
        }

        // 4. Vérifier que la période est valide
        if (debut.isAfter(fin)) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }

        // 5. Vérifier que la période ne dépasse pas 2 ans
        Period periode = Period.between(debut, fin);
        if (periode.getYears() > 2 || (periode.getYears() == 2 && periode.getMonths() > 0)) {
            throw new IllegalArgumentException("La période ne peut pas dépasser 2 ans");
        }

        // 6. Récupérer toutes les statistiques du cabinet
        List<Statistiques> toutesStatistiques = statistiquesRepository.findByCabinet(idCabinet);

        // 7. Filtrer par période
        List<Statistiques> statistiquesFiltrees = new ArrayList<>();
        for (Statistiques stat : toutesStatistiques) {
            // Vérifier si la statistique est dans la période
            if (!stat.getDateFin().isBefore(debut) && !stat.getDateFin().isAfter(fin)) {
                statistiquesFiltrees.add(stat);
            }
        }

        // 8. Trier par date de fin décroissante
        statistiquesFiltrees.sort((s1, s2) -> s2.getDateFin().compareTo(s1.getDateFin()));

        return statistiquesFiltrees;
    }

    @Override
    public List<Statistiques> analyserStatistiquesParCategorie(long idCabinet, CategorieStatistique categorie) {
        // 1. Validation des paramètres
        if (idCabinet <= 0) {
            throw new IllegalArgumentException("L'ID du cabinet doit être positif");
        }

        if (categorie == null) {
            throw new IllegalArgumentException("La catégorie ne peut pas être nulle");
        }

        // 2. Vérifier que le cabinet existe
        if (!cabinetMedicalRepository.existsById(idCabinet)) {
            throw new IllegalArgumentException("Cabinet introuvable avec l'ID: " + idCabinet);
        }

        // 3. Récupérer les statistiques par catégorie
        List<Statistiques> statistiques = statistiquesRepository.findByCabinetAndCategorie(idCabinet, categorie);

        // 4. Trier par date de fin décroissante
        statistiques.sort((s1, s2) -> s2.getDateFin().compareTo(s1.getDateFin()));

        return statistiques;
    }

    @Override
    public String genererRapportPerformance(long idCabinet, LocalDate debut, LocalDate fin) {
        // 1. Récupérer les statistiques pour la période
        List<Statistiques> statistiques = consulterStatistiquesCabinet(idCabinet, debut, fin);

        if (statistiques.isEmpty()) {
            return "Aucune statistique disponible pour la période spécifiée.";
        }

        // 2. Calculer les totaux et moyennes
        int totalPatients = 0;
        int totalRDV = 0;
        int totalConsultations = 0;
        double totalRevenus = 0.0;
        double totalTauxAnnulation = 0.0;
        int count = 0;

        for (Statistiques stat : statistiques) {
            if (stat.getNbPatients() != null) totalPatients += stat.getNbPatients();
            if (stat.getNbRdv() != null) totalRDV += stat.getNbRdv();
            if (stat.getNbConsultation() != null) totalConsultations += stat.getNbConsultation();
            if (stat.getRevenuTotal() != null) totalRevenus += stat.getRevenuTotal();
            if (stat.getTauxAnnulation() != null) totalTauxAnnulation += stat.getTauxAnnulation();
            count++;
        }

        double moyenneTauxAnnulation = count > 0 ? totalTauxAnnulation / count : 0.0;

        // 3. Trouver les actes les plus effectifs
        Map<String, Integer> actesCount = new HashMap<>();
        for (Statistiques stat : statistiques) {
            if (stat.getActePlusEffectif() != null && !stat.getActePlusEffectif().trim().isEmpty()) {
                String acte = stat.getActePlusEffectif().trim();
                actesCount.put(acte, actesCount.getOrDefault(acte, 0) + 1);
            }
        }

        String actePlusFrequent = "";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : actesCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                actePlusFrequent = entry.getKey();
            }
        }

        // 4. Analyser les catégories
        Map<CategorieStatistique, Integer> categoriesCount = new HashMap<>();
        for (Statistiques stat : statistiques) {
            if (stat.getCategorie() != null) {
                categoriesCount.put(stat.getCategorie(),
                        categoriesCount.getOrDefault(stat.getCategorie(), 0) + 1);
            }
        }

        // 5. Générer le rapport
        StringBuilder rapport = new StringBuilder();
        rapport.append("=== RAPPORT DE PERFORMANCE ===\n");
        rapport.append("Cabinet ID: ").append(idCabinet).append("\n");
        rapport.append("Période: ").append(debut.format(DATE_FORMATTER))
                .append(" - ").append(fin.format(DATE_FORMATTER)).append("\n");
        rapport.append("Nombre de périodes analysées: ").append(count).append("\n\n");

        rapport.append("=== SYNTHÈSE ===\n");
        rapport.append(String.format("Total patients: %,d\n", totalPatients));
        rapport.append(String.format("Total RDV: %,d\n", totalRDV));
        rapport.append(String.format("Total consultations: %,d\n", totalConsultations));
        rapport.append(String.format("Total revenus: %,.2f MAD\n", totalRevenus));
        rapport.append(String.format("Taux d'annulation moyen: %.2f%%\n\n", moyenneTauxAnnulation));

        // Calculer les ratios
        if (totalPatients > 0) {
            double revenuParPatient = totalRevenus / totalPatients;
            double rdvParPatient = (double) totalRDV / totalPatients;
            rapport.append(String.format("Revenu moyen par patient: %,.2f MAD\n", revenuParPatient));
            rapport.append(String.format("RDV moyen par patient: %.2f\n\n", rdvParPatient));
        }

        rapport.append("=== ANALYSE DES ACTES ===\n");
        if (!actePlusFrequent.isEmpty()) {
            rapport.append("Acte le plus fréquent: ").append(actePlusFrequent)
                    .append(" (").append(maxCount).append(" périodes)\n");
        }

        if (!actesCount.isEmpty()) {
            rapport.append("Répartition des actes:\n");
            for (Map.Entry<String, Integer> entry : actesCount.entrySet()) {
                double pourcentage = (double) entry.getValue() / count * 100;
                rapport.append(String.format("  - %s: %d fois (%.1f%%)\n",
                        entry.getKey(), entry.getValue(), pourcentage));
            }
        }
        rapport.append("\n");

        rapport.append("=== RÉPARTITION PAR CATÉGORIE ===\n");
        for (Map.Entry<CategorieStatistique, Integer> entry : categoriesCount.entrySet()) {
            double pourcentage = (double) entry.getValue() / count * 100;
            rapport.append(String.format("%s: %d périodes (%.1f%%)\n",
                    entry.getKey(), entry.getValue(), pourcentage));
        }
        rapport.append("\n");

        // 6. Ajouter des recommandations
        rapport.append("=== RECOMMANDATIONS ===\n");
        if (moyenneTauxAnnulation > 10.0) {
            rapport.append("⚠️  Taux d'annulation élevé. Envisager des rappels automatiques.\n");
        }

        if (totalRDV > 0) {
            double ratioConsultationRdv = (double) totalConsultations / totalRDV;
            if (ratioConsultationRdv < 0.8) {
                rapport.append("⚠️  Ratio consultations/RDV bas. Vérifier la planification.\n");
            }
        }

        if (totalRevenus > 0 && totalPatients > 0) {
            double revenuParPatient = totalRevenus / totalPatients;
            if (revenuParPatient < 500) {
                rapport.append("💰  Potentiel d'augmentation du revenu par patient.\n");
            }
        }

        rapport.append("\n=== FIN DU RAPPORT ===");

        return rapport.toString();
    }

    @Override
    public Statistiques consulterStatistiquesParId(long idStatistique) {
        Statistiques statistique = statistiquesRepository.findById(idStatistique);
        if (statistique == null) {
            throw new IllegalArgumentException("Statistiques introuvables avec l'ID: " + idStatistique);
        }
        return statistique;
    }

    @Override
    public boolean existeStatistiques(long idStatistique) {
        return statistiquesRepository.existsById(idStatistique);
    }

    @Override
    public long nombreTotalStatistiquesCabinet(long idCabinet) {
        if (idCabinet <= 0) {
            throw new IllegalArgumentException("L'ID du cabinet doit être positif");
        }

        if (!cabinetMedicalRepository.existsById(idCabinet)) {
            throw new IllegalArgumentException("Cabinet introuvable avec l'ID: " + idCabinet);
        }

        List<Statistiques> statistiques = statistiquesRepository.findByCabinet(idCabinet);
        return statistiques.size();
    }

    @Override
    public List<Statistiques> consulterDernieresStatistiques(long idCabinet, int nombre) {
        // 1. Validation des paramètres
        if (idCabinet <= 0) {
            throw new IllegalArgumentException("L'ID du cabinet doit être positif");
        }

        if (nombre <= 0 || nombre > 50) {
            throw new IllegalArgumentException("Le nombre doit être entre 1 et 50");
        }

        // 2. Vérifier que le cabinet existe
        if (!cabinetMedicalRepository.existsById(idCabinet)) {
            throw new IllegalArgumentException("Cabinet introuvable avec l'ID: " + idCabinet);
        }

        // 3. Récupérer toutes les statistiques du cabinet
        List<Statistiques> toutesStatistiques = statistiquesRepository.findByCabinet(idCabinet);

        // 4. Trier par date de fin décroissante
        toutesStatistiques.sort((s1, s2) -> s2.getDateFin().compareTo(s1.getDateFin()));

        // 5. Retourner les N premières
        return toutesStatistiques.subList(0, Math.min(nombre, toutesStatistiques.size()));
    }

    @Override
    public double calculerTauxCroissanceRevenus(long idCabinet,
                                                LocalDate periode1Debut, LocalDate periode1Fin,
                                                LocalDate periode2Debut, LocalDate periode2Fin) {
        // 1. Validation des paramètres
        if (idCabinet <= 0) {
            throw new IllegalArgumentException("L'ID du cabinet doit être positif");
        }

        if (!cabinetMedicalRepository.existsById(idCabinet)) {
            throw new IllegalArgumentException("Cabinet introuvable avec l'ID: " + idCabinet);
        }

        // 2. Calculer les revenus pour chaque période
        double revenusPeriode1 = calculerTotalRevenusParPeriode(idCabinet, periode1Debut, periode1Fin);
        double revenusPeriode2 = calculerTotalRevenusParPeriode(idCabinet, periode2Debut, periode2Fin);

        // 3. Calculer le taux de croissance
        if (revenusPeriode1 == 0) {
            return revenusPeriode2 > 0 ? 100.0 : 0.0; // Croissance infinie si on passe de 0 à >0
        }

        return ((revenusPeriode2 - revenusPeriode1) / revenusPeriode1) * 100;
    }

    @Override
    public String genererRapportComparatif(long idCabinet,
                                           LocalDate periodeCouranteDebut, LocalDate periodeCouranteFin,
                                           LocalDate periodePrecedenteDebut, LocalDate periodePrecedenteFin) {
        // 1. Validation des paramètres
        if (idCabinet <= 0) {
            throw new IllegalArgumentException("L'ID du cabinet doit être positif");
        }

        if (!cabinetMedicalRepository.existsById(idCabinet)) {
            throw new IllegalArgumentException("Cabinet introuvable avec l'ID: " + idCabinet);
        }

        // 2. Calculer les revenus pour chaque période
        double revenusPrecedents = calculerTotalRevenusParPeriode(idCabinet, periodePrecedenteDebut, periodePrecedenteFin);
        double revenusCourants = calculerTotalRevenusParPeriode(idCabinet, periodeCouranteDebut, periodeCouranteFin);

        // 3. Calculer le taux de croissance
        double tauxCroissance = calculerTauxCroissanceRevenus(idCabinet,
                periodePrecedenteDebut, periodePrecedenteFin,
                periodeCouranteDebut, periodeCouranteFin);

        // 4. Générer le rapport
        StringBuilder rapport = new StringBuilder();
        rapport.append("=== RAPPORT COMPARATIF ===\n");
        rapport.append("Cabinet ID: ").append(idCabinet).append("\n");
        rapport.append("Période précédente: ").append(periodePrecedenteDebut.format(DATE_FORMATTER))
                .append(" - ").append(periodePrecedenteFin.format(DATE_FORMATTER)).append("\n");
        rapport.append("Période courante: ").append(periodeCouranteDebut.format(DATE_FORMATTER))
                .append(" - ").append(periodeCouranteFin.format(DATE_FORMATTER)).append("\n\n");

        rapport.append("=== SYNTHÈSE FINANCIÈRE ===\n");
        rapport.append(String.format("Revenus période précédente: %,.2f MAD\n", revenusPrecedents));
        rapport.append(String.format("Revenus période courante: %,.2f MAD\n", revenusCourants));

        double difference = revenusCourants - revenusPrecedents;
        if (difference >= 0) {
            rapport.append(String.format("Augmentation: +%,.2f MAD\n", difference));
        } else {
            rapport.append(String.format("Diminution: %,.2f MAD\n", difference));
        }

        rapport.append(String.format("Taux de croissance: %.2f%%\n\n", tauxCroissance));

        // 5. Ajouter une analyse qualitative
        rapport.append("=== ANALYSE ===\n");
        if (tauxCroissance > 20) {
            rapport.append("📈  Excellente croissance (>20%)\n");
        } else if (tauxCroissance > 10) {
            rapport.append("📈  Bonne croissance (10-20%)\n");
        } else if (tauxCroissance > 0) {
            rapport.append("📈  Croissance modérée (0-10%)\n");
        } else if (tauxCroissance < -10) {
            rapport.append("📉  Forte décroissance (< -10%)\n");
        } else if (tauxCroissance < 0) {
            rapport.append("📉  Légère décroissance (0 à -10%)\n");
        } else {
            rapport.append("➡️  Stabilité (0% de croissance)\n");
        }

        // 6. Ajouter des recommandations
        rapport.append("\n=== RECOMMANDATIONS ===\n");
        if (tauxCroissance < 0) {
            rapport.append("⚠️  Décroissance détectée. Analyser les causes et ajuster la stratégie.\n");
        } else if (tauxCroissance < 5) {
            rapport.append("💡  Croissance modeste. Explorer de nouvelles opportunités de revenus.\n");
        } else {
            rapport.append("✅  Performance positive. Maintenir les bonnes pratiques.\n");
        }

        rapport.append("\n=== FIN DU RAPPORT ===");

        return rapport.toString();
    }

    // ============ MÉTHODES PRIVÉES UTILITAIRES ============

    private void calculerIndicateursManquants(Statistiques statistique) {
        // Si certains indicateurs ne sont pas fournis, les calculer si possible

        // Taux d'annulation calculé si non fourni
        if (statistique.getTauxAnnulation() == null &&
                statistique.getNbRdv() != null && statistique.getNbRdv() > 0) {
            // Valeur par défaut de 5% (à adapter selon vos besoins)
            statistique.setTauxAnnulation(5.0);
        }

        // On pourrait ajouter d'autres calculs automatiques ici
        // Par exemple: revenu moyen par consultation, taux de conversion, etc.
    }

    private double calculerTotalRevenusParPeriode(long idCabinet, LocalDate debut, LocalDate fin) {
        // Récupérer les statistiques pour la période
        List<Statistiques> statistiques = consulterStatistiquesCabinet(idCabinet, debut, fin);

        // Calculer le total des revenus
        double total = 0.0;
        for (Statistiques stat : statistiques) {
            if (stat.getRevenuTotal() != null) {
                total += stat.getRevenuTotal();
            }
        }

        return total;
    }
}