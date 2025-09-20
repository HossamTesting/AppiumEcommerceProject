package webdriverfactory;

import datadriven.ConfigLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.edge.EdgeOptions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static webdriverfactory.GetWebDriver.getArgumentForMode;
import static java.lang.invoke.MethodHandles.lookup;

/**
 * GetEdge is a utility class responsible for creating and configuring
 * {@link EdgeOptions} based on a flexible set of input modes.
 *
 * <p>Key Features:
 * <ul>
 *   <li>Accepts any number of optional string-based browser modes (e.g., "headless", "private").</li>
 *   <li>Normalizes and validates each mode before processing.</li>
 *   <li>Maps recognized modes to Edge CLI arguments via centralized mapping logic.</li>
 *   <li>Ignores null, empty, or unrecognized values gracefully.</li>
 *   <li>Prevents duplicate arguments using a {@link HashSet}.</li>
 *   <li>Logs detailed information about applied or skipped modes.</li>
 * </ul>
 *
 * <p>Supported modes include: headless, private, and more depending on Edge CLI support.</p>
 *
 * <p>Example usage:
 * <pre>{@code
 * EdgeOptions options = GetEdge.setupEdgeDriver("headless", "private");
 * }</pre>
 *
 * @author Hossam Atef
 * @version 1.0
 */
class GetEdge {

    private static final Logger log = LogManager.getLogger(lookup().lookupClass());

    /**
     * Creates and configures an {@link EdgeOptions} instance using the provided browser modes.
     * <br>Each mode is normalized and validated before being converted to a CLI argument.
     *
     * @param modes one or more string modes to apply to the Edge browser (e.g., "headless", "private").<br>
     *              May be null or contain null/empty entries, which will be ignored.
     * @return EdgeOptions instance with the desired configuration
     */
    static EdgeOptions setupEdgeDriver(String... modes) throws Exception {
        EdgeOptions options = getEdgePreferences();
        Set<String> appliedModes = new HashSet<>();
        if (modes != null) {
            for (String mode : modes) {
                String normalizedMode = null;
                if (mode != null) {
                    normalizedMode = mode.replaceAll(" ", "").toLowerCase();
                }
                String argument = getArgumentForMode(GetWebDriver.browsers.EDGE, normalizedMode);
                if (!argument.isEmpty()) {
                    options.addArguments(argument);
                    appliedModes.add(argument);
                }
            }
        } else {
            log.warn("Provided browser modes array is null. Default EdgeOptions will be used.");
        }
        if (appliedModes.isEmpty()) {
            log.info("Launching Edge browser with no special modes.");
        } else {
            log.info("Launching Edge browser with the following modes: '{}'",
                    String.join(", ", appliedModes));
        }
        return options; // Return the configured Edge WebDriver instance
    }

    private static EdgeOptions getEdgePreferences() throws Exception {
        EdgeOptions options = new EdgeOptions();
        ConfigLoader config = new ConfigLoader("src/test/resources/Config.properties");
//      System.setProperty("webdriver.edge.driver", config.getValue("edgeExe"));
        Map<String, Object> edgePrefs = new HashMap<>();
        edgePrefs.put("profile.default_content_setting_values.automatic_downloads", 1);
        edgePrefs.put("download.default_directory",config.getValue("downloadDirectory"));
        options.setExperimentalOption("prefs", edgePrefs);
        return options;
    }
}