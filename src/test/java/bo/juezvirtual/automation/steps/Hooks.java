package bo.juezvirtual.automation.steps;

import bo.juezvirtual.automation.driver.DriverFactory;
import bo.juezvirtual.automation.utils.AdminContestCleaner;
import bo.juezvirtual.automation.utils.AdminProblemCleaner;
import bo.juezvirtual.automation.utils.SharedState;
import bo.juezvirtual.automation.utils.ScreenshotUtils;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Cucumber hooks that manage webdriver lifetime and screenshot capture.
 */
public final class Hooks {
    private WebDriver driver;

    @Before(order = 0)
    public void setUp() {
        SharedState.clearContestParticipantAlias();
        driver = DriverFactory.getDriver();
    }

    @Before(value = "@clean_contest", order = 1)
    public void finishRunningContestsBeforeCleanContestScenarios() {
        AdminContestCleaner.finishRunningContestsAsAdmin(driver);
        SharedState.clearContestTitles();
    }

    @Before(value = "@clean_problems", order = 2)
    public void deleteProblemsBeforeCleanProblemsScenarios() {
        AdminProblemCleaner.deleteAllProblemsAsAdmin(driver);
    }

    @After
    public void tearDown(Scenario scenario) {
        if (driver != null) {
            if (scenario.isFailed()) {
                // 1. Save standard screenshot file in the filesystem
                ScreenshotUtils.takeScreenshot(driver, scenario.getName());
                
                // 2. Embed screenshot directly inside the Cucumber HTML Report
                try {
                    byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    scenario.attach(screenshotBytes, "image/png", scenario.getName());
                } catch (Exception e) {
                    System.err.println("Could not attach screenshot to Cucumber report: " + e.getMessage());
                }
            }
            DriverFactory.quitDriver();
        }
    }
}
