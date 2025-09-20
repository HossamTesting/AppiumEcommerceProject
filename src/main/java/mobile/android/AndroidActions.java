package mobile.android;

import actions.UIActions;
import com.google.common.collect.ImmutableMap;
import io.appium.java_client.android.AndroidDriver;
import mobileDriverFactory.GetMobileDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.ArrayList;
import java.util.List;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Provides high-level reusable Android actions for AppiumDriver.
 * <br>This class supports gestures, scrolling, context switching, and activity management.
 * <p>All actions are logged for easier debugging and reporting.</p>
 *
 * @author Hossam
 * @version 1.0
 */
public class AndroidActions {

    private static final Logger log = LogManager.getLogger(lookup().lookupClass());
    private final UIActions uiActions;
    private final AndroidDriver androidDriver;

    public AndroidActions(UIActions uiActions) {
        log.info("Initializing AndroidActions object.");
        this.uiActions = uiActions;
        this.androidDriver = (AndroidDriver) GetMobileDriver.getLocalDriver();
    }

    /**
     * Returns a simple string description of the WebElement.
     *
     * @param element the WebElement to describe
     * @return string representation of the element
     */
    private String describeElement(WebElement element) {
        try {
            return element.toString().replaceAll(".*-> ", "").replaceFirst("]", "");
        } catch (Exception e) {
            return "Unknown WebElement";
        }
    }

    // ====================================== Gestures ======================================

    /**
     * Performs a long click gesture on the specified element.
     *
     * @param webElement element to long-click
     * @param duration   duration in milliseconds
     */
    public void longClickGesture(WebElement webElement, int duration) {
        androidDriver.executeScript("mobile: longClickGesture", ImmutableMap.of(
                "elementId", ((RemoteWebElement) webElement).getId(),
                "duration", duration));
        log.info("Performed longClickGesture on element '{}' for '{}' ms.", describeElement(webElement), duration);
    }

    /**
     * Performs a long click gesture at a specific coordinate.
     *
     * @param x        horizontal coordinate
     * @param y        vertical coordinate
     * @param duration duration in milliseconds
     */
    public void longClickGesture(int x, int y, int duration) {
        androidDriver.executeScript("mobile: longClickGesture", ImmutableMap.of(
                "x", x,
                "y", y,
                "duration", duration));
        log.info("Performed longClickGesture at x:'{}', y:'{}' for '{}' ms.", x, y, duration);
    }

    /**
     * Performs a click gesture on the specified element.
     *
     * @param webElement element to click
     */
    public void clickGesture(WebElement webElement) {
        androidDriver.executeScript("mobile: clickGesture", ImmutableMap.of(
                "elementId", ((RemoteWebElement) webElement).getId()));
        log.info("Performed clickGesture on element '{}'.", describeElement(webElement));
    }

    /**
     * Performs a click gesture at a specific coordinate.
     *
     * @param x horizontal coordinate
     * @param y vertical coordinate
     */
    public void clickGesture(int x, int y) {
        androidDriver.executeScript("mobile: clickGesture", ImmutableMap.of(
                "x", x,
                "y", y));
        log.info("Performed clickGesture at x:'{}', y:'{}'.", x, y);
    }

    /**
     * Performs a drag gesture starting from an element to an end coordinate.
     *
     * @param webElement element to start drag
     * @param endX       end X coordinate
     * @param endY       end Y coordinate
     */
    public void dragGesture(WebElement webElement, int endX, int endY) {
        androidDriver.executeScript("mobile: dragGesture", ImmutableMap.of(
                "elementId", ((RemoteWebElement) webElement).getId(),
                "endX", endX,
                "endY", endY));
        log.info("Performed dragGesture on element '{}' to x: '{}', y: '{}'.", describeElement(webElement), endX, endY);
    }

    /**
     * Performs a drag gesture from a start coordinate to an end coordinate.
     *
     * @param startX start X coordinate
     * @param startY start Y coordinate
     * @param endX   end X coordinate
     * @param endY   end Y coordinate
     */
    public void dragGesture(int startX, int startY, int endX, int endY) {
        androidDriver.executeScript("mobile: dragGesture", ImmutableMap.of(
                "startX", startX,
                "startY", startY,
                "endX", endX,
                "endY", endY));
        log.info("Performed dragGesture from x:'{}', y:'{}' to x:'{}', y:'{}'.", startX, startY, endX, endY);
    }

    // ====================================== Scrolling ======================================


    /**
     * Scrolls until the element containing the text is present in the DOM.
     *
     * @param text text to scroll to
     */
    public void scrollToText(String text) {
        uiActions.findWebElement(UIActions.LocatorType.androidUIAutomator,
                "new UiScrollable(new UiSelector()).scrollIntoView(textContains(\"" + text + "\"))",
                UIActions.ExplicitWaitCondition.presenceOfElement);
        log.info("Scrolled to text '{}'.", text);
    }

    /**
     * Scrolls within a scrollable container until the element is visible.
     *
     * @param scrollableContainer scrollable WebElement
     * @param elementLocatorType  locator type of target element
     * @param elementSelector     locator string of target element
     * @param scrollingDirection  direction to scroll
     * @param percent             scroll distance percentage (0.0 to 1.0)
     * @return the WebElement if found
     */
    public WebElement scrollUntilElementVisible(WebElement scrollableContainer, UIActions.LocatorType elementLocatorType,
                                                String elementSelector, Direction scrollingDirection, double percent)
    {
        boolean canScrollMore;
        do {

            WebElement element = uiActions.tryFindElement(elementLocatorType, elementSelector, UIActions.ExplicitWaitCondition.none);
            if (element != null && uiActions.isElementDisplayed(element)) {
                log.info("Scrolled to element '{}'.", describeElement(element));
                return element;
            }
                Object canScrollMoreObj = androidDriver.executeScript("mobile: scrollGesture", ImmutableMap.of(
                        "elementId", ((RemoteWebElement) scrollableContainer).getId(),
                        "direction", scrollingDirection.toString(),
                        "percent", percent
                ));
                if (canScrollMoreObj == null) {
                    throw new RuntimeException("scrollGesture returned null.");
                }
                canScrollMore = (Boolean) canScrollMoreObj;

        }while (canScrollMore);

        WebElement element = uiActions.tryFindElement(elementLocatorType, elementSelector, UIActions.ExplicitWaitCondition.none);
        if (element != null && uiActions.isElementDisplayed(element)) {
            log.info("Scrolled to element '{}'.", describeElement(element));
            return element;
        } else {
            log.error("Element '{}' not found after scrolling.", elementSelector);
            throw new RuntimeException("Element not displayed after scroll.");
        }
    }

    // ====================================== Context & Activity ======================================

    /**
     * Returns the current Android activity.
     *
     * @return current activity name
     */
    public String getCurrentActivity() {
        log.info("Current activity: '{}'.", androidDriver.currentActivity());
        return androidDriver.currentActivity();
    }

    /**
     * Returns the current context handle.
     *
     * @return context handle string
     */
    public String getContextHandle() {
        String contextHandle = androidDriver.getContext();
        log.info("Current context handle: '{}'.", contextHandle);
        return contextHandle;
    }

    /**
     * Returns all available context handles.
     *
     * @return list of context handles
     */
    public List<String> getContextHandles() {
        List<String> contextHandles = new ArrayList<>(androidDriver.getContextHandles());
        log.info("Retrieved {} context handle(s): {}.", contextHandles.size(), contextHandles);
        return contextHandles;
    }

    /**
     * Switches to a specific context by handle.
     *
     * @param contextHandle context handle to switch to
     */
    public void switchToContext(String contextHandle) {
        androidDriver.context(contextHandle);
        log.info("Switched to context '{}'.", contextHandle);
    }

    // ====================================== Direction Enum ======================================

    public enum Direction {
        up, down, left, right
    }
}
