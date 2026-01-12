package ma.dentalTech.configuration;

import ma.dentalTech.mvc.controllers.modules.auth.api.AuthController;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;

import ma.dentalTech.repository.modules.dossierMedical.api.*;
import ma.dentalTech.repository.modules.dossierMedical.impl.DossierMedicalRepoImpl;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.repository.modules.users.api.MedecinRepository;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;

import ma.dentalTech.service.modules.auth.api.AuthService;
import ma.dentalTech.service.modules.auth.api.CredentialsValidator;
import ma.dentalTech.service.modules.auth.api.PasswordEncoder;

import ma.dentalTech.service.modules.dossierMedicale.api.DossierMedicalService;
import ma.dentalTech.service.modules.dossierMedicale.api.OrdonnanceService;
import ma.dentalTech.service.modules.dossierMedicale.api.PrescriptionService;
import ma.dentalTech.service.modules.dossierMedicale.impl.DossierMedicalServiceImpl;
import ma.dentalTech.service.modules.notifications.api.NotificationService;
import ma.dentalTech.service.modules.dossierMedicale.api.SituationFinanciereService;

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
            // 2) OPTIONAL: antecedent repo/service
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

                String medicamentRepoClass = props.getProperty("medicamentRepo");
                if (notBlank(medicamentRepoClass)) {
                    MedicamentRepo repo = (MedicamentRepo) Class.forName(medicamentRepoClass)
                            .getDeclaredConstructor()
                            .newInstance();

                    putBean(MedicamentRepo.class, "medicamentRepo", repo);
                }

                String ordonnanceRepoClass = props.getProperty("ordonnanceRepo");
                if (!isBlank(ordonnanceRepoClass)) {
                    OrdonnanceRepo ordonnanceRepo = (OrdonnanceRepo) Class.forName(ordonnanceRepoClass)
                            .getDeclaredConstructor()
                            .newInstance();
                    putBean(OrdonnanceRepo.class, "ordonnanceRepo", ordonnanceRepo);
                }

                String prescriptionRepoClass = props.getProperty("prescriptionRepo");
                if (!isBlank(prescriptionRepoClass)) {
                    PrescriptionRepo prescriptionRepo = (PrescriptionRepo) Class.forName(prescriptionRepoClass)
                            .getDeclaredConstructor()
                            .newInstance();
                    putBean(PrescriptionRepo.class, "prescriptionRepo", prescriptionRepo);
                }

                String sfRepoClass = props.getProperty("situationFinanciereRepo");
                if (!isBlank(sfRepoClass)) {
                    SituationFinanciereRepo sfRepo = (SituationFinanciereRepo) Class.forName(sfRepoClass)
                            .getDeclaredConstructor()
                            .newInstance();
                    putBean(SituationFinanciereRepo.class, "situationFinanciereRepo", sfRepo);
                }

                String ordonnanceServiceClass = props.getProperty("ordonnanceService");
                if (!isBlank(ordonnanceServiceClass)) {
                    OrdonnanceRepo ordRepo = getBean(OrdonnanceRepo.class);
                    PrescriptionRepo presRepo = getBean(PrescriptionRepo.class);
                    if (ordRepo == null || presRepo == null) throw new IllegalStateException("Deps missing for ordonnanceService");

                    OrdonnanceService svc = (OrdonnanceService) Class.forName(ordonnanceServiceClass)
                            .getDeclaredConstructor(OrdonnanceRepo.class, PrescriptionRepo.class)
                            .newInstance(ordRepo, presRepo);
                    putBean(OrdonnanceService.class, "ordonnanceService", svc);
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
            // DOSSIER MEDICAL REPOS
            // =====================
            String ordonnanceRepoClass = props.getProperty("ordonnanceRepo");
            if (!isBlank(ordonnanceRepoClass)) {
                OrdonnanceRepo ordonnanceRepo = newNoArg(ordonnanceRepoClass, OrdonnanceRepo.class);
                putBean(OrdonnanceRepo.class, "ordonnanceRepo", ordonnanceRepo);
            }

            String prescriptionRepoClass = props.getProperty("prescriptionRepo");
            if (!isBlank(prescriptionRepoClass)) {
                PrescriptionRepo prescriptionRepo = newNoArg(prescriptionRepoClass, PrescriptionRepo.class);
                putBean(PrescriptionRepo.class, "prescriptionRepo", prescriptionRepo);
            }

            String sfRepoClass = props.getProperty("situationFinanciereRepo");
            if (!isBlank(sfRepoClass)) {
                SituationFinanciereRepo sfRepo = newNoArg(sfRepoClass, SituationFinanciereRepo.class);
                putBean(SituationFinanciereRepo.class, "situationFinanciereRepo", sfRepo);
            }

            // =====================
            // DOSSIER MEDICAL SERVICES
            // =====================
            String sfServiceClass = props.getProperty("situationFinanciereService");
            if (!isBlank(sfServiceClass)) {
                SituationFinanciereRepo sfRepo = getBean(SituationFinanciereRepo.class);
                if (sfRepo == null) throw new IllegalStateException("situationFinanciereRepo introuvable");

                SituationFinanciereService sfService =
                        (SituationFinanciereService) Class.forName(sfServiceClass)
                                .getDeclaredConstructor(SituationFinanciereRepo.class)
                                .newInstance(sfRepo);

                putBean(SituationFinanciereService.class, "situationFinanciereService", sfService);
            }

            String ordonnanceServiceClass = props.getProperty("ordonnanceService");
            if (!isBlank(ordonnanceServiceClass)) {
                OrdonnanceRepo ordRepo = getBean(OrdonnanceRepo.class);
                PrescriptionRepo presRepo = getBean(PrescriptionRepo.class);
                if (ordRepo == null) throw new IllegalStateException("ordonnanceRepo introuvable");
                if (presRepo == null) throw new IllegalStateException("prescriptionRepo introuvable");

                OrdonnanceService ordService =
                        (OrdonnanceService) Class.forName(ordonnanceServiceClass)
                                .getDeclaredConstructor(OrdonnanceRepo.class, PrescriptionRepo.class)
                                .newInstance(ordRepo, presRepo);

                putBean(OrdonnanceService.class, "ordonnanceService", ordService);
            }

            String prescriptionServiceClass = props.getProperty("prescriptionService");
            if (!isBlank(prescriptionServiceClass)) {
                PrescriptionRepo presRepo = getBean(PrescriptionRepo.class);
                OrdonnanceRepo ordRepo = getBean(OrdonnanceRepo.class);
                if (presRepo == null) throw new IllegalStateException("prescriptionRepo introuvable");
                if (ordRepo == null) throw new IllegalStateException("ordonnanceRepo introuvable");

                PrescriptionService presService =
                        (PrescriptionService) Class.forName(prescriptionServiceClass)
                                .getDeclaredConstructor(PrescriptionRepo.class, OrdonnanceRepo.class)
                                .newInstance(presRepo, ordRepo);

                putBean(PrescriptionService.class, "prescriptionService", presService);
            }

            String authorizationServiceClass = props.getProperty("authorizationService");
            if (notBlank(authorizationServiceClass)) {
                AuthorizationService authorizationService = (AuthorizationService) Class.forName(authorizationServiceClass)
                        .getDeclaredConstructor()
                        .newInstance();
                putBean(AuthorizationService.class, "authorizationService", authorizationService);
            }

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
            // =====================
// DOSSIER MEDICAL (repo/service/controller) - SessionFactory style
// =====================
            String dossierMedicalRepoClass = props.getProperty("dossierMedicalRepo");
            if (!isBlank(dossierMedicalRepoClass)) {
                var dmRepo = newNoArg(dossierMedicalRepoClass, ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepo.class);
                putBean(ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepo.class, "dossierMedicalRepo", dmRepo);
            }

            String dossierMedicalServiceClass = props.getProperty("dossierMedicalService");
            if (!isBlank(dossierMedicalServiceClass)) {
                var dmRepo = getBean(ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepo.class);
                if (dmRepo == null) throw new IllegalStateException("dossierMedicalRepo missing for dossierMedicalService");

                var dmService = (ma.dentalTech.service.modules.dossierMedicale.api.DossierMedicalService)
                        Class.forName(dossierMedicalServiceClass)
                                .getDeclaredConstructor(ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepo.class)
                                .newInstance(dmRepo);

                putBean(ma.dentalTech.service.modules.dossierMedicale.api.DossierMedicalService.class, "dossierMedicalService", dmService);
            }

            String dossierMedicalControllerClass = props.getProperty("dossierMedicalController");
            if (!isBlank(dossierMedicalControllerClass)) {
                var dmService = getBean(ma.dentalTech.service.modules.dossierMedicale.api.DossierMedicalService.class);
                if (dmService == null) throw new IllegalStateException("dossierMedicalService missing for dossierMedicalController");

                var dmController = (ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController)
                        Class.forName(dossierMedicalControllerClass)
                                .getDeclaredConstructor(ma.dentalTech.service.modules.dossierMedicale.api.DossierMedicalService.class)
                                .newInstance(dmService);

                putBean(ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController.class, "dossierMedicalController", dmController);
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
                                ma.dentalTech.mvc.controllers.modules.dossierMedical.api.DossierMedicalController.class   // ✅ ajouté
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
                                dossierMedicalController   // ✅ ajouté
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
                    var factureRepo = getBean(ma.dentalTech.repository.modules.facturation.api.FactureRepo.class);
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

                // ---- Optional dossierMedical beans (SituationFinanciere) ----
                String situationFinanciereRepoClass = props.getProperty("situationFinanciereRepo");
                String situationFinanciereServiceClass = props.getProperty("situationFinanciereService");

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
