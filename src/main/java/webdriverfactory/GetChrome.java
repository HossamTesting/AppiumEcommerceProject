package webdriverfactory;

import datadriven.ConfigLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static webdriverfactory.GetWebDriver.getArgumentForMode;
import static java.lang.invoke.MethodHandles.lookup;

/**
 * GetChrome is a utility class responsible for creating and configuring
 * {@link ChromeOptions} based on a flexible set of input modes.
 *
 * <p>Key Features:
 * <ul>
 *   <li>Accepts any number of optional string-based browser modes (e.g., "headless", "incognito").</li>
 *   <li>Normalizes and validates each mode before processing.</li>
 *   <li>Maps recognized modes to Chrome CLI arguments via centralized mapping logic.</li>
 *   <li>Ignores null, empty, or unrecognized values gracefully.</li>
 *   <li>Prevents duplicate arguments using a {@link HashSet}.</li>
 *   <li>Logs detailed information about applied or skipped modes.</li>
 * </ul>
 *
 * <p>Supported modes include: headless, incognito, fullscreen, disable-extensions, and more.</p>
 *
 * <p>Example usage:
 * <pre>{@code
 * ChromeOptions options = GetChrome.setupChromeDriver("headless", "incognito");
 * }</pre>
 *
 * @author Hossam Atef
 * @version 1.0
 */
class GetChrome {

    private static final Logger log = LogManager.getLogger(lookup().lookupClass());

    /**
     * Creates and configures a {@link ChromeOptions} instance using the provided browser modes.
     * <br>Each mode is normalized and validated before being converted to a CLI argument.
     *
     * @param modes one or more string modes to apply to the Chrome browser (e.g., "headless", "fullscreen").
     *              <br>May be null or contain null/empty entries, which will be ignored.
     * @return ChromeOptions instance with the desired configuration
     */
    static ChromeOptions setupChromeDriver(String... modes) throws Exception {
        ChromeOptions options = getChromePreferences();
        Set<String> appliedModes = new HashSet<>();  // Set to store applied modes for logging & ignore duplication
        if (modes != null) {
            for (String mode : modes) {
                String normalizedMode = null;
                if (mode != null) {
                    normalizedMode = mode.replaceAll(" ", "").toLowerCase();
                }
                String argument = getArgumentForMode(GetWebDriver.browsers.CHROME, normalizedMode);  // Get the corresponding CLI argument for the mode
                if (!argument.isEmpty()) {
                    options.addArguments(argument);  // Add the recognized argument to Chrome options
                    appliedModes.add(argument);     // Track applied mode for logging
                }
            }
        } else {
            log.warn("Provided browser modes array is null. Default ChromeOptions will be used.");
        }
        if (appliedModes.isEmpty()) {         // Log the modes applied or that no modes were used
            log.info("Launching Chrome browser with no special modes.");
        } else {
            log.info("Launching Chrome browser with the following modes: '{}'", String.join(", ", appliedModes));
        }
        return options;  // Return the configured Chrome WebDriver instance
    }

    private static ChromeOptions getChromePreferences() throws Exception {
        ChromeOptions options = new ChromeOptions();
        ConfigLoader config = new ConfigLoader("src/test/resources/Config.properties");
        //Set the preferences
        Map<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("profile.default_content_setting_values.automatic_downloads", 1); // allow multiple downloads
        chromePrefs.put("download.default_directory",config.getValue("downloadDirectory"));
        options.setExperimentalOption("prefs", chromePrefs);
        return options;
    }


}