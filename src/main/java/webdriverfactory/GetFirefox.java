package webdriverfactory;

import datadriven.ConfigLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.HashSet;
import java.util.Set;

import static webdriverfactory.GetWebDriver.getArgumentForMode;
import static java.lang.invoke.MethodHandles.lookup;

/**
 * GetFirefox is a utility class responsible for creating and configuring
 * {@link FirefoxOptions} based on a flexible set of input modes.
 *
 * <p>Key Features:
 * <ul>
 *   <li>Accepts any number of optional string-based browser modes (e.g., "headless", "private").</li>
 *   <li>Normalizes and validates each mode before processing.</li>
 *   <li>Maps recognized modes to Firefox CLI arguments via centralized mapping logic.</li>
 *   <li>Ignores null, empty, or unrecognized values gracefully.</li>
 *   <li>Prevents duplicate arguments using a {@link HashSet}.</li>
 *   <li>Logs detailed information about applied or skipped modes.</li>
 * </ul>
 *
 * <p>Supported modes include: headless, incognito, fullscreen, disable-extensions, and more.</p>
 *
 * <p>Example usage:
 * <pre>{@code
 * FirefoxOptions options = GetFirefox.setupFirefoxDriver("headless", "private");
 * }</pre>
 *
 * @author Hossam Atef
 * @version 1.0
 */
class GetFirefox {

    private static final Logger log = LogManager.getLogger(lookup().lookupClass());

    /**
     * Creates and configures a {@link FirefoxOptions} instance using the provided browser modes.
     * <br>Each mode is normalized and validated before being converted to a CLI argument.
     *
     * @param modes one or more string modes to apply to the Firefox browser (e.g., "headless", "fullscreen").
     *              <br>May be null or contain null/empty entries, which will be ignored.
     * @return ChromeOptions instance with the desired configuration
     */
    static FirefoxOptions setupFirefoxDriver(String... modes) throws Exception {
        FirefoxOptions options = new FirefoxOptions();
        ConfigLoader config = new ConfigLoader("src/test/resources/Config.properties");
        options.addPreference("browser.download.folderList", 2);
        options.addPreference("browser.download.dir", config.getValue("downloadDirectory"));
        options.addPreference("browser.download.useDownloadDir", true);
        options.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf"); // adjust MIME types as needed
        options.addPreference("pdfjs.disabled", true); // disable built-in PDF viewer
        Set<String> appliedModes = new HashSet<>();
        if (modes != null) {
            for (String mode : modes) {
                String normalizedMode = null;
                if (mode != null) {
                    normalizedMode = mode.replaceAll(" ", "").toLowerCase();
                }
                String argument = getArgumentForMode(GetWebDriver.browsers.FIREFOX, normalizedMode);
                if (!argument.isEmpty()) {
                    options.addArguments(argument);
                    appliedModes.add(argument);
                }
            }
        } else {
            log.warn("Provided browser modes array is null. Default FirefoxOptions will be used.");
        }
        if (appliedModes.isEmpty()) {
            log.info("Launching Firefox browser with no special modes.");
        } else {
            log.info("Launching Firefox browser with the following modes: '{}'",
                    String.join(", ", appliedModes));
        }
        return options;  // Return the configured Firefox WebDriver instance
    }
}