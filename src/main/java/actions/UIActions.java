package actions;


import io.appium.java_client.AppiumBy;
import mobile.android.AndroidActions;
import mobileDriverFactory.GetMobileDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import webdriverfactory.GetWebDriver;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * UIActions provides a centralized abstraction layer for interacting with web UI elements merging logging and wait for all interactions
 * using Selenium WebDriver.<br>It encapsulates actions such as clicking, sending keys, retrieving
 * element properties, interacting with dropdowns, handling JavaScript execution, and performing
 * complex mouse and keyboard operations.
 *
 * <p>
 * This utility class improves code reusability, readability, and consistency across test scripts.
 * </p>
 *
 * <p>Usage example:</p>
 * <pre>
 *     UIActions uiActions = new UIActions();
 *     uiActions.click(LocatorType.id, "submitBtn", ExplicitWaitCondition.elementToBeClickable);
 * </pre>
 *
 * @author Hossam Atef
 * @version 1.0
 */
public class UIActions {

    private static final Logger log = LogManager.getLogger(lookup().lookupClass());
    private final WebDriverWait wait;
    private final Actions actions;
    private final JavascriptExecutor js;
    private final WebDriver driver;


    /**
     * Initializes WebDriver, WebDriverWait, Actions, and JavascriptExecutor instances.
     *
     * @param waitDuration related to that element to be used.
     */
    public UIActions(int waitDuration, platform WebOrMobile) {
        log.info("Initializing UIActions object.");
        checkNullDriver();
        if (WebOrMobile.equals(platform.web)) {
            driver = GetWebDriver.getLocalDriver();
        } else {
            driver = GetMobileDriver.getLocalDriver();
        }
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(waitDuration));
        this.actions = new Actions(driver);
        js = (JavascriptExecutor) driver;
        log.info("UIActions initialized with wait duration: '{}' seconds", waitDuration);
    }

    /**
     * Verifies that the WebDriver instance has been properly initialized.
     * <p>
     * <br>If the WebDriver is {@code null},
     * it logs an error message and throws an {@link IllegalStateException}
     * to prevent further execution.
     * </p>
     *
     * @throws IllegalStateException if the WebDriver has not been initialized
     */
    private static void checkNullDriver() {
        if (GetWebDriver.getLocalDriver() == null && GetMobileDriver.getLocalDriver() == null) {
            log.error("WebDriver instance is null. WebDriver must be initialized before using UI actions.");
            throw new IllegalStateException("WebDriver has not been initialized.");
        }
    }

    //________________________________________________________________________________________________________________//
    //Enumeration

    /**
     * Defines explicit wait conditions for locating elements.
     */
    public enum ExplicitWaitCondition {
        elementToBeClickable,
        presenceOfElement,
        visibilityOfElement,
        none
    }

    /**
     * Supported locator types for finding elements on Web and Mobile.
     */
    public enum LocatorType {
        // Web locators
        xPath,
        id,
        className,
        name,
        css,
        tagName,
        linkText,
        partialLinkText,

        // Mobile locators (Appium)
        accessibilityId,
        androidUIAutomator,
        androidViewTag,
        iosClassChain,
        iosNsPredicate
    }


    /**
     * Defines selection methods for dropdowns.
     */
    public enum SelectBy {
        value,
        text,
        index
    }

    /**
     * Defines platform.
     */
    public enum platform {
        web,
        mobile
    }

    //________________________________________________________________________________________________________________//
    // Elements & Locators

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

    public WebElement tryFindElement(LocatorType locator, String selector, ExplicitWaitCondition condition) {
        try {
            return switch (condition) {
                case presenceOfElement ->
                        wait.until(ExpectedConditions.presenceOfElementLocated(findLocator(locator, selector)));
                case elementToBeClickable ->
                        wait.until(ExpectedConditions.elementToBeClickable(findLocator(locator, selector)));
                case visibilityOfElement ->
                        wait.until(ExpectedConditions.visibilityOfElementLocated(findLocator(locator, selector)));
                case none -> driver.findElement(findLocator(locator, selector));

            };
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Constructs a {@link By} locator using the specified type and selector.
     *
     * @param locator  The locator strategy.
     * @param selector The selector value.
     * @return A By object representing the locator.
     */
    private By findLocator(LocatorType locator, String selector) {
        return switch (locator) {
            case id -> By.id(selector);
            case name -> By.name(selector);
            case className -> By.className(selector);
            case tagName -> By.tagName(selector);
            case linkText -> By.linkText(selector);
            case partialLinkText -> By.partialLinkText(selector);
            case css -> By.cssSelector(selector);
            case xPath -> By.xpath(selector);

            case accessibilityId -> AppiumBy.accessibilityId(selector);
            case androidUIAutomator -> AppiumBy.androidUIAutomator(selector);
            case androidViewTag -> AppiumBy.androidViewTag(selector);
            case iosClassChain -> AppiumBy.iOSClassChain(selector);
            case iosNsPredicate -> AppiumBy.iOSNsPredicateString(selector);
        };
    }

    /**
     * Finds a webElement using the specified locator and explicit wait condition.
     *
     * @param locator   The type of locator (e.g., ID, CSS, XPATH).
     * @param selector  The locator string used to find the element.
     * @param condition The explicit wait condition to apply.
     * @return The found WebElement after waiting for the condition, or null if unsupported condition.
     */
    public WebElement findWebElement(LocatorType locator, String selector, ExplicitWaitCondition condition) {
        try {
            WebElement element = switch (condition) {
                case presenceOfElement ->
                        wait.until(ExpectedConditions.presenceOfElementLocated(findLocator(locator, selector)));
                case elementToBeClickable ->
                        wait.until(ExpectedConditions.elementToBeClickable(findLocator(locator, selector)));
                case visibilityOfElement ->
                        wait.until(ExpectedConditions.visibilityOfElementLocated(findLocator(locator, selector)));
                case none -> driver.findElement(findLocator(locator, selector));
            };
            log.info("Successfully found element located by [{}: '{}'] with wait condition '{}'.", locator, selector, condition);
            return element;
        } catch (Exception e) {
            log.error("Unable to find element located by [{}: '{}'] with wait condition '{}'.", locator, selector, condition);
            throw e;

        }
    }


    /**
     * Finds and returns a list of elements matching the locator and explicit wait condition.
     *
     * @param locator   The type of locator (e.g., ID, CSS, XPATH).
     * @param selector  The locator string used to find the element.
     * @param condition The explicit wait condition to apply.
     * @return List of located WebElements.
     */

    public List<WebElement> findWebElements(LocatorType locator, String selector, ExplicitWaitCondition condition) {
        try {
            By by = findLocator(locator, selector);
            List<WebElement> elements = switch (condition) {
                case presenceOfElement -> wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
                case visibilityOfElement -> wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
                default ->
                        throw new UnsupportedOperationException("Unsupported wait condition for multiple elements: " + condition);
            };
            log.info("Successfully found '{}' elements located by [{}: '{}'] with condition '{}'.", elements.size(), locator, selector, condition);
            return elements;
        } catch (TimeoutException e) {
            log.warn("Timeout while waiting for elements located by [{}: '{}'] with condition '{}', Returning an empty list.", locator, selector, condition);
            return List.of();
        } catch (Exception e) {
            log.error("Error locating elements located by [{}: '{}'] with condition '{}'.", locator, selector, condition);
            throw e;
        }
    }

    /**
     * Finds and returns a list of elements within a given WebElement context,
     * using the specified locator type and selector string.
     *
     * @param parent   The parent WebElement to search within.
     * @param locator  The type of locator (e.g., ID, CSS, XPATH).
     * @param selector The locator string used to find the elements.
     * @return List of located WebElements within the context.
     */
    public List<WebElement> findWebElementsInParent(WebElement parent, LocatorType locator, String selector) {
        try {
            By by = findLocator(locator, selector);
            List<WebElement> elements = parent.findElements(by);
            log.info("Found '{}' elements inside parent element [{}] using [{}: '{}'].", elements.size(), describeElement(parent), locator, selector);
            return elements;
        } catch (Exception e) {
            log.error("Error locating elements inside parent element [{}] using [{}: '{}'].", describeElement(parent), locator, selector);
            throw e;
        }
    }


    //________________________________________________________________________________________________________________//
    //Element Interactions

    /**
     * Clicks an element identified by locator and wait condition.
     */
    public void click(LocatorType locator, String selector, ExplicitWaitCondition condition) {
        findWebElement(locator, selector, condition).click();
        log.info("Clicked on element located by [{}: '{}'].", locator, selector);
    }

    /**
     * Clicks a given WebElement.
     */
    public void click(WebElement element) {
        element.click();
        log.info("Clicked on WebElement: '{}'.", describeElement(element));
    }

    /**
     * Clears text from an element identified by locator and wait condition.
     */
    public void clearText(LocatorType locator, String selector, ExplicitWaitCondition condition) {
        findWebElement(locator, selector, condition).clear();
        log.info("Cleared text from element located by [{}: '{}'].", locator, selector);
    }

    /**
     * Clears text from a given WebElement.
     */
    public void clearText(WebElement element) {
        element.clear();
        log.info("Cleared text from WebElement: '{}'.", describeElement(element));
    }

    /**
     * Sends keys to an element identified by locator and wait condition.
     */
    public void sendKeys(LocatorType locator, String selector, ExplicitWaitCondition condition, String text) {
        findWebElement(locator, selector, condition).sendKeys(text);
        log.info("Sent text '{}' to element located by [{}: '{}'].", text, locator, selector);

    }

    /**
     * Sends keys to a given WebElement.
     */
    public void sendKeys(WebElement element, String text) {
        element.sendKeys(text);
        log.info("Sent text '{}' to WebElement: '{}'.", text, describeElement(element));

    }

    /**
     * Gets text from an element identified by locator and wait condition.
     */
    public String getElementText(LocatorType locator, String selector, ExplicitWaitCondition condition) {
        String text = findWebElement(locator, selector, condition).getText();
        log.info("Retrieved text from element located by [{}: '{}']: '{}'.", locator, selector, text);
        return text;
    }

    /**
     * Gets text from a given WebElement.
     */
    public String getElementText(WebElement element) {
        String text = element.getText();
        log.info("Retrieved text from WebElement: '{}'.", text);
        return text;
    }

    /**
     * Checks if an element is displayed.
     */
    public boolean isElementDisplayed(LocatorType locator, String selector, ExplicitWaitCondition condition) {
        boolean displayed = findWebElement(locator, selector, condition).isDisplayed();
        log.info("Element located by [{}: '{}'] is displayed: '{}'.", locator, selector, displayed);
        return displayed;
    }

    /**
     * Checks if a WebElement is displayed.
     */
    public boolean isElementDisplayed(WebElement element) {
        boolean displayed = element.isDisplayed();
        log.info("WebElement '{}' is displayed: '{}'.", describeElement(element), displayed);
        return displayed;
    }

    /**
     * Checks if an element is enabled.
     */
    public boolean isElementEnabled(LocatorType locator, String selector, ExplicitWaitCondition condition) {
        boolean enabled = findWebElement(locator, selector, condition).isEnabled();
        log.info("Element located by [{}: '{}'] is enabled: '{}'.", locator, selector, enabled);
        return enabled;
    }

    /**
     * Checks if a WebElement is enabled.
     */
    public boolean isElementEnabled(WebElement element) {
        boolean enabled = element.isEnabled();
        log.info("WebElement '{}' is enabled: '{}'.", describeElement(element), enabled);
        return enabled;
    }

    /**
     * Checks if an element is selected.
     */
    public boolean isElementSelected(LocatorType locator, String selector, ExplicitWaitCondition condition) {
        boolean selected = findWebElement(locator, selector, condition).isSelected();
        log.info("Element located by [{}: '{}'] is selected: '{}'.", locator, selector, selected);
        return selected;
    }

    /**
     * Checks if a WebElement is selected.
     */
    public boolean isElementSelected(WebElement element) {
        boolean selected = element.isSelected();
        log.info("WebElement '{}' is enabled: '{}'.", describeElement(element), selected);
        return selected;
    }

    /**
     * Gets an attribute value from an element.
     */
    public String getElementAttribute(LocatorType locator, String selector, ExplicitWaitCondition condition, String attributeName) {
        String value = findWebElement(locator, selector, condition).getDomAttribute(attributeName);
        log.info("Retrieved attribute '{}' from element located by [{}: '{}'] is '{}'.", attributeName, locator, selector, value);
        return value;
    }

    /**
     * Gets an attribute value from a WebElement.
     */
    public String getElementAttribute(WebElement element, String attributeName) {
        String value = element.getDomAttribute(attributeName);
        log.info("Retrieved attribute '{}' from WebElement '{}' is '{}'.", attributeName, describeElement(element), value);
        return value;
    }

    /**
     * Gets the tag name of an element.
     */
    public String getElementTagName(LocatorType locator, String selector, ExplicitWaitCondition condition) {
        String tagName = findWebElement(locator, selector, condition).getTagName();
        log.info("Retrieved tag name from element located by [{}: '{}'] is '{}'.", locator, selector, tagName);
        return tagName;
    }

    /**
     * Gets the tag name of a WebElement.
     */
    public String getElementTagName(WebElement element) {
        String tagName = element.getTagName();
        log.info("Retrieved tag name from WebElement '{}' is '{}'.", describeElement(element), tagName);
        return tagName;
    }

    /**
     * Executes JavaScript code on an element.
     */
    public void executeJavaScriptCode(LocatorType locator, String selector, ExplicitWaitCondition condition, String javaScript) {
        js.executeScript(javaScript, findWebElement(locator, selector, condition));
        log.info("Executed JavaScript on element located by [{}: '{}'] with script: '{}'.", locator, selector, javaScript);
    }

    /**
     * Executes JavaScript code on a WebElement.
     */
    public void executeJavaScriptCode(WebElement element, String javaScript) {
        js.executeScript(javaScript, element);
        log.info("Executed JavaScript on WebElement '{}' with script: '{}'", describeElement(element), javaScript);
    }

    /**
     * Waits until the specified attribute of a WebElement contains the expected value.
     * <p>
     *
     * @param element       The WebElement whose attribute you want to check.
     * @param attributeName The name of the attribute to monitor.
     * @param expectedValue The substring value expected in the attribute.
     * @param timeoutSec    Maximum number of seconds to wait.
     * @throws TimeoutException If the attribute does not contain the expected value within the timeout.
     */
    public void waitForAttributeContainsValueOrFail(WebElement element, String attributeName, String expectedValue, int timeoutSec) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));

            customWait.until((ExpectedCondition<Boolean>)
                    driver -> {
                        String attrValue = getElementAttribute(element, attributeName);
                        return attrValue != null && attrValue.contains(expectedValue);
                    });

            log.info("Attribute '{}' of element '{}' contains '{}'.", attributeName, describeElement(element), expectedValue);

        } catch (Exception e) {
            log.error("Attribute '{}' of element '{}' doesn't contain '{}'.", attributeName, describeElement(element), expectedValue);
            throw e;
        }
    }

    /**
     * Waits until the specified WebElement is visible, enabled, and not readonly.
     * <p>
     * This method uses WebDriverWait to repeatedly check whether the element:
     * <ul>
     *     <li>Is displayed on the page</li>
     *     <li>Is enabled for interaction</li>
     *     <li>Does not have a readonly attribute set to "true"</li>
     * </ul>
     * The wait will continue until all conditions are met or the timeout is reached.
     *
     * @param element    The WebElement to check for interactability.
     * @param timeoutSec Maximum number of seconds to wait before failing.
     * @throws TimeoutException If the element is not interactable within the timeout period.
     */
    public void waitUntilElementIsVisibleAndInteractableOrFail(WebElement element, int timeoutSec) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));

            customWait.until((ExpectedCondition<Boolean>) driver -> {
                boolean isVisible = isElementDisplayed(element);
                boolean isEnabled = isElementEnabled(element);
                String readonlyAttr = getElementAttribute(element, "readonly");
                boolean isNotReadonly = readonlyAttr == null || readonlyAttr.isEmpty() || !"true".equalsIgnoreCase(readonlyAttr);

                return isVisible && isEnabled && isNotReadonly;
            });

            log.info("Element '{}' is visible, enabled, and not readonly.", describeElement(element));

        } catch (Exception e) {
            log.error("Element '{}' is not interactable (visible, enabled, and not readonly) within {} seconds.",
                    describeElement(element), timeoutSec);
            throw e;
        }
    }

    public void waitUntilInvisibilityOfElement(WebElement element, int timeoutSec) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));
            customWait.until(ExpectedConditions.invisibilityOf(element));
            log.info("Element '{}' is now invisible.", describeElement(element));
        } catch (Exception e) {
            log.error("An error occurred while waiting for element '{}' to become invisible", describeElement(element));
            throw e;
        }
    }

    /**
     * Waits until the specified WebElement contains non-empty text within the given timeout.
     *
     * <p>This method uses a custom WebDriverWait to poll the element until its text is not null,
     * not empty, and not just whitespace. Useful for verifying dynamic content loading.</p>
     *
     * @param element    The WebElement to monitor for text.
     * @param timeoutSec The maximum time to wait in seconds before timing out.
     * @throws Exception if the element does not contain text within the timeout.
     */
    public void waitUntilElementHaveText(WebElement element, int timeoutSec) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));

            customWait.until((ExpectedCondition<Boolean>) driver -> {
                String text = getElementText(element);
                return text != null && !text.trim().isEmpty();
            });

            log.info("Text '{}' is shown for '{}' element.", element.getText(), describeElement(element));

        } catch (Exception e) {
            log.error("Text is not shown for '{}' element within '{}' seconds.",
                    describeElement(element), timeoutSec);
            throw e;
        }
    }

    /**
     * Waits until at least one file appears in the specified download directory within the given timeout.
     *
     * <p>This method is useful for verifying that a file download has completed by checking the presence
     * of files in the target directory. It uses WebDriverWait to poll the directory contents.</p>
     *
     * @param downloadDirPath The absolute path to the download directory.
     * @param timeoutSec      The maximum time to wait in seconds before timing out.
     * @throws Exception if no files are found in the directory within the timeout.
     */
    public void waitUntilFilesAreDownloaded(String downloadDirPath, int timeoutSec) {
        File downloadDir = new File(downloadDirPath);

        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));

            customWait.until((ExpectedCondition<Boolean>) driver -> {
                File[] files = downloadDir.listFiles();
                return files != null && files.length > 0;
            });

            log.info("Files have successfully downloaded to '{}'.", downloadDirPath);

        } catch (Exception e) {
            log.error("Files haven't successfully downloaded within '{}' seconds.", timeoutSec);
            throw e;
        }
    }

    /**
     * Waits until exactly two PDF files appear in the specified download directory within the given timeout.
     *
     * <p>This method is useful for verifying that a file download has completed by checking for the presence
     * of two files with a ".pdf" extension in the target directory. It uses WebDriverWait to poll the directory contents.</p>
     *
     * @param downloadDirPath The absolute path to the download directory.
     * @param timeoutSec      The maximum time to wait in seconds before timing out.
     * @throws Exception if two PDF files are not found in the directory within the timeout.
     */
    public void waitUntilTwoPdfFilesAreDownloaded(String downloadDirPath, int timeoutSec) {
        File downloadDir = new File(downloadDirPath);

        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));

            customWait.until((ExpectedCondition<Boolean>) driver -> {

                File[] pdfFiles = downloadDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
                return pdfFiles != null && pdfFiles.length == 2;
            });

            log.info("Two PDF files have successfully downloaded to '{}'.", downloadDirPath);

        } catch (Exception e) {
            log.error("Two PDF files haven't successfully downloaded within '{}' seconds.", timeoutSec);
            throw e;
        }
    }


    //________________________________________________________________________________________________________________//
    //Dropdown & Select Operations:

    /**
     * Finds a dropdown element using the specified locator and wait condition,
     * and returns it as a Select object for interaction.
     *
     * @param locator   The type of locator (e.g., ID, CSS, XPATH).
     * @param selector  The locator value to find the element.
     * @param condition The explicit wait condition to apply.
     * @return A Select object representing the dropdown element.
     */
    public Select findDropDownElement(LocatorType locator, String selector, ExplicitWaitCondition condition) {
        Select select = new Select(findWebElement(locator, selector, condition));
        log.info("Created Select object for dropdown element located by [{}: '{}'] with wait condition '{}'.", locator, selector, condition);
        return select;
    }

    /**
     * Deselects an option from the given dropdown element using the specified selection method.
     *
     * @param dropdownElement The Select object representing the dropdown.
     * @param method          The method to identify the option (text, value, or index).
     * @param option          The value to be deselected (text/value/index as a string).
     */
    public void deselectDropDownOption(Select dropdownElement, SelectBy method, String option) {
        switch (method) {
            case text:
                dropdownElement.deselectByVisibleText(option);
                log.info("Deselected dropdown option with visible text: '{}'.", option);
                break;
            case value:
                dropdownElement.deselectByValue(option);
                log.info("Deselected dropdown option with value: '{}'.", option);
                break;
            case index:
                dropdownElement.deselectByIndex(Integer.parseInt(option));
                log.info("Deselected dropdown option with index: '{}'.", option);
                break;
        }
    }

    /**
     * Selects an option from the given dropdown element using the specified selection method.
     *
     * @param dropdownElement The Select object representing the dropdown.
     * @param method          The method to identify the option (text, value, or index).
     * @param option          The value to be selected (text/value/index as a string).
     */
    public void selectDropDownOption(Select dropdownElement, SelectBy method, String option) {
        switch (method) {
            case text:
                dropdownElement.selectByVisibleText(option);
                log.info("Selected dropdown option with visible text: '{}'.", option);
                break;
            case value:
                dropdownElement.selectByValue(option);
                log.info("Selected dropdown option with value: '{}'.", option);
                break;
            case index:
                dropdownElement.selectByIndex(Integer.parseInt(option));
                log.info("Selected dropdown option with index: '{}'.", option);
                break;
        }
    }

    /**
     * Retrieves all selected options from the given dropdown element.
     *
     * @param dropdownElement The Select object representing the dropdown.
     * @return A list of strings containing the visible text of all selected options.
     */
    public List<String> getAllSelectedDropDownOptions(Select dropdownElement) {
        List<String> selectedOptions = dropdownElement.getAllSelectedOptions().stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
        log.info("Retrieved selected dropdown options: '{}'.", selectedOptions);
        return selectedOptions;
    }

    /**
     * Retrieves all available options from the given dropdown element.
     *
     * @param dropdownElement The Select object representing the dropdown.
     * @return A list of strings containing the visible text of all available options.
     */
    public List<String> getAllDropDownOptions(Select dropdownElement) {
        List<String> options = dropdownElement.getOptions()
                .stream().map(WebElement::getText)
                .collect(Collectors.toList());
        log.info("Retrieved all dropdown options: '{}'.", options);
        return options;
    }

    //________________________________________________________________________________________________________________//
    //Mouse & Keyboard Actions

    /**
     * Performs a right-click (context click) on the specified web element.
     *
     * @param locator   The type of locator.
     * @param selector  The selector value.
     * @param condition The wait condition to apply before locating the element.
     */
    public void rightClick(LocatorType locator, String selector, ExplicitWaitCondition condition) {
        actions.contextClick(findWebElement(locator, selector, condition)).perform();
        log.info("Performed right-click on provided element located by '[{}: '{}']'.", locator, selector);
    }

    /**
     * Performs a right-click (context click) on the provided WebElement.
     *
     * @param element The WebElement to right-click on.
     */
    public void rightClick(WebElement element) {
        actions.contextClick(element).perform();
        log.info("Performed right-click on provided WebElement: '{}'.", describeElement(element));
    }

    /**
     * Performs a double-click on the specified web element.
     *
     * @param locator   The type of locator.
     * @param selector  The selector value.
     * @param condition The wait condition to apply before locating the element.
     */
    public void doubleClick(LocatorType locator, String selector, ExplicitWaitCondition condition) {
        actions.doubleClick(findWebElement(locator, selector, condition)).perform();
        log.info("Performed double-click on provided element located by '[{}: '{}']'.", locator, selector);
    }

    /**
     * Performs a double-click on the provided WebElement.
     *
     * @param element The WebElement to double-click on.
     */
    public void doubleClick(WebElement element) {
        actions.doubleClick(element).perform();
        log.info("Performed double-click on provided WebElement: '{}'.", describeElement(element));
    }

    /**
     * Performs a click-and-hold on the specified web element for a specified duration.
     *
     * @param locator       The type of locator.
     * @param selector      The selector value.
     * @param condition     The wait condition to apply before locating the element.
     * @param pauseDuration Duration to hold the click in seconds.
     */
    public void clickAndHold(LocatorType locator, String selector, ExplicitWaitCondition condition, int pauseDuration) {
        actions.clickAndHold(findWebElement(locator, selector, condition)).pause(Duration.ofSeconds(pauseDuration)).release().perform();
        log.info("Performed click-and-hold on provided element located by '[{}: '{}']' for '{}' seconds.", locator, selector, pauseDuration);
    }

    /**
     * Performs a click-and-hold on the given WebElement for a specified duration.
     *
     * @param element       The WebElement to click and hold.
     * @param pauseDuration Duration to hold the click in seconds.
     */
    public void clickAndHold(WebElement element, int pauseDuration) {
        actions.clickAndHold(element).pause(Duration.ofSeconds(pauseDuration)).release().perform();
        log.info("Performed click-and-hold on provided WebElement: '{}' for '{}' seconds.", describeElement(element), pauseDuration);
    }

    /**
     * Moves the cursor to the specified web element.
     *
     * @param locator   The type of locator.
     * @param selector  The selector value.
     * @param condition The wait condition to apply before locating the element.
     */
    public void moveToElement(LocatorType locator, String selector, ExplicitWaitCondition condition) {
        actions.moveToElement(findWebElement(locator, selector, condition)).perform();
        log.info("Moved to provided element: '[{}: '{}']'.", locator, selector);
    }

    /**
     * Moves the cursor to the given WebElement.
     *
     * @param element The WebElement to move to.
     */
    public void moveToElement(WebElement element) {
        actions.moveToElement(element).perform();
        log.info("Moved to provided WebElement: '{}'.", describeElement(element));
    }

    /**
     * Performs a drag-and-drop operation from the source to destination element.
     *
     * @param srcLocator   The locator type of the source element.
     * @param srcSelector  The selector of the source element.
     * @param destLocator  The locator type of the destination element.
     * @param destSelector The selector of the destination element.
     * @param condition    The wait condition to apply before locating elements.
     */
    public void dragAndDrop(LocatorType srcLocator, String srcSelector,
                            LocatorType destLocator, String destSelector,
                            ExplicitWaitCondition condition) {
        WebElement srcElement = findWebElement(srcLocator, srcSelector, condition);
        WebElement destElement = findWebElement(destLocator, destSelector, condition);
        actions.dragAndDrop(srcElement, destElement).perform();

        log.info("Performed drag-and-drop from source element: '[{}: '{}']' to destination element: '[{}: '{}']'.",
                srcLocator, srcSelector, destLocator, destSelector);
    }

    /**
     * Performs a drag-and-drop operation from one WebElement to another.
     *
     * @param srcElement  The source WebElement.
     * @param destElement The destination WebElement.
     */
    public void dragAndDrop(WebElement srcElement, WebElement destElement) {
        actions.dragAndDrop(srcElement, destElement).perform();
        log.info("Performed drag-and-drop from source WebElement: '{}' to destination WebElement: '{}'.",
                describeElement(srcElement), describeElement(destElement));
    }


    /**
     * Sends a key combination using multiple modifier keys followed by one or more characters.
     * <p>Example usage: ALT + SHIFT + A</p>
     *
     * @param modifiers A list of modifier keys to hold down (e.g., ALT, SHIFT).
     * @param keys      One or more characters to send with the modifier keys.
     */
    public void enterKeyCombination(List<Keys> modifiers, String... keys) {
        for (Keys mod : modifiers) {
            actions.keyDown(mod);
        }

        actions.sendKeys(keys);

        for (int i = modifiers.size() - 1; i >= 0; i--) {
            actions.keyUp(modifiers.get(i));
        }

        actions.perform();

        log.info("Performed key combination: '{}' + '{}'",
                modifiers.stream().map(Keys::name).collect(Collectors.joining(" + ")),
                String.join("", keys));
    }

    /**
     * Sends a key combination using a single modifier key followed by one or more characters.
     * <p>Example usage: CONTROL + A</p>
     *
     * @param modifier A single modifier key to hold down (e.g., CONTROL, ALT).
     * @param keys     One or more characters to send with the modifier key.
     */
    public void enterKeyCombination(Keys modifier, String... keys) {
        actions.keyDown(modifier)
                .sendKeys(keys)
                .keyUp(modifier)
                .perform();

        log.info("Performed key combination: '{}' + '{}'.", modifier.name(), keys);
    }


    //________________________________________________________________________________________________________________//
    //Tables

    /**
     * Extracts and returns the text content of all cells within a table structure.
     * The method locates the table using the provided locator, then iterates through
     * each row and cell to collect their textual values.
     *
     * @param tableLocator  The locator type used to identify the table element (e.g., ID, CLASS_NAME, XPATH).
     * @param tableSelector The selector string corresponding to the table locator.
     * @param rowLocator    The locator type used to identify row elements within the table.
     * @param rowSelector   The selector string corresponding to the row locator.
     * @param cellLocator   The locator type used to identify cell elements within each row.
     * @param cellSelector  The selector string corresponding to the cell locator.
     * @return A list of strings containing the text of each cell found in the table.
     */
    public List<String> getTableCellValues(LocatorType tableLocator, String tableSelector,
                                           LocatorType rowLocator, String rowSelector,
                                           LocatorType cellLocator, String cellSelector) {
        WebElement table = findWebElement(tableLocator, tableSelector, ExplicitWaitCondition.visibilityOfElement);
        List<WebElement> rows = findWebElementsInParent(table, rowLocator, rowSelector);

        List<String> cellsValues = new ArrayList<>();
        for (WebElement row : rows) {
            List<WebElement> cells = findWebElementsInParent(row, cellLocator, cellSelector);
            for (WebElement cell : cells) {
                cellsValues.add(cell.getText());
            }
        }
        return cellsValues;
    }


}


