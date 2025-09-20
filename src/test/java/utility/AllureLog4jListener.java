package utility;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

/**
 * AllureLog4jListener is a TestNG listener class that enhances test reporting by integrating
 * screenshots and log file attachments into the Allure report after each test method execution.
 * <p>
 * Key Responsibilities:
 * <ul>
 *   <li>Captures a screenshot if a test fails and attaches it to the Allure report.</li>
 *   <li>Attaches application logs and filtered warn/error logs to the Allure report.</li>
 *   <li>Provides post-invocation logic using {@link IInvokedMethodListener}.</li>
 * </ul>
 *
 * <p>Dependencies:
 * <ul>
 *   <li>Selenium WebDriver for screenshots (via {@link Screenshot}).</li>
 *   <li>Log4j for logging execution details.</li>
 *   <li>Allure for reporting enhancements.</li>
 * </ul>
 *
 * <p>Usage:
 * Register this listener in your <code>testng.xml</code> file or via annotations:
 *
 * <pre>{@code
 * <listeners>
 *     <listener class-name="utility.AllureLog4jListener"/>
 * </listeners>
 * }</pre>
 * Or using Java annotation:
 * <pre>{@code
 * @Listeners(utility.AllureLog4jListener.class)
 * }</pre>
 *
 * @author Hossam Atef
 * @version 1.0
 */
public class AllureLog4jListener implements IInvokedMethodListener {

    private static final Logger log = LogManager.getLogger(AllureLog4jListener.class);

    /**
     * Executes after each test method. It captures a screenshot and attaches relevant
     * log files to the Allure report for enhanced traceability.
     *
     * @param method     the invoked method
     * @param testResult the result of the test method
     */
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {                    //Check it's annotated with @Test not @BeforeTest or @AfterTest
            try {
                Screenshot.takeScreenShot(testResult);
            } catch (IOException e) {
                log.error("Failed to capture a screenshot");
                throw new RuntimeException(e);
            }

            try {
                attachLogsToAllure();
            } catch (Exception e) {
                log.error("Failed to attach logs to Allure");
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Attaches the application's main log file and filtered warn/error log file
     * to the Allure report, if they exist and are not empty.
     *
     * @throws FileNotFoundException if log files cannot be found
     */
    private void attachLogsToAllure() throws FileNotFoundException {
        File logFile = new File("logs/application.log"); // Path to your log file
        File warnErrorFile = new File("logs/warn_error_logs.log"); // Path to your log file

        if (logFile.exists() && logFile.length() > 0) {
            try {
                FileInputStream applicationStream = new FileInputStream(logFile);
                FileInputStream warnErrorStream = new FileInputStream(warnErrorFile);
                // Attach the log file to the Allure report
                log.info("Logs attached to the allure report 'Application Logs' & 'WarnError Logs'.");
                Allure.addAttachment("Application Logs", "text/plain", applicationStream, "log");
                Allure.addAttachment("WarnError Logs", "text/plain", warnErrorStream, "log");
            } catch (IOException e) {
                log.error("Error attaching log to Allure report: ");
                throw e;
            }
        }
    }
}

