package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.HomePage;
import pages.ProductPage;
import pages.WebPage;
import utility.AllureLog4jListener;
import utility.AnnotationTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static java.lang.invoke.MethodHandles.lookup;

@Listeners({AllureLog4jListener.class, AnnotationTransformer.class})

public class PositiveTestCases extends BaseTest {


    private static final Logger log = LogManager.getLogger(lookup().lookupClass());


    @Description("This testcase aim is to ensure that a user can enters product page after filling the form data and clicking shop button.")
    @Feature("PositiveScenarios")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 1)
    public void TC1_userCanLoginByFillingForm() throws Exception {
        ThreadContext.put("TestName", methodName.get());
        log.info("************ Starting method: '{}' ************", methodName.get());

        homePage.set(new HomePage(uiActions.get()));
        homePage.get().getTitle();
        homePage.get().selectCountry(jsonFileManager.get().getKeyAndValueByKey("TC1_userCanLoginByFillingForm").get("country").toString());
        homePage.get().enterName(jsonFileManager.get().getKeyAndValueByKey("TC1_userCanLoginByFillingForm").get("name").toString());
        String gender = jsonFileManager.get().getKeyAndValueByKey("TC1_userCanLoginByFillingForm").get("gender").toString().toLowerCase();
        homePage.get().selectGender(HomePage.Gender.valueOf(gender));
        homePage.get().clickShopBtn();
        homePage.get().assertProductPageOpened();

    }

    @Description("This testcase aim is to ensure that a user can select a product or more and click on the cart button to navigate to the cart page.")
    @Feature("PositiveScenarios")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 2)
    public void TC2_userCanNavigateToCartPageAfterAddingProducts() throws Exception {
        ThreadContext.put("TestName", methodName.get());
        log.info("************ Starting method: '{}' ************", methodName.get());

        homePage.set(new HomePage(uiActions.get()));
        homePage.get().selectCountry(jsonFileManager.get().getKeyAndValueByKey("TC2_userCanNavigateToCartPageAfterAddingProducts").get("country").toString());
        homePage.get().enterName(jsonFileManager.get().getKeyAndValueByKey("TC2_userCanNavigateToCartPageAfterAddingProducts").get("name").toString());
        String gender = jsonFileManager.get().getKeyAndValueByKey("TC2_userCanNavigateToCartPageAfterAddingProducts").get("gender").toString().toLowerCase();
        homePage.get().selectGender(HomePage.Gender.valueOf(gender));
        homePage.get().clickShopBtn();
        productPage.set(new ProductPage(uiActions.get()));
        List<?> products = (List<?>) jsonFileManager.get()
                .getKeyAndValueByKey("TC2_userCanNavigateToCartPageAfterAddingProducts")
                .get("products");
        String[] stringArray = products.stream()
                .map(String::valueOf)
                .toArray(String[]::new);
        productPage.get().addItemToCart(stringArray);
        productPage.get().clickOnCartBtn();
        productPage.get().assertCartPageOpened();

    }

    @Description("This testcase aim is to ensure that all products selected by the user are triggering cart counter and are shown in the cart with their exact name and prices.")
    @Feature("PositiveScenarios")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 3)
    public void TC3_userFindSelectedProductsAtCart() throws Exception {
        ThreadContext.put("TestName", methodName.get());
        log.info("************ Starting method: '{}' ************", methodName.get());

        homePage.set(new HomePage(uiActions.get()));
        homePage.get().selectCountry(jsonFileManager.get().getKeyAndValueByKey("TC3_userFindSelectedProductsAtCart").get("country").toString());
        homePage.get().enterName(jsonFileManager.get().getKeyAndValueByKey("TC3_userFindSelectedProductsAtCart").get("name").toString());
        String gender = jsonFileManager.get().getKeyAndValueByKey("TC3_userFindSelectedProductsAtCart").get("gender").toString().toLowerCase();
        homePage.get().selectGender(HomePage.Gender.valueOf(gender));
        homePage.get().clickShopBtn();
        productPage.set(new ProductPage(uiActions.get()));
        List<?> products = (List<?>) jsonFileManager.get()
                .getKeyAndValueByKey("TC3_userFindSelectedProductsAtCart")
                .get("products");
        String[] productsArray = products.stream()
                .map(String::valueOf)
                .toArray(String[]::new);
        productPage.get().addItemToCart(productsArray);
        productPage.get().assertCartCounterShowsAddedProductsCounter(String.valueOf(productsArray.length), softAssert.get());
        productPage.get().clickOnCartBtn();
        productPage.get().assertCartPageOpened();
        ArrayList<String> productPrices = productPage.get().getPricesArray();
        cartPage.set(new CartPage(uiActions.get()));
        cartPage.get().assertItemsAddedShownInCart(productsArray, productPrices, softAssert.get());
        softAssert.get().assertAll();


    }

    @Description("This testcase aim is to ensure that the total amount field is showing correctly the sum of all the added products.")
    @Feature("PositiveScenarios")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 4)
    public void TC4_totalProductsAmountIsShownCorrectly() throws Exception {
        ThreadContext.put("TestName", methodName.get());
        log.info("************ Starting method: '{}' ************", methodName.get());

        homePage.set(new HomePage(uiActions.get()));
        homePage.get().selectCountry(jsonFileManager.get().getKeyAndValueByKey("TC4_totalProductsAmountIsShownCorrectly").get("country").toString());
        homePage.get().enterName(jsonFileManager.get().getKeyAndValueByKey("TC4_totalProductsAmountIsShownCorrectly").get("name").toString());
        String gender = jsonFileManager.get().getKeyAndValueByKey("TC4_totalProductsAmountIsShownCorrectly").get("gender").toString().toLowerCase();
        homePage.get().selectGender(HomePage.Gender.valueOf(gender));
        homePage.get().clickShopBtn();
        homePage.get().assertProductPageOpened();
        productPage.set(new ProductPage(uiActions.get()));
        List<?> products = (List<?>) jsonFileManager.get()
                .getKeyAndValueByKey("TC4_totalProductsAmountIsShownCorrectly")
                .get("products");
        String[] productsArray = products.stream()
                .map(String::valueOf)
                .toArray(String[]::new);
        productPage.get().addItemToCart(productsArray);
        productPage.get().clickOnCartBtn();
        productPage.get().assertCartPageOpened();
        Double ExpectedTotalPrice = productPage.get().getProductsSum();
        cartPage.set(new CartPage(uiActions.get()));
        cartPage.get().getTotalSum();
        cartPage.get().assertTotalPriceIsCorrect(ExpectedTotalPrice);


    }

    @Description("This testcase aim is to ensure the functionality of cart page 'Terms', 'Send mails' and 'Visit Store' checkboxes/buttons")
    @Feature("PositiveScenarios")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 5)
    public void TC5_cartButtonsWorkingCorrectly() throws Exception {
        ThreadContext.put("TestName", methodName.get());
        log.info("************ Starting method: '{}' ************", methodName.get());

        homePage.set(new HomePage(uiActions.get()));
        homePage.get().selectCountry(jsonFileManager.get().getKeyAndValueByKey("TC5_cartButtonsWorkingCorrectly").get("country").toString());
        homePage.get().enterName(jsonFileManager.get().getKeyAndValueByKey("TC5_cartButtonsWorkingCorrectly").get("name").toString());
        String gender = jsonFileManager.get().getKeyAndValueByKey("TC5_cartButtonsWorkingCorrectly").get("gender").toString().toLowerCase();
        homePage.get().selectGender(HomePage.Gender.valueOf(gender));
        homePage.get().clickShopBtn();
        homePage.get().assertProductPageOpened();
        productPage.set(new ProductPage(uiActions.get()));
        List<?> products = (List<?>) jsonFileManager.get()
                .getKeyAndValueByKey("TC5_cartButtonsWorkingCorrectly")
                .get("products");
        String[] productsArray = products.stream()
                .map(String::valueOf)
                .toArray(String[]::new);
        productPage.get().addItemToCart(productsArray);
        productPage.get().clickOnCartBtn();
        productPage.get().assertCartPageOpened();
        cartPage.set(new CartPage(uiActions.get()));
        cartPage.get().clickOnSendMailsBtn();
        cartPage.get().assertMailsCheckboxChecked();
        cartPage.get().clickOnTermsBtn();
        cartPage.get().assertTermsMessageShown();
        cartPage.get().clickOnCloseTermsMessageBtn();
        cartPage.get().clickOnVisitWebsiteBtn();

    }

    @Description("This testcase aim is to click on the web button and navigate to the web app preforming logging")
    @Feature("PositiveScenarios")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 6)
    public void TC6_userCanLoggingWhenNavigateToWebsite() throws Exception {
        ThreadContext.put("TestName", methodName.get());
        log.info("************ Starting method: '{}' ************", methodName.get());

        homePage.set(new HomePage(uiActions.get()));
        homePage.get().selectCountry(jsonFileManager.get().getKeyAndValueByKey("TC6_userCanLoggingWhenNavigateToWebsite").get("country").toString());
        homePage.get().enterName(jsonFileManager.get().getKeyAndValueByKey("TC6_userCanLoggingWhenNavigateToWebsite").get("name").toString());
        String gender = jsonFileManager.get().getKeyAndValueByKey("TC6_userCanLoggingWhenNavigateToWebsite").get("gender").toString().toLowerCase();
        homePage.get().selectGender(HomePage.Gender.valueOf(gender));
        homePage.get().clickShopBtn();
        homePage.get().assertProductPageOpened();
        productPage.set(new ProductPage(uiActions.get()));
        List<?> products = (List<?>) jsonFileManager.get()
                .getKeyAndValueByKey("TC6_userCanLoggingWhenNavigateToWebsite")
                .get("products");
        String[] productsArray = products.stream()
                .map(String::valueOf)
                .toArray(String[]::new);
        productPage.get().addItemToCart(productsArray);
        productPage.get().clickOnCartBtn();
        productPage.get().assertCartPageOpened();
        cartPage.set(new CartPage(uiActions.get()));
        cartPage.get().clickOnVisitWebsiteBtn();
        cartPage.get().assertWebPageOpenedSuccessfully();
        webPage.set(new WebPage(uiActions.get()));
        webPage.get().switchToWeb();
        webPage.get().loginHerokuApp("tomsmith", "SuperSecretPassword!");
        webPage.get().assertLoggedSuccessfully();


    }

    @Description("This testcase aim is to click on the web button and navigate to the web app preforming logging")
    @Feature("PositiveScenarios")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 6)
    public void test123 () throws Exception {



    }


}

