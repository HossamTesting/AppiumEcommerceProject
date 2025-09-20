package tests;

import actions.UIActions;
import datadriven.ConfigLoader;
import datadriven.JsonFileManager;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import mobileDriverFactory.GetMobileDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import pages.*;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

import static java.lang.invoke.MethodHandles.lookup;

public class BaseTest {


    private static final Logger log = LogManager.getLogger(lookup().lookupClass());
    protected static ThreadLocal<ConfigLoader> configurationLoader = new ThreadLocal<>();
    protected ThreadLocal<JsonFileManager> jsonFileManager = new ThreadLocal<>();
    protected ThreadLocal<String> methodName = new ThreadLocal<>();
    protected ThreadLocal<SoftAssert> softAssert = new ThreadLocal<>();
    protected ThreadLocal<HomePage> homePage = new ThreadLocal<>();
    protected ThreadLocal<ProductPage> productPage = new ThreadLocal<>();
    protected ThreadLocal<CartPage> cartPage = new ThreadLocal<>();
    protected ThreadLocal<WebPage> webPage = new ThreadLocal<>();
    protected ThreadLocal<String> acquiredUserId = new ThreadLocal<>();
    protected ThreadLocal<UIActions> uiActions = new ThreadLocal<>();

    private AppiumDriverLocalService driverLocalService;

    @BeforeMethod
    public void setup(Method method) throws Exception {
        ThreadContext.put("TestName", "setup_" + method.getName());
        methodName.set(method.getName());
        log.info("************ Starting method: setup ************");

        configurationLoader.set(new ConfigLoader("src/test/resources/Config.properties"));
        jsonFileManager.set(new JsonFileManager("src/test/resources/jsonNewData.json"));
        //Setup Appium Server
        driverLocalService = new AppiumServiceBuilder()
                .withAppiumJS(new File(configurationLoader.get().getValue("appiumServerPath")))
                .withIPAddress(configurationLoader.get().getValue("appiumServerIPAddress"))
                .usingPort(Integer.parseInt(configurationLoader.get().getValue("appiumServerPort")))
                .build();
        driverLocalService.start();

        URI appiumServerUri = new URI(configurationLoader.get().getValue("appiumServerURI"));
        String platform = configurationLoader.get().getValue("platform");
        Map<String, String> mobileConfig = Map.of(
                "platform", platform,
                "deviceName", configurationLoader.get().getValue("deviceName"),
                "appLocation", configurationLoader.get().getValue("appLocation"),
                "chromeExePath",configurationLoader.get().getValue("chromeExePath")
        );

        GetMobileDriver.getInstance(platform,
                appiumServerUri,
                mobileConfig
                );

        uiActions.set(new UIActions(20, UIActions.platform.mobile));
        softAssert.set(new SoftAssert());
    }

    @BeforeSuite
    public void restingJULBridge() {

        ThreadContext.put("TestName", "restingJULBridge");
        log.info("************ Starting method: restingJULBridge ************");
        org.apache.logging.log4j.jul.LogManager.getLogManager().reset();


    }


    @AfterMethod
    public void quitDriver() throws InterruptedException {
        ThreadContext.put("TestName", "quitDriver_" + methodName.get());
        log.info("************ Starting method: 'quitDriver' ************");
        Thread.sleep(5000);
        GetMobileDriver.quitDriver();
        clearThreadLocals();
    }

    private void clearThreadLocals() {
        softAssert.remove();
        methodName.remove();
        configurationLoader.remove();
        jsonFileManager.remove();
        homePage.remove();
        productPage.remove();
        acquiredUserId.remove();
        homePage.remove();
        productPage.remove();
        cartPage.remove();
        webPage.remove();


    }
}
