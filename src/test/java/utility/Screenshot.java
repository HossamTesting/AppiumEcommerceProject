package utility;

import io.qameta.allure.Allure;
import mobileDriverFactory.GetMobileDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import webdriverfactory.GetWebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Screenshot is a utility class designed to capture and manage browser screenshots
 * during automated test execution using Selenium WebDriver.
 *
 * <p>Key Features:
 * <ul>
 *   <li>Captures screenshots for failed test cases automatically.</li>
 *   <li>Saves screenshots with timestamped filenames in a structured folder hierarchy.</li>
 *   <li>Attaches captured screenshots to the Allure test report for enhanced reporting.</li>
 *   <li>Integrated logging using Log4j for visibility and debugging.</li>
 * </ul>
 *
 * <p>Screenshots are saved under the <code>screenshots/FailedTests_yyyy-MM-dd</code> directory.
 * Files are named using the test method name and timestamp to prevent overwriting.
 *
 * <p>Example usage (typically invoked from a TestNG listener):
 * <pre>{@code
 *   Screenshot.takeScreenShot(testResult);
 * }</pre>
 *
 * <p>If the WebDriver is not initialized at the time of capture, the screenshot will be skipped
 * with a warning logged.
 *
 * @author Hossam Atef
 * @version 1.0
 */
public class Screenshot {

    private static final Logger log = LogManager.getLogger(lookup().lookupClass());

    /**
     * Captures a screenshot of the current browser state.
     * <br>The screenshot is saved under the "screenshots/FailedTests_<date>/" directory,
     * with a filename based on the test method name and current timestamp.
     *
     * @param fileName The base name to use for the screenshot file (typically the test method name).
     * @return The {@link File} object pointing to the saved screenshot, or {@code null} if the WebDriver is not initialized.
     * @throws IOException If there is an error during screenshot capture or saving to disk.
     */
    private static File takeShot(String fileName) throws IOException {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        try {
            File dir = new File("screenshots/FailedTests_" + timestamp.substring(0, 10));
            if (!dir.exists() && dir.mkdirs()) {
                log.info("Screenshot directory created at: {}", dir.getAbsolutePath());
            }
            String fullPath = dir.getPath() + "/" + fileName + "_" + timestamp + ".png";
            TakesScreenshot screenshot = null;

            WebDriver webDriver = GetWebDriver.getLocalDriver();
            if (webDriver instanceof TakesScreenshot) {
                screenshot = (TakesScreenshot) webDriver;
            }
            else {
                WebDriver mobileDriver = GetMobileDriver.getLocalDriver();
                if (mobileDriver != null) {
                    screenshot = (TakesScreenshot) mobileDriver;
                }
            }
            if (screenshot == null) {
                log.warn("The driver isn't initiated, screenshot cannot be taken.");
                return null;
            }
            File srcFile = screenshot.getScreenshotAs(OutputType.FILE);
            File destFile = new File(fullPath);
            FileHandler.copy(srcFile, destFile);
            log.info("Screenshot captured and saved to: {}", destFile.getAbsolutePath());
            log.warn("Screenshot captured and saved as: {}", destFile.getName());
            return destFile;

        } catch (IOException e) {
            log.error("Failed to save screenshot in the directory '{}'.", "FailedTests");
            throw e;
        }
    }

    /**
     * Captures a screenshot and attaches it to the Allure report if the test failed.
     *
     * <p>This method should be called after each test method execution, typically from a TestNG listener.
     * Screenshots are only taken for failed tests. The screenshot will be saved locally and attached to the Allure report.</p>
     *
     * @param testCaseResult The {@link ITestResult} representing the outcome of the executed test method.
     * @throws IOException If an error occurs while capturing or saving the screenshot.
     */
    static void takeScreenShot(ITestResult testCaseResult) throws IOException {
        if (testCaseResult.getStatus() == ITestResult.FAILURE) {
            log.warn("Test case '{}' failed. Taking screenshot...", testCaseResult.getName());
            try {
                File screenShot = Screenshot.takeShot(testCaseResult.getName());
                if (screenShot == null) return;
                Allure.attachment(screenShot.getName(), FileUtils.openInputStream(screenShot));
                log.info("Screenshot had been added to the Allure report '{}'.", screenShot.getName());
            } catch (Exception e) {
                log.error("An error occurred while taking a screenshot.");
                throw (e);
            }
        } else {
            log.info("Test case '{}' passed. No screenshot will be captured.", testCaseResult.getName());
        }
    }
}