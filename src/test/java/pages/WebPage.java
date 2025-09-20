package pages;

import actions.BrowserActions;
import actions.UIActions;
import io.appium.java_client.android.AndroidDriver;
import mobileDriverFactory.GetMobileDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.Assert;

import java.util.List;

import static java.lang.invoke.MethodHandles.lookup;

public class WebPage extends BasePage{

    private static final Logger log = LogManager.getLogger(lookup().lookupClass());

    public WebPage(UIActions uiActions) {
        super(uiActions);
        log.info("Initializing ProductPage object.");
    }

    public void loginHerokuApp(String username, String password) throws InterruptedException {
        BrowserActions.navigateToPage("https://the-internet.herokuapp.com/login");
        Thread.sleep(2000);
        uiActions.sendKeys(UIActions.LocatorType.name,"username", UIActions.ExplicitWaitCondition.elementToBeClickable,username);
        uiActions.sendKeys(UIActions.LocatorType.name,"password", UIActions.ExplicitWaitCondition.elementToBeClickable,password);
        uiActions.click(UIActions.LocatorType.css,".radius", UIActions.ExplicitWaitCondition.elementToBeClickable);
    }
    public void assertLoggedSuccessfully(){
        uiActions.findWebElement(UIActions.LocatorType.xPath,"//*[contains(text(), 'You logged into a secure area!')]", UIActions.ExplicitWaitCondition.visibilityOfElement);
        log.info("\nAssertion Passed, User Logged in successfully.\n");
    }



}
