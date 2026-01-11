package ma.dentalTech.service.test;

import ma.dentalTech.configuration.ApplicationContext;

import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.patient.Antecedent;

import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;

import ma.dentalTech.service.modules.auth.api.AuthService;
import ma.dentalTech.service.modules.auth.dto.AuthRequest;
import ma.dentalTech.service.modules.auth.dto.AuthResult;

import ma.dentalTech.service.modules.patient.api.PatientService;
import ma.dentalTech.service.modules.patient.api.AntecedentService;

import ma.dentalTech.service.modules.notifications.api.NotificationService;
import ma.dentalTech.entities.enums.TitreNotification;
import ma.dentalTech.entities.enums.TypeNotification;

import java.time.LocalDate;
import java.util.List;

public class TestWorkflowServices {

    public static void main(String[] args) throws Exception {

        AuthService authService = ApplicationContext.getBean(AuthService.class);
        PatientService patientService = ApplicationContext.getBean(PatientService.class);
        AntecedentService antecedentService = ApplicationContext.getBean(AntecedentService.class);
        NotificationService notificationService = ApplicationContext.getBean(NotificationService.class);

        if (authService == null) throw new IllegalStateException("AuthService bean is null");
        if (patientService == null) throw new IllegalStateException("PatientService bean is null");
        if (antecedentService == null) throw new IllegalStateException("AntecedentService bean is null");

        System.out.println("========== 1) AUTH OK ==========");
        AuthResult ok = authService.authenticate(
                AuthRequest.builder()
                        .login("admin")          // <-- change si besoin
                        .password("admin123")        // <-- change si besoin
                        .build()
        );

        System.out.println(ok);
        if (!ok.isSuccess()) throw new IllegalStateException("Auth should be OK, got: " + ok.getMessage());

        System.out.println("\n========== 2) AUTH FAIL ==========");
        AuthResult fail = authService.authenticate(
                AuthRequest.builder()
                        .login("admin")
                        .password("wrong")
                        .build()
        );
        System.out.println(fail);

        System.out.println("\n========== 3) CREATE PATIENT ==========");
        Patient p = new Patient();

        // ⚠️ adapte selon ta classe Patient (noms exacts des setters)
        p.setNom("Patient Test");
        p.setTelephone("0600" + (int)(Math.random() * 1000000));

        p.setSexe(Sexe.MASCULIN);
        p.setAssurance(Assurance.AUCUNE);
        p.setDateNaissance(LocalDate.of(2000, 1, 1));

        // audit
        p.setCreePar(ok.getEmail());
        p.setModifiePar(ok.getEmail());

        patientService.create(p);
        System.out.println("DEBUG idEntite=" + p.getId() + " idPatient=" + p.getIdPatient());


        Long patientId = p.getIdPatient(); // <-- assure-toi que ton repo set la PK
        if (patientId == null) {
            throw new IllegalStateException("Patient idPatient is null after create. Check repo keys mapping.");
        }

        System.out.println("Patient created idPatient=" + patientId);

        System.out.println("\n========== 4) LOAD 2 ANTECEDENTS (seeded) ==========");
        Antecedent a1 = antecedentService.findByNom("Hypertension")
                .orElseThrow(() -> new IllegalStateException("Antecedent 'Hypertension' not found - run SeedAntecedents"));

        Antecedent a2 = antecedentService.findByNom("Allergie pénicilline")
                .orElseThrow(() -> new IllegalStateException("Antecedent 'Allergie pénicilline' not found - run SeedAntecedents"));

        System.out.println("a1=" + a1);
        System.out.println("a2=" + a2);

        Long idA1 = a1.getIdAntecedent();
        Long idA2 = a2.getIdAntecedent();
        if (idA1 == null || idA2 == null) throw new IllegalStateException("Antecedent IDs are null");

        System.out.println("\n========== 5) LINK patient_antecedent ==========");
        patientService.addAntecedentToPatient(patientId, idA1);
        patientService.addAntecedentToPatient(patientId, idA2);
        System.out.println("Links inserted.");

        System.out.println("\n========== 6) FETCH + PRINT PATIENT ANTECEDENTS ==========");
        List<Antecedent> ants = patientService.getAntecedentsOfPatient(patientId);
        System.out.println("Patient " + patientId + " antecedents count=" + ants.size());
        for (Antecedent a : ants) {
            System.out.println(" - " + a.getNom() + " | cat=" + a.getCategorie() + " | risque=" + a.getNiveauDeRisque());
        }

        System.out.println("\n========== 7) OPTIONAL NOTIFICATION ==========");
        if (notificationService != null) {
            notificationService.sendToUser(
                    ok.getIdUser(),
                    TitreNotification.INFO,
                    "Workflow OK: patient " + patientId + " created + antecedents linked",
                    TypeNotification.MESSAGE_SYSTEME
            );
            System.out.println("Notification sent to userId=" + ok.getIdUser());
        } else {
            System.out.println("NotificationService bean not configured -> skip");
        }

        System.out.println("\n✅ WORKFLOW DONE.");
    }
}
