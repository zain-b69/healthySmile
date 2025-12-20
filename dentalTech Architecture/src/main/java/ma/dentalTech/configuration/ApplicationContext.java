package ma.dentalTech.configuration;

import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import ma.dentalTech.service.modules.patient.api.PatientService;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class ApplicationContext {

    private static final Map<Class<?>, Object> context = new HashMap<>();
    private static final Map<String, Object> contextByName = new HashMap<>();

    static {
        try {
            var configFile = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("config/beans.properties");

            if (configFile == null) {
                throw new IllegalStateException("Erreur : config/beans.properties introuvable");
            }

            Properties props = new Properties();
            props.load(configFile);

            // ---- Required keys (si ton prof l’exige) ----
            String patientRepoClass = props.getProperty("patientRepo");
            String patientServiceClass = props.getProperty("patientService");
            String patientControllerClass = props.getProperty("patientController");

            if (patientRepoClass == null || patientServiceClass == null || patientControllerClass == null) {
                throw new IllegalStateException("beans.properties doit contenir: patientRepo, patientService, patientController");
            }

            // ---- Optional repos for your tests ----
            String antecedentRepoClass = props.getProperty("antecedentRepo");
            String utilisateurRepoClass = props.getProperty("utilisateurRepo");

            // ---- Instantiate PatientRepository ----
            PatientRepository patientRepo = (PatientRepository) Class.forName(patientRepoClass)
                    .getDeclaredConstructor()
                    .newInstance();

            context.put(PatientRepository.class, patientRepo);
            contextByName.put("patientRepo", patientRepo);

            // ---- Instantiate AntecedentRepository (optional but needed for your test) ----
            if (antecedentRepoClass != null && !antecedentRepoClass.isBlank()) {
                AntecedentRepository antecedentRepo = (AntecedentRepository) Class.forName(antecedentRepoClass)
                        .getDeclaredConstructor()
                        .newInstance();

                context.put(AntecedentRepository.class, antecedentRepo);
                contextByName.put("antecedentRepo", antecedentRepo);
            }

            // ---- Instantiate UtilisateurRepository (optional but needed for auth test) ----
            if (utilisateurRepoClass != null && !utilisateurRepoClass.isBlank()) {
                UtilisateurRepository utilisateurRepo = (UtilisateurRepository) Class.forName(utilisateurRepoClass)
                        .getDeclaredConstructor()
                        .newInstance();

                context.put(UtilisateurRepository.class, utilisateurRepo);
                contextByName.put("utilisateurRepo", utilisateurRepo);
            }

            // ---- Instantiate PatientService ----
            PatientService patientService = (PatientService) Class.forName(patientServiceClass)
                    .getDeclaredConstructor(PatientRepository.class)
                    .newInstance(patientRepo);

            context.put(PatientService.class, patientService);
            contextByName.put("patientService", patientService);

            // ---- Instantiate PatientController ----
            PatientController patientController = (PatientController) Class.forName(patientControllerClass)
                    .getDeclaredConstructor(PatientService.class)
                    .newInstance(patientService);

            context.put(PatientController.class, patientController);
            contextByName.put("patientController", patientController);

        } catch (Exception e) {
            throw new RuntimeException("Erreur d'initialisation ApplicationContext", e);
        }
    }

    public static Object getBean(String beanName) {
        return contextByName.get(beanName);
    }

    public static <T> T getBean(Class<T> beanClass) {
        return beanClass.cast(context.get(beanClass));
    }

    private ApplicationContext() {}
}
