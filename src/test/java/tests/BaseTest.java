package tests;

import actions.UIActions;
import datadriven.ConfigLoader;
import datadriven.JsonFileManager;
import mobileDriverFactory.GetMobileDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.asserts.SoftAssert;
import pages.CartPage;
import pages.HomePage;
import pages.ProductPage;
import pages.WebPage;
import utility.DevicesManager;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static java.lang.invoke.MethodHandles.lookup;

public class BaseTest {


    private static final Logger log = LogManager.getLogger(lookup().lookupClass());
    private static ConfigLoader configurationLoader;
    private static DevicesManager deviceManager;
    private static boolean deviceInitialized = false;

    protected static JsonFileManager jsonFileManager;
    protected ThreadLocal<String> methodName = new ThreadLocal<>();
    protected ThreadLocal<SoftAssert> softAssert = new ThreadLocal<>();
    protected ThreadLocal<HomePage> homePage = new ThreadLocal<>();
    protected ThreadLocal<ProductPage> productPage = new ThreadLocal<>();
    protected ThreadLocal<CartPage> cartPage = new ThreadLocal<>();
    protected ThreadLocal<WebPage> webPage = new ThreadLocal<>();
    protected ThreadLocal<UIActions> uiActions = new ThreadLocal<>();
    protected ThreadLocal<Map<String, Object>> acquiredDevice = new ThreadLocal<>();


    @BeforeSuite
    public void startAppiumServices() throws Exception {

        ThreadContext.put("TestName", "startAppiumServices");
        log.info("************ Starting method: startAppiumServices ************");
        org.apache.logging.log4j.jul.LogManager.getLogManager().reset();
        configurationLoader = new ConfigLoader("src/test/resources/Config.properties");
        jsonFileManager = new JsonFileManager("src/test/resources/jsonNewData.json");

        if (!deviceInitialized) {                                           // Initializing devices pool
            deviceManager = new DevicesManager();

            List<Map<String, Object>> devices = jsonFileManager.getListOfMapsByKey("devices");
            deviceManager.initialize(devices);

            deviceManager.startAllServices(configurationLoader);            // Start Appium servers once for all devices
            deviceInitialized = true;
        }

    }

    @BeforeMethod
    public void setup(Method method) throws Exception {
        ThreadContext.put("TestName", "setup_" + method.getName());
        methodName.set(method.getName());
        log.info("************ Starting method: setup ************");

        Map<String, Object> device = deviceManager.acquireDevice();
        acquiredDevice.set(device);

        URI appiumServerUri = new URI("http://" + configurationLoader.getValue("appiumServerIPAddress")
                + ":" +((Double) acquiredDevice.get().get("serverPort")).intValue());
        String platform = configurationLoader.getValue("platform");
        Map<String, String> mobileConfig = Map.of(
                "platform", platform,
                "deviceName", acquiredDevice.get().get("deviceName").toString(),
                "appLocation", configurationLoader.getValue("appLocation"),
                "chromeExePath", configurationLoader.getValue("chromeExePath"),
                "uuid", acquiredDevice.get().get("uuid").toString(),
                "systemPort", Integer.toString(((Double) acquiredDevice.get().get("systemPort")).intValue()));


        GetMobileDriver.getInstance(platform,                                  // Getting MobileDriver instance
                appiumServerUri,
                mobileConfig
        );
        uiActions.set(new UIActions(20, UIActions.platform.mobile));
        softAssert.set(new SoftAssert());
    }


    @AfterMethod
    public void quitDriver() {
        ThreadContext.put("TestName", "quitDriver_" + methodName.get());
        log.info("************ Starting method: 'quitDriver' ************");
        GetMobileDriver.quitDriver();


        Map<String, Object> device = acquiredDevice.get();                    // Release device back to pool
        if (device != null) {
            deviceManager.releaseDevice(device);
            log.info("Device '{}' is released back to pool.", device.get("deviceName"));
        }
        clearThreadLocals();
    }

    @AfterSuite
    public void stopAppiumServices() {
        ThreadContext.put("TestName", "stopAppiumServices");
        log.info("************ Starting method: stopAppiumServices ************");
        deviceManager.stopAllServices();
    }


    private void clearThreadLocals() {
        softAssert.remove();
        methodName.remove();
        homePage.remove();
        productPage.remove();
        acquiredDevice.remove();
        homePage.remove();
        productPage.remove();
        cartPage.remove();
        webPage.remove();


    }
}
