package webdriverfactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.Map;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * GetWebDriver is a factory and thread-safe manager for creating and handling WebDriver instances
 * in automated testing frameworks.<br>It supports multiple browsers and modes while ensuring
 * that each thread gets a dedicated WebDriver instance via ThreadLocal.
 *
 * <p>Key Features:
 * <ul>
 *   <li>Supports Chrome, Firefox, and Edge browsers.</li>
 *   <li>Thread-safe driver management using ThreadLocal.</li>
 *   <li>Flexible browser modes like headless, incognito, fullscreen, etc.</li>
 *   <li>Encapsulates setup logic to initialize WebDriver with flags.</li>
 *   <li>Integrated Log4j-based logging for tracking driver lifecycle events.</li>
 *   <li>Prevents duplicate WebDriver creation for the same thread.</li>
 *   <li>Graceful shutdown and cleanup of driver instances.</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 *   GetWebDriver.getInstance("chrome", "headless", "incognito");
 *   GetWebDriver.getLocalDriver();
 *   GetWebDriver.quitDriver();
 * }</pre>
 *
 * @author Hossam Atef
 * @version 1.0
 */


public class GetWebDriver {

    private static final Logger log = LogManager.getLogger(lookup().lookupClass());
    private static final ThreadLocal<WebDriver> localDrivers = new ThreadLocal<>();
    private static final ThreadLocal<GetWebDriver> getWebDriver = new ThreadLocal<>();


    /**
     * Private constructor to initialize the browser driver.
     * <br>Ensures a browser is launched with specified modes only once per thread.
     *
     * @param browserName the browser name (e.g., "chrome", "firefox", "edge")
     * @param modes       optional modes such as "headless", "incognito", etc.
     */
    private GetWebDriver(String browserName, String... modes) throws Exception {
        launchBrowser(browserName, modes);
    }

    /**
     * Returns a singleton instance of the GetWebDriver class.
     * <br>Initializes and launches the browser only once per test run (if not already launched).
     *
     * @param browserName the browser name (e.g., "chrome", "firefox", "edge")
     * @param modes       optional browser modes (e.g., "headless", "incognito", etc.)
     */
    public static void getInstance(String browserName, String... modes) throws Exception {
        if (getWebDriver.get() == null) {
            getWebDriver.set(new GetWebDriver(browserName, modes));
        } else
            log.warn("WebDriver is already initialized for this thread, Using existing instance instead of launching a new '{}' instance.", browserName);

    }

    /**
     * Sets the WebDriver instance for the current thread.
     *
     * @param driver the WebDriver to associate with the current thread
     */
    private void setLocalDriver(WebDriver driver) {

        localDrivers.set(driver);
    }

    /**
     * Returns the WebDriver instance associated with the current thread.
     *
     * @return Current WebDriver instance, It returns null if the instance isn't initialized
     */
    public static WebDriver getLocalDriver() {
        return localDrivers.get();
    }

    /**
     * Launches the specified browser with the provided modes.
     *
     * @param browserName the browser name (e.g., "chrome", "firefox", "edge")
     * @param modes       one or more browser mode strings (e.g., "headless", "incognito", "inprivate")
     * @throws IllegalArgumentException if an unknown browser is specified
     */
    private void launchBrowser(String browserName, String... modes) throws Exception {
        if (browserName == null){
            log.error("Provided browserName is 'null', Please use 'chrome', 'firefox', or 'edge' only.");
            throw new IllegalArgumentException("Unknown browser specified, Supported browsers are: chrome, firefox, edge.");
        }
        String normalizedBrowserName = browserName.toLowerCase().trim();  // Normalize the browser name (e.g., 'chrome', 'edge', etc.)

        // Select the appropriate browser and return the corresponding WebDriver
        WebDriver driver = switch (normalizedBrowserName) {

            case "chrome" -> new ChromeDriver(GetChrome.setupChromeDriver(modes));
            case "firefox" -> new FirefoxDriver(GetFirefox.setupFirefoxDriver(modes));
            case "edge" -> new EdgeDriver(GetEdge.setupEdgeDriver(modes));
            default -> {  // Handle unknown browser names
                log.error("Unknown browser specified: '{}', Please use 'chrome', 'firefox', or 'edge' only.", browserName);
                throw new IllegalArgumentException("Unknown browser specified, Supported browsers are: chrome, firefox, edge.");
            }
        };

        log.info("Driver instance is Launched successfully with '{}' browser.", normalizedBrowserName);  // Log the successful browser launch
        setLocalDriver(driver);
        getLocalDriver();
    }

    /**
     * Gets the CLI argument for a given mode based on the browser.
     *
     * @param browserName the browser (e.g., CHROME, FIREFOX, EDGE)
     * @param mode        the mode to get the argument for (e.g., "headless", "incognito","inprivate")
     * @return the CLI argument string for the mode or an empty string if the mode is invalid
     */
    static String getArgumentForMode(browsers browserName, String mode) {
        Map<String, String> modeMap = switch (browserName) {
            case browsers.CHROME -> CHROME_MODES;
            case browsers.FIREFOX -> FIREFOX_MODES;
            case browsers.EDGE -> EDGE_MODES;
        };
        if (mode == null || mode.isEmpty()) {
            log.warn("Mode provided '{}' is either null or empty or spaces!", mode);
            return "";  // Return empty if the mode is null or empty
        }
        if (modeMap.containsKey(mode)) {
            // Return the corresponding argument from the mode map , if the mode is valid.
            String argument = modeMap.get(mode);
            log.info("Mode '{}' recognized for {} browser.", argument, browserName);
            return argument;
        } else {
            // Log a warning if the mode is invalid for the specified browser
            log.warn("Mode '{}' is invalid for {} browser. Supported modes are: {}", mode, browserName, String.join(", ", modeMap.keySet()));
            return "";  // Return empty string for invalid mode
        }
    }


    /**
     * Quits the WebDriver associated with the current thread and performs cleanup.
     */
    public static void quitDriver() {
        WebDriver driver = getLocalDriver();

        if (driver != null) {
            try {
                // Check if the session is still valid before quitting
                driver.getWindowHandle(); // Will throw if session is already closed
                driver.quit();
                log.info("Driver instance quit successfully.");
            } catch (Exception e) {
                log.warn("Last driver session might already be closed or unreachable. Proceeding with quitting the current driver.");
            } finally {
                // Always clean up thread-local variables
                localDrivers.remove();
                getWebDriver.remove();
            }
        } else {
            log.warn("Driver instance isn't initiated yet! Please get an instance first " +
                    "Or Driver is already quit.");
        }
    }



    // Define supported modes for each browser
    @SuppressWarnings("SpellCheckingInspection")
    private static final Map<String, String> CHROME_MODES = Map.ofEntries(
            Map.entry("headless", "--headless"),
            Map.entry("headlessmode", "--headless"),
            Map.entry("--headless", "--headless"),


            Map.entry("incognito", "--incognito"),
            Map.entry("incognitomode", "--incognito"),
            Map.entry("inco", "--incognito"),
            Map.entry("--incognito", "--incognito"),

            Map.entry("fullscreen", "--start-fullscreen"),
            Map.entry("--start-fullscreen", "--start-fullscreen"),
            Map.entry("startfullscreen", "--start-fullscreen"),
            Map.entry("fullscreenmode", "--start-fullscreen"),


            Map.entry("disable-extensions", "--disable-extensions"),
            Map.entry("disableextensions", "--disable-extensions"),
            Map.entry("--disable-extensions", "--disable-extensions"),


            Map.entry("disable-gpu", "--disable-gpu"),
            Map.entry("disablegpu", "--disable-gpu"),
            Map.entry("--disable-gpu", "--disable-gpu"),

            Map.entry("max", "--start-maximized"),
            Map.entry("maximized", "--start-maximized"),
            Map.entry("start-maximized", "--start-maximized"),
            Map.entry("startmaximized", "--start-maximized"),
            Map.entry("--start-maximized", "--start-maximized"),
            Map.entry("startmaximizedmode", "--start-maximized"),

            Map.entry("no-sandbox", "--no-sandbox"),
            Map.entry("nosandbox", "--no-sandbox"),
            Map.entry("--no-sandbox", "--no-sandbox"),

            Map.entry("enable-logging", "--enable-logging"),
            Map.entry("enablelogging", "--enable-logging"),
            Map.entry("--enable-logging", "--enable-logging"),

            Map.entry("disable-dev-shm-usage", "--disable-dev-shm-usage"),
            Map.entry("disabledevshmusage", "--disable-dev-shm-usage"),
            Map.entry("--disable-dev-shm-usage", "--disable-dev-shm-usage"),


            Map.entry("inprivate", "--inprivate"),
            Map.entry("--inprivate", "--inprivate")
    );

    private static final Map<String, String> FIREFOX_MODES = Map.ofEntries(
            Map.entry("headless", "--headless"),
            Map.entry("headlessmode", "--headless"),
            Map.entry("--headless", "--headless"),
            Map.entry("private", "--private"),
            Map.entry("inprivate", "--inprivate"),
            Map.entry("--inprivate", "--inprivate")

    );

    @SuppressWarnings("SpellCheckingInspection")
    private static final Map<String, String> EDGE_MODES = Map.ofEntries(
            Map.entry("max", "--start-maximized"),
            Map.entry("maximized", "--start-maximized"),
            Map.entry("start-maximized", "--start-maximized"),
            Map.entry("startmaximized", "--start-maximized"),
            Map.entry("--start-maximized", "--start-maximized"),
            Map.entry("startmaximizedmode", "--start-maximized"),
            Map.entry("headless", "--headless"),
            Map.entry("headlessmode", "--headless"),
            Map.entry("--headless", "-headless"),
            Map.entry("inprivate", "--inprivate"),
            Map.entry("--inprivate", "--inprivate"),
            Map.entry("private", "--inprivate")
//            Map.entry("fullscreen", "--start-fullscreen")



    );

    /**
     * Enumeration for supported browsers. Used to define valid browser types.
     */
    enum browsers {
        CHROME,
        EDGE,
        FIREFOX

    }

}



