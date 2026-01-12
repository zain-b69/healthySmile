package ma.dentalTech.service.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.entities.dossierMedical.PrescriptionMedicament;
import ma.dentalTech.entities.dossierMedical.SituationFinanciere;
import ma.dentalTech.repository.modules.dossierMedical.api.SituationFinanciereRepo;
import ma.dentalTech.service.modules.dossierMedicale.api.OrdonnanceService;
import ma.dentalTech.service.modules.dossierMedicale.api.PrescriptionService;
import ma.dentalTech.service.modules.dossierMedicale.api.SituationFinanciereService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

public class TestServicesRunner {

    public static void main(String[] args) throws Exception {

        // ===== Services =====
        SituationFinanciereService sfService = ApplicationContext.getBean(SituationFinanciereService.class);
        OrdonnanceService ordonnanceService = ApplicationContext.getBean(OrdonnanceService.class);
        PrescriptionService prescriptionService = ApplicationContext.getBean(PrescriptionService.class);

        // ===== Repo (juste pour récupérer idSf facilement) =====
        SituationFinanciereRepo sfRepo = ApplicationContext.getBean(SituationFinanciereRepo.class);

        // =========================================================
        // 1) TEST SituationFinanciereService
        // =========================================================
        SituationFinanciere sf = sfRepo.findPage(1, 0).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Aucune situation_financiere en base. Insère-en une d'abord."
                ));

        Long idSf = sf.getIdSf();
        System.out.println("✅ [SF] idSf utilisé = " + idSf);

        System.out.println("[SF] Reste à payer = " + sfService.calculerResteAPayer(idSf));

        sfService.enregistrerPaiement(idSf, 50.0);
        System.out.println("✅ [SF] paiement OK");

        sfService.recalculerCreditEtStatut(idSf);
        System.out.println("✅ [SF] recalcul OK");

        // =========================================================
        // 2) TEST OrdonnanceService + PrescriptionService
        //    -> on récupère automatiquement 1 id_dm, 1 id_consultation, 1 id_mct
        // =========================================================
        Long idDm = pickFirstLong("SELECT id_dm FROM dossier_medicale ORDER BY id_dm DESC LIMIT 1");
        if (idDm == null) {
            throw new IllegalStateException("Aucun dossier_medicale en base. Insère au moins 1 dossier.");
        }

        Long idConsultation = pickFirstLong(
                "SELECT id_consultation FROM consultation WHERE id_dm = " + idDm + " ORDER BY id_consultation DESC LIMIT 1"
        );
        if (idConsultation == null) {
            throw new IllegalStateException("Aucune consultation liée au dossier id_dm=" + idDm + ". Insère au moins 1 consultation.");
        }

        Long idMct = pickFirstLong("SELECT id_mct FROM medicament ORDER BY id_mct DESC LIMIT 1");
        if (idMct == null) {
            throw new IllegalStateException("Aucun medicament en base. Insère au moins 1 medicament.");
        }

        System.out.println("✅ [DATA] idDm=" + idDm + " | idConsultation=" + idConsultation + " | idMct=" + idMct);

        Ordonnance o = Ordonnance.builder()
                .idDm(idDm)
                .idConsultation(idConsultation)
                .date(LocalDate.now())
                .build();
        // champs BaseEntity (pas dans le builder)
        o.setCreePar("test");
        o.setModifiePar("test");

        Prescription p = Prescription.builder()
                .categorie("ANTIBIOTIQUE")
                .frequence("2x/j")
                .dureeEnJours(5)
                .build();
        // champs BaseEntity (pas dans le builder)
        p.setCreePar("test");
        p.setModifiePar("test");

        // Pour satisfaire la validation "au moins 1 medicament"
        p.getLignes().add(PrescriptionMedicament.builder()
                .idMct(idMct)
                .quantite(10)
                .build());

        ordonnanceService.createOrdonnanceAvecPrescriptions(o, List.of(p));
        System.out.println("✅ [ORD] Ordonnance créée idOrd=" + o.getIdOrd());

        List<Prescription> pres = prescriptionService.findByOrdonnance(o.getIdOrd());
        System.out.println("✅ [PR] Prescriptions de l'ordonnance = " + pres.size());

        if (!pres.isEmpty()) {
            Long idPr = pres.get(0).getIdPr();
            System.out.println("✅ [PR] idPr=" + idPr);

            // Test pivot quantité (si présent dans ton service)
            prescriptionService.updateLineQuantity(idPr, idMct, 20);
            System.out.println("✅ [PR] updateLineQuantity OK");

            System.out.println("[PR] Quantité totale = " + prescriptionService.calculerQuantiteTotale(idPr));
            System.out.println("[PR] Résumé = " + prescriptionService.resumerPrescription(idPr));
        }

        System.out.println("✅ Tests (SF + Ordonnance + Prescription) terminés");
    }

    /**
     * Helper JDBC pour récupérer un Long (1ère colonne de la 1ère ligne),
     * en réutilisant la connexion du projet via SessionFactory.
     */
    private static Long pickFirstLong(String sql) {
        try (Connection cn = SessionFactory.getInstance().getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            return rs.next() ? rs.getLong(1) : null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL dans pickFirstLong: " + sql, e);
        }
    }
}
