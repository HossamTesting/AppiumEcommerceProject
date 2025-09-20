package utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import webdriverfactory.GetWebDriver;

import java.awt.*;
import java.awt.event.KeyEvent;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Utility class that provides system-level scrolling operations using {@link Robot} and
 * viewport-level scrolling using {@link JavascriptExecutor}.
 * <p>
 * This class is designed to offer simple programmatic scrolling support for UI test automation.
 * <br>Thread-safe for parallel execution by using a {@link ThreadLocal} Robot instance.
 * </p>
 *
 * @author Hossam Atef
 * @version 1.0
 */
public class Scrolling {

    private static final Logger log = LogManager.getLogger(lookup().lookupClass());

    /**
     * Thread-local Robot instance.
     */
    private static final ThreadLocal<Robot> robot = ThreadLocal.withInitial(() -> {
        try {
            return new Robot();
        } catch (AWTException e) {
            log.error("Failed to initialize Robot instance for thread {}", Thread.currentThread().getName());
            throw new RuntimeException("Failed to initialize Robot instance");
        }
    });

    /**
     * Scrolls to the bottom of the page.
     */
    public static void scrollBottom() {
        robot.get().keyPress(KeyEvent.VK_END);
        robot.get().keyRelease(KeyEvent.VK_END);
        log.info("Scrolled to the bottom of the page.");
    }

    /**
     * Scrolls to the top of the page.
     */
    public static void scrollTop() {
        robot.get().keyPress(KeyEvent.VK_HOME);
        robot.get().keyRelease(KeyEvent.VK_HOME);
        log.info("Scrolled to the top of the page.");
    }

    /**
     * Scrolls upward by one small unit (line or step).
     */
    public static void scrollUp() {
        robot.get().keyPress(KeyEvent.VK_PAGE_UP);
        robot.get().keyRelease(KeyEvent.VK_PAGE_UP);
        log.info("Scrolled a step up.");
    }

    /**
     * Scrolls downward by one small unit (line or step).
     */
    public static void scrollDown() {
        robot.get().keyPress(KeyEvent.VK_PAGE_DOWN);
        robot.get().keyRelease(KeyEvent.VK_PAGE_DOWN);
        log.info("Scrolled a step down.");
    }

    /**
     * Scrolls the viewport by the specified horizontal and vertical pixel offsets
     *
     * @param x the number of pixels to scroll horizontally (positive is right, negative is left)
     * @param y the number of pixels to scroll vertically (positive is down, negative is up)
     */
    public static void scrollByOffset(int x, int y) {
        JavascriptExecutor js = (JavascriptExecutor) GetWebDriver.getLocalDriver();
        js.executeScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
        log.info("Scrolled vertically & horizontally by x: '{}', y: '{}'.", x, y);
    }
}