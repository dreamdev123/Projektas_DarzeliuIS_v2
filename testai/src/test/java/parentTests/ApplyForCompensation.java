package parentTests;

import generalMethods.GeneralMethods;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import managerPages.CompensationPage;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;

import static generalMethods.ApiAdminMethods.createNewUser;
import static generalMethods.ApiAdminMethods.deleteUser;
import static generalMethods.ApiGeneralMethods.logInApi;
import static generalMethods.ApiGeneralMethods.logOutApi;
import static generalMethods.ApiManagerMethods.deleteCompensationApplicationByChildId;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertTrue;

public class ApplyForCompensation extends GeneralMethods {

    /**
     * Test steps:
     * API - Create new USER for this test.
     * UI -
     * Log in as USER.
     * Click dropdown 'sukurti prasyma'.
     * Click 'prasymas del kompensacijos'.
     * Fill in 3 forms.
     * Click 'pateikti'.
     * Assert popup ~success~ is shown.
     * Click OK on popup. Log out.
     * -
     * API - Get and assert application was created.
     * API - Delete application.
     * API - Delete USER created for this test.
     */

    @Test(groups = "regression", priority = 1, dataProvider = "parameters")
    public void successfullyApplyForCompensation(String childId) {

        RequestSpecification reqSpec = new RequestSpecBuilder().
                setBaseUri("https://sextet.akademijait.vtmc.lt/darzelis/").
                setContentType(ContentType.JSON).
                addFilters(Arrays.asList(new RequestLoggingFilter(), new ResponseLoggingFilter())).
                build();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

        // Create new USER for this test
        HashMap<String, Object> user = new HashMap<>();
        user.put("email", "andriusd@andrius.lt");
        user.put("name", "Andrius");
        user.put("password", "andriusd@andrius.lt");
        user.put("role", "USER");
        user.put("surname", "Andriulis");
        user.put("username", "andriusd@andrius.lt");

        logInApi("admin@admin.lt", "admin@admin.lt", reqSpec);
        createNewUser(user, reqSpec);
        logOutApi(reqSpec);

        // USER fills in application for compensation
        logInUi("andriusd@andrius.lt", "andriusd@andrius.lt");

        fillInCompensationForm(childId);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@role='dialog']/div[1]"))); // wait for popup
        clickOkButton();
        logOutUi();

        // View application as manager
        CompensationPage compensationPage = new CompensationPage(driver);

        logInUi("manager@manager.lt", "manager@manager.lt");
        wait.until(ExpectedConditions.elementToBeClickable(compensationPage.buttonPrasymai));
        compensationPage.clickButtonPrasymai();
        wait.until(ExpectedConditions.elementToBeClickable(compensationPage.navManagerKompensacijos));
        compensationPage.clickNavManagerKompensacijos();
        wait.until(ExpectedConditions.elementToBeClickable(compensationPage.buttonPerziureti));
        compensationPage.clickPerziureti();
        assertTrue(driver.findElement(By.xpath("//h3")).getText().contains("prašymo peržiūra"), "'Prasymas' page loaded");
        compensationPage.clickAtgal();

        // Download application
        wait.until(ExpectedConditions.elementToBeClickable(compensationPage.buttonAtsisiusti));
        compensationPage.clickAtsisiusti();
        logOutUi();

        // Delete application
        logInApi("manager@manager.lt", "manager@manager.lt", reqSpec);
        deleteCompensationApplicationByChildId(childId, reqSpec).
                then().
                statusCode(204);

        // Delete USER created for this test
        logInApi("admin@admin.lt", "admin@admin.lt", reqSpec);
        deleteUser("andriusd@andrius.lt", reqSpec).
                then().
                statusCode(200).
                body(equalTo("Naudotojas ištrintas sėkmingai"));
    }

    @DataProvider
    public Object[][] parameters() {

        return new Object[][]{
                {"51609260036"}, // child name with Latin characters only
                {"51609260189"}  // child name with LT characters
        };
    }

}
