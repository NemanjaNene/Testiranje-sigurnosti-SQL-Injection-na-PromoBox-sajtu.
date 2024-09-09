

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.Status;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import org.openqa.selenium.io.FileHandler;

    public class SigurnostSajta {

        WebDriver driver;
        ExtentReports extent;
        ExtentTest test;
        ExtentSparkReporter reporter;

        @BeforeTest
        public void setup() {
            driver = new ChromeDriver();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            // Set up ExtentReports
            String path = System.getProperty("user.dir") + "\\Reports1\\Promobox.html";
            reporter = new ExtentSparkReporter(path);
            extent = new ExtentReports();
            extent.attachReporter(reporter);
            extent.setSystemInfo("Tester", "Nemanja Nikitovic");
            reporter.config().setReportName("Rezultati Testiranja");
            reporter.config().setDocumentTitle("Web testiranje Prmobox Sajta");

            // Otvaranje sajta
            driver.get("https://promobox.com/en/");
        }

        @Test
        public void testSearchFunction() {
            test = extent.createTest("Test SQL Injection Sigurnosti na PromoBox sajtu");

            // Klik na lupu za pretragu (ako otvara polje za unos)
            driver.findElement(By.cssSelector(".promagnifier")).click();


            // Unos SQL injection koda u polje za pretragu
            driver.findElement(By.name("phrase")).sendKeys("' OR '1'='1");

            // Klik na dugme za pretragu
            driver.findElement(By.cssSelector(".promagnifier")).click();

            // Provera da li stranica reaguje na SQL injection
            String pageSource = driver.getPageSource();
            Assert.assertFalse(pageSource.contains("SQL syntax"), "Stranica je ranjiva na SQL Injection!");
        }

        @AfterMethod
        public void tearDown(ITestResult result) {
            if (result.getStatus() == ITestResult.FAILURE) {
                // Logovanje greške u izveštaj
                test.log(Status.FAIL, "Test nije uspeo: " + result.getThrowable());

                // Pravljenje screenshot-a
                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                try {
                    // Putanja gde će biti snimljen screenshot
                    FileHandler.copy(screenshot, new File("reports/screenshots/" + result.getName() + ".png"));
                    test.addScreenCaptureFromPath("screenshots/" + result.getName() + ".png"); // Dodavanje slike u izveštaj
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (result.getStatus() == ITestResult.SUCCESS) {
                test.log(Status.PASS, "Test je uspešno prošao.");
            }
        }

        @AfterTest
        public void close() {
            driver.quit(); // Zatvaranje browsera
            extent.flush(); // Generisanje HTML izveštaja
        }
    }

