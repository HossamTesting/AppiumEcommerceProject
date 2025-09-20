package mobileDriverFactory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.util.Map;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * GetMobileDriver is a factory and lifecycle manager for initializing and managing
 * Appium mobile drivers across different platforms (Android, iOS).
 *
 * <p>Key Features:
 * <ul>
 *   <li>Supports both Android and iOS platforms with dedicated option builders.</li>
 *   <li>Thread-safe using {@link ThreadLocal}, allowing parallel test execution.</li>
 *   <li>Prevents multiple driver initializations per thread.</li>
 *   <li>Provides lifecycle management (initialize, get, quit) for mobile drivers.</li>
 *   <li>Logs detailed driver initialization and cleanup activities.</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 * URI appiumServer = URI.create("http://127.0.0.1:4723/wd/hub");
 * Map<String, String> config = Map.of("platformVersion", "14.0", "deviceName", "iPhone 12");
 * GetMobileDriver.initDriver("ios", appiumServer, config);
 *
 * // ... test logic ...
 * GetMobileDriver.quitDriver();
 * }</pre>
 *
 * @author Hossam
 * @version 1.1
 */
public class GetMobileDriver {

    private static final Logger log = LogManager.getLogger(lookup().lookupClass());
    private static final ThreadLocal<AppiumDriver> localDriver = new ThreadLocal<>();

    private GetMobileDriver() {
        // Utility class: prevent instantiation
    }

    /**
     * Initializes an {@link AppiumDriver} instance for the current thread if not already initialized.
     *
     * @param platform        the mobile platform ("android" or "ios"), case-insensitive
     * @param appiumServerUri the URI of the Appium server (e.g., http://127.0.0.1:4723/")
     * @param config          map of configuration values (capabilities such as deviceName, platformVersion, etc.)
     * @throws MalformedURLException if the Appium server URI is invalid
     * @throws IllegalArgumentException if the platform is not supported
     */
    public static void getInstance(String platform, URI appiumServerUri, Map<String, String> config) throws MalformedURLException {
        if (localDriver.get() != null) {
            log.warn("Mobile driver already initialized for this thread. Reusing existing instance.");
            return;
        }

        AppiumDriver driver;
        switch (platform.toLowerCase()) {
            case "android" -> driver = new AndroidDriver(appiumServerUri.toURL(), GetAndroid.setupAndroidOptions(config));
            case "ios" -> driver = new IOSDriver(appiumServerUri.toURL(), GetIOS.setupIOSOptions(config));
            default -> throw new IllegalArgumentException("Unsupported platform: " + platform);
        }

        localDriver.set(driver);
        log.info("Initialized '{}' driver successfully.", platform);
    }

    /**
     * Retrieves the current thread's {@link AppiumDriver} instance.
     *
     * @return the AppiumDriver for the current thread, or {@code null} if not initialized
     */
    public static AppiumDriver getLocalDriver() {
        return localDriver.get();
    }

    /**
     * Quits and cleans up the Appium driver for the current thread.
     * <br>Ensures proper resource deallocation and removes thread-local references.
     */
    public static void quitDriver() {
        AppiumDriver driver = localDriver.get();
        if (driver != null) {
            try {
                driver.quit();
                log.info("Mobile driver quit successfully.");
            } catch (Exception e) {
                log.warn("Error quitting mobile driver: {}", e.getMessage());
            } finally {
                localDriver.remove();
            }
        } else {
            log.warn("No mobile driver found for this thread.");
        }
    }
}
