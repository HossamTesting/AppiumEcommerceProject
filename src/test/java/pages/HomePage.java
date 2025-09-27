package pages;

import actions.UIActions;
import mobile.android.AndroidActions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import static java.lang.invoke.MethodHandles.lookup;

public class HomePage extends BasePage {

    private static final Logger log = LogManager.getLogger(lookup().lookupClass());

    public HomePage(UIActions uiActions) {
        super(uiActions);
        log.info("Initializing HomePage object.");

    }

    //***************************************************Functions***************************************************//


    public void enterName(String name) {
        uiActions.sendKeys(UIActions.LocatorType.id, "com.androidsample.generalstore:id/nameField", UIActions.ExplicitWaitCondition.visibilityOfElement, name);
    }

    public void selectGender(Gender MaleOrFemale) {
        if (MaleOrFemale.equals(Gender.female)) {
            uiActions.click(UIActions.LocatorType.androidUIAutomator, "new UiSelector().resourceId(\"com.androidsample.generalstore:id/radioFemale\")", UIActions.ExplicitWaitCondition.elementToBeClickable);
        } else {
            uiActions.click(UIActions.LocatorType.androidUIAutomator, "new UiSelector().resourceId(\"com.androidsample.generalstore:id/radioMale\")", UIActions.ExplicitWaitCondition.elementToBeClickable);
        }
    }

    public void clickShopBtn() throws InterruptedException {
        uiActions.click(UIActions.LocatorType.id, "com.androidsample.generalstore:id/btnLetsShop", UIActions.ExplicitWaitCondition.elementToBeClickable);
        Thread.sleep(2000);
    }

    public void selectCountry(String Nationality) {
        uiActions.click(UIActions.LocatorType.id, "com.androidsample.generalstore:id/spinnerCountry", UIActions.ExplicitWaitCondition.elementToBeClickable);
        WebElement scrollable = uiActions.findWebElement(UIActions.LocatorType.className, "android.widget.ListView", UIActions.ExplicitWaitCondition.visibilityOfElement);
        androidActions.scrollUntilElementVisible(scrollable, UIActions.LocatorType.androidUIAutomator, "new UiSelector().text(\"" + Nationality + "\")", AndroidActions.Direction.down, 0.7);
        uiActions.click(UIActions.LocatorType.androidUIAutomator, "new UiSelector().text(\"" + Nationality + "\")", UIActions.ExplicitWaitCondition.elementToBeClickable);
    }

    public enum Gender {
        male, female
    }


    //***************************************************Assertions***************************************************//

    public void assertProductPageOpened() throws InterruptedException {
        try {
            String pageName = uiActions.getElementAttribute(UIActions.LocatorType.id, "com.androidsample.generalstore:id/toolbar_title", UIActions.ExplicitWaitCondition.presenceOfElement, "text");
            Assert.assertTrue(pageName.toLowerCase().contains("products"));
            log.info("\nAssertion Passed, Product page is shown as expected.\n");
        } catch (Exception e) {
            log.error("Assertion Failed, Product page isn't shown as expected");
            throw e;
        }
    }

    public void assertErrorMessageShown() {

        try {
           uiActions.getElementAttribute(UIActions.LocatorType.xPath, "//android.widget.Toast[@text='Please enter your name']", UIActions.ExplicitWaitCondition.presenceOfElement,"text");
            log.info("\nAssertion Passed, Error message is 'Please enter your name' shown as expected.\n");
        } catch (Exception e) {
            log.error("Assertion Failed, Error message isn't shown as expected.");
            Assert.fail("Assertion Failed, Error message isn't shown.");
        }

    }





}
