package service;

import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for handling configuration properties.
 */
public class ConfigUtility {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    /**
     * Returns the value for the specified key from the property file available on the classpath.
     *
     * @param key key whose value is needed
     * @return the corresponding value
     * @throws RuntimeException if the key is not found or if there is an error loading the configuration file
     */
    public static String loadConfigValue(String key) {
        try {

            Properties properties = loadConfig("application.properties");
            String value = properties.getProperty(key);
            if (value == null) {
                throw new IOException("Configuration key '" + key + "' not found in application.properties");
            }
            return value;

        } catch (IOException e) {
            log.error("Error loading configuration from file: {}", e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    /**
     * Returns the entire Properties object for the property file whose filename is passed.
     *
     * @param fileName filename of the property file
     * @return the corresponding properties object contained in the file
     * @throws IOException if there is an error loading the configuration file
     */
    private static Properties loadConfig(String fileName) throws IOException {
        Properties properties = new Properties();
        InputStream stream = FhirClient.class.getClassLoader().getResourceAsStream(fileName);
        if (stream == null) {
            throw new IOException("Configuration file '" + fileName + "' not found");
        }
        properties.load(stream);
        return properties;
    }
}
