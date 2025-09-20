package datadriven;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.util.Properties;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * ConfigLoader is a utility class designed to load and retrieve configuration values
 * from java .properties file.<br>It supports retrieval of single values and
 * comma-separated array values, with optional control over exception behavior when
 * keys are missing.
 *
 * <p>Key Features:
 * <ul>
 *   <li>Loads properties from a file on initialization.</li>
 *   <li>Provides methods to retrieve single or array values.</li>
 *   <li>Optional flag-based behavior to throw exceptions or log warnings when keys are missing.</li>
 *   <li>Integrated logging using Log4j for traceability and debugging.</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 *   ConfigLoader config = new ConfigLoader("Credentials.properties");
 *   String browserName = config.getValue("browserName");
 *   String[] modes = config.getArrayValues("browserModes");
 * }</pre>
 *
 * @author Hossam Atef
 * @version 1.0
 */
public class ConfigLoader {

    private static final Logger log = LogManager.getLogger(lookup().lookupClass());
    private Properties properties;

    /**
     * Constructor.
     *
     * @param filepath the path to the properties file.
     */
    public ConfigLoader(String filepath) throws Exception {
        loadProperties(filepath);
    }

    /**
     * Loads the properties from the given file path into the Properties object.
     *
     * @param filepath the path to the properties file.
     */
    private void loadProperties(String filepath) throws Exception {
        properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream(filepath);
            properties.load(fileInputStream);
            log.info("Properties loaded successfully from: '{}'", filepath);
        } catch (Exception e) {
            log.error("Failed to load properties from: '{}'", filepath);
            throw e;
        }
    }

    /**
     * Retrieves the value of a property as a String for the given key.
     *
     * @param key  the key whose associated value is to be returned.
     * @param flag optional flag that, if true, throws an exception when the key is not found.
     *             If false or not provided, a warning will be logged and null will be returned.
     * @return the value associated with the key, or null if not found and flag is false or omitted.
     * @throws IllegalArgumentException if the key is not found and flag is explicitly true.
     */
    public String getValue(String key, boolean... flag) {
        boolean flagValue = flag.length > 0 && flag[0];  // Only true if flag is explicitly true
        String value = properties.getProperty(key);

        if (value != null) {
            log.info("Property value retrieved for key '{}': {}", key, value);
            return value.trim();
        } else {
            if (flagValue) {
                log.error("Property key '{}' not found in the selected properties file.", key);
                throw new IllegalArgumentException("Failed to find the provided config key: '" + key + "' in the selected properties file.");
            } else {
                log.warn("Property key '{}' not found in the selected properties file.", key);
                return null;
            }
        }
    }

    /**
     * Retrieves the value of a property as a String array for the given key.
     * Assumes the values are comma-separated in the properties file.
     *
     * @param key  the key whose associated value is to be returned as an array.
     * @param flag optional flag that, if true, throws an exception when the key is not found.
     *             If false or not provided, a warning will be logged and null will be returned.
     * @return an array of strings split by commas.
     * @throws IllegalArgumentException if the key is not found and suppression flag is not explicitly true.
     */
    public String[] getArrayValue(String key, boolean... flag) {
        boolean flagValue = flag.length > 0 && flag[0];
        String value = properties.getProperty(key);
        if (value != null) {
            String[] values = value.split(",", -1);
            log.info("Property array value retrieved for key '{}': {}", key, values);
            return values;
        } else {
            if (flagValue) {
                log.error("Property key (array) '{}' not found in the selected properties file.", key);
                throw new IllegalArgumentException("Failed to find the provided config key (array): '" + key + "' " + "in the selected properties file.");
            }
            log.warn("Property key (array) '{}' not found in the selected properties file.", key);

            return null;
        }
    }
}




