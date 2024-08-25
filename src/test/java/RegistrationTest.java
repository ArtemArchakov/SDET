import io.qameta.allure.Attachment;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;

public class RegistrationTest {

    static WebDriver driver;
    static WebDriverWait wait;

    @BeforeAll
    public static void setUp() {
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-site-isolation-trials");
        options.addArguments("--allow-running-insecure-content");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://demoqa.com/automation-practice-form");
    }

    @Test
    @Description("Test for registration form submission")
    public void registrationTest() {
        fillForm();
        submitForm();
        validateResults();
    }

    @Step("Fill out the registration form")
    private void fillForm() {
        WebElement firstNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("firstName")));
        firstNameField.sendKeys("John");

        WebElement lastNameField = driver.findElement(By.id("lastName"));
        lastNameField.sendKeys("Wick");

        WebElement emailField = driver.findElement(By.id("userEmail"));
        emailField.sendKeys("john.wick@example.com");

        WebElement maleRadioLabel = driver.findElement(By.cssSelector("label[for='gender-radio-1']"));
        maleRadioLabel.click();

        WebElement mobileField = driver.findElement(By.id("userNumber"));
        mobileField.sendKeys("9214502773");

        WebElement dateOfBirthInput = driver.findElement(By.id("dateOfBirthInput"));
        dateOfBirthInput.click();

        Select yearSelect = new Select(driver.findElement(By.className("react-datepicker__year-select")));
        yearSelect.selectByVisibleText("1998");

        Select monthSelect = new Select(driver.findElement(By.className("react-datepicker__month-select")));
        monthSelect.selectByVisibleText("June");

        WebElement day = driver.findElement(By.xpath("//div[contains(@class, 'react-datepicker__day') and text()='29']"));
        day.click();

        WebElement subjectsInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("subjectsInput")));
        subjectsInput.click();
        subjectsInput.sendKeys("Eco");
        subjectsInput.sendKeys(Keys.ENTER);

        String filePath = new File("src/main/resources/Toolsqa.jpg").getAbsolutePath();
        WebElement uploadPicture = driver.findElement(By.id("uploadPicture"));
        uploadPicture.sendKeys(filePath);

        WebElement currentAddress = driver.findElement(By.id("currentAddress"));
        currentAddress.sendKeys("123 Test Street, Test City");


        WebDriverWait wait = new WebDriverWait(driver, 15);

        try {
            // Wait for the element to be clickable
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(text(), 'Select State')]")));

            // Scroll to the element
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView(true);", element);

            // Click the element using Actions
            Actions actions = new Actions(driver);
            actions.moveToElement(element).click().perform();

        } catch (Exception e) {
            e.printStackTrace();
        }
        WebElement stateOption = driver.findElement(By.xpath("//div[contains(text(), 'NCR')]"));
        stateOption.click();

        WebElement cityDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(text(), 'Select City')]")));
        cityDropdown.click();
        WebElement cityOption = driver.findElement(By.xpath("//div[contains(text(), 'Delhi')]"));
        cityOption.click();
    }


    @Step("Submit the registration form")
    private void submitForm() {
        WebElement submitButton = driver.findElement(By.id("submit"));
        submitButton.click();
    }

    @Step("Validate the registration results")
    private void validateResults() {
        WebElement popup = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal-body")));

        WebElement popupTitle = driver.findElement(By.className("modal-title"));
        Assertions.assertEquals(popupTitle.getText(), "Thanks for submitting the form");

        WebElement table = driver.findElement(By.className("table-responsive"));
        String tableContent = table.getText();
        System.out.println("Содержание таблицы: " + tableContent);

        Assertions.assertTrue(tableContent.contains("John Wick"), "Student Name не найдено");
        Assertions.assertTrue(tableContent.contains("john.wick@example.com"), "Student Email не найдено");
        Assertions.assertTrue(tableContent.contains("Male"), "Gender не найдено");
        Assertions.assertTrue(tableContent.contains("9214502773"), "Mobile не найдено");
        Assertions.assertTrue(tableContent.contains("29 June,1998"), "Date of Birth не найдено");
        Assertions.assertTrue(tableContent.contains("Economics"), "Subjects не найдено");
        Assertions.assertTrue(tableContent.contains("Toolsqa.jpg"), "Picture не найдено");
        Assertions.assertTrue(tableContent.contains("123 Test Street, Test City"), "Address не найдено");
        Assertions.assertTrue(tableContent.contains("NCR Delhi"), "State and City не найдено");

        // Take a screenshot after validation
        takeScreenshot();
    }


    @Attachment(value = "Screenshot", type = "image/png")
    public byte[] takeScreenshot() {
        TakesScreenshot ts = (TakesScreenshot) driver;
        return ts.getScreenshotAs(OutputType.BYTES);
    }

    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
