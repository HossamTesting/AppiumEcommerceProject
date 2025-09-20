package actions;

import mobileDriverFactory.GetMobileDriver;
import webdriverfactory.GetWebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.invoke.MethodHandles.lookup;


/**
 * Provides high-level reusable browser actions for both Web and Mobile drivers (hybrid support).
 * <br>This class automatically resolves whether to use a WebDriver or AppiumDriver
 * based on the active driver available in the current thread.
 *
 * <p>Typical usage includes navigation, page control, retrieving information,
 * and performing interactions common to both web and hybrid mobile contexts.</p>
 *
 * @author Hossam
 * @version 2.0
 */

public class BrowserActions {

    private static final Logger log = LogManager.getLogger(lookup().lookupClass());


    /**
     * Resolves the currently active driver.
     * Priority: WebDriver > MobileDriver.
     *
     * @return Active WebDriver instance (either standard WebDriver or AppiumDriver)
     * @throws IllegalStateException if no driver is initialized
     */
    private static WebDriver resolveActiveDriver() {
        WebDriver webDriver = GetWebDriver.getLocalDriver();
        WebDriver mobileDriver = GetMobileDriver.getLocalDriver();

        if (webDriver == null && mobileDriver == null) {
            throw new IllegalStateException("Both WebDriver and MobileDriver are null. Cannot proceed.");
        }

        if (webDriver != null) {
            log.debug("Resolved active driver: WebDriver instance.");
            return webDriver;
        }

        log.debug("Resolved active driver: MobileDriver instance.");
        return mobileDriver;
    }


    /**
     * Returns a simple string description of the WebElement.
     *
     * @param element the WebElement to describe
     * @return string representation of the element
     */
    private static String describeElement(WebElement element) {
        try {
            return element.toString().replaceAll(".*-> ", "").replaceFirst("]", "");
        } catch (Exception e) {
            return "Unknown WebElement";
        }
    }

    //________________________________________________________________________________________________________________//
    // Navigation

    /**
     * Navigates to the specified URL.
     *
     * @param url target URL to navigate to (must not be null)
     * @throws IllegalArgumentException if the URL is null or blank
     */
    public static void navigateToPage(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL must not be null or blank.");
        }
        WebDriver driver = resolveActiveDriver();
        driver.navigate().to(url);
        log.info("Navigated to URL: '{}'", url);
    }

    /**
     * Navigates back in browser history.
     */
    public static void navigateBack() {
        WebDriver driver = resolveActiveDriver();
        driver.navigate().back();
        log.info("Navigated back in browser history.");
    }

    /**
     * Navigates forward in browser history.
     */
    public static void navigateForward() {
        WebDriver driver = resolveActiveDriver();
        driver.navigate().forward();
        log.info("Navigated forward in browser history.");
    }

    /**
     * Refreshes the current page.
     */
    public static void refreshPage() {
        WebDriver driver = resolveActiveDriver();
        driver.navigate().refresh();
        log.info("Refreshed the current page.");
    }

    //________________________________________________________________________________________________________________//
    // Window Management

    /** Maximizes the browser window if supported. */
    public static void maximizeWindow() {
        WebDriver driver = resolveActiveDriver();
        try {
            driver.manage().window().maximize();
            log.info("Browser window maximized.");
        } catch (Exception e) {
            log.error("Maximize not supported on this driver.");
                    throw e;
        }
    }

    /** Minimizes the browser window if supported. */
    public static void minimizeWindow() {
        WebDriver driver = resolveActiveDriver();
        try {
            driver.manage().window().minimize();
            log.info("Browser window minimized.");
        } catch (Exception e) {
            log.error("Minimize not supported on this driver.");
            throw e;
        }
    }

    /** Sets the browser window to full screen if supported. */
    public static void fullscreen() {
        WebDriver driver = resolveActiveDriver();
        try {
            driver.manage().window().fullscreen();
            log.info("Browser set to fullscreen.");
        } catch (Exception e) {
            log.error("Fullscreen not supported on this driver.");
            throw e;
        }
    }

    /**
     * Sets the browser window size.
     *
     * @param width  desired width in pixels
     * @param height desired height in pixels
     */
    public static void setScreenSize(int width, int height) {
        WebDriver driver = resolveActiveDriver();
        try {
            driver.manage().window().setSize(new Dimension(width, height));
            log.info("Window size set to width: {}, height: {}", width, height);
        } catch (Exception e) {
            log.error("Set screen size not supported.");
            throw e;
        }
    }

    /**
     * Sets the window position.
     *
     * @param x horizontal position (pixels from left)
     * @param y vertical position (pixels from top)
     */
    public static void setWindowPosition(int x, int y) {
        WebDriver driver = resolveActiveDriver();
        try {
            driver.manage().window().setPosition(new Point(x, y));
            log.info("Window position set to x: {}, y: {}", x, y);
        } catch (Exception e) {
            log.error("Set window position not supported.");
            throw e;

        }
    }

    //________________________________________________________________________________________________________________//
    // Alerts

    /** Switches to the currently active alert. */
    public static Alert switchToAlert() {
        Alert alert = resolveActiveDriver().switchTo().alert();
        log.info("Switched to alert.");
        return alert;
    }

    /** Accepts the currently active alert. */
    public static void acceptAlert() {
        resolveActiveDriver().switchTo().alert().accept();
        log.info("Alert accepted.");
    }

    /** Dismisses the currently active alert. */
    public static void dismissAlert() {
        resolveActiveDriver().switchTo().alert().dismiss();
        log.info("Alert dismissed.");
    }

    /**
     * Sends text to the active alert.
     *
     * @param text text to send
     */
    public static void setAlertText(String text) {
        resolveActiveDriver().switchTo().alert().sendKeys(text);
        log.info("Text '{}' entered into alert.", text);
    }

    /** @return text from the currently active alert */
    public static String getAlertText() {
        String text = resolveActiveDriver().switchTo().alert().getText();
        log.info("Retrieved alert text: '{}'.", text);
        return text;
    }

    //________________________________________________________________________________________________________________//
    // Windows/Tabs

    /** @return handle of the current window/tab */
    public static String getWindowHandle() {
        String handle = resolveActiveDriver().getWindowHandle();
        log.info("Current window handle: '{}'", handle);
        return handle;
    }

    /** @return list of all open window handles */
    public static List<String> getWindowHandles() {
        List<String> handles = new ArrayList<>(resolveActiveDriver().getWindowHandles());
        log.info("Retrieved {} window handles: {}", handles.size(), handles);
        return handles;
    }

    /**
     * Switches to a window by handle.
     *
     * @param windowHandle handle of the window/tab
     */
    public static void switchToWindow(String windowHandle) {
        resolveActiveDriver().switchTo().window(windowHandle);
        log.info("Switched to window with handle: '{}'", windowHandle);
    }

    /** @return title of the current window/tab */
    public static String getWindowTitle() {
        String title = resolveActiveDriver().getTitle();
        log.info("Window title: '{}'", title);
        return title;
    }

    /** @return current page URL */
    public static String getURL() {
        String url = resolveActiveDriver().getCurrentUrl();
        log.info("Current URL: '{}'", url);
        return url;
    }

    /** Opens and switches to a new tab (if supported). */
    public static void openAndSwitchToNewTab() {
        WebDriver driver = resolveActiveDriver();
        try {
            driver.switchTo().newWindow(WindowType.TAB);
            log.info("Opened and switched to a new tab.");
        } catch (Exception e) {
            log.error("Opening new tab not supported.");
            throw e;
        }
    }

    /** Opens and switches to a new window (if supported). */
    public static void openAndSwitchToNewWindow() {
        WebDriver driver = resolveActiveDriver();
        try {
            driver.switchTo().newWindow(WindowType.WINDOW);
            log.info("Opened and switched to a new window.");
        } catch (Exception e) {
            log.error("Opening new window not supported.");
            throw e;
        }
    }

    /** Closes the current window/tab. */
    public static void closeWindow() {
        resolveActiveDriver().close();
        log.info("Closed current window.");
    }

    //________________________________________________________________________________________________________________//
    // Frames

    /**
     * Switches to a frame by index.
     *
     * @param frameId frame index
     */
    public static void switchToFrame(int frameId) {
        resolveActiveDriver().switchTo().frame(frameId);
        log.info("Switched to frame with index: {}", frameId);
    }

    /**
     * Switches to a frame by name or ID.
     *
     * @param frameName frame name or ID
     */
    public static void switchToFrame(String frameName) {
        resolveActiveDriver().switchTo().frame(frameName);
        log.info("Switched to frame with name/ID: '{}'", frameName);
    }

    /**
     * Switches to a frame using its WebElement.
     *
     * @param frameElement frame WebElement
     */
    public static void switchToFrame(WebElement frameElement) {
        resolveActiveDriver().switchTo().frame(frameElement);
        log.info("Switched to frame: {}", describeElement(frameElement));
    }

    /** Switches back to the parent frame. */
    public static void switchToParentFrame() {
        resolveActiveDriver().switchTo().parentFrame();
        log.info("Switched to parent frame.");
    }

    /** Switches back to the default content. */
    public static void switchToDefaultContent() {
        resolveActiveDriver().switchTo().defaultContent();
        log.info("Switched to default content.");
    }

}


