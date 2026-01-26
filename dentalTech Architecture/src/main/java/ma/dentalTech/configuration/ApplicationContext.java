package ma.dentalTech.configuration;

import ma.dentalTech.mvc.controllers.modules.auth.api.AuthController;
import ma.dentalTech.mvc.controllers.modules.dossierMedical.util.ConsultationWorkflowController;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;

import ma.dentalTech.repository.modules.dossierMedical.api.*;
import ma.dentalTech.repository.modules.dossierMedical.impl.DossierMedicalRepoImpl;
import ma.dentalTech.repository.modules.facturation.api.FactureRepo;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.repository.modules.users.api.MedecinRepository;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;

import ma.dentalTech.service.modules.auth.api.AuthService;
import ma.dentalTech.service.modules.auth.api.CredentialsValidator;
import ma.dentalTech.service.modules.auth.api.PasswordEncoder;

import ma.dentalTech.service.modules.caisse.api.CaisseService;
import ma.dentalTech.service.modules.caisse.impl.CaisseServiceImpl;
import ma.dentalTech.service.modules.dossierMedicale.api.*;
import ma.dentalTech.service.modules.dossierMedicale.impl.DossierMedicalServiceImpl;
import ma.dentalTech.service.modules.notifications.api.NotificationService;

import ma.dentalTech.service.modules.patient.api.AntecedentService;
import ma.dentalTech.service.modules.patient.api.PatientService;

import ma.dentalTech.service.modules.users.api.RoleManagementService;
import ma.dentalTech.service.modules.users.api.UserManagementService;
import ma.dentalTech.service.modules.users.api.UserValidatorService;
import ma.dentalTech.service.modules.auth.api.AuthorizationService;
import ma.dentalTech.mvc.controllers.modules.dashboard.api.DashboardController;


import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static ma.dentalTech.service.modules.users.impl.UserManagementServiceImpl.notBlank;

public final class ApplicationContext {

    private static final Map<Class<?>, Object> context = new HashMap<>();
    private static final Map<String, Object> contextByName = new HashMap<>();

    static {
        try {
            // ============================================================
            // 0) Load beans.properties
            // ============================================================
            Properties props = loadProps("config/beans.properties");

            // ============================================================
            // 0bis) Load db.properties (only if datasource bean is used)
            // ============================================================
            Properties dbProps = tryLoadProps("config/db.properties"); // may be null

            // ============================================================
            // 1) PATIENT (REQUIRED by prof)
            // ============================================================
            String patientRepoClass = props.getProperty("patientRepo");
            String patientServiceClass = props.getProperty("patientService");
            String patientControllerClass = props.getProperty("patientController");

            if (isBlank(patientRepoClass) || isBlank(patientServiceClass) || isBlank(patientControllerClass)) {
                throw new IllegalStateException("beans.properties doit contenir: patientRepo, patientService, patientController");
            }

            PatientRepository patientRepo = newNoArg(patientRepoClass, PatientRepository.class);
            putBean(PatientRepository.class, "patientRepo", patientRepo);

            PatientService patientService = new1Dep(patientServiceClass, PatientService.class, PatientRepository.class, patientRepo);
            putBean(PatientService.class, "patientService", patientService);

            PatientController patientController = new1Dep(patientControllerClass, PatientController.class, PatientService.class, patientService);
            putBean(PatientController.class, "patientController", patientController);

            // ============================================================
            // 2) OPTIONAL: antecedent repo/service/controller
            // ============================================================
            String antecedentRepoClass = props.getProperty("antecedentRepo");
            if (!isBlank(antecedentRepoClass)) {

                AntecedentRepository antecedentRepo = newNoArg(antecedentRepoClass, AntecedentRepository.class);
                putBean(AntecedentRepository.class, "antecedentRepo", antecedentRepo);

                String antecedentServiceClass = props.getProperty("antecedentService");
                if (!isBlank(antecedentServiceClass)) {
                    AntecedentService antecedentService = new1Dep(
                            antecedentServiceClass,
                            AntecedentService.class,
                            AntecedentRepository.class,
                            antecedentRepo
                    );
                    putBean(AntecedentService.class, "antecedentService", antecedentService);

                    // ✅ controller
                    String antecedentControllerClass = props.getProperty("antecedentController");
                    if (!isBlank(antecedentControllerClass)) {
                        var ctrl = (ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentController)
                                Class.forName(antecedentControllerClass)
                                        .getDeclaredConstructor(AntecedentService.class)
                                        .newInstance(antecedentService);

                        putBean(ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentController.class,
                                "antecedentController",
                                ctrl);
                    }
                }
            }

            // ============================================================
            // 3) OPTIONAL: utilisateur repo (needed by auth/users/notifications)
            // ============================================================
            String utilisateurRepoClass = props.getProperty("utilisateurRepo");
            if (!isBlank(utilisateurRepoClass)) {
                UtilisateurRepository utilisateurRepo = newNoArg(utilisateurRepoClass, UtilisateurRepository.class);
                putBean(UtilisateurRepository.class, "utilisateurRepo", utilisateurRepo);
            }

            // ============================================================
            // 4) AUTH (optional)
            // ============================================================
            String passwordEncoderClass = props.getProperty("passwordEncoder");
            if (!isBlank(passwordEncoderClass)) {
                PasswordEncoder encoder = newNoArg(passwordEncoderClass, PasswordEncoder.class);
                putBean(PasswordEncoder.class, "passwordEncoder", encoder);
            }

            String credentialsValidatorClass = props.getProperty("credentialsValidator");
            if (!isBlank(credentialsValidatorClass)) {
                CredentialsValidator validator = newNoArg(credentialsValidatorClass, CredentialsValidator.class);
                putBean(CredentialsValidator.class, "credentialsValidator", validator);
            }

            String authServiceClass = props.getProperty("authService");
            if (!isBlank(authServiceClass)) {
                UtilisateurRepository utilisateurRepo = getBean(UtilisateurRepository.class);
                PasswordEncoder encoder = getBean(PasswordEncoder.class);
                CredentialsValidator validator = getBean(CredentialsValidator.class);

                if (utilisateurRepo == null) throw new IllegalStateException("utilisateurRepo bean not found (required for authService)");
                if (encoder == null) throw new IllegalStateException("passwordEncoder bean not found (required for authService)");
                if (validator == null) throw new IllegalStateException("credentialsValidator bean not found (required for authService)");

                AuthService authService = (AuthService) Class.forName(authServiceClass)
                        .getDeclaredConstructor(UtilisateurRepository.class, PasswordEncoder.class, CredentialsValidator.class)
                        .newInstance(utilisateurRepo, encoder, validator);

                putBean(AuthService.class, "authService", authService);

                String authControllerClass = props.getProperty("authController");
                if (!isBlank(authControllerClass)) {
                    AuthController authController = (AuthController) Class.forName(authControllerClass)
                            .getDeclaredConstructor(AuthService.class)
                            .newInstance(authService);
                    putBean(AuthController.class, "authController", authController);
                }
            }

            // ============================================================
            // 5) USERS SERVICES (optional)
            // ============================================================
            String userValidatorServiceClass = props.getProperty("userValidatorService");
            if (!isBlank(userValidatorServiceClass)) {
                UserValidatorService userValidatorService = newNoArg(userValidatorServiceClass, UserValidatorService.class);
                putBean(UserValidatorService.class, "userValidatorService", userValidatorService);
            }

            String roleManagementServiceClass = props.getProperty("roleManagementService");
            if (!isBlank(roleManagementServiceClass)) {
                RoleManagementService roleService = newNoArg(roleManagementServiceClass, RoleManagementService.class);
                putBean(RoleManagementService.class, "roleManagementService", roleService);
            }

            String userManagementServiceClass = props.getProperty("userManagementService");
            if (!isBlank(userManagementServiceClass)) {
                UtilisateurRepository utilisateurRepo = getBean(UtilisateurRepository.class);
                PasswordEncoder encoder = getBean(PasswordEncoder.class);

                if (utilisateurRepo == null) throw new IllegalStateException("utilisateurRepo bean not found (required for userManagementService)");
                if (encoder == null) throw new IllegalStateException("passwordEncoder bean not found (required for userManagementService)");

                UserManagementService userService = (UserManagementService) Class.forName(userManagementServiceClass)
                        .getDeclaredConstructor(UtilisateurRepository.class, PasswordEncoder.class)
                        .newInstance(utilisateurRepo, encoder);

                putBean(UserManagementService.class, "userManagementService", userService);
            }

            // ============================================================
            // PASSWORD MANAGEMENT SERVICE (optional)  (UtilisateurRepo + PasswordEncoder + CredentialsValidator)
            // ============================================================
            String passwordManagementServiceClass = props.getProperty("passwordManagementService");
            if (!isBlank(passwordManagementServiceClass)) {

                UtilisateurRepository utilisateurRepo = getBean(UtilisateurRepository.class);
                PasswordEncoder encoder = getBean(PasswordEncoder.class);
                CredentialsValidator validator = getBean(CredentialsValidator.class);

                if (utilisateurRepo == null)
                    throw new IllegalStateException("utilisateurRepo bean not found (required for passwordManagementService)");
                if (encoder == null)
                    throw new IllegalStateException("passwordEncoder bean not found (required for passwordManagementService)");
                if (validator == null)
                    throw new IllegalStateException("credentialsValidator bean not found (required for passwordManagementService)");

                Object svc = Class.forName(passwordManagementServiceClass)
                        .getDeclaredConstructor(UtilisateurRepository.class, PasswordEncoder.class, CredentialsValidator.class)
                        .newInstance(utilisateurRepo, encoder, validator);

                contextByName.put("passwordManagementService", svc);
                context.put(ma.dentalTech.service.modules.users.api.PasswordManagementService.class, svc);
            }


            // ============================================================
            // 6) NOTIFICATIONS (optional)
            // ============================================================
            String notificationRepoClass = props.getProperty("notificationRepo");
            if (!isBlank(notificationRepoClass)) {
                NotificationRepository notificationRepo = newNoArg(notificationRepoClass, NotificationRepository.class);
                putBean(NotificationRepository.class, "notificationRepo", notificationRepo);

                String notificationServiceClass = props.getProperty("notificationService");
                if (!isBlank(notificationServiceClass)) {
                    UtilisateurRepository utilisateurRepo = getBean(UtilisateurRepository.class);
                    if (utilisateurRepo == null) throw new IllegalStateException("utilisateurRepo bean not found (required for notificationService)");

                    NotificationService notifService = (NotificationService) Class.forName(notificationServiceClass)
                            .getDeclaredConstructor(NotificationRepository.class, UtilisateurRepository.class)
                            .newInstance(notificationRepo, utilisateurRepo);

                    putBean(NotificationService.class, "notificationService", notifService);
                }
            }

            // =====================
            // DOSSIER MEDICAL SERVICES
            // =====================

            // =====================
            // AGENDA / RDV (repos)
            // =====================
            String agendaMensuelRepoClass = props.getProperty("agendaMensuelRepo");
            if (!isBlank(agendaMensuelRepoClass)) {
                var agendaRepo = newNoArg(agendaMensuelRepoClass, ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository.class);
                putBean(ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository.class, "agendaMensuelRepo", agendaRepo);
            }

            String rdvRepoClass = props.getProperty("rdvRepo");
            if (!isBlank(rdvRepoClass)) {
                var rdvRepo = newNoArg(rdvRepoClass, ma.dentalTech.repository.modules.agenda.api.RDVRepository.class);
                putBean(ma.dentalTech.repository.modules.agenda.api.RDVRepository.class, "rdvRepo", rdvRepo);
            }

            // MedecinRepo (utilisé par RDVService)
            String medecinRepoClass = props.getProperty("medecinRepo");
            if (!isBlank(medecinRepoClass)) {
                var medRepo = newNoArg(medecinRepoClass, ma.dentalTech.repository.modules.users.api.MedecinRepository.class);
                putBean(ma.dentalTech.repository.modules.users.api.MedecinRepository.class, "medecinRepo", medRepo);
            }

            // =====================
            // RDV Service + Controller
            // =====================
            String rdvServiceClass = props.getProperty("rdvService");
            if (!isBlank(rdvServiceClass)) {
                var rdvRepo = getBean(ma.dentalTech.repository.modules.agenda.api.RDVRepository.class);
                var agendaRepo = getBean(ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository.class);
                var medRepo = getBean(MedecinRepository.class);

                if (rdvRepo == null || agendaRepo == null || patientRepo == null || medRepo == null)
                    throw new IllegalStateException("Deps missing for rdvService");

                var svc = (ma.dentalTech.service.modules.agendas.api.RDVService) Class.forName(rdvServiceClass)
                        .getDeclaredConstructor(
                                ma.dentalTech.repository.modules.agenda.api.RDVRepository.class,
                                ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository.class,
                                ma.dentalTech.repository.modules.patient.api.PatientRepository.class,
                                ma.dentalTech.repository.modules.users.api.MedecinRepository.class
                        )
                        .newInstance(rdvRepo, agendaRepo, patientRepo, medRepo);

                putBean(ma.dentalTech.service.modules.agendas.api.RDVService.class, "rdvService", svc);
            }

            String rdvControllerClass = props.getProperty("rdvController");
            if (!isBlank(rdvControllerClass)) {
                var svc = getBean(ma.dentalTech.service.modules.agendas.api.RDVService.class);
                if (svc == null) throw new IllegalStateException("rdvService missing for rdvController");

                var ctrl = (ma.dentalTech.mvc.controllers.modules.agenda.api.RDVController) Class.forName(rdvControllerClass)
                        .getDeclaredConstructor(ma.dentalTech.service.modules.agendas.api.RDVService.class)
                        .newInstance(svc);

                putBean(ma.dentalTech.mvc.controllers.modules.agenda.api.RDVController.class, "rdvController", ctrl);
            }

            // =====================
            // AGENDA Service
            // =====================
            String agendaMensuelServiceClass = props.getProperty("agendaMensuelService");
            if (!isBlank(agendaMensuelServiceClass)) {

                Object svcObj;
                Class<?> cls = Class.forName(agendaMensuelServiceClass);

                var agendaRepo = getBean(ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository.class);
                var medRepo = getBean(ma.dentalTech.repository.modules.users.api.MedecinRepository.class);
                var rdvRepo = getBean(ma.dentalTech.repository.modules.agenda.api.RDVRepository.class);

                try {
                    // 1) try (AgendaMensuelRepository)
                    svcObj = cls.getDeclaredConstructor(ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository.class)
                            .newInstance(agendaRepo);

                } catch (NoSuchMethodException e1) {
                    try {
                        // 2) try (AgendaMensuelRepository, MedecinRepository)
                        svcObj = cls.getDeclaredConstructor(
                                        ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository.class,
                                        ma.dentalTech.repository.modules.users.api.MedecinRepository.class
                                )
                                .newInstance(agendaRepo, medRepo);

                    } catch (NoSuchMethodException e2) {
                        try {
                            // 3) try (AgendaMensuelRepository, RDVRepository)
                            svcObj = cls.getDeclaredConstructor(
                                            ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository.class,
                                            ma.dentalTech.repository.modules.agenda.api.RDVRepository.class
                                    )
                                    .newInstance(agendaRepo, rdvRepo);

                        } catch (NoSuchMethodException e3) {
                            // 4) fallback no-arg
                            svcObj = cls.getDeclaredConstructor().newInstance();
                        }
                    }
                }

                var svc = (ma.dentalTech.service.modules.agendas.api.AgendaMensuelService) svcObj;
                putBean(ma.dentalTech.service.modules.agendas.api.AgendaMensuelService.class, "agendaMensuelService", svc);
            }


            // =====================
            // AGENDA Controller
            // =====================
            String agendaMensuelControllerClass = props.getProperty("agendaMensuelController");
            if (!isBlank(agendaMensuelControllerClass)) {
                var agendaSvc = getBean(ma.dentalTech.service.modules.agendas.api.AgendaMensuelService.class);
                var medRepo = getBean(ma.dentalTech.repository.modules.users.api.MedecinRepository.class);

                if (agendaSvc == null) throw new IllegalStateException("agendaMensuelService missing for agendaMensuelController");
                if (medRepo == null) throw new IllegalStateException("medecinRepo missing for agendaMensuelController");

                var ctrl = (ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaMensuelController) Class.forName(agendaMensuelControllerClass)
                        .getDeclaredConstructor(
                                ma.dentalTech.service.modules.agendas.api.AgendaMensuelService.class,
                                ma.dentalTech.repository.modules.users.api.MedecinRepository.class
                        )
                        .newInstance(agendaSvc, medRepo);

                putBean(ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaMensuelController.class, "agendaMensuelController", ctrl);
            }
            // ============================================================
            // DOSSIER MEDICAL MODULE (Repos -> Services -> Controller)
            // ============================================================

// ---- 1) D'abord tous les REPOS ----
            String dossierMedicalRepoClass = props.getProperty("dossierMedicalRepo");
            String consultationRepoClass = props.getProperty("consultationRepo");
            String interventionMedecinRepoClass = props.getProperty("interventionMedecinRepo");
            String acteRepoClass = props.getProperty("acteRepo");
            String medicamentRepoClass = props.getProperty("medicamentRepo");
            String certificatRepoClass = props.getProperty("certificatRepo");
            String ordonnanceRepoClass = props.getProperty("ordonnanceRepo");
            String prescriptionRepoClass = props.getProperty("prescriptionRepo");
            String situationFinanciereRepoClass = props.getProperty("situationFinanciereRepo");

// Instancier uniquement si pas déjà fait
            if (!isBlank(dossierMedicalRepoClass) && getBean(ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepo.class) == null) {
                var repo = newNoArg(dossierMedicalRepoClass, ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepo.class);
                putBean(ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepo.class, "dossierMedicalRepo", repo);
            }

            if (!isBlank(consultationRepoClass) && getBean(ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo.class) == null) {
                var repo = newNoArg(consultationRepoClass, ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo.class);
                putBean(ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo.class, "consultationRepo", repo);
            }

            if (!isBlank(interventionMedecinRepoClass) && getBean(ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepo.class) == null) {
                var repo = newNoArg(interventionMedecinRepoClass, ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepo.class);
                putBean(ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepo.class, "interventionMedecinRepo", repo);
            }

            if (!isBlank(acteRepoClass) && getBean(ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository.class) == null) {
                var repo = newNoArg(acteRepoClass, ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository.class);
                putBean(ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository.class, "acteRepo", repo);
            }

            if (!isBlank(medicamentRepoClass) && getBean(ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepo.class) == null) {
                var repo = newNoArg(medicamentRepoClass, ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepo.class);
                putBean(ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepo.class, "medicamentRepo", repo);
            }

            if (!isBlank(certificatRepoClass) && getBean(ma.dentalTech.repository.modules.dossierMedical.api.CertificatRepo.class) == null) {
                var repo = newNoArg(certificatRepoClass, ma.dentalTech.repository.modules.dossierMedical.api.CertificatRepo.class);
                putBean(ma.dentalTech.repository.modules.dossierMedical.api.CertificatRepo.class, "certificatRepo", repo);
            }

            if (!isBlank(ordonnanceRepoClass) && getBean(ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepo.class) == null) {
                var repo = newNoArg(ordonnanceRepoClass, ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepo.class);
                putBean(ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepo.class, "ordonnanceRepo", repo);
            }

            if (!isBlank(prescriptionRepoClass) && getBean(ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo.class) == null) {
                var repo = newNoArg(prescriptionRepoClass, ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo.class);
                putBean(ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo.class, "prescriptionRepo", repo);
            }

            if (!isBlank(situationFinanciereRepoClass) && getBean(ma.dentalTech.repository.modules.dossierMedical.api.SituationFinanciereRepo.class) == null) {
                var repo = newNoArg(situationFinanciereRepoClass, ma.dentalTech.repository.modules.dossierMedical.api.SituationFinanciereRepo.class);
                putBean(ma.dentalTech.repository.modules.dossierMedical.api.SituationFinanciereRepo.class, "situationFinanciereRepo", repo);
            }

// ---- 2) Ensuite tous les SERVICES ----
            String dossierMedicalServiceClass = props.getProperty("dossierMedicalService");
            String consultationServiceClass = props.getProperty("consultationService");
            String acteServiceClass = props.getProperty("acteService");
            String medicamentServiceClass = props.getProperty("medicamentService");
            String certificatServiceClass = props.getProperty("certificatService");
            String ordonnanceServiceClass = props.getProperty("ordonnanceService");
            String prescriptionServiceClass = props.getProperty("prescriptionService");
            String situationFinanciereServiceClass = props.getProperty("situationFinanciereService");
            String interventionMedecinServiceClass = props.getProperty("interventionMedecinService");

// Services simples (1 dépendance)
            if (!isBlank(dossierMedicalServiceClass) && getBean(ma.dentalTech.service.modules.dossierMedicale.api.DossierMedicalService.class) == null) {
                var repo = getBean(ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepo.class);
                if (repo == null) throw new IllegalStateException("dossierMedicalRepo manquant");

                var service = (ma.dentalTech.service.modules.dossierMedicale.api.DossierMedicalService)
                        Class.forName(dossierMedicalServiceClass)
                                .getDeclaredConstructor(ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepo.class)
                                .newInstance(repo);
                putBean(ma.dentalTech.service.modules.dossierMedicale.api.DossierMedicalService.class, "dossierMedicalService", service);
            }

            if (!isBlank(consultationServiceClass) && getBean(ma.dentalTech.service.modules.dossierMedicale.api.ConsultationService.class) == null) {
                var repo = getBean(ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo.class);
                if (repo == null) throw new IllegalStateException("consultationRepo manquant");

                var service = (ma.dentalTech.service.modules.dossierMedicale.api.ConsultationService)
                        Class.forName(consultationServiceClass)
                                .getDeclaredConstructor(ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo.class)
                                .newInstance(repo);
                putBean(ma.dentalTech.service.modules.dossierMedicale.api.ConsultationService.class, "consultationService", service);
            }

            if (!isBlank(acteServiceClass) && getBean(ma.dentalTech.service.modules.dossierMedicale.api.ActeService.class) == null) {
                var repo = getBean(ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository.class);
                if (repo == null) throw new IllegalStateException("acteRepo manquant");

                var service = (ma.dentalTech.service.modules.dossierMedicale.api.ActeService)
                        Class.forName(acteServiceClass)
                                .getDeclaredConstructor(ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository.class)
                                .newInstance(repo);
                putBean(ma.dentalTech.service.modules.dossierMedicale.api.ActeService.class, "acteService", service);
            }

            if (!isBlank(medicamentServiceClass) && getBean(ma.dentalTech.service.modules.dossierMedicale.api.MedicamentService.class) == null) {
                var repo = getBean(ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepo.class);
                if (repo == null) throw new IllegalStateException("medicamentRepo manquant");

                var service = (ma.dentalTech.service.modules.dossierMedicale.api.MedicamentService)
                        Class.forName(medicamentServiceClass)
                                .getDeclaredConstructor(ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepo.class)
                                .newInstance(repo);
                putBean(ma.dentalTech.service.modules.dossierMedicale.api.MedicamentService.class, "medicamentService", service);
            }

            if (!isBlank(certificatServiceClass) && getBean(ma.dentalTech.service.modules.dossierMedicale.api.CertificatService.class) == null) {
                var repo = getBean(ma.dentalTech.repository.modules.dossierMedical.api.CertificatRepo.class);
                if (repo == null) throw new IllegalStateException("certificatRepo manquant");

                var service = (ma.dentalTech.service.modules.dossierMedicale.api.CertificatService)
                        Class.forName(certificatServiceClass)
                                .getDeclaredConstructor(ma.dentalTech.repository.modules.dossierMedical.api.CertificatRepo.class)
                                .newInstance(repo);
                putBean(ma.dentalTech.service.modules.dossierMedicale.api.CertificatService.class, "certificatService", service);
            }

            if (!isBlank(situationFinanciereServiceClass) && getBean(ma.dentalTech.service.modules.dossierMedicale.api.SituationFinanciereService.class) == null) {
                var repo = getBean(ma.dentalTech.repository.modules.dossierMedical.api.SituationFinanciereRepo.class);
                if (repo == null) throw new IllegalStateException("situationFinanciereRepo manquant");

                var service = (ma.dentalTech.service.modules.dossierMedicale.api.SituationFinanciereService)
                        Class.forName(situationFinanciereServiceClass)
                                .getDeclaredConstructor(ma.dentalTech.repository.modules.dossierMedical.api.SituationFinanciereRepo.class)
                                .newInstance(repo);
                putBean(ma.dentalTech.service.modules.dossierMedicale.api.SituationFinanciereService.class, "situationFinanciereService", service);
            }

// Services avec 2 dépendances
            if (!isBlank(ordonnanceServiceClass) && getBean(ma.dentalTech.service.modules.dossierMedicale.api.OrdonnanceService.class) == null) {
                var ordRepo = getBean(ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepo.class);
                var presRepo = getBean(ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo.class);
                if (ordRepo == null) throw new IllegalStateException("ordonnanceRepo manquant");
                if (presRepo == null) throw new IllegalStateException("prescriptionRepo manquant");

                var service = (ma.dentalTech.service.modules.dossierMedicale.api.OrdonnanceService)
                        Class.forName(ordonnanceServiceClass)
                                .getDeclaredConstructor(
                                        ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepo.class,
                                        ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo.class
                                )
                                .newInstance(ordRepo, presRepo);
                putBean(ma.dentalTech.service.modules.dossierMedicale.api.OrdonnanceService.class, "ordonnanceService", service);
            }

            if (!isBlank(prescriptionServiceClass) && getBean(ma.dentalTech.service.modules.dossierMedicale.api.PrescriptionService.class) == null) {
                var presRepo = getBean(ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo.class);
                var ordRepo = getBean(ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepo.class);
                if (presRepo == null) throw new IllegalStateException("prescriptionRepo manquant");
                if (ordRepo == null) throw new IllegalStateException("ordonnanceRepo manquant");

                var service = (ma.dentalTech.service.modules.dossierMedicale.api.PrescriptionService)
                        Class.forName(prescriptionServiceClass)
                                .getDeclaredConstructor(
                                        ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo.class,
                                        ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepo.class
                                )
                                .newInstance(presRepo, ordRepo);
                putBean(ma.dentalTech.service.modules.dossierMedicale.api.PrescriptionService.class, "prescriptionService", service);
            }

            if (!isBlank(interventionMedecinServiceClass) && getBean(ma.dentalTech.service.modules.dossierMedicale.api.InterventionMedecinService.class) == null) {
                var imRepo = getBean(ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepo.class);
                var acteRepo = getBean(ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository.class);
                if (imRepo == null) throw new IllegalStateException("interventionMedecinRepo manquant");
                if (acteRepo == null) throw new IllegalStateException("acteRepo manquant");

                var service = (ma.dentalTech.service.modules.dossierMedicale.api.InterventionMedecinService)
                        Class.forName(interventionMedecinServiceClass)
                                .getDeclaredConstructor(
                                        ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepo.class,
                                        ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository.class
                                )
                                .newInstance(imRepo, acteRepo);
                putBean(ma.dentalTech.service.modules.dossierMedicale.api.InterventionMedecinService.class, "interventionMedecinService", service);
            }

// ---- 3) Enfin le CONTROLLER (avec toutes les dépendances) ----
            String dossierMedicalControllerClass = props.getProperty("dossierMedicalController");
            if (!isBlank(dossierMedicalControllerClass) && getBean(ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController.class) == null) {

                var dmService = getBean(ma.dentalTech.service.modules.dossierMedicale.api.DossierMedicalService.class);
                var consService = getBean(ma.dentalTech.service.modules.dossierMedicale.api.ConsultationService.class);
                var imService = getBean(ma.dentalTech.service.modules.dossierMedicale.api.InterventionMedecinService.class);
                var acteServiceBean = getBean(ma.dentalTech.service.modules.dossierMedicale.api.ActeService.class);
                var medService = getBean(ma.dentalTech.service.modules.dossierMedicale.api.MedicamentService.class);
                var ordService = getBean(ma.dentalTech.service.modules.dossierMedicale.api.OrdonnanceService.class);
                var presService = getBean(ma.dentalTech.service.modules.dossierMedicale.api.PrescriptionService.class);
                var sitService = getBean(ma.dentalTech.service.modules.dossierMedicale.api.SituationFinanciereService.class);
                var certService = getBean(ma.dentalTech.service.modules.dossierMedicale.api.CertificatService.class);

                if (dmService == null) throw new IllegalStateException("dossierMedicalService manquant");
                if (consService == null) throw new IllegalStateException("consultationService manquant");
                if (imService == null) throw new IllegalStateException("interventionMedecinService manquant");
                if (acteServiceBean == null) throw new IllegalStateException("acteService manquant");
                if (medService == null) throw new IllegalStateException("medicamentService manquant");
                if (ordService == null) throw new IllegalStateException("ordonnanceService manquant");
                if (presService == null) throw new IllegalStateException("prescriptionService manquant");
                if (sitService == null) throw new IllegalStateException("situationFinanciereService manquant");
                if (certService == null) throw new IllegalStateException("certificatService manquant");

                var controller = (ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController)
                        Class.forName(dossierMedicalControllerClass)
                                .getDeclaredConstructor(
                                        ma.dentalTech.service.modules.dossierMedicale.api.DossierMedicalService.class,
                                        ma.dentalTech.service.modules.dossierMedicale.api.ConsultationService.class,
                                        ma.dentalTech.service.modules.dossierMedicale.api.InterventionMedecinService.class,
                                        ma.dentalTech.service.modules.dossierMedicale.api.ActeService.class,
                                        ma.dentalTech.service.modules.dossierMedicale.api.MedicamentService.class,
                                        ma.dentalTech.service.modules.dossierMedicale.api.OrdonnanceService.class,
                                        ma.dentalTech.service.modules.dossierMedicale.api.PrescriptionService.class,
                                        ma.dentalTech.service.modules.dossierMedicale.api.SituationFinanciereService.class,
                                        ma.dentalTech.service.modules.dossierMedicale.api.CertificatService.class
                                )
                                .newInstance(dmService, consService, imService, acteServiceBean, medService,
                                        ordService, presService, sitService, certService);

                putBean(ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController.class,
                        "dossierMedicalController", controller);
            }

            String acteControllerClass = props.getProperty("acteController");
            if (!isBlank(acteControllerClass)) {
                var svc = getBean(ma.dentalTech.service.modules.dossierMedicale.api.ActeService.class);
                if (svc == null) throw new IllegalStateException("acteService missing for acteController");

                Object ctrl = Class.forName(acteControllerClass)
                        .getDeclaredConstructor(ma.dentalTech.service.modules.dossierMedicale.api.ActeService.class)
                        .newInstance(svc);

                contextByName.put("acteController", ctrl);
                context.put(ma.dentalTech.mvc.controllers.modules.acte.api.ActeController.class, ctrl);
            }
            String factureRepoClass = props.getProperty("factureRepo");
            if (!isBlank(factureRepoClass)) {
                var factureRepo = newNoArg(factureRepoClass, ma.dentalTech.repository.modules.facturation.api.FactureRepo.class);
                putBean(ma.dentalTech.repository.modules.facturation.api.FactureRepo.class, "factureRepo", factureRepo);
            }

            // =====================
            // CAISSE SERVICE (SANS DATASOURCE)
            // =====================
            FactureRepo factureRepo =
                    getBean(ma.dentalTech.repository.modules.facturation.api.FactureRepo.class);

            ConsultationRepo consultationRepo =
                    getBean(ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo.class);

            InterventionMedecinService interventionMedecinService =
                    getBean(ma.dentalTech.service.modules.dossierMedicale.api.InterventionMedecinService.class);

            if (factureRepo == null)
                throw new IllegalStateException("factureRepo manquant pour CaisseService");
            if (consultationRepo == null)
                throw new IllegalStateException("consultationRepo manquant pour CaisseService");
            if (interventionMedecinService == null)
                throw new IllegalStateException("interventionMedecinService manquant pour CaisseService");

            CaisseService caisseService = new CaisseServiceImpl(
                    factureRepo,
                    consultationRepo,
                    interventionMedecinService
            );

            putBean(
                    ma.dentalTech.service.modules.caisse.api.CaisseService.class,
                    "caisseService",
                    caisseService
            );

            String caisseControllerClass = props.getProperty("caisseController");
            if (!isBlank(caisseControllerClass)) {
                var cs = getBean(ma.dentalTech.service.modules.caisse.api.CaisseService.class);
                if (cs == null) throw new IllegalStateException("caisseService missing for caisseController");

                Object ctrl = Class.forName(caisseControllerClass)
                        .getDeclaredConstructor(ma.dentalTech.service.modules.caisse.api.CaisseService.class)
                        .newInstance(cs);

                contextByName.put("caisseController", ctrl);
                context.put(ma.dentalTech.mvc.controllers.modules.caisse.api.CaisseController.class, ctrl);
            }

            String cabinetRepoClass = props.getProperty("cabinetRepo");
            if (!isBlank(cabinetRepoClass)) {
                Object repo = Class.forName(cabinetRepoClass)
                        .getDeclaredConstructor()
                        .newInstance();

                contextByName.put("cabinetRepo", repo);
                context.put(ma.dentalTech.repository.modules.cabinet.api.CabinetMedicalRepository.class, repo);
            }

            // =====================
            // STATISTIQUES (Repo → Service → Controller)
            // =====================
            String statistiquesRepoClass = props.getProperty("statistiquesRepo");
            if (!isBlank(statistiquesRepoClass)) {
                Object repo = Class.forName(statistiquesRepoClass)
                        .getDeclaredConstructor()
                        .newInstance();

                contextByName.put("statistiquesRepo", repo);
                context.put(ma.dentalTech.repository.modules.cabinet.api.StatistiquesRepository.class, repo);
            }

            String statistiquesServiceClass = props.getProperty("statistiquesService");
            if (!isBlank(statistiquesServiceClass)) {
                var statsRepo = getBean(ma.dentalTech.repository.modules.cabinet.api.StatistiquesRepository.class);
                var cabinetRepo = getBean(ma.dentalTech.repository.modules.cabinet.api.CabinetMedicalRepository.class);

                if (statsRepo == null) throw new IllegalStateException("statistiquesRepo missing for statistiquesService");
                if (cabinetRepo == null) throw new IllegalStateException("cabinetMedicalRepository missing for statistiquesService");

                Object svc = Class.forName(statistiquesServiceClass)
                        .getDeclaredConstructor(
                                ma.dentalTech.repository.modules.cabinet.api.StatistiquesRepository.class,
                                ma.dentalTech.repository.modules.cabinet.api.CabinetMedicalRepository.class
                        )
                        .newInstance(statsRepo, cabinetRepo);

                contextByName.put("statistiquesService", svc);
                context.put(ma.dentalTech.service.modules.cabinet.api.StatistiquesService.class, svc);
            }

            String statistiquesControllerClass = props.getProperty("statistiquesController");
            if (!isBlank(statistiquesControllerClass)) {
                var svc = getBean(ma.dentalTech.service.modules.cabinet.api.StatistiquesService.class);
                if (svc == null) throw new IllegalStateException("statistiquesService missing for statistiquesController");

                Object ctrl = Class.forName(statistiquesControllerClass)
                        .getDeclaredConstructor(ma.dentalTech.service.modules.cabinet.api.StatistiquesService.class)
                        .newInstance(svc);

                contextByName.put("statistiquesController", ctrl);
                context.put(ma.dentalTech.mvc.controllers.modules.statistiques.api.StatistiquesController.class, ctrl);
            }
            // ============================================================
            // USERS Controller (UserManagementService + PasswordManagementService)
            // ============================================================
            String usersControllerClass = props.getProperty("usersController");
            if (!isBlank(usersControllerClass)) {

                var userSvc = getBean(ma.dentalTech.service.modules.users.api.UserManagementService.class);
                var pwdSvc  = getBean(ma.dentalTech.service.modules.users.api.PasswordManagementService.class);

                if (userSvc == null) throw new IllegalStateException("userManagementService missing for usersController");
                if (pwdSvc == null)  throw new IllegalStateException("passwordManagementService missing for usersController");

                var ctrl = (ma.dentalTech.mvc.controllers.modules.users.api.UsersController)
                        Class.forName(usersControllerClass)
                                .getDeclaredConstructor(
                                        ma.dentalTech.service.modules.users.api.UserManagementService.class,
                                        ma.dentalTech.service.modules.users.api.PasswordManagementService.class
                                )
                                .newInstance(userSvc, pwdSvc);

                putBean(ma.dentalTech.mvc.controllers.modules.users.api.UsersController.class, "usersController", ctrl);
            }

            // =====================
            // INTERVENTION MEDECIN (Repo → Service → Controller)
            // =====================
//            String interventionMedecinControllerClass = props.getProperty("interventionMedecinController");
//            if (!isBlank(interventionMedecinControllerClass)) {
//                var svc = getBean(ma.dentalTech.service.modules.dossierMedicale.api.InterventionMedecinService.class);
//
//                if (svc == null) throw new IllegalStateException("interventionMedecinService missing for interventionMedecinController");
//
//                Object ctrl = Class.forName(interventionMedecinControllerClass)
//                        .getDeclaredConstructor(
//                                ma.dentalTech.service.modules.dossierMedicale.api.InterventionMedecinService.class
//                        )
//                        .newInstance(svc);
//
//                contextByName.put("interventionMedecinController", ctrl);
//                context.put(ma.dentalTech.mvc.controllers.modules.dossierMedical.api.InterventionMedecinController.class, ctrl);
//            }
            // =====================
            // CONSULTATION WORKFLOW SERVICE
            // =====================
            String workflowServiceClass = props.getProperty("workflowService");
            if (!isBlank(workflowServiceClass)) {

                var consultationService = getBean(ma.dentalTech.service.modules.dossierMedicale.api.ConsultationService.class);
                var interventionService = getBean(ma.dentalTech.service.modules.dossierMedicale.api.InterventionMedecinService.class);

                if (consultationService == null) throw new IllegalStateException("consultationService missing for workflowService");
                if (interventionService == null) throw new IllegalStateException("interventionMedecinService missing for workflowService");
                if (caisseService == null) throw new IllegalStateException("caisseService missing for workflowService");

                Object svc = Class.forName(workflowServiceClass)
                        .getDeclaredConstructor(
                                ma.dentalTech.service.modules.dossierMedicale.api.ConsultationService.class,
                                ma.dentalTech.service.modules.dossierMedicale.api.InterventionMedecinService.class,
                                ma.dentalTech.service.modules.caisse.api.CaisseService.class
                        )
                        .newInstance(consultationService, interventionService, caisseService);

                putBean(ma.dentalTech.service.modules.dossierMedicale.util.ConsultationWorkflowService.class,
                        "workflowService",
                        (ma.dentalTech.service.modules.dossierMedicale.util.ConsultationWorkflowService) svc);
            }

            String workflowControllerClass = props.getProperty("consultationWorkflowController");
            if (!isBlank(workflowControllerClass)) {

                var consultationService = getBean(ma.dentalTech.service.modules.dossierMedicale.api.ConsultationService.class);
                var interventionService = getBean(ma.dentalTech.service.modules.dossierMedicale.api.InterventionMedecinService.class);
                var workflowService = getBean(ma.dentalTech.service.modules.dossierMedicale.util.ConsultationWorkflowService.class);

                if (consultationService == null) throw new IllegalStateException("consultationService missing");
                if (interventionService == null) throw new IllegalStateException("interventionMedecinService missing");
                if (caisseService == null) throw new IllegalStateException("caisseService missing");
                if (workflowService == null) throw new IllegalStateException("workflowService missing");

                var ctrl = Class.forName(workflowControllerClass)
                        .getDeclaredConstructor(
                                ma.dentalTech.service.modules.dossierMedicale.util.ConsultationWorkflowService.class
                        )
                        .newInstance(workflowService);

                putBean(ma.dentalTech.mvc.controllers.modules.dossierMedical.util.ConsultationWorkflowController.class,
                        "consultationWorkflowController",
                        (ma.dentalTech.mvc.controllers.modules.dossierMedical.util.ConsultationWorkflowController) ctrl);
            }

            // ============================================================
            // AUTHORIZATION SERVICE (needed by dashboard)
            // ============================================================
            String authorizationServiceClass = props.getProperty("authorizationService");
            if (!isBlank(authorizationServiceClass) && getBean(AuthorizationService.class) == null) {

                AuthorizationService authorizationService =
                        (AuthorizationService) Class.forName(authorizationServiceClass)
                                .getDeclaredConstructor()
                                .newInstance();

                putBean(AuthorizationService.class, "authorizationService", authorizationService);
            }

            // ============================================================
            // dashboard
            // ============================================================
            String dashboardControllerClass = props.getProperty("dashboardController");
            if (notBlank(dashboardControllerClass)) {
                AuthorizationService authorizationService = getBean(AuthorizationService.class);
                AuthController authController = getBean(AuthController.class);

                var rdvController = getBean(ma.dentalTech.mvc.controllers.modules.agenda.api.RDVController.class);
                var medecinRepo = getBean(ma.dentalTech.repository.modules.users.api.MedecinRepository.class);
                var agendaController = getBean(ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaMensuelController.class);
                var agendaService = getBean(ma.dentalTech.service.modules.agendas.api.AgendaMensuelService.class);
                var rdvRepo = getBean(ma.dentalTech.repository.modules.agenda.api.RDVRepository.class);
                var dossierMedicalController = getBean(ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController.class);
                var acteController = getBean(ma.dentalTech.mvc.controllers.modules.acte.api.ActeController.class);
                var caisseController = getBean(ma.dentalTech.mvc.controllers.modules.caisse.api.CaisseController.class);
                var statistiquesController = getBean(ma.dentalTech.mvc.controllers.modules.statistiques.api.StatistiquesController.class);
                var usersController = getBean(ma.dentalTech.mvc.controllers.modules.users.api.UsersController.class);
                var antecedentController = getBean(ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentController.class);
                var workflowController = getBean(ma.dentalTech.mvc.controllers.modules.dossierMedical.util.ConsultationWorkflowController.class);

                if (dossierMedicalController == null)
                    throw new IllegalStateException("dossierMedicalController missing for dashboardController");
                if (authorizationService == null)
                    throw new IllegalStateException("authorizationService missing for dashboardController");
                if (authController == null)
                    throw new IllegalStateException("authController missing for dashboardController");
                if (patientController == null)
                    throw new IllegalStateException("patientController missing for dashboardController");
                if (rdvController == null)
                    throw new IllegalStateException("rdvController missing for dashboardController");
                if (medecinRepo == null)
                    throw new IllegalStateException("medecinRepo missing for dashboardController");
                if (agendaController == null)
                    throw new IllegalStateException("agendaMensuelController missing for dashboardController");
                if (agendaService == null)
                    throw new IllegalStateException("agendaMensuelService missing for dashboardController");
                if (acteController == null)
                    throw new IllegalStateException("acteController missing for dashboardController");
                if (caisseController == null)
                    throw new IllegalStateException("caisseController missing for dashboardController");
                if (statistiquesController == null)
                    throw new IllegalStateException("statistiquesController missing for dashboardController");
                if (usersController == null)
                    throw new IllegalStateException("usersController missing for dashboardController");
                if (workflowController == null)
                    throw new IllegalStateException("workflowController missing for dashboardController");

                DashboardController dashboardController = (DashboardController) Class.forName(dashboardControllerClass)
                        .getDeclaredConstructor(
                                AuthorizationService.class,
                                AuthController.class,
                                PatientController.class,
                                ma.dentalTech.repository.modules.agenda.api.RDVRepository.class,
                                ma.dentalTech.mvc.controllers.modules.agenda.api.RDVController.class,
                                ma.dentalTech.repository.modules.users.api.MedecinRepository.class,
                                ma.dentalTech.mvc.controllers.modules.agenda.api.AgendaMensuelController.class,
                                ma.dentalTech.service.modules.agendas.api.AgendaMensuelService.class,
                                ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController.class,
                                ma.dentalTech.mvc.controllers.modules.acte.api.ActeController.class,
                                ma.dentalTech.mvc.controllers.modules.caisse.api.CaisseController.class,
                                ma.dentalTech.mvc.controllers.modules.statistiques.api.StatistiquesController.class,
                                ma.dentalTech.mvc.controllers.modules.users.api.UsersController.class,
                                ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentController.class,
                                ma.dentalTech.mvc.controllers.modules.dossierMedical.util.ConsultationWorkflowController.class
                                // ✅ ajouté
                        )
                        .newInstance(
                                authorizationService,
                                authController,
                                patientController,
                                rdvRepo,
                                rdvController,
                                medecinRepo,
                                agendaController,
                                agendaService,
                                dossierMedicalController,
                                acteController,
                                caisseController,
                                statistiquesController,
                                usersController,
                                antecedentController,
                                workflowController
                                // ✅ ajouté
                        );

                putBean(DashboardController.class, "dashboardController", dashboardController);


            }
// ----------------------------------------------------------------------------------------------------------------------------------------
            // ============================================================
            // 7) DATASOURCE + repos/services that require DataSource (optional)
            // ============================================================
            String dataSourceClass = props.getProperty("dataSource");
            if (!isBlank(dataSourceClass)) {

                if (dbProps == null) {
                    throw new IllegalStateException("config/db.properties introuvable alors que dataSource est défini");
                }

                // support both formats:
                // - db.url / db.user / db.password
                // - datasource.url / datasource.user / datasource.password
                String url = firstNonBlank(dbProps.getProperty("db.url"), dbProps.getProperty("datasource.url"));
                String user = firstNonBlank(dbProps.getProperty("db.user"), dbProps.getProperty("datasource.user"));
                String password = firstNonBlank(dbProps.getProperty("db.password"), dbProps.getProperty("datasource.password"));

                if (isBlank(url)) throw new IllegalStateException("db.url (ou datasource.url) manquant dans config/db.properties");

                DataSource ds = (DataSource) Class.forName(dataSourceClass)
                        .getDeclaredConstructor(String.class, String.class, String.class)
                        .newInstance(url, user, password);

                putBean(DataSource.class, "dataSource", ds);

                // ---- repos with DataSource constructor (only if key exists) ----
                instantiateRepoWithDataSource(props, "consultationRepo", ds,
                        ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo.class);

                instantiateRepoWithDataSource(props, "interventionMedecinRepo", ds,
                        ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepo.class);

                instantiateRepoWithDataSource(props, "dossierMedicalRepo", ds,
                        ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepo.class);

                instantiateRepoWithDataSource(props, "certificatRepo", ds,
                        ma.dentalTech.repository.modules.dossierMedical.api.CertificatRepo.class);

                instantiateRepoWithDataSource(props, "acteRepo", ds,
                        ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository.class);

                instantiateRepoWithDataSource(props, "medicamentRepo", ds,
                        ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepo.class);

                instantiateRepoWithDataSource(props, "factureRepo", ds,
                        ma.dentalTech.repository.modules.facturation.api.FactureRepo.class);

                // ---- services with 1 dep (only if key exists) ----
                instantiateService1Dep(props, "consultationService",
                        ma.dentalTech.service.modules.dossierMedicale.api.ConsultationService.class,
                        ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo.class);

                instantiateService1Dep(props, "certificatService",
                        ma.dentalTech.service.modules.dossierMedicale.api.CertificatService.class,
                        ma.dentalTech.repository.modules.dossierMedical.api.CertificatRepo.class);

                instantiateService1Dep(props, "dossierMedicalService",
                        ma.dentalTech.service.modules.dossierMedicale.api.DossierMedicalService.class,
                        ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepo.class);

                instantiateService1Dep(props, "acteService",
                        ma.dentalTech.service.modules.dossierMedicale.api.ActeService.class,
                        ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository.class);

                instantiateService1Dep(props, "medicamentService",
                        ma.dentalTech.service.modules.dossierMedicale.api.MedicamentService.class,
                        ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepo.class);

                // ---- services with 2 deps ----
                String imServiceClass = props.getProperty("interventionMedecinService");
                if (!isBlank(imServiceClass)) {
                    var imRepo = getBean(ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepo.class);
                    var acteRepo = getBean(ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository.class);
                    if (imRepo == null || acteRepo == null) throw new IllegalStateException("Deps missing for interventionMedecinService");

                    Object service = Class.forName(imServiceClass)
                            .getDeclaredConstructor(
                                    ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepo.class,
                                    ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository.class
                            )
                            .newInstance(imRepo, acteRepo);

                    contextByName.put("interventionMedecinService", service);
                    context.put(ma.dentalTech.service.modules.dossierMedicale.api.InterventionMedecinService.class, service);
                }

                // ---- dashboard/statistics (3 deps then 1 dep) ----
                String statsServiceClass = props.getProperty("statistiquesService");
                if (!isBlank(statsServiceClass)) {
                    var consRepo = getBean(ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo.class);
                    var imRepo = getBean(ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepo.class);
                    if (consRepo == null || imRepo == null || factureRepo == null) throw new IllegalStateException("Deps missing for statistiquesService");

                    Object statsService = Class.forName(statsServiceClass)
                            .getDeclaredConstructor(
                                    ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo.class,
                                    ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepo.class,
                                    ma.dentalTech.repository.modules.facturation.api.FactureRepo.class
                            )
                            .newInstance(consRepo, imRepo, factureRepo);

                    contextByName.put("statistiquesService", statsService);
                    context.put(ma.dentalTech.service.modules.dashboard_statistiques.api.StatistiquesService.class, statsService);
                }

                String dashboardServiceClass = props.getProperty("dashboardService");
                if (!isBlank(dashboardServiceClass)) {
                    var statsService = getBean(ma.dentalTech.service.modules.dashboard_statistiques.api.StatistiquesService.class);
                    if (statsService == null) throw new IllegalStateException("statistiquesService missing for dashboardService");

                    Object dashboardService = Class.forName(dashboardServiceClass)
                            .getDeclaredConstructor(ma.dentalTech.service.modules.dashboard_statistiques.api.StatistiquesService.class)
                            .newInstance(statsService);

                    contextByName.put("dashboardService", dashboardService);
                    context.put(ma.dentalTech.service.modules.dashboard_statistiques.api.DashboardService.class, dashboardService);
                }

                // ---- Instantiate OrdonnanceRepo ----
                if (ordonnanceRepoClass != null && !ordonnanceRepoClass.isBlank()) {
                    OrdonnanceRepo ordonnanceRepo = (OrdonnanceRepo) Class.forName(ordonnanceRepoClass)
                            .getDeclaredConstructor()
                            .newInstance();

                    context.put(OrdonnanceRepo.class, ordonnanceRepo);
                    contextByName.put("ordonnanceRepo", ordonnanceRepo);
                }

                // ---- Instantiate PrescriptionRepo ----
                if (prescriptionRepoClass != null && !prescriptionRepoClass.isBlank()) {
                    PrescriptionRepo prescriptionRepo = (PrescriptionRepo) Class.forName(prescriptionRepoClass)
                            .getDeclaredConstructor()
                            .newInstance();

                    context.put(PrescriptionRepo.class, prescriptionRepo);
                    contextByName.put("prescriptionRepo", prescriptionRepo);
                }

                // ---- Instantiate OrdonnanceService (needs OrdonnanceRepo + PrescriptionRepo) ----
                if (ordonnanceServiceClass != null && !ordonnanceServiceClass.isBlank()) {
                    OrdonnanceRepo ordonnanceRepo = getBean(OrdonnanceRepo.class);
                    PrescriptionRepo prescriptionRepo = getBean(PrescriptionRepo.class);

                    if (ordonnanceRepo == null) {
                        throw new IllegalStateException("ordonnanceRepo bean not found");
                    }
                    if (prescriptionRepo == null) {
                        throw new IllegalStateException("prescriptionRepo bean not found");
                    }

                    OrdonnanceService ordonnanceService = (OrdonnanceService) Class.forName(ordonnanceServiceClass)
                            .getDeclaredConstructor(OrdonnanceRepo.class, PrescriptionRepo.class)
                            .newInstance(ordonnanceRepo, prescriptionRepo);

                    context.put(OrdonnanceService.class, ordonnanceService);
                    contextByName.put("ordonnanceService", ordonnanceService);
                }

                // ---- Instantiate PrescriptionService (needs PrescriptionRepo) ----
                // ---- Instantiate PrescriptionService (needs PrescriptionRepo + OrdonnanceRepo) ----

                if (prescriptionServiceClass != null && !prescriptionServiceClass.isBlank()) {
                    PrescriptionRepo prescriptionRepo = getBean(PrescriptionRepo.class);
                    OrdonnanceRepo ordonnanceRepo = getBean(OrdonnanceRepo.class);

                    if (prescriptionRepo == null) {
                        throw new IllegalStateException("prescriptionRepo bean not found");
                    }
                    if (ordonnanceRepo == null) {
                        throw new IllegalStateException("ordonnanceRepo bean not found (required by PrescriptionServiceImpl)");
                    }

                    PrescriptionService prescriptionService = (PrescriptionService) Class.forName(prescriptionServiceClass)
                            .getDeclaredConstructor(PrescriptionRepo.class, OrdonnanceRepo.class)
                            .newInstance(prescriptionRepo, ordonnanceRepo);

                    context.put(PrescriptionService.class, prescriptionService);
                    contextByName.put("prescriptionService", prescriptionService);
                }


                // =========================================================
                // Dossier Medical (Situation Financiere)
                // =========================================================

                // ---- Instantiate SituationFinanciereRepo ----
                if (situationFinanciereRepoClass != null && !situationFinanciereRepoClass.isBlank()) {
                    SituationFinanciereRepo situationFinanciereRepo = (SituationFinanciereRepo) Class.forName(situationFinanciereRepoClass)
                            .getDeclaredConstructor()
                            .newInstance();

                    context.put(SituationFinanciereRepo.class, situationFinanciereRepo);
                    contextByName.put("situationFinanciereRepo", situationFinanciereRepo);
                }

                // ---- Instantiate SituationFinanciereService (needs SituationFinanciereRepo) ----
                if (situationFinanciereServiceClass != null && !situationFinanciereServiceClass.isBlank()) {
                    SituationFinanciereRepo situationFinanciereRepo = getBean(SituationFinanciereRepo.class);

                    if (situationFinanciereRepo == null) {
                        throw new IllegalStateException("situationFinanciereRepo bean not found");
                    }

                    SituationFinanciereService situationFinanciereService = (SituationFinanciereService) Class.forName(situationFinanciereServiceClass)
                            .getDeclaredConstructor(SituationFinanciereRepo.class)
                            .newInstance(situationFinanciereRepo);

                    context.put(SituationFinanciereService.class, situationFinanciereService);
                    contextByName.put("situationFinanciereService", situationFinanciereService);
                }


            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur d'initialisation ApplicationContext", e);
        }
    }

    // ========================= Helpers =========================

    private static Properties loadProps(String path) throws Exception {
        var in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (in == null) throw new IllegalStateException("Erreur : " + path + " introuvable");
        Properties p = new Properties();
        p.load(in);
        return p;
    }

    private static Properties tryLoadProps(String path) throws Exception {
        var in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (in == null) return null;
        Properties p = new Properties();
        p.load(in);
        return p;
    }

    private static <T> T newNoArg(String fqcn, Class<T> iface) throws Exception {
        Object obj = Class.forName(fqcn).getDeclaredConstructor().newInstance();
        return iface.cast(obj);
    }

    private static <T, D> T new1Dep(String fqcn, Class<T> iface, Class<D> depClass, D dep) throws Exception {
        Object obj = Class.forName(fqcn).getDeclaredConstructor(depClass).newInstance(dep);
        return iface.cast(obj);
    }

    private static <T> void putBean(Class<T> type, String name, T bean) {
        context.put(type, bean);
        contextByName.put(name, bean);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String firstNonBlank(String a, String b) {
        return !isBlank(a) ? a : b;
    }

    private static <T> void instantiateRepoWithDataSource(Properties props,
                                                          String key,
                                                          DataSource ds,
                                                          Class<T> iface) throws Exception {
        String cls = props.getProperty(key);
        if (isBlank(cls)) return;
        if (ds == null) throw new IllegalStateException("DataSource manquant pour instancier: " + key);

        Object repo = Class.forName(cls)
                .getDeclaredConstructor(DataSource.class)
                .newInstance(ds);

        contextByName.put(key, repo);
        context.put(iface, repo);
    }

    private static <S, D> void instantiateService1Dep(Properties props,
                                                      String key,
                                                      Class<S> serviceIface,
                                                      Class<D> depIface) throws Exception {
        String cls = props.getProperty(key);
        if (isBlank(cls)) return;

        D dep = getBean(depIface);
        if (dep == null) throw new IllegalStateException("Dépendance manquante pour " + key + ": " + depIface.getSimpleName());

        Object service = Class.forName(cls)
                .getDeclaredConstructor(depIface)
                .newInstance(dep);

        contextByName.put(key, service);
        context.put(serviceIface, service);
    }

    // ========================= API =========================

    public static Object getBean(String beanName) {
        return contextByName.get(beanName);
    }

    public static <T> T getBean(Class<T> beanClass) {
        Object bean = context.get(beanClass);
        return (bean == null) ? null : beanClass.cast(bean);
    }

    private ApplicationContext() {}
}
