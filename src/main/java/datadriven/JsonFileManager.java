package datadriven;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * JsonFileManager is a utility class designed to load and manage structured data
 * from JSON files.<br>It enables flexible access to flat and nested JSON elements
 * through key-based, prefix-based, and type-safe lookup methods.
 *
 * <p>Key Features:
 * <ul>
 *   <li>Loads a JSON file into memory as a map structure.</li>
 *   <li>Supports retrieval of values as single objects, maps, or lists.</li>
 *   <li>Allows prefix-based filtering of keys or values.</li>
 *   <li>Optional flag-based behavior to throw exceptions or log warnings when data is missing.</li>
 *   <li>Provides built-in null data checking via {@code checkNullData()} utility.</li>
 *   <li>Integrated logging using Log4j for diagnostics and traceability.</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 *   JsonFileManager jsonManager = new JsonFileManager("data.json");
 *   Object value = jsonManager.getValueByKey("username");
 *   List<String> items = jsonManager.getValueListByKey("roles");
 * }</pre>
 *
 * @author Hossam Atef
 * @version 1.0
 */
public class JsonFileManager {

    private static LinkedHashMap<String, Object> data;
    private Type type;
    private static final Logger log = LogManager.getLogger(lookup().lookupClass());

    /**
     * Initializes the JSON file manager and loads data from the specified JSON file.
     *
     * @param jsonPath the path to the JSON file.
     * @throws Exception if an error occurs during file reading or deserialization.
     */
    public JsonFileManager(String jsonPath) throws Exception {
        initialization();
        try {
            data = new Gson().fromJson(new FileReader(jsonPath), type);
            log.info("JSON data loaded successfully from: '{}'", jsonPath);
        } catch (Exception e) {
            log.error("Failed to load JSON data from: '{}'.", jsonPath);
            throw e;
        }
    }

    /**
     * Retrieves a value by key from the loaded JSON data.
     *
     * @param key  the key whose value should be retrieved.
     * @param flag optional flag:
     *             <ul>
     *               <li>If true, throws an exception if the key is not found.</li>
     *               <li>If false or not provided, logs a warning and returns null.</li>
     *             </ul>
     * @return the corresponding object's value associated with the given key, or null if not found.
     * @throws IllegalArgumentException if key is null/empty or not found and flag is true.
     */
    public Object getValueByKey(String key, boolean... flag) {
        boolean flagValue = flag.length > 0 && flag[0];
        if (key == null || key.isEmpty()) {
            if (flagValue) {
                log.error("The provided key is null or empty.");
                throw new IllegalArgumentException("The provided key: '" + key + "' is null or empty.");
            }
            log.warn("The provided key is null or empty.");
            return null;
        }
        boolean checkNullData = checkNullData(flagValue);
        if (!checkNullData) {
            if (data.containsKey(key)) {
                Object value = data.get(key);
                log.info("Value retrieved for key '{}': '{}'", key, value);
                return value;
            } else {
                if (flagValue) {
                    log.error("Key '{}' not found in JSON data.", key);
                    throw new IllegalArgumentException("Key '" + key + "' not found in JSON data.");
                }
                log.warn("Key '{}' not found in JSON data.", key);
                return null;
            }
        }
        return null;
    }

    /**
     * Retrieves a map value associated with the specified key.
     *
     * @param key  the key to look up.
     * @param flag optional flag:
     *             <ul>
     *               <li>If true, throws an exception if the key is not found or is not a map.</li>
     *               <li>If false or not provided, logs a warning and returns null.</li>
     *             </ul>
     * @return the corresponding map or null if not found or not a valid map.
     * @throws IllegalArgumentException if key is null/empty or not found and flag is true.
     * @throws ClassCastException       if the value of the key is not a map.
     */
    public LinkedHashMap<String, Object> getKeyAndValueByKey(String key, boolean... flag) {
        boolean flagValue = flag.length > 0 && flag[0];
        if (key == null || key.isEmpty()) {
            if (flagValue) {
                log.error("The provided key is null or empty.");
                throw new IllegalArgumentException("The provided key: '" + key + "' is null or empty.");
            }
            log.warn("The provided key is null or empty.");
            return null;

        }
        boolean checkNullData = checkNullData(flagValue);
        if (!checkNullData) {
            if (!data.containsKey(key)) {
                if (flagValue) {
                    log.error("Key '{}' not found in JSON data.", key);
                    throw new IllegalArgumentException("Key '" + key + "' not found in JSON data.");
                }
                log.warn("Key '{}' not found in JSON data.", key);
                return null;
            }

            try {
                Map<?, ?> rawMap = (Map<?, ?>) data.get(key);
                LinkedHashMap<String, Object> result = new LinkedHashMap<>();
                for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                    result.put(entry.getKey().toString(), entry.getValue());
                }
                log.info("Map retrieved for key '{}': {}", key, result);
                return result;
            } catch (ClassCastException e) {
                log.error("Value under key '{}' is not a valid map.", key);
                throw new ClassCastException("Value under key '" + key + "' is not a valid map, Please use another proper function to retrieve it's value.");
            }
        }
        return null;
    }

    /**
     * Retrieves a list of string values by key.
     *
     * @param key  the key to search for.
     * @param flag optional flag:
     *             <ul>
     *               <li>If true, throws an exception if the key is not found or value is not a list.</li>
     *               <li>If false or not provided, logs a warning and returns null.</li>
     *             </ul>
     * @return the corresponding list of strings, or null if not found or invalid.
     * @throws IllegalArgumentException if key is null/empty or not found and flag is true.
     * @throws ClassCastException       if the value of the key is not a list.
     */
    public List<String> getValueListByKey(String key, boolean... flag) {
        boolean flagValue = flag.length > 0 && flag[0];
        if (key == null || key.isEmpty()) {
            if (flagValue) {
                log.error("The provided key is null or empty.");
                throw new IllegalArgumentException("The provided key: '" + key + "' is null or empty.");
            }
            log.warn("The provided key is null or empty.");
            return null;
        }
        boolean checkNullData = checkNullData(flagValue);
        if (!checkNullData) {
            if (!data.containsKey(key)) {
                if (flagValue) {
                    log.error("Key '{}' not found in JSON data.", key);
                    throw new IllegalArgumentException("Key '" + key + "' not found in JSON data.");
                }
                log.warn("Key '{}' not found in JSON data.", key);
                return null;
            }
            try {
                List<?> valueList = (List<?>) data.get(key);
                log.info("List retrieved for key '{}': {}", key, valueList);
                List<String> valueStringList = new ArrayList<>();
                for (Object value : valueList) {
                    valueStringList.add(String.valueOf(value));
                }
                return valueStringList;
            } catch (Exception e) {
                log.error("Value under key '{}' is not a valid list.", key);
                throw new ClassCastException("Value under key '" + key + "' is not a valid list, Please use another function to retrieve it's value.");
            }
        }
        return null;
    }

    /**
     * Retrieves all keys from the JSON data.
     *
     * @param flag optional flag:
     *             <ul>
     *               <li>If true, throws an exception if the JSON is empty or null.</li>
     *               <li>If false or not provided, logs a warning and returns null.</li>
     *             </ul>
     * @return list of keys or null if data is empty.
     * @throws IllegalArgumentException if the JSON data is null or empty and flag is true
     */
    public List<String> getKeys(boolean... flag) {
        boolean flagValue = flag.length > 0 && flag[0];
        boolean checkNullData = checkNullData(flagValue);
        if (!checkNullData) {
            List<String> keys = new ArrayList<>(data.keySet());
            log.info("All the keys retrieved: '{}'", keys);
            return keys;
        }
        return null;
    }

    /**
     * Retrieves all keys that contain the specified prefix.
     *
     * @param keyPrefix the string to match against keys (case-insensitive, whitespace ignored).
     * @param flag      optional flag:
     *                  <ul>
     *                    <li>If true, throws an exception if no matching keys are found.</li>
     *                    <li>If false or not provided, logs a warning and returns null.</li>
     *                  </ul>
     * @return a list of matching keys or null if none found.
     * @throws IllegalArgumentException if no key matches or key prefix is null/empty and flag is true.
     */
    public List<String> getKeys(String keyPrefix, boolean... flag) {
        boolean flagValue = flag.length > 0 && flag[0];

        if (keyPrefix == null || keyPrefix.isEmpty()) {
            if (flagValue) {
                log.error("The provided keyPrefix is null or empty.");
                throw new IllegalArgumentException("The provided keyPrefix is null or empty." + keyPrefix);
            }
            log.warn("The provided keyPrefix is null or empty.");
            return null;
        }
        boolean checkNullData = checkNullData(flagValue);
        if (!checkNullData) {
            List<String> keys = new ArrayList<>();
            for (String key : data.keySet()) {
                if (key.toLowerCase().replaceAll("\\s+", "")
                        .contains(keyPrefix.toLowerCase().replaceAll("\\s+", ""))) {
                    keys.add(key);
                }
            }
            if (!keys.isEmpty()) {
                log.info("Matching keys for prefix '{}': {}", keyPrefix, keys);
                return keys;
            } else {
                if (flagValue) {
                    log.error("No matching keys found for prefix '{}'.", keyPrefix);
                    throw new IllegalArgumentException("No keys match the prefix: " + keyPrefix);
                }
                log.warn("No matching keys found for prefix '{}'.", keyPrefix);

                return null;
            }
        }
        return null;
    }

    /**
     * Retrieves all values that contain the specified prefix.
     *
     * @param valuePrefix the value substring to match (case-insensitive, whitespace ignored).
     * @param flag        optional flag:
     *                    <ul>
     *                      <li>If true, throws an exception if no matching values are found.</li>
     *                      <li>If false or not provided, logs a warning and returns null.</li>
     *                    </ul>
     * @return a list of matching values or null if none found.
     * @throws IllegalArgumentException if no value matches or value prefix is null/empty and flag is true.
     */
    public List<Object> getValues(String valuePrefix, boolean... flag) {
        boolean flagValue = flag.length > 0 && flag[0];

        if (valuePrefix == null || valuePrefix.isEmpty()) {
            log.warn("The provided valuePrefix is null or empty.");
            if (flagValue) {
                log.error("The provided valuePrefix is null or empty.");
                throw new IllegalArgumentException("The provided valuePrefix is null or empty." + valuePrefix);
            }
            return null;
        }

        boolean checkNullData = checkNullData(flagValue);
        if (!checkNullData) {
            List<Object> values = new ArrayList<>();
            for (Object value : data.values()) {
                if (value != null && value.toString().toLowerCase().replaceAll("\\s+", "")
                        .contains(valuePrefix.toLowerCase().replaceAll("\\s+", ""))) {
                    values.add(value);
                }
            }

            if (!values.isEmpty()) {
                log.info("Matching values for prefix '{}': {}", valuePrefix, values);
                return values;
            } else {
                if (flagValue) {
                    log.error("No matching values found for prefix '{}'.", valuePrefix);
                    throw new IllegalArgumentException("No values match the prefix: " + valuePrefix);
                }
                log.warn("No matching values found for prefix '{}'.", valuePrefix);
                return null;
            }
        }
        return null;
    }

    /**
     * Retrieves all values from the JSON data.
     *
     * @param flag optional flag:
     *             <ul>
     *               <li>If true, throws an exception if the JSON is empty or null.</li>
     *               <li>If false or not provided, logs a warning and returns null.</li>
     *             </ul>
     * @return list of values or null if data is empty.
     * @throws IllegalArgumentException if the JSON data is null or empty and flag is true
     */
    public List<Object> getValues(boolean... flag) {
        boolean flagValue = flag.length > 0 && flag[0];
        boolean checkNullData = checkNullData(flagValue);
        if (!checkNullData) {
            List<Object> values = new ArrayList<>(data.values());
            log.info("All values retrieved: {}", values);
            return values;
        }
        return null;
    }

    /**
     * Resets the internal data map and sets the expected type for JSON deserialization.
     */
    private void initialization() {
        data = null;
        type = new TypeToken<LinkedHashMap<String, Object>>() {
        }.getType();
        log.debug("Initialized type structure for JSON mapping.");
    }

    /**
     * Validates whether the internal JSON data map is initialized and not empty.
     * @param flagValue determines the behavior on null/empty data:
     *                  <ul>
     *                      <li>If true, throws a NullPointerException when data is null or empty.</li>
     *                      <li>If false, logs a warning and returns true indicating data is null/empty.</li>
     *                  </ul>
     * @return true if data is null or empty; false otherwise.
     * @throws NullPointerException if data is null or empty and flagValue is true.
     */
    private static boolean checkNullData(boolean flagValue) {
        if (data == null || data.isEmpty()) {
            if (flagValue) {
                log.error("JSON data is empty or not initialized '{}'.", data);
                throw new NullPointerException("JSON data is either empty or null");
            }
            log.warn("JSON data is empty or not initialized '{}'.", data);
            return true;
        }
        return false;
    }


}