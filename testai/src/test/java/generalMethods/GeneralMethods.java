package generalMethods;

import adminPages.CreateAndDeleteNewUserPage;
import basetest.BaseTest;
import managerPages.CreateAndDeleteNewKindergartenPage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.ChangeAndResetUserAccountFieldsAndPasswordPage;
import pages.LoginPage;
import parentPages.ApplyForCompensationPage;
import parentPages.SubmitNewApplicationPage;
import parentPages.UploadMedicalDocumentPDFPage;
import utilities.FileReaderUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.regex.Matcher;

public class GeneralMethods extends BaseTest {


    protected static String adminLogIn = "admin@admin.lt";
    protected static String managerLogIn = "manager@manager.lt";
    protected static String parentLogIn = "user@user.lt";
    protected String createNewUserAdminEmail = "admin123@admin.lt";
    protected String createNewUserManagerEmail = "manager123@manager.lt";
    protected String createNewUserParentEmail = "user123@parent.lt";
    private final String newUserName = "Jonas";
    private final String newUserSurname = "Jonaitis";
    private String pdfFileLocation = System.getProperty("user.dir") + "/src/test/resources/Testas.pdf";



    // LOGIN/ LOGOUT METHODS

    public void logInUi(String username, String password) {
        LoginPage loginPage = new LoginPage(driver);
        waitForLoginToLoad();
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLoginButton();
    }

    public void uiLogInAsAdmin() {
        LoginPage loginPage = new LoginPage(driver);
        waitForLoginToLoad();
        loginPage.enterUsername(adminLogIn);
        loginPage.enterPassword(adminLogIn);
        loginPage.clickLoginButton();
    }

    public void logOutUi() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement logoutElement = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("btnLogout")));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", logoutElement);
    }

    public void verifyIfAdminIsLoggedIn() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.textToBe(By.id("navAdminUserList"), "Naudotojai"));
    }

    public void verifyIfManagerIsLoggedIn() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.textToBe(By.id("navManagerKindergartenList"), "Darželių sąrašas"));
    }

    // CREATE AND DELETE NEW USER

    // create new admin
    public void createNewAdmin(int index) {
        uiLogInAsAdmin();
        verifyIfAdminIsLoggedIn();

        // select user role
        Select dropdownUserRole = new Select(driver.findElement(By.id("selRole")));
        dropdownUserRole.selectByIndex(index);

        CreateAndDeleteNewUserPage createNewUserPage = new CreateAndDeleteNewUserPage(driver);
        createNewUserPage.enterEmail(createNewUserAdminEmail);
        createNewUserPage.enterName(newUserName);
        createNewUserPage.enterSurname(newUserSurname);

        createNewUserPage.clickCreateButton();
        // check success message
        userIsCreatedMessage();
        createNewUserPage.clickOKButtonUserIsCreated();
    }

    public void userNotLoggedInPopUp() {
        CreateAndDeleteNewUserPage newUser = new CreateAndDeleteNewUserPage(driver);
        LoginPage loginPage = new LoginPage(driver);

        if (newUser.userNotLoggedInButton.isDisplayed()) {
            newUser.clickOkUserNotLoggedInButton();
            loginPage.clickLoginButton();
        } else {
            loginPage.clickLoginButton();
        }
    }

    // create new kindergarten specialist
    public void createNewManager(int index) {
        uiLogInAsAdmin();
        verifyIfAdminIsLoggedIn();

        // select user role
        Select dropdownUserRole = new Select(driver.findElement(By.id("selRole")));
        dropdownUserRole.selectByIndex(index);

        CreateAndDeleteNewUserPage createNewUserPage = new CreateAndDeleteNewUserPage(driver);
        createNewUserPage.enterEmail(createNewUserManagerEmail);
        createNewUserPage.enterName(newUserName);
        createNewUserPage.enterSurname(newUserSurname);

        createNewUserPage.clickCreateButton();
        // check success message
        userIsCreatedMessage();
        createNewUserPage.clickOKButtonUserIsCreated();
    }

    // create new parent/ guardian
    public void createNewParent(int index) throws InterruptedException {
        CreateAndDeleteNewUserPage createNewUserPage = new CreateAndDeleteNewUserPage(driver);
        verifyIfAdminIsLoggedIn();

        // select user role
        Select dropdownUserRole = new Select(driver.findElement(By.id("selRole")));
        dropdownUserRole.selectByIndex(index);

        createNewUserPage.enterEmail(createNewUserParentEmail);
        createNewUserPage.enterName(newUserName);
        createNewUserPage.enterSurname(newUserSurname);
        createNewUserPage.enterPersonalCode("12345678911");
        createNewUserPage.enterPhoneNumber("+37061212123");
        createNewUserPage.enterAddress("Adreso g. 8");
        createNewUserPage.enterCity("Vilnius");
        driver.findElement(By.tagName("body")).sendKeys(Keys.END);
        Thread.sleep(200);

        createNewUserPage.clickCreateButton();

        // check success message
        userIsCreatedMessage();
        createNewUserPage.clickOKButtonUserIsCreated();
    }

    public void deleteNewUser() {
        clickDeleteUserButton();
        // agree to delete user (pop up)
        waitToAgreePopUp();
        waitToPressOKPopUp();

        // logout after deleting the user
        logOutUi();
    }

    // CHANGE USER DETAILS (MANO PASKYRA PAGE)

    public void inputUserDetails() {
        // change kindergarten specialist details
        ChangeAndResetUserAccountFieldsAndPasswordPage changeAccountDetails = new ChangeAndResetUserAccountFieldsAndPasswordPage(driver);
        assertThatMyAccountPageHasLoaded();
        String changedUserName = "Pakeistas";
        changeAccountDetails.changeUserName(changedUserName);
        String changedUserSurname = "Pakeistas";
        changeAccountDetails.changeUserSurname(changedUserSurname);
        String changedUserEmail = "pakeistas@email.lt";
        changeAccountDetails.changeUserEmail(changedUserEmail);
    }

    public void changeUserPassword(String userLogin) {
        ChangeAndResetUserAccountFieldsAndPasswordPage changeAccountDetails = new ChangeAndResetUserAccountFieldsAndPasswordPage(driver);
        // click button "Keisti"
        changeAccountDetails.clickChangeUserPasswordButton();

        // enter old and new password
        changeAccountDetails.enterOldPassword(userLogin);
        String newPassword = "Naujas321";
        changeAccountDetails.enterNewPassword(newPassword);
        changeAccountDetails.enterRepeatedNewPassword(newPassword);

        // save the new password
        changeAccountDetails.clickButtonSaveChangedPassword();

        // assert that password was changed
        assertThatUserPasswordWasUpdated();
        changeAccountDetails.clickOkButtonPasswordChanged();

        logOutUi();

        // check if user can log in with changed password
        waitForLoginToLoad();
        logInUi(userLogin, newPassword);

        // logout
        logOutUi();
    }

    public void resetUserPassword(String userLogin) {
        ChangeAndResetUserAccountFieldsAndPasswordPage changeAccountDetails = new ChangeAndResetUserAccountFieldsAndPasswordPage(driver);
        clickUserForgotPasswordButton();

        // enter user email
        enterUserEmail(userLogin);
        changeAccountDetails.clickOkResetPasswordButton();
        clickDoneButtonForgotPassword();

        // login as admin
        uiLogInAsAdmin();

        // reset password that needs to be reset (the button "Atkurti" becomes grey when it needs to be reset)
        clickResetPasswordButton(userLogin);
        changeAccountDetails.clickAgreeToResetUserPasswordButton();

        // assert message that user password was reset
        assertThatPasswordWasReset();
        changeAccountDetails.clickOkButtonPasswordIsReset();

        // logout and check if user can login with original password
        logOutUi();
        waitForLoginToLoad();
        logInUi(userLogin, userLogin);
        logOutUi();
    }

    public void clickChangeUserDetails() {
        ChangeAndResetUserAccountFieldsAndPasswordPage changeAccountDetails = new ChangeAndResetUserAccountFieldsAndPasswordPage(driver);
        changeAccountDetails.clickButtonUpdateUserDetails();
        // assert that information was updated
        changeAccountDetails.assertThatUserInformationWasUpdated();
        changeAccountDetails.clickOkButtonUserDetailsUpdated();
    }

    // CREATE AND DELETE NEW KINDERGARTEN

    public void successfullyCreateNewKindergarten() throws InterruptedException {

        // login as kindergarten specialist
        logInUi(managerLogIn, managerLogIn);

        // wait for the page to load and check if the kindergarten specialist is logged in
        verifyIfManagerIsLoggedIn();

        // input new kindergarten details
        CreateAndDeleteNewKindergartenPage createNewKindergarten = new CreateAndDeleteNewKindergartenPage(driver);
        createNewKindergarten.inputKindergartenID("000000001");
        createNewKindergarten.inputKindergartenName("AaTestinis");
        createNewKindergarten.inputKindergartenAddress("Adreso g. 5");
        Select dropdownUserRole = new Select(driver.findElement(By.id("elderate")));
        dropdownUserRole.selectByIndex(5);
        createNewKindergarten.inputDirectorName("Vaišvydas");
        createNewKindergarten.inputDirectorSurname("Nasvytis");
        driver.findElement(By.tagName("body")).sendKeys(Keys.END);
        Thread.sleep(200);
        createNewKindergarten.capacityAgeGroup2to3.sendKeys(Keys.BACK_SPACE);
        createNewKindergarten.inputCapacityAgeGroup2to3("1");
        createNewKindergarten.capacityAgeGroup3to6.sendKeys(Keys.BACK_SPACE);
        createNewKindergarten.inputCapacityAgeGroup3to6("1");

        // save and create new kindergarten
        createNewKindergarten.clickButtonSaveKindergarten();
        createNewKindergarten.clickOKPopUp();

        // search for the newly created kindergarten
        createNewKindergarten.searchForTheNewlyCreatedKindergarten("AaTestinis");

        // assert that the new kindergarten is found in the searched list
        createNewKindergarten.newKindergartenSearchResult();

        // update and save the kindergarten details
        createNewKindergarten.clickButtonUpdateKindergarten();
        createNewKindergarten.updateNewKindergartenName("AaTestinis darželis");
        createNewKindergarten.updateKindergartenNumberCapacity3to6("1");
        createNewKindergarten.clickSaveUpdatedKindergarten();
    }

    public void deleteNewKindergarten() {
        CreateAndDeleteNewKindergartenPage createNewKindergarten = new CreateAndDeleteNewKindergartenPage(driver);
        createNewKindergarten.clickButtonDeleteKindergarten();
        createNewKindergarten.clickButtonAgreeToDeleteKindergarten();
        createNewKindergarten.assertKindergartenWasDeletedSuccessfully();
        waitToPressOKPopUp();
    }

    // REGISTRATION TO KINDERGARTEN METHODS


    public void fillInCompensationForm(String childId) {
        ApplyForCompensationPage compensationPage = new ApplyForCompensationPage(driver);
        clickNavButtonNewApplication();
        clickDrpDnButtonCompensation();

        // fill child form
        compensationPage.inputChildPersonalId(childId);

        // fill guardian form
        compensationPage.inputGuardianName("Andrius");
        compensationPage.inputGuardianSurname("Andriulis");
        compensationPage.inputGuardianPersonalId("54634565466");
        compensationPage.inputGuardianPhone("+37054756754");
        compensationPage.inputGuardianEmail("andrius@andriulis.lt");
        compensationPage.inputGuardianAddress("Gatve");

        // fill kindergarten form
        compensationPage.inputKindergartenName("Pagrandukas");
        compensationPage.inputKindergartenCode("456645645");
        compensationPage.inputKindergartenAddress("Gerve");
        compensationPage.inputKindergartenPhone("+37012312345");
        compensationPage.inputKindergartenEmail("pagran@dukas.lt");
        compensationPage.inputKindergartenBankName("Swedbank");
        compensationPage.inputKindergartenAccountNumber("LT123456789123456789");
        compensationPage.inputKindergartenBankCode("12345");

        compensationPage.clickBtnSubmit();

    }

    public void fillInTheApplication() throws IOException, InterruptedException {
        SubmitNewApplicationPage newApplication = new SubmitNewApplicationPage(driver);
        clickNavButtonNewApplication();
        clickDrpDnButtonRegistration();

        // add additional parent/ guardian
        newApplication.clickAddAdditionalGuardianButton();

        // fill in additional parent/ guardian details into the form
        applicationFormSecondParentDetails();

        // fill in child details into the form
        applicationFormChildDetails();

        // child priorities and first kindergarten priority
        checkPrioritiesAndChooseAKindergarten();

        // submit application
        newApplication.clickButtonSubmitApplication();
        waitToClickSubmitButton();
    }


    public void applicationFormSecondParentDetails() throws IOException {
        SubmitNewApplicationPage newApplication = new SubmitNewApplicationPage(driver);
        List<String> formData = FileReaderUtils.getTestData("src/test/resources/parentAndChildDetails.txt");
        String secondParentName = formData.get(0);
        String secondParentSurname = formData.get(1);
        String secondParentPersonalCode = formData.get(2);
        String secondParentPhoneNumber = formData.get(3);
        String secondParentEmail = formData.get(4);
        String secondParentAddress = formData.get(5);
        newApplication.inputSecondParentName(secondParentName);
        newApplication.inputSecondParentSurname(secondParentSurname);
        newApplication.inputSecondParentPersonalCode(secondParentPersonalCode);
        newApplication.inputSecondParentPhoneNumber(secondParentPhoneNumber);
        newApplication.inputSecondParentEmail(secondParentEmail);
        newApplication.inputSecondParentAddress(secondParentAddress);
    }

    public void applicationFormChildDetails() {
        SubmitNewApplicationPage newApplication = new SubmitNewApplicationPage(driver);
        newApplication.inputChildPersonalCode("51609260091");

    }

    public void checkPrioritiesAndChooseAKindergarten() throws InterruptedException {
        SubmitNewApplicationPage newApplication = new SubmitNewApplicationPage(driver);

        // check priorities
        newApplication.clickPriorityOne();
        newApplication.clickPriorityTwo();
        newApplication.clickPriorityThree();
        newApplication.clickPriorityFour();
        newApplication.clickPriorityFive();

        // choose a kindergarten from the list
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        newApplication.openKindergartenListDropdownPriorityOne();
    }

    // UPLOAD USER MEDICAL DOCUMENTS (PDF)

    public void uploadPDF() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        UploadMedicalDocumentPDFPage uploadDocument = new UploadMedicalDocumentPDFPage(driver);
        uploadDocument.clickUploadDocumentButton();
        pdfFileLocation = pdfFileLocation.replaceAll("/", Matcher.quoteReplacement(File.separator));
        uploadDocument.inputUploadDocument.sendKeys(pdfFileLocation);
        wait.until(ExpectedConditions.elementToBeClickable(uploadDocument.buttonIkelti));
        uploadDocument.clickButtonIkelti();
        waitToPressOKPopUp();
    }

    public void deletePDF() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        UploadMedicalDocumentPDFPage uploadDocument = new UploadMedicalDocumentPDFPage(driver);
        uploadDocument.clickDeleteDocumentButton();
        waitToAgreePopUp();
        waitToPressOKPopUp();
    }


    // WAIT FOR PAGES TO LOAD

    public void waitForLoginToLoad() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.textToBe(By.xpath("//h3"), "Prisijungti"));
    }

    public void assertThatMyAccountPageHasLoaded() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.textToBe(By.xpath("//div//h6"), "Redaguoti duomenis"));
    }

    // WAIT TO ASSERT MESSAGE

    public void applicationSuccessful() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.textToBe(By.xpath("/html/body/div[2]/div/div[1]"), "Prašymas sukurtas sėkmingai"));
    }

    public void userIsCreatedMessage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.textToBe(By.xpath("//body/div[2]/div/div[1]"), "Naujas naudotojas buvo sėkmingai sukurtas."));
    }

    public void checkErrorMessage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        String expectedErrorMessage = "Neteisingas prisijungimo vardas ir/arba slaptažodis!";
        wait.until(ExpectedConditions.textToBe(By.id("incorrectLoginData"), expectedErrorMessage));
    }

    public void assertThatPasswordWasReset() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.textToBe(By.xpath("//*[@role='dialog']/div[1]"), "Slaptažodis atkurtas sėkmingai"));
    }

    public void assertThatUserPasswordWasUpdated() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.textToBe
                (By.xpath("//body/div[2]/div/div[1]"), "Naudotojo slaptažodis atnaujintas sėkmingai"));
    }

    public void assertThatMyDocumentsPageLoaded() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.textToBe
                (By.xpath("//*/div[1]//h6"), "Mano pažymos"));
    }

    // WAIT TO CLICK BUTTONS

    public void waitToClickSubmitButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement clickButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class, 'confirm')]")));
        clickButton.click();
    }

    public void clickDeleteApplication() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement delete = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("btnDeleteApplication")));
        delete.click();
    }

    public void clickNavButtonAdminMyAccount() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement navMyAccountAdmin = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("navAdminMyAccount")));
        navMyAccountAdmin.click();
    }

    public void clickNavButtonMyDocumentsParent() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement navMyDocuments = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("navUserDocuments")));
        navMyDocuments.click();
    }

    public void clickNavButtonMyAccountParent() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement navMyAccountParent = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("navUserMyAccount")));
        navMyAccountParent.click();
    }

    public void waitToAgreePopUp() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement agreeToDeleteUser = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//*[contains(@class, 'confirm') and contains(text(), 'Taip')]")));
        agreeToDeleteUser.click();
    }

    public void waitToPressOKPopUp() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement popUpClickOK = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='swal-button swal-button--confirm']")));
        popUpClickOK.click();
    }

    public void clickOkButton() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", driver.findElement(By.xpath("//button[@class='swal-button swal-button--confirm']")));
    }

    public void clickUserForgotPasswordButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement forgotPassword = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.className("btn-link")));
        forgotPassword.click();
    }

    public void clickDoneButtonForgotPassword() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement clickDone = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//div/button")));
        clickDone.click();
    }

    public void clickResetPasswordButton(String userLogin) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement clickResetPassword = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//tr[contains(., '" + userLogin + "')]//button[@id='btnRestoreUserPassword']")));
        clickResetPassword.click();
    }

    public void clickNavButtonSpecialistMyAccount() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement navMyAccountSpecialist = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.linkText("Mano paskyra")));
        navMyAccountSpecialist.click();
    }


    public void clickDeleteUserButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement deleteUserButton = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("btnDeleteUser")));
        deleteUserButton.click();
    }

    public void clickNavButtonNewApplication() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement navNewApplication = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("navUserNewApplication")));
        navNewApplication.click();
    }

    public void clickDrpDnButtonRegistration() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement navButtonRegistration = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='dropdown-item' and contains(text(), 'registracijos')]")));
        navButtonRegistration.click();
    }

    public void clickDrpDnButtonCompensation() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement navButtonCompensation = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='dropdown-item' and contains(text(), 'kompensacijos')]")));
        navButtonCompensation.click();
    }


    // WAIT TO ENTER USER EMAIL WHILE RESETTING PASSWORD

    public void enterUserEmail(String value) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement enterUserEmail = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//div[3]/input")));
        enterUserEmail.sendKeys(value);
    }

}
