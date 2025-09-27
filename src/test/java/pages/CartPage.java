package pages;

import actions.BrowserActions;
import actions.UIActions;
import mobile.android.AndroidActions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;

import static java.lang.invoke.MethodHandles.lookup;

public class CartPage extends BasePage {


    private static final Logger log = LogManager.getLogger(lookup().lookupClass());

    public CartPage(UIActions uiActions) {
        super(uiActions);
        log.info("Initializing CartPage object.");
    }

    //***************************************************Functions***************************************************//

    public void clickOnSendMailsBtn() {
        uiActions.click(UIActions.LocatorType.className, "android.widget.CheckBox", UIActions.ExplicitWaitCondition.elementToBeClickable);
    }

    public void clickOnVisitWebsiteBtn() throws InterruptedException {
        uiActions.click(UIActions.LocatorType.id, "com.androidsample.generalstore:id/btnProceed", UIActions.ExplicitWaitCondition.elementToBeClickable);
        Thread.sleep(4000);
    }

    public void clickOnTermsBtn() {
        WebElement termsBtn = uiActions.findWebElement(UIActions.LocatorType.id, "com.androidsample.generalstore:id/termsButton", UIActions.ExplicitWaitCondition.elementToBeClickable);
        androidActions.longClickGesture(termsBtn, 2000);
    }

    public void clickOnCloseTermsMessageBtn() {
        uiActions.click(UIActions.LocatorType.id, "android:id/button1", UIActions.ExplicitWaitCondition.elementToBeClickable);
    }

    public double getTotalSum() {
        String total = uiActions.getElementText(UIActions.LocatorType.id, "com.androidsample.generalstore:id/totalAmountLbl", UIActions.ExplicitWaitCondition.presenceOfElement);
        return Double.parseDouble(total.substring(1));

    }

    //***************************************************Assertions***************************************************//

    public void assertItemsAddedShownInCart(String[] items, ArrayList<String> expectedItemsPrices, SoftAssert softAssert) {
        boolean indicator = true;

        WebElement scrollableContainer = uiActions.findWebElement(
                UIActions.LocatorType.className,
                "android.support.v7.widget.RecyclerView",
                UIActions.ExplicitWaitCondition.presenceOfElement
        );

        for (int i = 0; i < items.length; i++) {

            androidActions.scrollUntilElementVisible(
                    scrollableContainer,
                    UIActions.LocatorType.xPath,
                    "//android.widget.TextView[contains(@text,\"" + expectedItemsPrices.get(i) + "\")]",
                    AndroidActions.Direction.down,
                    0.7
            );

            String actualItemName = uiActions.getElementAttribute(
                    UIActions.LocatorType.xPath,
                    "//android.widget.TextView[@text='" + expectedItemsPrices.get(i) + "']/ancestor::android.widget.RelativeLayout[1]//android.widget.TextView[@resource-id='com.androidsample.generalstore:id/productName']",
                    UIActions.ExplicitWaitCondition.presenceOfElement,
                    "text"
            );

            softAssert.assertTrue(actualItemName.contains(items[i]));
            if (actualItemName.contains(items[i])) {
                if (actualItemName.equals(items[i])) {
                    indicator = true;
                } else {
                    indicator = true;
                    log.warn("The sent productName '{}' isn't exactly as the product existing name '{}'.", items[i], actualItemName);
                }
            } else {
                indicator = false;

            }
        }
        if (indicator) {
            log.info("\nAssertion Passed, All the products added are shown in the cart with their prices.\n");
        }
    }

    public void assertTotalPriceIsCorrect(Double expectedTotalPrice) {
        double actualTotalPrice = getTotalSum();
        if (actualTotalPrice == expectedTotalPrice) {
            Assert.assertEquals(getTotalSum(), expectedTotalPrice);
            log.info("\nAssertion Passed, Total Amount field is shown the correct summation '{}' of all the products as expected.\n", actualTotalPrice);
        } else {
            log.error("Assertion Failed, Total Amount field is shown wrong summation '{}' than expected total amount '{}'.", actualTotalPrice, expectedTotalPrice);
            Assert.fail("Assertion Failed, Total amount field is shown wrong summation than expected total amount.");
        }
    }

    public void assertMailsCheckboxChecked() {
        String checked = uiActions.getElementAttribute(UIActions.LocatorType.className, "android.widget.CheckBox", UIActions.ExplicitWaitCondition.visibilityOfElement, "checked");
        System.out.println(checked);
        if (checked.equals("true")) {
            log.info("\nAssertion Passed, 'Send Mails' checkbox is checked as expected.\n");
        } else {
            log.error("Assertion Failed, 'Send Mails' checkbox isn't checked as expected.");
            Assert.fail("Assertion Failed, 'Send Mails' checkbox isn't checked.");
        }
    }

    public void assertTermsMessageShown() {
        String terms = uiActions.getElementAttribute(UIActions.LocatorType.id, "com.androidsample.generalstore:id/alertTitle", UIActions.ExplicitWaitCondition.visibilityOfElement, "text");
        if (terms.equals("Terms Of Conditions")) {
            log.info("\nAssertion Passed, 'Terms of Condition' message is shown as expected.\n");
        } else {
            log.error("Assertion Failed, 'Terms of Condition' message isn't shown as expected.");
            Assert.fail("Assertion Failed, 'Terms of Condition' message isn't shown.");
        }
    }

    public void assertWebPageOpenedSuccessfully() {
        switchToWeb();
        Assert.assertEquals(BrowserActions.getWindowTitle(), "Google");
        log.info("\nAssertion Passed, WebPage 'Google' opened successfully.\n");
    }

}

