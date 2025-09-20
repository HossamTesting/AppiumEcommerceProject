package mobileDriverFactory;



import io.appium.java_client.ios.options.XCUITestOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static java.lang.invoke.MethodHandles.lookup;

    class GetIOS {

        private static final Logger log = LogManager.getLogger(lookup().lookupClass());

        static XCUITestOptions setupIOSOptions(Map<String, String> config) {
            XCUITestOptions options = new XCUITestOptions()
                    .setDeviceName(config.get("deviceName"))
                    .setApp(config.get("appLocation"))
                    .setPlatformVersion(config.get("platformVersion"))
                    .setAutomationName("XCUITest");

            log.info("Configured iOS options for device '{}'", config.get("deviceName"));
            return options;
        }
    }




