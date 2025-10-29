package ma.dentalTech.conf;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.service.modules.patient.api.PatientService;

public class ApplicationContext {

    private static final Map<Class<?>, Object> context       = new HashMap<>();
    private static final Map<String, Object>   contextByName = new HashMap<>(); // Ajout d'une deuxième map

    static {
        var configFile = Thread.currentThread().getContextClassLoader().getResourceAsStream("config/beans.properties");

        if (configFile != null) {
            Properties properties = new Properties();
            try {
                properties.load(configFile);
                String daoClassName = properties.getProperty("patientRepo");
                String servClassName = properties.getProperty("patientService");
                String ctrlClassName = properties.getProperty("patientController");

                Class<?> cRepository = Class.forName(daoClassName);
                PatientRepository repository = (PatientRepository) cRepository.getDeclaredConstructor().newInstance();

                Class<?> cService = Class.forName(servClassName);
                PatientService service = (PatientService) cService.getDeclaredConstructor(PatientRepository.class).newInstance(repository);

                Class<?> cController = Class.forName(ctrlClassName);
                PatientController controller = (PatientController) cController.getDeclaredConstructor(PatientService.class).newInstance(service);

                // Stockage des beans dans le contexte
                context.put(PatientRepository.class, repository);
                context.put(PatientService.class, service);
                context.put(PatientController.class, controller);

                // Enregistrement des beans aussi avec des noms explicites
                contextByName.put("patientDao", repository);
                contextByName.put("patientService", service);
                contextByName.put("patientController", controller);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Erreur : Le fichier beans.properties est introuvable !");
        }
    }

    /**
     * Retourne un composant bean en fonction de son nom (String).
     */
    public static Object getBean(String beanName) {
        return contextByName.get(beanName);
    }

    /**
     * Retourne un composant bean en fonction de sa classe.
     */
    public static <T> T getBean(Class<T> beanClass) {
        return beanClass.cast(context.get(beanClass));
    }


}








