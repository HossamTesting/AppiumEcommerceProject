package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.ProductPage;
import utility.AllureLog4jListener;
import utility.AnnotationTransformer;

import static java.lang.invoke.MethodHandles.lookup;

@Listeners({AllureLog4jListener.class, AnnotationTransformer.class})
public class NegativeTestCases extends BaseTest{

    private static final Logger log = LogManager.getLogger(lookup().lookupClass());



    @Description("This testcase aim is to ensure that a user can't navigate to cart page without adding products.")
    @Feature("NegativeScenarios")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 7)
    public void TC7_userCannotNavigateToCartPageWithoutAddingProducts() throws Exception {
        ThreadContext.put("TestName", methodName.get());
        log.info("************ Starting method: '{}' ************", methodName.get());

        homePage.set(new HomePage(uiActions.get()));
        homePage.get().selectCountry(jsonFileManager.getKeyAndValueByKey("TC7_userCannotNavigateToCartPageWithoutAddingProducts").get("country").toString());
        homePage.get().enterName(jsonFileManager.getKeyAndValueByKey("TC7_userCannotNavigateToCartPageWithoutAddingProducts").get("name").toString());
        String gender = jsonFileManager.getKeyAndValueByKey("TC7_userCannotNavigateToCartPageWithoutAddingProducts").get("gender").toString().toLowerCase();
        homePage.get().selectGender(HomePage.Gender.valueOf(gender));
        homePage.get().clickShopBtn();
        productPage.set(new ProductPage(uiActions.get()));
        productPage.get().clickOnCartBtn();
        productPage.get().assertErrorMessageShown();

    }


    @Description("This testcase aim is to ensure that a user can't navigate to product page without filling form.")
    @Feature("NegativeScenarios")
    @Severity(SeverityLevel.CRITICAL)
    @Test(priority = 8)
    public void TC8_userCannotNavigateToLoginWithoutFillingForm() throws Exception {
        ThreadContext.put("TestName", methodName.get());
        log.info("************ Starting method: '{}' ************", methodName.get());
        homePage.set(new HomePage(uiActions.get()));
        homePage.get().clickShopBtn();
        homePage.get().assertErrorMessageShown();

    }




}
