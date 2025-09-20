package utility;

import datadriven.ConfigLoader;
import io.qameta.allure.Allure;
import webdriverfactory.GetWebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * RetryAnalyzer is a TestNG retry mechanism that re-executes failed tests up to a configurable number of times.
 *
 * <p>Key Features:
 * <ul>
 *   <li>Reads the maximum retry count from a configuration file using {@link ConfigLoader}.</li>
 *   <li>Automatically retries failed tests up to the specified limit.</li>
 *   <li>Logs each retry attempt and final failure using Log4j.</li>
 *   <li>Attaches failure details to the Allure report on final failure.</li>
 *   <li>Ensures clean WebDriver state by quitting the driver before retrying.</li>
 * </ul>
 *
 * <p>Configuration:
 * <ul>
 *   <li>The maximum number of retries must be defined in the Config.properties file using the key <code>maxRetryCount</code>.</li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 * @Test(retryAnalyzer = utility.RetryAnalyzer.class)
 * public void testThatMayNeedRetries() {
 *     // test logic
 * }
 * }</pre>
 *
 * @author Hossam Atef
 * @version 1.0
 */

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private static final Logger log = LogManager.getLogger(lookup().lookupClass());

    /**
     * Implements retry logic for TestNG tests.
     *
     * <p>This method:
     * <ul>
     *   <li>Loads the retry limit from properties (only once per test instance).</li>
     *   <li>Logs retry attempts and cleanups between retries.</li>
     *   <li>Returns {@code true} to retry the test if within the retry limit.</li>
     *   <li>Attaches failure summary to Allure if retry limit is reached.</li>
     * </ul>
     *
     * @param result The result of the test execution from TestNG.
     * @return {@code true} if the test should be retried, {@code false} otherwise.
     */
    @Override
    public boolean retry(ITestResult result) {
        String maxRetryCountString = null;
        int maxRetryCount ;
        try {
            log.info("Checking the value of the 'maxRetryCount' parameter.");
            maxRetryCountString = new ConfigLoader("src/test/resources/Config.properties").getValue("maxRetryCount",true);
            maxRetryCount = Integer.parseInt(maxRetryCountString);
        } catch (NumberFormatException e) {
            log.error("The 'maxRetryCount' value provided isn't a valid integer: '{}'.", maxRetryCountString);
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("The 'maxRetryCount' parameter isn't exist in the config file.");
            throw new RuntimeException(e);
        }
        if (maxRetryCount <= 0) {
            log.warn("Test '{}' won't be retried. As 'maxRetryCount' provided value is: '{}', It should be greater than 0.",
                    result.getName(), maxRetryCount);
            return false;
        }
        log.info("Starting retry analyzer mechanism for test '{}' by '{}' attempts.", result.getName(), maxRetryCount);

        if (retryCount < maxRetryCount) {
            try {
                GetWebDriver.quitDriver();
            } catch (Exception e) {
                log.info("The driver instance had been already quit");
            }
            log.warn("Retrying test '{}' - attempt {}/{}", result.getName(), retryCount + 1, maxRetryCount);
            retryCount++;
            return true; // Retry the test
        } else {
            String finalFailureMessage = String.format("Test '%s' failed after '%d' retries.", result.getName(), maxRetryCount);
            log.error(finalFailureMessage);
            // Attach final failure info to Allure report
            Allure.addAttachment("Final Retry Failure", "text/plain", finalFailureMessage);
        }

        return false; // Do not retry if the max retry count is reached
    }
}


