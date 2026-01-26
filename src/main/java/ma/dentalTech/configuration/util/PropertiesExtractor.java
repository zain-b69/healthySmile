package ma.dentalTech.configuration.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesExtractor {

    public static String CONFIG_PATH;

    public static Properties loadConfigFile(String PROPS_PATH) {

        CONFIG_PATH = PROPS_PATH;
        Properties properties = new Properties();

        try (InputStream in = Thread.currentThread()
                                    .getContextClassLoader()
                                    .getResourceAsStream(PROPS_PATH)) {
            if (in == null)
                  throw new IllegalStateException("config file not found: " + PROPS_PATH);
            properties.load(in);
            return properties;

        } catch (IOException e) {
            throw new RuntimeException("Erreur lecture " + PROPS_PATH, e);
        }
    }

    public static String getPropertyValue(String key, Properties properties) {

        String v = properties.getProperty(key);
        if (v == null) {
            throw new IllegalStateException("property key not found : " + CONFIG_PATH + " : " + key);
        }
        return v.trim();
    }
}

