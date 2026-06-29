package bo.juezvirtual.automation.tests;

import bo.juezvirtual.automation.driver.DriverFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;

/**
 * Base test class for traditional non-BDD JUnit 5 test cases.
 * Handles lifecycle initialization and cleanup of WebDriver.
 */
public abstract class BaseTest {
    protected WebDriver driver;

    @BeforeEach
    public void setUp() {
        driver = DriverFactory.getDriver();
    }

    @AfterEach
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}
