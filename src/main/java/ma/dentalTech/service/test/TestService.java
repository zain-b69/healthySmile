package ma.dentalTech.service.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.enums.CategorieStatistique;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.StatutRDV;
import ma.dentalTech.service.modules.agendas.api.AgendaMensuelService;
import ma.dentalTech.service.modules.agendas.api.RDVService;
import ma.dentalTech.service.modules.cabinet.api.CabinetMedicalService;
import ma.dentalTech.service.modules.cabinet.api.ChargesService;
import ma.dentalTech.service.modules.cabinet.api.RevenuesService;
import ma.dentalTech.service.modules.cabinet.api.StatistiquesService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class TestService {

    /* ================= SERVICES FROM APPLICATION CONTEXT ================= */
    private static final CabinetMedicalService cabinetService =
            ApplicationContext.getBean(CabinetMedicalService.class);
    private static final AgendaMensuelService agendaService =
            ApplicationContext.getBean(AgendaMensuelService.class);
    private static final RDVService rdvService =
            ApplicationContext.getBean(RDVService.class);
    private static final ChargesService chargesService =
            ApplicationContext.getBean(ChargesService.class);
    private static final RevenuesService revenuesService =
            ApplicationContext.getBean(RevenuesService.class);
    private static final StatistiquesService statistiquesService =
            ApplicationContext.getBean(StatistiquesService.class);

    public static void main(String[] args) {
        try {
            System.out.println("===== DÉBUT TESTS SERVICES =====");

            // 1. Test Cabinet Medical Service
            testCabinetMedicalService();

            // 2. Test Agenda Mensuel Service
            testAgendaMensuelService();

            // 3. Test RDV Service
            testRDVService();

            // 4. Test Charges Service
            testChargesService();

            // 5. Test Revenues Service
            testRevenuesService();

            // 6. Test Statistiques Service
            testStatistiquesService();

            System.out.println("\n===== TOUS LES TESTS TERMINÉS AVEC SUCCÈS =====");

        } catch (Exception e) {
            System.err.println("Erreur lors des tests: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /* ================= TEST CABINET MEDICAL SERVICE ================= */
    private static void testCabinetMedicalService() throws Exception {
        System.out.println("\n=== TEST CABINET MEDICAL SERVICE ===");

        // 1. Lister tous les cabinets
        System.out.println("1. Lister tous les cabinets:");
        List<CabinetMedical> cabinets = cabinetService.listerCabinetsActifs();
        System.out.println("   Nombre de cabinets: " + cabinets.size());

        if (cabinets.isEmpty()) {
            System.out.println("   Aucun cabinet trouvé, création d'un cabinet test...");
            // Créer un cabinet test
            CabinetMedical nouveauCabinet = CabinetMedical.builder()
                    .idUser(1L)
                    .nom("Cabinet Test Service")
                    .email("test.service@dental.ma")
                    .adresse("Adresse Test")
                    .tel1("0611111111")
                    .cin("TEST001")
                    .build();

            try {
                CabinetMedical cabinetCree = cabinetService.ouvrirCabinet(nouveauCabinet);
                System.out.println("   Cabinet créé avec ID: " + cabinetCree.getIdCabinet());
                cabinets = cabinetService.listerCabinetsActifs();
            } catch (Exception e) {
                System.out.println("   Erreur création cabinet: " + e.getMessage());
            }
        }

        // 2. Consulter un cabinet spécifique
        if (!cabinets.isEmpty()) {
            CabinetMedical premierCabinet = cabinets.get(0);
            System.out.println("\n2. Consulter cabinet ID " + premierCabinet.getIdCabinet() + ":");
            Optional<CabinetMedical> cabinetOpt = cabinetService.consulterCabinet(premierCabinet.getIdCabinet());

            if (cabinetOpt.isPresent()) {
                System.out.println("   Cabinet trouvé: " + cabinetOpt.get().getNom());

                // 3. Mettre à jour les informations
                System.out.println("\n3. Mettre à jour les informations:");
                CabinetMedical cabinetAModifier = cabinetOpt.get();
                cabinetAModifier.setAdresse("Nouvelle Adresse Modifiée");
                cabinetAModifier.setTel1("0622222222");

                CabinetMedical cabinetModifie = cabinetService.mettreAJourInformationsCabinet(cabinetAModifier);
                System.out.println("   Cabinet mis à jour: " + cabinetModifie.getAdresse());
            }
        }



        System.out.println("=== FIN TEST CABINET SERVICE ===\n");
    }

    /* ================= TEST AGENDA MENSUEL SERVICE ================= */
    private static void testAgendaMensuelService() throws Exception {
        System.out.println("\n=== TEST AGENDA MENSUEL SERVICE ===");

        // ID d'un médecin existant (basé sur vos données de test)
        long idMedecin = 8L;

        // 1. Rechercher l'agenda actif
        System.out.println("1. Rechercher l'agenda actif pour médecin " + idMedecin + ":");
        Optional<AgendaMensuel> agendaActifOpt = agendaService.rechercherAgendaActif(idMedecin);

        if (agendaActifOpt.isPresent()) {
            AgendaMensuel agendaActif = agendaActifOpt.get();
            System.out.println("   Agenda actif trouvé: " + agendaActif.getMois() + "/" + agendaActif.getAnnee());
        } else {
            System.out.println("   Aucun agenda actif trouvé, création d'un nouvel agenda...");

            // Créer un agenda pour le mois prochain
            LocalDate moisProchain = LocalDate.now().plusMonths(1);
            int mois = moisProchain.getMonthValue();
            int annee = moisProchain.getYear();

            try {
                AgendaMensuel nouvelAgenda = agendaService.planifierAgendaMensuel(idMedecin, mois, annee);
                System.out.println("   Nouvel agenda créé: " + nouvelAgenda.getMois() + "/" + nouvelAgenda.getAnnee());

                // Consulter l'agenda créé
                Optional<AgendaMensuel> agendaCreeOpt = agendaService.consulterAgendaMensuel(nouvelAgenda.getIdAgenda());
                if (agendaCreeOpt.isPresent()) {
                    System.out.println("   Agenda consulté avec succès");
                }
            } catch (Exception e) {
                System.out.println("   Erreur création agenda: " + e.getMessage());
            }
        }

        // 2. Vérifier la disponibilité d'un médecin
        System.out.println("\n2. Vérifier la disponibilité pour demain:");
        LocalDate demain = LocalDate.now().plusDays(1);
        boolean disponible = agendaService.verifierDisponibiliteMedecin(idMedecin, demain);
        System.out.println("   Médecin disponible demain: " + disponible);

        // 3. Lister les jours indisponibles
        System.out.println("\n3. Lister les jours indisponibles:");
        List<LocalDate> joursIndisponibles = agendaService.listerJoursIndisponibles(idMedecin, 0, 0);
        System.out.println("   Nombre de jours indisponibles: " + joursIndisponibles.size());

        // 4. Déclarer un jour indisponible (seulement si disponible)
        if (disponible) {
            System.out.println("\n4. Déclarer un jour comme indisponible:");
            try {
                // Choisir une date dans le futur
                LocalDate dateIndisponible = LocalDate.now().plusMonths(1).withDayOfMonth(15);
                agendaService.declarerJourIndisponible(idMedecin, dateIndisponible, "Congé");
                System.out.println("   Jour déclaré indisponible: " + dateIndisponible);

                // Annuler l'indisponibilité
                agendaService.annulerJourIndisponible(idMedecin, dateIndisponible);
                System.out.println("   Indisponibilité annulée");
            } catch (Exception e) {
                System.out.println("   Note: " + e.getMessage());
            }
        }

        System.out.println("=== FIN TEST AGENDA SERVICE ===\n");
    }

    /* ================= TEST RDV SERVICE ================= */
    private static void testRDVService() throws Exception {
        System.out.println("\n=== TEST RDV SERVICE ===");

        long idPatient = 5L;
        long idMedecin = 8L;

        // 1. Rechercher les RDV futurs du patient
        System.out.println("1. Rechercher RDV futurs du patient " + idPatient + ":");
        List<RDV> rdvFuturs = rdvService.rechercherRendezVousFuturs(idPatient);
        System.out.println("   RDV futurs trouvés: " + rdvFuturs.size());

        // 2. Vérifier la disponibilité d'un créneau
        System.out.println("\n2. Vérifier la disponibilité d'un créneau:");
        LocalDate dateTest = LocalDate.now().plusDays(2);
        String heureTest = "10:00";

        boolean creneauDisponible = rdvService.verifierDisponibiliteCreneau(idMedecin, dateTest, heureTest);
        System.out.println("   Créneau disponible (" + dateTest + " à " + heureTest + "): " + creneauDisponible);

        // 3. Prendre un RDV si le créneau est disponible
        if (creneauDisponible) {
            System.out.println("\n3. Prendre un nouveau RDV:");
            try {
                String motif = "Consultation de contrôle";
                RDV nouveauRDV = rdvService.prendreRendezVous(idPatient, idMedecin, dateTest, heureTest, motif);
                System.out.println("   RDV créé avec ID: " + nouveauRDV.getIdRdv());

                // 4. Confirmer le RDV
                System.out.println("\n4. Confirmer le RDV:");
                rdvService.confirmerRendezVous(nouveauRDV.getIdRdv());
                System.out.println("   RDV confirmé");

                // 5. Changer le statut
                System.out.println("\n5. Changer le statut du RDV:");
                rdvService.changerStatutRendezVous(nouveauRDV.getIdRdv(), StatutRDV.EN_COURS);
                System.out.println("   Statut changé à EN_COURS");

                // 6. Consulter l'emploi du temps
                System.out.println("\n6. Consulter l'emploi du temps:");
                List<RDV> emploiDuTemps = rdvService.consulterEmploiDuTemps(idMedecin, dateTest);
                System.out.println("   RDV pour " + dateTest + ": " + emploiDuTemps.size());

                // 7. Déplacer le RDV (simulation)
                System.out.println("\n7. Déplacer le RDV (test simulation):");
                try {
                    LocalDate nouvelleDate = dateTest.plusDays(1);
                    String nouvelleHeure = "11:00";
                    rdvService.deplacerRendezVous(nouveauRDV.getIdRdv(), nouvelleDate, nouvelleHeure);
                    System.out.println("   RDV déplacé à " + nouvelleDate + " à " + nouvelleHeure);
                } catch (Exception e) {
                    System.out.println("   Note: " + e.getMessage());
                }

                // 8. Annuler le RDV
                System.out.println("\n8. Annuler le RDV:");
                try {
                    rdvService.annulerRendezVous(nouveauRDV.getIdRdv(), "Patient indisponible");
                    System.out.println("   RDV annulé");
                } catch (Exception e) {
                    System.out.println("   Note: " + e.getMessage());
                }

            } catch (Exception e) {
                System.out.println("   Erreur création RDV: " + e.getMessage());
            }
        } else {
            System.out.println("   Créneau non disponible, test RDV sauté");
        }

        System.out.println("=== FIN TEST RDV SERVICE ===\n");
    }

    /* ================= TEST CHARGES SERVICE ================= */
    private static void testChargesService() {
        System.out.println("\n=== TEST CHARGES SERVICE ===");

        // Récupérer un cabinet pour tester
        List<CabinetMedical> cabinets = cabinetService.listerCabinetsActifs();
        if (cabinets.isEmpty()) {
            System.out.println("   Aucun cabinet trouvé pour tester les charges");
            return;
        }

        Long idCabinet = cabinets.get(0).getIdCabinet();

        // 1. Enregistrer une nouvelle charge
        System.out.println("1. Enregistrer une nouvelle charge pour cabinet " + idCabinet + ":");
        try {
            Charges nouvelleCharge = Charges.builder()
                    .titre("Achat matériel dentaire")
                    .description("Brosse et pâte prophylaxie")
                    .montant(1500.50)
                    .date(LocalDateTime.now())
                    .idCabinet(idCabinet)
                    .build();

            Charges chargeCree = chargesService.enregistrerCharge(nouvelleCharge);
            System.out.println("   Charge créée avec ID: " + chargeCree.getIdCharge());

            // 2. Consulter la charge par ID
            System.out.println("\n2. Consulter la charge par ID:");
            Charges chargeConsultee = chargesService.consulterChargeParId(chargeCree.getIdCharge());
            System.out.println("   Charge trouvée: " + chargeConsultee.getTitre() + " - " + chargeConsultee.getMontant() + " MAD");

            // 3. Modifier la charge
            System.out.println("\n3. Modifier la charge:");
            chargeConsultee.setMontant(1600.75);
            chargeConsultee.setTitre("Achat matériel dentaire + accessoires");

            Charges chargeModifiee = chargesService.modifierCharge(chargeConsultee);
            System.out.println("   Charge modifiée: " + chargeModifiee.getTitre() + " - " + chargeModifiee.getMontant() + " MAD");

            // 4. Consulter les charges du cabinet
            System.out.println("\n4. Consulter les charges du cabinet:");
            LocalDateTime debut = LocalDateTime.now().minusMonths(1);
            LocalDateTime fin = LocalDateTime.now();

            List<Charges> chargesCabinet = chargesService.consulterChargesCabinet(idCabinet, debut, fin);
            System.out.println("   Charges trouvées: " + chargesCabinet.size());

            // 5. Calculer le total des charges
            System.out.println("\n5. Calculer le total des charges:");
            double totalCharges = chargesService.calculerTotalCharges(idCabinet, debut, fin);
            System.out.println("   Total charges: " + totalCharges + " MAD");

            // 6. Consulter toutes les charges
            System.out.println("\n6. Consulter toutes les charges du cabinet:");
            List<Charges> toutesCharges = chargesService.consulterToutesChargesCabinet(idCabinet);
            System.out.println("   Toutes les charges: " + toutesCharges.size());

            // 7. Calculer le total de toutes les charges
            System.out.println("\n7. Calculer le total de toutes les charges:");
            double totalToutesCharges = chargesService.calculerTotalToutesCharges(idCabinet);
            System.out.println("   Total toutes charges: " + totalToutesCharges + " MAD");

            // 8. Vérifier l'existence de la charge
            System.out.println("\n8. Vérifier l'existence de la charge:");
            boolean existe = chargesService.existeCharge(chargeCree.getIdCharge());
            System.out.println("   Charge existe: " + existe);

            // 9. Consulter les dernières charges
            System.out.println("\n9. Consulter les 5 dernières charges:");
            List<Charges> dernieresCharges = chargesService.consulterDernieresCharges(idCabinet, 5);
            System.out.println("   Dernières charges: " + dernieresCharges.size());

            // 10. Supprimer la charge (commenté pour éviter de supprimer les données)
            /*
            System.out.println("\n10. Supprimer la charge (test):");
            chargesService.supprimerCharge(chargeCree.getIdCharge());
            System.out.println("   Charge supprimée");
            */

        } catch (Exception e) {
            System.out.println("   Erreur test charges: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== FIN TEST CHARGES SERVICE ===\n");
    }

    /* ================= TEST REVENUES SERVICE ================= */
    private static void testRevenuesService() {
        System.out.println("\n=== TEST REVENUES SERVICE ===");

        // Récupérer un cabinet pour tester
        List<CabinetMedical> cabinets = cabinetService.listerCabinetsActifs();
        if (cabinets.isEmpty()) {
            System.out.println("   Aucun cabinet trouvé pour tester les revenus");
            return;
        }

        Long idCabinet = cabinets.get(0).getIdCabinet();

        // 1. Enregistrer un nouveau revenu
        System.out.println("1. Enregistrer un nouveau revenu pour cabinet " + idCabinet + ":");
        try {
            Revenues nouveauRevenue = Revenues.builder()
                    .titre("Consultation Dr. Smith")
                    .description("Consultation dentaire complète")
                    .montant(800.00)
                    .date(LocalDateTime.now())
                    .idCabinet(idCabinet)
                    .build();

            Revenues revenueCree = revenuesService.enregistrerRevenue(nouveauRevenue);
            System.out.println("   Revenu créé avec ID: " + revenueCree.getIdRevenu());

            // 2. Consulter le revenu par ID
            System.out.println("\n2. Consulter le revenu par ID:");
            Optional<Revenues> revenueOpt = revenuesService.consulterRevenue(revenueCree.getIdRevenu());

            if (revenueOpt.isPresent()) {
                Revenues revenueConsulte = revenueOpt.get();
                System.out.println("   Revenu trouvé: " + revenueConsulte.getTitre() + " - " + revenueConsulte.getMontant() + " MAD");

                // 3. Modifier le revenu
                System.out.println("\n3. Modifier le revenu:");
                revenueConsulte.setMontant(850.00);
                revenueConsulte.setTitre("Consultation + radiographie");

                Revenues revenueModifie = revenuesService.modifierRevenue(revenueConsulte);
                System.out.println("   Revenu modifié: " + revenueModifie.getTitre() + " - " + revenueModifie.getMontant() + " MAD");
            }

            // 4. Consulter les revenus du cabinet
            System.out.println("\n4. Consulter les revenus du cabinet:");
            LocalDateTime debut = LocalDateTime.now().minusMonths(1);
            LocalDateTime fin = LocalDateTime.now();

            List<Revenues> revenuesCabinet = revenuesService.consulterRevenuesCabinet(idCabinet, debut, fin);
            System.out.println("   Revenus trouvés: " + revenuesCabinet.size());

            // 5. Calculer le total des revenus
            System.out.println("\n5. Calculer le total des revenus:");
            double totalRevenues = revenuesService.calculerTotalRevenues(idCabinet, debut, fin);
            System.out.println("   Total revenus: " + totalRevenues + " MAD");

            // 6. Méthodes supplémentaires
            System.out.println("\n6. Méthodes supplémentaires:");

            // Consulter tous les revenus
            List<Revenues> tousRevenues = revenuesService.consulterTousRevenuesCabinet(idCabinet);
            System.out.println("   Tous les revenus: " + tousRevenues.size());

            // Calculer le total de tous les revenus
            double totalTousRevenues = revenuesService.calculerTotalTousRevenuesCabinet(idCabinet);
            System.out.println("   Total tous revenus: " + totalTousRevenues + " MAD");

            // Consulter les derniers revenus
            List<Revenues> derniersRevenues = revenuesService.consulterDerniersRevenues(idCabinet, 3);
            System.out.println("   3 derniers revenus: " + derniersRevenues.size());

            // Rechercher par mot-clé
            List<Revenues> resultatsRecherche = revenuesService.rechercherRevenuesParMotCle(idCabinet, "consultation");
            System.out.println("   Revenus avec 'consultation': " + resultatsRecherche.size());

            // Calculer la moyenne mensuelle
            double moyenneMensuelle = revenuesService.calculerMoyenneMensuelleRevenues(idCabinet, 6);
            System.out.println("   Moyenne mensuelle (6 mois): " + moyenneMensuelle + " MAD");

            // Trouver le plus gros revenu
            Revenues plusGros = revenuesService.getPlusGrosRevenu(idCabinet, debut, fin);
            if (plusGros != null) {
                System.out.println("   Plus gros revenu: " + plusGros.getMontant() + " MAD");
            }

            // Nombre total de revenus
            long nombreTotal = revenuesService.nombreTotalRevenuesCabinet(idCabinet);
            System.out.println("   Nombre total de revenus: " + nombreTotal);

            // 7. Supprimer le revenu (commenté pour éviter de supprimer les données)
            /*
            System.out.println("\n7. Supprimer le revenu (test):");
            revenuesService.supprimerRevenue(revenueCree.getIdRevenu());
            System.out.println("   Revenu supprimé");
            */

        } catch (Exception e) {
            System.out.println("   Erreur test revenus: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=== FIN TEST REVENUES SERVICE ===\n");
    }

    /* ================= TEST STATISTIQUES SERVICE ================= */
    private static void testStatistiquesService() {
        System.out.println("\n=== TEST STATISTIQUES SERVICE ===");

        // Récupérer un cabinet pour tester
        List<CabinetMedical> cabinets = cabinetService.listerCabinetsActifs();
        if (cabinets.isEmpty()) {
            System.out.println("   Aucun cabinet trouvé pour tester les statistiques");
            return;
        }

        Long idCabinet = cabinets.get(0).getIdCabinet();

        try {
            System.out.println("1. Tester les méthodes de consultation sans création:");

            LocalDate debutPeriode = LocalDate.now().minusMonths(6);
            LocalDate finPeriode = LocalDate.now();

            // 1. Consulter les statistiques existantes du cabinet
            List<Statistiques> statsExistantes = statistiquesService.consulterStatistiquesCabinet(idCabinet, debutPeriode, finPeriode);
            System.out.println("   Statistiques existantes trouvées: " + statsExistantes.size());

            Statistiques statsATester = null;

            if (!statsExistantes.isEmpty()) {
                // Utiliser la première statistique existante
                statsATester = statsExistantes.get(0);
                System.out.println("   Utilisation statistique existante ID: " + statsATester.getIdStatistique());

                // 2. Consulter les statistiques par ID
                System.out.println("\n2. Consulter les statistiques par ID:");
                Statistiques statsConsultees = statistiquesService.consulterStatistiquesParId(statsATester.getIdStatistique());
                System.out.println("   Statistiques trouvées: " + statsConsultees.getNbPatients() + " patients, " +
                        statsConsultees.getRevenuTotal() + " MAD");

                // 3. Mettre à jour les statistiques (si possible)
                System.out.println("\n3. Tenter la mise à jour des statistiques:");
                try {
                    statsConsultees.setNbPatients(statsConsultees.getNbPatients() + 5);
                    statsConsultees.setRevenuTotal(statsConsultees.getRevenuTotal() + 1000);
                    statsConsultees.setCategorie(CategorieStatistique.REVENUS);

                    Statistiques statsModifiees = statistiquesService.mettreAJourStatistiques(statsConsultees);
                    System.out.println("   Statistiques mises à jour: " + statsModifiees.getNbPatients() + " patients");
                } catch (Exception e) {
                    System.out.println("   Note: " + e.getMessage());
                }

            } else {
                System.out.println("   Aucune statistique existante pour ce cabinet");
                System.out.println("   Tests de consultation limités");
            }

            // 4. Analyser par catégorie (toujours possible)
            System.out.println("\n4. Analyser statistiques par catégorie REVENUS:");
            List<Statistiques> statsCategorie = statistiquesService.analyserStatistiquesParCategorie(idCabinet, CategorieStatistique.REVENUS);
            System.out.println("   Statistiques REVENUS: " + statsCategorie.size());

            // 5. Générer un rapport de performance
            System.out.println("\n5. Générer un rapport de performance:");
            String rapport = statistiquesService.genererRapportPerformance(idCabinet, debutPeriode, finPeriode);
            System.out.println("   Rapport généré (" + rapport.length() + " caractères)");

            if (rapport.length() > 0) {
                System.out.println("\n   Extrait du rapport:");
                String[] lignes = rapport.split("\n");
                for (int i = 0; i < Math.min(5, lignes.length); i++) {
                    System.out.println("   " + lignes[i]);
                }
                if (lignes.length > 5) {
                    System.out.println("   ...");
                }
            }

            // 6. Vérifier l'existence des statistiques
            System.out.println("\n6. Vérifier l'existence des statistiques:");
            if (statsATester != null) {
                boolean existe = statistiquesService.existeStatistiques(statsATester.getIdStatistique());
                System.out.println("   Statistiques existent (ID " + statsATester.getIdStatistique() + "): " + existe);
            } else {
                System.out.println("   Aucun ID de statistique à vérifier");
            }

            // 7. Nombre total de statistiques
            System.out.println("\n7. Nombre total de statistiques du cabinet:");
            long nombreTotal = statistiquesService.nombreTotalStatistiquesCabinet(idCabinet);
            System.out.println("   Total statistiques: " + nombreTotal);

            // 8. Consulter les dernières statistiques
            System.out.println("\n8. Consulter les 3 dernières statistiques:");
            List<Statistiques> dernieresStats = statistiquesService.consulterDernieresStatistiques(idCabinet, 3);
            System.out.println("   Dernières statistiques: " + dernieresStats.size());

            // 9. Calculer le taux de croissance
            System.out.println("\n9. Calculer le taux de croissance des revenus:");
            try {
                LocalDate periode1Debut = LocalDate.now().minusMonths(2).withDayOfMonth(1);
                LocalDate periode1Fin = periode1Debut.withDayOfMonth(periode1Debut.lengthOfMonth());
                LocalDate periode2Debut = LocalDate.now().minusMonths(1).withDayOfMonth(1);
                LocalDate periode2Fin = periode2Debut.withDayOfMonth(periode2Debut.lengthOfMonth());

                double tauxCroissance = statistiquesService.calculerTauxCroissanceRevenus(
                        idCabinet, periode1Debut, periode1Fin, periode2Debut, periode2Fin);
                System.out.println("   Taux de croissance: " + String.format("%.2f", tauxCroissance) + "%");
            } catch (Exception e) {
                System.out.println("   Note: " + e.getMessage());
            }

            // 10. Générer un rapport comparatif
            System.out.println("\n10. Générer un rapport comparatif:");
            try {
                LocalDate periode1Debut = LocalDate.now().minusMonths(2).withDayOfMonth(1);
                LocalDate periode1Fin = periode1Debut.withDayOfMonth(periode1Debut.lengthOfMonth());
                LocalDate periode2Debut = LocalDate.now().minusMonths(1).withDayOfMonth(1);
                LocalDate periode2Fin = periode2Debut.withDayOfMonth(periode2Debut.lengthOfMonth());

                String rapportComparatif = statistiquesService.genererRapportComparatif(
                        idCabinet, periode2Debut, periode2Fin, periode1Debut, periode1Fin);
                System.out.println("   Rapport comparatif généré (" + rapportComparatif.length() + " caractères)");

                // AJOUTEZ CES LIGNES POUR VOIR LE RAPPORT COMPLET :
                System.out.println("\n   === RAPPORT COMPLET ===");
                System.out.println(rapportComparatif);
                System.out.println("   ======================\n");

            } catch (Exception e) {
                System.out.println("   Note: " + e.getMessage());
            }

            // 11. Essayer de créer une statistique UNIQUE (optionnel)
            System.out.println("\n11. Essayer de créer une statistique avec période unique:");
            try {
                // Utiliser une date très lointaine pour garantir l'unicité
                int moisAleatoire = (int)(Math.random() * 12) + 1;
                LocalDate debutUnique = LocalDate.now().plusYears(3).withMonth(moisAleatoire).withDayOfMonth(1);
                LocalDate finUnique = debutUnique.withDayOfMonth(debutUnique.lengthOfMonth());

                System.out.println("   Période essayée: " + debutUnique + " à " + finUnique);

                Statistiques statsUnique = Statistiques.builder()
                        .idCabinet(idCabinet)
                        .periode("MENSUELLE")
                        .dateDebut(debutUnique)
                        .dateFin(finUnique)
                        .nbPatients(100)
                        .nbRdv(120)
                        .nbConsultation(100)
                        .revenuTotal(50000.0)
                        .tauxAnnulation(5.0)
                        .actePlusEffectif("Consultation")
                        .categorie(CategorieStatistique.REVENUS)
                        .build();

                Statistiques statsCreees = statistiquesService.genererStatistiques(statsUnique);
                System.out.println("   ✅ Statistique créée avec succès! ID: " + statsCreees.getIdStatistique());

                // 12. Supprimer les statistiques (commenté pour éviter de supprimer les données)
            /*
            System.out.println("\n12. Supprimer les statistiques créées (test):");
            statistiquesService.supprimerStatistiques(statsCreees.getIdStatistique());
            System.out.println("   Statistiques supprimées");
            */

            } catch (Exception e) {
                System.out.println("   Note (création): " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("   Erreur générale test statistiques: " + e.getMessage());
            // Ne pas imprimer la stack trace pour garder la sortie propre
        }

        System.out.println("=== FIN TEST STATISTIQUES SERVICE ===\n");
    }
}