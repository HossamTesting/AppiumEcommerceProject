package pages;

import actions.UIActions;
import mobile.android.AndroidActions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;

import static java.lang.invoke.MethodHandles.lookup;

public class ProductPage extends BasePage {


    private static final Logger log = LogManager.getLogger(lookup().lookupClass());
    private ArrayList<String> selectedItemsPrices;

    public ProductPage(UIActions uiActions) {
        super(uiActions);
        log.info("Initializing ProductPage object.");
    }

    //***************************************************Functions***************************************************//

    public void addItemToCart(String[] items) {
        ArrayList<String> itemsPrices = new ArrayList<>();
        WebElement scrollableContainer = uiActions.findWebElement(UIActions.LocatorType.xPath, "//android.support.v7.widget.RecyclerView[@resource-id=\"com.androidsample.generalstore:id/rvProductList\"]"
                , UIActions.ExplicitWaitCondition.visibilityOfElement);
        for (String item : items) {

            androidActions.scrollUntilElementVisible(scrollableContainer, UIActions.LocatorType.xPath,
                    "//android.widget.TextView[contains(@text,\"" + item + "\")]/ancestor::android.widget.LinearLayout[1]//android.widget.TextView[@text='ADD TO CART']",
                    AndroidActions.Direction.down, 0.7);
            uiActions.click(
                    UIActions.LocatorType.xPath,
                    "//android.widget.TextView[contains(@text,\"" + item + "\")]/ancestor::android.widget.LinearLayout[1]//android.widget.TextView[@text='ADD TO CART']"
                    , UIActions.ExplicitWaitCondition.elementToBeClickable
            );

            itemsPrices.add(uiActions.getElementText(
                    UIActions.LocatorType.xPath,
                    "//android.widget.TextView[contains(@text,\"" + item + "\")]/ancestor::android.widget.LinearLayout[1]//android.widget.TextView[contains(@text,'$')]"
                    , UIActions.ExplicitWaitCondition.presenceOfElement
            ));

        }
        selectedItemsPrices = itemsPrices;
    }

    public void clickOnCartBtn() throws InterruptedException {
        uiActions.click(UIActions.LocatorType.id, "com.androidsample.generalstore:id/appbar_btn_cart", UIActions.ExplicitWaitCondition.elementToBeClickable);
        Thread.sleep(1000);

    }

    public double getProductsSum() {
        double sum = 0;
        for (String item : selectedItemsPrices) {
            double itemPrice = Double.parseDouble(item.substring(1));
            sum = sum + itemPrice;
        }
        return sum;
    }

    public String getCartCounter() {
        return uiActions.getElementText(UIActions.LocatorType.id, "com.androidsample.generalstore:id/counterText", UIActions.ExplicitWaitCondition.presenceOfElement);

    }

    public ArrayList<String> getPricesArray() {
        return selectedItemsPrices;
    }


    //***************************************************Assertions***************************************************//

    public void assertErrorMessageShown() {

        try {
            uiActions.findWebElement(UIActions.LocatorType.xPath, "//android.widget.Toast[@text='Please add some product at first']", UIActions.ExplicitWaitCondition.presenceOfElement);
            log.info("\nAssertion Passed, Error message 'Please add some product at first' is shown.\n");
        } catch (Exception e) {
            log.error("Assertion Failed, Error message isn't shown as expected.");
            Assert.fail("Assertion Failed, Error message isn't shown.");

        }

    }

    public void assertCartPageOpened() {
        try {
            String pageName = uiActions.getElementAttribute(UIActions.LocatorType.id,
                    "com.androidsample.generalstore:id/toolbar_title",
                    UIActions.ExplicitWaitCondition.presenceOfElement,
                    "text");
            Assert.assertTrue(pageName.toLowerCase().contains("cart"));
            log.info("\nAssertion Passed, Cart page is shown as expected.\n");
        } catch (Exception e) {
            log.error("Assertion Failed, Cart page isn't shown as expected");
            throw e;
        }
    }

    public void assertCartCounterShowsAddedProductsCounter(String itemsSize, SoftAssert softAssert) {
        if (getCartCounter().equals(itemsSize)) {
            softAssert.assertEquals(getCartCounter(), itemsSize);
        } else {
            log.error("Assertion Failed, Cart counter '{}' isn't the same as selected items size '{}'.", getCartCounter(), itemsSize);
            throw new RuntimeException("Assertion Failed, Cart counter isn't the same as selected items size.");
        }
    }


}
