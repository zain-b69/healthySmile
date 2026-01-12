package ma.dentalTech.service.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.StatutRDV;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.enums.CategorieStatistique;

import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.dentalTech.repository.modules.agenda.api.RDVRepository;
import ma.dentalTech.repository.modules.cabinet.api.CabinetMedicalRepository;
import ma.dentalTech.repository.modules.cabinet.api.ChargesRepository;
import ma.dentalTech.repository.modules.cabinet.api.RevenuesRepository;
import ma.dentalTech.repository.modules.cabinet.api.StatistiquesRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import ma.dentalTech.configuration.SessionFactory;

public class TestRepo {

    /* ================= REPOSITORIES FROM APPLICATION CONTEXT ================= */
    private static final AgendaMensuelRepository agendaRepository =
            ApplicationContext.getBean(AgendaMensuelRepository.class);
    private static final RDVRepository rdvRepository =
            ApplicationContext.getBean(RDVRepository.class);
    private static final CabinetMedicalRepository cabinetRepository =
            ApplicationContext.getBean(CabinetMedicalRepository.class);
    private static final ChargesRepository chargesRepository =
            ApplicationContext.getBean(ChargesRepository.class);
    private static final RevenuesRepository revenuesRepository =
            ApplicationContext.getBean(RevenuesRepository.class);
    private static final StatistiquesRepository statistiquesRepository =
            ApplicationContext.getBean(StatistiquesRepository.class);

    public static void main(String[] args) throws SQLException {
        System.out.println("===== TEST AGENDA REPOSITORY =====");
        AgendaMensuel agenda = insertAgenda();
        selectAgenda();
        updateAgenda();
        // deleteAgenda(agenda); // TEST DELETE COMMENTÉ
        System.out.println("===== FIN TEST AGENDA =====");

        System.out.println("===== TEST RDV REPOSITORY =====");
        RDV rdv = insertRDV();
        selectRDV();
        updateRDV();
        // deleteRDV(rdv); // TEST DELETE COMMENTÉ
        System.out.println("===== FIN TEST RDV =====");

        System.out.println("===== TEST CABINET MEDICAL REPOSITORY =====");
        CabinetMedical cabinet = insertCabinet();
        selectCabinet();
        updateCabinet();
        // deleteCabinet(cabinet); // TEST DELETE COMMENTÉ
        System.out.println("===== FIN TEST CABINET =====");

        System.out.println("===== TEST CHARGES REPOSITORY =====");
        Charges charge = insertCharge();
        selectCharge();
        updateCharge();
        // deleteCharge(charge); // TEST DELETE COMMENTÉ
        System.out.println("===== FIN TEST CHARGES =====");

        System.out.println("===== TEST REVENUES REPOSITORY =====");
        Revenues revenue = insertRevenue();
        selectRevenue();
        updateRevenue();
        // deleteRevenue(revenue); // TEST DELETE COMMENTÉ
        System.out.println("===== FIN TEST REVENUES =====");

        System.out.println("===== TEST STATISTIQUES REPOSITORY =====");
        Statistiques statistique = insertStatistique();
        selectStatistique();
        updateStatistique();
        // deleteStatistique(statistique); // TEST DELETE COMMENTÉ
        System.out.println("===== FIN TEST STATISTIQUES =====");
    }

    /* ================= AGENDA REPOSITORY ================= */
    public static AgendaMensuel insertAgenda() throws SQLException {
        System.out.println("\n--- INSERT AGENDA VIA REPOSITORY ---");

        // Chercher un agenda existant pour médecin 8, décembre 2025
        Optional<AgendaMensuel> existingAgenda = agendaRepository
                .findByMedecinAndMoisAndAnnee(8L, 12, 2025);

        AgendaMensuel agenda;
        if (existingAgenda.isPresent()) {
            agenda = existingAgenda.get();
            System.out.println("Agenda existant trouvé : " + agenda);
        } else {
            // Créer un nouvel agenda
            agenda = AgendaMensuel.builder()
                    .mois(12)
                    .annee(2025)
                    .idMedecin(8L)
                    .build();
            agendaRepository.create(agenda);
            System.out.println("Nouvel agenda créé : " + agenda);

            // Ajouter un jour non disponible
            agendaRepository.addJourNonDisponible(agenda.getIdAgenda(),
                    LocalDate.of(2025, 12, 10));
            System.out.println("Jour non disponible ajouté");
        }
        return agenda;
    }

    public static void selectAgenda() {
        System.out.println("\n--- SELECT AGENDA VIA REPOSITORY ---");
        List<AgendaMensuel> agendas = agendaRepository.findAll();
        System.out.println("Nombre d'agendas trouvés : " + agendas.size());
        agendas.forEach(agenda -> {
            System.out.println(agenda);
            // Récupérer les jours non disponibles pour chaque agenda
            List<LocalDate> joursNonDisponibles = agendaRepository.getJoursNonDisponibles(agenda.getIdAgenda());
            System.out.println("  Jours non disponibles : " + joursNonDisponibles);
        });

        // Tester findByMedecin
        List<AgendaMensuel> agendasMedecin = agendaRepository.findByMedecin(8L);
        System.out.println("Agendas pour médecin 8 : " + agendasMedecin.size());
    }

    public static void updateAgenda() {
        System.out.println("\n--- UPDATE AGENDA VIA REPOSITORY ---");
        List<AgendaMensuel> agendas = agendaRepository.findAll();
        if (!agendas.isEmpty()) {
            // Prendre le dernier agenda créé (celui de décembre 2025)
            AgendaMensuel agenda = agendas.get(agendas.size() - 1);

            // Trouver un mois disponible pour 2026
            int annee = 2026;
            int moisDisponible = -1;

            // Essayer les mois de 4 à 12 (avril à décembre)
            for (int mois = 4; mois <= 12; mois++) {
                Optional<AgendaMensuel> existing = agendaRepository.findByMedecinAndMoisAndAnnee(
                        agenda.getIdMedecin(), mois, annee);

                if (existing.isEmpty()) {
                    moisDisponible = mois;
                    break;
                }
            }

            if (moisDisponible == -1) {
                // Essayer 2027
                annee = 2027;
                for (int mois = 1; mois <= 12; mois++) {
                    Optional<AgendaMensuel> existing = agendaRepository.findByMedecinAndMoisAndAnnee(
                            agenda.getIdMedecin(), mois, annee);

                    if (existing.isEmpty()) {
                        moisDisponible = mois;
                        break;
                    }
                }
            }

            if (moisDisponible != -1) {
                agenda.setMois(moisDisponible);
                agenda.setAnnee(annee);
                agendaRepository.update(agenda);
                System.out.println("Agenda mis à jour pour " + moisDisponible + "/" + annee + " : " + agenda);
            } else {
                System.out.println("Aucun mois disponible trouvé pour la mise à jour");
            }
        }
    }

    /*
    // TEST DELETE AGENDA (COMMENTÉ)
    public static void deleteAgenda(AgendaMensuel agenda) {
        System.out.println("\n--- DELETE AGENDA VIA REPOSITORY ---");
        if (agenda != null && agenda.getIdAgenda() != null) {
            // D'abord supprimer les jours non disponibles
            agendaRepository.clearJoursNonDisponibles(agenda.getIdAgenda());
            System.out.println("Jours non disponibles nettoyés");

            // Puis supprimer l'agenda
            agendaRepository.deleteById(agenda.getIdAgenda());
            System.out.println("Agenda supprimé avec ID: " + agenda.getIdAgenda());

            // Vérifier la suppression
            AgendaMensuel deleted = agendaRepository.findById(agenda.getIdAgenda());
            System.out.println("Agenda après suppression: " + (deleted == null ? "null" : "existe toujours"));
        }
    }
    */



    /* ================= INSERER UN CRÉNEAU (TOUJOURS CRÉER NOUVEAU) ================= */
    public static Long insererCreneau(Long idAgenda) throws SQLException {
        System.out.println("\n--- INSERTION CRÉNEAU ---");

        // Vérifier que l'agenda existe
        AgendaMensuel agenda = agendaRepository.findById(idAgenda);
        if (agenda == null) {
            throw new IllegalArgumentException("Agenda non trouvé avec ID: " + idAgenda);
        }

        java.sql.Connection c = SessionFactory.getInstance().getConnection();

        // Toujours créer un nouveau créneau pour éviter les problèmes de contraintes
        // Utiliser la date/heure actuelle + un offset aléatoire
        LocalDate dateCreneau = LocalDate.now()
                .plusYears(1)  // 1 an dans le futur
                .plusDays((int)(Math.random() * 365));  // Jour aléatoire

        java.time.LocalTime heureDebut = java.time.LocalTime.of(8 + (int)(Math.random() * 8), 0); // Entre 8h et 16h
        java.time.LocalTime heureFin = heureDebut.plusMinutes(30);

        String insertSql = "INSERT INTO creneau (id_agenda, date_jour, heure_debut, heure_fin, statut) VALUES (?, ?, ?, ?, ?)";
        try (java.sql.PreparedStatement ps = c.prepareStatement(insertSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, idAgenda);
            ps.setDate(2, java.sql.Date.valueOf(dateCreneau));
            ps.setTime(3, java.sql.Time.valueOf(heureDebut));
            ps.setTime(4, java.sql.Time.valueOf(heureFin));
            ps.setString(5, "LIBRE");

            ps.executeUpdate();

            try (java.sql.ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    Long idCreneau = keys.getLong(1);
                    System.out.println("Nouveau créneau créé avec ID: " + idCreneau);
                    c.close();
                    return idCreneau;
                }
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            // En cas de conflit (très rare), réessayer avec une date différente
            dateCreneau = dateCreneau.plusDays(1);

            try (java.sql.PreparedStatement ps = c.prepareStatement(insertSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, idAgenda);
                ps.setDate(2, java.sql.Date.valueOf(dateCreneau));
                ps.setTime(3, java.sql.Time.valueOf(heureDebut));
                ps.setTime(4, java.sql.Time.valueOf(heureFin));
                ps.setString(5, "LIBRE");

                ps.executeUpdate();

                try (java.sql.ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        Long idCreneau = keys.getLong(1);
                        System.out.println("Nouveau créneau créé avec ID: " + idCreneau + " (après réessai)");
                        c.close();
                        return idCreneau;
                    }
                }
            }
        }

        c.close();
        throw new IllegalStateException("Impossible de créer un créneau");
    }
    /* ================= RDV REPOSITORY ================= */
    public static RDV insertRDV() throws SQLException {
        System.out.println("\n--- INSERT RDV VIA REPOSITORY ---");

        // Chercher un agenda pour associer le RDV
        List<AgendaMensuel> agendas = agendaRepository.findAll();
        if (agendas.isEmpty()) {
            System.out.println("Aucun agenda trouvé pour créer un RDV");
            return null;
        }

        // Trouver un agenda de 2026 (janvier, février ou mars)
        AgendaMensuel agenda = null;
        for (AgendaMensuel a : agendas) {
            if (a.getAnnee() == 2026 && (a.getMois() == 1 || a.getMois() == 2 || a.getMois() == 3)) {
                agenda = a;
                break;
            }
        }

        if (agenda == null) {
            System.out.println("Aucun agenda 2026 trouvé");
            return null;
        }

        // 1. D'abord insérer un créneau
        Long idCreneau = insererCreneau(agenda.getIdAgenda());

        if (idCreneau == null) {
            throw new IllegalStateException("Impossible de créer un créneau pour le RDV");
        }

        // 2. Maintenant créer le RDV avec le créneau valide
        RDV rdv = RDV.builder()
                .idPatient(5L)
                .idMedecin(8L)
                .date(LocalDate.of(2026, 1, 15))
                .heure("10:00")
                .motif("Consultation générale")
                .statut(StatutRDV.EN_ATTENTE)
                .noteMedecin("")
                .idDm(3L)
                .idCreneau(idCreneau) // ← ID du créneau créé
                .agendaMensuelId(agenda.getIdAgenda())
                .build();

        rdvRepository.create(rdv);
        System.out.println("RDV créé avec créneau ID: " + idCreneau);
        System.out.println("RDV: " + rdv);

        return rdv;
    }

    public static void selectRDV() {
        System.out.println("\n--- SELECT RDV VIA REPOSITORY ---");

        // findAll()
        List<RDV> rdvs = rdvRepository.findAll();
        System.out.println("Nombre total de RDV : " + rdvs.size());

        // Tester différentes méthodes de recherche
        if (!rdvs.isEmpty()) {
            RDV firstRdv = rdvs.get(0);

            // findById()
            RDV rdvById = rdvRepository.findById(firstRdv.getIdRdv());
            System.out.println("RDV par ID : " + rdvById);

            // findByMedecinAndDate
            List<RDV> rdvsMedecinDate = rdvRepository.findByMedecinAndDate(
                    firstRdv.getIdMedecin(), firstRdv.getDate());
            System.out.println("RDV pour médecin " + firstRdv.getIdMedecin() +
                    " le " + firstRdv.getDate() + " : " + rdvsMedecinDate.size());

            // findByPatient
            List<RDV> rdvsPatient = rdvRepository.findByPatient(firstRdv.getIdPatient());
            System.out.println("RDV pour patient " + firstRdv.getIdPatient() +
                    " : " + rdvsPatient.size());

            // findByStatut
            List<RDV> rdvsStatut = rdvRepository.findByStatut(StatutRDV.EN_ATTENTE);
            System.out.println("RDV avec statut EN_ATTENTE : " + rdvsStatut.size());

            // count()
            long count = rdvRepository.count();
            System.out.println("Total RDV (via count) : " + count);
        }
    }

    public static void updateRDV() {
        System.out.println("\n--- UPDATE RDV VIA REPOSITORY ---");
        List<RDV> rdvs = rdvRepository.findAll();
        if (!rdvs.isEmpty()) {
            RDV rdv = rdvs.get(0);
            rdv.setStatut(StatutRDV.TERMINE);
            rdv.setNoteMedecin("Consultation terminée avec succès");
            rdvRepository.update(rdv);
            System.out.println("RDV mis à jour : " + rdv);
        }
    }

    /*
    // TEST DELETE RDV (COMMENTÉ)
    public static void deleteRDV(RDV rdv) {
        System.out.println("\n--- DELETE RDV VIA REPOSITORY ---");
        if (rdv != null && rdv.getIdRdv() != null) {
            rdvRepository.deleteById(rdv.getIdRdv());
            System.out.println("RDV supprimé avec ID: " + rdv.getIdRdv());

            // Vérifier la suppression
            RDV deleted = rdvRepository.findById(rdv.getIdRdv());
            System.out.println("RDV après suppression: " + (deleted == null ? "null" : "existe toujours"));
        }
    }
    */

    /* ================= CABINET REPOSITORY ================= */
    public static CabinetMedical insertCabinet() throws SQLException {
        System.out.println("\n--- INSERT CABINET VIA REPOSITORY ---");

        // Vérifier si un cabinet existe déjà
        Optional<CabinetMedical> existingCabinet = cabinetRepository.findByNom("Cabinet Smile Dental");

        CabinetMedical cabinet;
        if (existingCabinet.isPresent()) {
            cabinet = existingCabinet.get();
            System.out.println("Cabinet existant trouvé : " + cabinet);
        } else {
            // Créer un nouveau cabinet
            cabinet = CabinetMedical.builder()
                    .idUser(8L)
                    .nom("Cabinet Smile Dental")
                    .email("contact@smiledental.ma")
                    .adresse("Rabat Agdal")
                    .tel1("0612345678")
                    .cin("AB123456")
                    .siteWeb("www.smiledental.ma")
                    .description("Cabinet dentaire spécialisé en orthodontie")
                    .build();
            cabinetRepository.create(cabinet);
            System.out.println("Nouveau cabinet créé : " + cabinet);
        }
        return cabinet;
    }

    public static void selectCabinet() {
        System.out.println("\n--- SELECT CABINET VIA REPOSITORY ---");

        // findAll()
        List<CabinetMedical> cabinets = cabinetRepository.findAll();
        System.out.println("Nombre total de cabinets : " + cabinets.size());

        if (!cabinets.isEmpty()) {
            CabinetMedical firstCabinet = cabinets.get(0);

            // findById()
            CabinetMedical cabinetById = cabinetRepository.findById(firstCabinet.getIdCabinet());
            System.out.println("Cabinet par ID : " + cabinetById);

            // findByEmail
            Optional<CabinetMedical> cabinetByEmail = cabinetRepository.findByEmail(firstCabinet.getEmail());
            System.out.println("Cabinet par email : " + cabinetByEmail.orElse(null));

            // searchByNomOrAdresse
            List<CabinetMedical> searchResults = cabinetRepository.searchByNomOrAdresse("Smile");
            System.out.println("Résultats de recherche 'Smile' : " + searchResults.size());

            // existsById
            boolean exists = cabinetRepository.existsById(firstCabinet.getIdCabinet());
            System.out.println("Cabinet existe (ID=" + firstCabinet.getIdCabinet() + ") : " + exists);
        }
    }

    public static void updateCabinet() {
        System.out.println("\n--- UPDATE CABINET VIA REPOSITORY ---");
        List<CabinetMedical> cabinets = cabinetRepository.findAll();
        if (!cabinets.isEmpty()) {
            CabinetMedical cabinet = cabinets.get(0);
            cabinet.setAdresse("Rabat Centre Ville");
            cabinet.setTel1("0699999999");
            cabinetRepository.update(cabinet);
            System.out.println("Cabinet mis à jour : " + cabinet);
        }
    }

    /*
    // TEST DELETE CABINET (COMMENTÉ)
    public static void deleteCabinet(CabinetMedical cabinet) {
        System.out.println("\n--- DELETE CABINET VIA REPOSITORY ---");
        if (cabinet != null && cabinet.getIdCabinet() != null) {
            cabinetRepository.deleteById(cabinet.getIdCabinet());
            System.out.println("Cabinet supprimé avec ID: " + cabinet.getIdCabinet());

            // Vérifier la suppression
            boolean exists = cabinetRepository.existsById(cabinet.getIdCabinet());
            System.out.println("Cabinet existe après suppression: " + exists);
        }
    }
    */

    /* ================= CHARGES REPOSITORY ================= */
    public static Charges insertCharge() throws SQLException {
        System.out.println("\n--- INSERT CHARGE VIA REPOSITORY ---");

        // Chercher un cabinet pour associer la charge
        List<CabinetMedical> cabinets = cabinetRepository.findAll();
        if (cabinets.isEmpty()) {
            System.out.println("Aucun cabinet trouvé pour créer une charge");
            return null;
        }

        CabinetMedical cabinet = cabinets.get(0);

        // Créer une charge
        Charges charge = Charges.builder()
                .titre("Achat Matériel")
                .description("Achat de matériel dentaire")
                .montant(1500.0)
                .date(LocalDateTime.now())
                .idCabinet(cabinet.getIdCabinet())
                .build();

        chargesRepository.create(charge);
        System.out.println("Charge créée : " + charge);
        return charge;
    }

    public static void selectCharge() {
        System.out.println("\n--- SELECT CHARGE VIA REPOSITORY ---");

        // findAll()
        List<Charges> charges = chargesRepository.findAll();
        System.out.println("Nombre total de charges : " + charges.size());

        if (!charges.isEmpty()) {
            Charges firstCharge = charges.get(0);

            // findById()
            Charges chargeById = chargesRepository.findById(firstCharge.getIdCharge());
            System.out.println("Charge par ID : " + chargeById);

            // findByCabinet
            //List<Charges> chargesCabinet = chargesRepository.findByCabinet(firstCharge.getIdCabinet());
            //System.out.println("Charges pour cabinet " + firstCharge.getIdCabinet() +
            //        " : " + chargesCabinet.size());

            // findByDateBetween
            LocalDateTime start = LocalDateTime.now().minusDays(30);
            LocalDateTime end = LocalDateTime.now();
            List<Charges> chargesDateRange = chargesRepository.findByDateBetween(start, end);
            System.out.println("Charges entre " + start + " et " + end +
                    " : " + chargesDateRange.size());

            // getCabinetOfCharge
            CabinetMedical cabinet = chargesRepository.getCabinetOfCharge(firstCharge.getIdCharge());
            System.out.println("Cabinet associé à la charge : " + cabinet);
        }
    }

    public static void updateCharge() {
        System.out.println("\n--- UPDATE CHARGE VIA REPOSITORY ---");
        List<Charges> charges = chargesRepository.findAll();
        if (!charges.isEmpty()) {
            Charges charge = charges.get(0);
            charge.setMontant(charge.getMontant() + 500);
            charge.setTitre("Achat Matériel + Fournitures");
            chargesRepository.update(charge);
            System.out.println("Charge mise à jour : " + charge);
        }
    }

    /*
    // TEST DELETE CHARGE (COMMENTÉ)
    public static void deleteCharge(Charges charge) {
        System.out.println("\n--- DELETE CHARGE VIA REPOSITORY ---");
        if (charge != null && charge.getIdCharge() != null) {
            chargesRepository.deleteById(charge.getIdCharge());
            System.out.println("Charge supprimée avec ID: " + charge.getIdCharge());

            // Vérifier la suppression
            Charges deleted = chargesRepository.findById(charge.getIdCharge());
            System.out.println("Charge après suppression: " + (deleted == null ? "null" : "existe toujours"));
        }
    }
    */

    /* ================= REVENUES REPOSITORY ================= */
    public static Revenues insertRevenue() throws SQLException {
        System.out.println("\n--- INSERT REVENUE VIA REPOSITORY ---");

        // Chercher un cabinet pour associer le revenu
        List<CabinetMedical> cabinets = cabinetRepository.findAll();
        if (cabinets.isEmpty()) {
            System.out.println("Aucun cabinet trouvé pour créer un revenu");
            return null;
        }

        CabinetMedical cabinet = cabinets.get(0);

        // Créer un revenu
        Revenues revenue = Revenues.builder()
                .titre("Consultation")
                .description("Revenu pour consultation médicale")
                .montant(500.0)
                .date(LocalDateTime.now())
                .idCabinet(cabinet.getIdCabinet())
                .build();

        revenuesRepository.create(revenue);
        System.out.println("Revenu créé : " + revenue);
        return revenue;
    }

    public static void selectRevenue() {
        System.out.println("\n--- SELECT REVENUE VIA REPOSITORY ---");

        // findAll()
        List<Revenues> revenues = revenuesRepository.findAll();
        System.out.println("Nombre total de revenus : " + revenues.size());

        if (!revenues.isEmpty()) {
            Revenues firstRevenue = revenues.get(0);

            // findById()
            Revenues revenueById = revenuesRepository.findById(firstRevenue.getIdRevenu());
            System.out.println("Revenu par ID : " + revenueById);

            // findByCabinet
            List<Revenues> revenuesCabinet = revenuesRepository.findByCabinet(firstRevenue.getIdCabinet());
            System.out.println("Revenus pour cabinet " + firstRevenue.getIdCabinet() +
                    " : " + revenuesCabinet.size());

            // getCabinetOfRevenue
            CabinetMedical cabinet = revenuesRepository.getCabinetOfRevenue(firstRevenue.getIdRevenu());
            System.out.println("Cabinet associé au revenu : " + cabinet);
        }
    }

    public static void updateRevenue() {
        System.out.println("\n--- UPDATE REVENUE VIA REPOSITORY ---");
        List<Revenues> revenues = revenuesRepository.findAll();
        if (!revenues.isEmpty()) {
            Revenues revenue = revenues.get(0);
            revenue.setMontant(revenue.getMontant() + 200);
            revenue.setTitre("Consultation + Supplément");
            revenuesRepository.update(revenue);
            System.out.println("Revenu mis à jour : " + revenue);
        }
    }

    /*
    // TEST DELETE REVENUE (COMMENTÉ)
    public static void deleteRevenue(Revenues revenue) {
        System.out.println("\n--- DELETE REVENUE VIA REPOSITORY ---");
        if (revenue != null && revenue.getIdRevenu() != null) {
            revenuesRepository.deleteById(revenue.getIdRevenu());
            System.out.println("Revenue supprimé avec ID: " + revenue.getIdRevenu());

            // Vérifier la suppression
            Revenues deleted = revenuesRepository.findById(revenue.getIdRevenu());
            System.out.println("Revenue après suppression: " + (deleted == null ? "null" : "existe toujours"));
        }
    }
    */

    /* ================= STATISTIQUES REPOSITORY ================= */
    public static Statistiques insertStatistique() throws SQLException {
        System.out.println("\n--- INSERT STATISTIQUE VIA REPOSITORY ---");

        // Chercher un cabinet pour associer la statistique
        List<CabinetMedical> cabinets = cabinetRepository.findAll();
        if (cabinets.isEmpty()) {
            System.out.println("Aucun cabinet trouvé pour créer une statistique");
            return null;
        }

        CabinetMedical cabinet = cabinets.get(0);

        // Vérifier si une statistique existe déjà pour ce cabinet, cette période et ce mois
        LocalDate dateDebut = LocalDate.now().withDayOfMonth(1);
        LocalDate dateFin = LocalDate.now();

        // D'abord, chercher une statistique existante
        List<Statistiques> statsExistantes = statistiquesRepository.findByCabinet(cabinet.getIdCabinet());
        Statistiques statExistante = null;

        for (Statistiques s : statsExistantes) {
            if (s.getPeriode().equals("MENSUELLE") &&
                    s.getDateDebut().equals(dateDebut) &&
                    s.getDateFin().equals(dateFin)) {
                statExistante = s;
                break;
            }
        }

        if (statExistante != null) {
            System.out.println("Statistique existante trouvée : " + statExistante);
            return statExistante;
        }

        // Si aucune statistique n'existe pour ce mois, créer une nouvelle avec des dates uniques
        // Essayer différents mois jusqu'à trouver un mois disponible
        int moisEssai = dateDebut.getMonthValue();
        int anneeEssai = dateDebut.getYear();
        boolean trouve = false;

        for (int i = 0; i < 12; i++) { // Essayer jusqu'à 12 mois différents
            LocalDate nouvelleDateDebut = LocalDate.of(anneeEssai, moisEssai, 1);
            LocalDate nouvelleDateFin = nouvelleDateDebut.withDayOfMonth(nouvelleDateDebut.lengthOfMonth());

            // Vérifier si cette combinaison existe déjà
            boolean existeDeja = false;
            for (Statistiques s : statsExistantes) {
                if (s.getPeriode().equals("MENSUELLE") &&
                        s.getDateDebut().equals(nouvelleDateDebut)) {
                    existeDeja = true;
                    break;
                }
            }

            if (!existeDeja) {
                dateDebut = nouvelleDateDebut;
                dateFin = nouvelleDateFin;
                trouve = true;
                break;
            }

            // Passer au mois précédent
            moisEssai--;
            if (moisEssai < 1) {
                moisEssai = 12;
                anneeEssai--;
            }
        }

        if (!trouve) {
            // Si tous les mois des 12 derniers mois sont pris, utiliser un mois dans le passé lointain
            dateDebut = LocalDate.now().minusMonths(13).withDayOfMonth(1);
            dateFin = dateDebut.withDayOfMonth(dateDebut.lengthOfMonth());
        }

        // Créer une statistique
        Statistiques stat = Statistiques.builder()
                .idCabinet(cabinet.getIdCabinet())
                .periode("MENSUELLE")
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .nbPatients(50)
                .nbRdv(60)
                .nbConsultation(45)
                .revenuTotal(15000.0)
                .tauxAnnulation(5.0)
                .actePlusEffectif("Consultation")
                .categorie(CategorieStatistique.REVENUS)
                .build();

        statistiquesRepository.create(stat);
        System.out.println("Nouvelle statistique créée : " + stat);
        return stat;
    }

    public static void selectStatistique() {
        System.out.println("\n--- SELECT STATISTIQUE VIA REPOSITORY ---");

        // findAll()
        List<Statistiques> stats = statistiquesRepository.findAll();
        System.out.println("Nombre total de statistiques : " + stats.size());

        if (!stats.isEmpty()) {
            Statistiques firstStat = stats.get(0);

            // findById()
            Statistiques statById = statistiquesRepository.findById(firstStat.getIdStatistique());
            System.out.println("Statistique par ID : " + statById);

            // findByCabinet
            List<Statistiques> statsCabinet = statistiquesRepository.findByCabinet(firstStat.getIdCabinet());
            System.out.println("Statistiques pour cabinet " + firstStat.getIdCabinet() +
                    " : " + statsCabinet.size());

            // findByCategorie
            List<Statistiques> statsCategorie = statistiquesRepository.findByCategorie(CategorieStatistique.REVENUS);
            System.out.println("Statistiques par catégorie REVENUS : " + statsCategorie.size());

            // findByCabinetAndCategorie
            List<Statistiques> statsCabinetCat = statistiquesRepository.findByCabinetAndCategorie(
                    firstStat.getIdCabinet(), CategorieStatistique.REVENUS);
            System.out.println("Statistiques pour cabinet " + firstStat.getIdCabinet() +
                    " et catégorie REVENUS : " + statsCabinetCat.size());

            // findByDate
            List<Statistiques> statsDate = statistiquesRepository.findByDate(firstStat.getDateFin());
            System.out.println("Statistiques pour date " + firstStat.getDateFin() +
                    " : " + statsDate.size());
        }
    }

    public static void updateStatistique() {
        System.out.println("\n--- UPDATE STATISTIQUE VIA REPOSITORY ---");
        List<Statistiques> stats = statistiquesRepository.findAll();
        if (!stats.isEmpty()) {
            Statistiques stat = stats.get(0);
            stat.setNbPatients(stat.getNbPatients() + 10);
            stat.setRevenuTotal(stat.getRevenuTotal() + 2000);
            stat.setCategorie(CategorieStatistique.REVENUS);
            statistiquesRepository.update(stat);
            System.out.println("Statistique mise à jour : " + stat);
        }
    }

    /*
    // TEST DELETE STATISTIQUE (COMMENTÉ)
    public static void deleteStatistique(Statistiques statistique) {
        System.out.println("\n--- DELETE STATISTIQUE VIA REPOSITORY ---");
        if (statistique != null && statistique.getIdStatistique() != null) {
            statistiquesRepository.deleteById(statistique.getIdStatistique());
            System.out.println("Statistique supprimée avec ID: " + statistique.getIdStatistique());

            // Vérifier la suppression
            Statistiques deleted = statistiquesRepository.findById(statistique.getIdStatistique());
            System.out.println("Statistique après suppression: " + (deleted == null ? "null" : "existe toujours"));
        }
    }
    */
}