package mobileDriverFactory;

import io.appium.java_client.android.options.UiAutomator2Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * GetAndroid is a utility class responsible for creating and configuring
 * {@link UiAutomator2Options} for Android devices and emulators.
 *
 * <p>Key Features:
 * <ul>
 *   <li>Builds Android-specific options using values from a configuration map.</li>
 *   <li>Sets required capabilities such as device name, app path, and chromedriver path.</li>
 *   <li>Uses UiAutomator2 as the default automation engine.</li>
 *   <li>Logs the applied configuration for debugging and traceability.</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 * Map<String, String> config = Map.of(
 *     "deviceName", "Pixel_6_API_33",
 *     "appLocation", "src/test/resources/apps/MyApp.apk",
 *     "chromeExePath", "C:/drivers/chromedriver.exe"
 * );
 *
 * UiAutomator2Options options = GetAndroid.setupAndroidOptions(config);
 * }</pre>
 *
 * @author Hossam
 * @version 1.0
 */
class GetAndroid {


    private static final Logger log = LogManager.getLogger(lookup().lookupClass());

    /**
     * Creates and configures {@link UiAutomator2Options} using the provided configuration map.
     *
     * @param config configuration map containing keys such as:
     *               <ul>
     *                 <li>{@code deviceName} → the name of the Android device/emulator</li>
     *                 <li>{@code appLocation} → path to the APK file</li>
     *                 <li>{@code chromeExePath} → path to the ChromeDriver executable (if webviews are used)</li>
     *               </ul>
     * @return a fully configured {@link UiAutomator2Options} instance
     */
    static UiAutomator2Options setupAndroidOptions(Map<String, String> config) {
        UiAutomator2Options options = new UiAutomator2Options()
                .setChromedriverExecutable(config.get("chromeExePath"))
                .setDeviceName(config.get("deviceName"))
                .setUdid(config.get("uuid"))
                .setApp(config.get("appLocation"))
                .setAutomationName("UiAutomator2")
                .setPlatformName("android");
                options.setSystemPort(Integer.parseInt(config.get("systemPort")));

        // options.setCapability("browserName", "Chrome");
        log.info("Configured Android options for device '{}'.", config.get("deviceName"));
        return options;
    }
}
