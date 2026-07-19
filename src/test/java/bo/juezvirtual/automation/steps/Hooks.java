package bo.juezvirtual.automation.steps;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import bo.juezvirtual.automation.driver.DriverFactory;
import bo.juezvirtual.automation.utils.AdminContestCleaner;
import bo.juezvirtual.automation.utils.AdminProblemCleaner;
import bo.juezvirtual.automation.utils.ScreenshotUtils;
import bo.juezvirtual.automation.utils.SharedState;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

/**
 * Cucumber hooks that manage webdriver lifetime and screenshot capture.
 */
public final class Hooks {
    private WebDriver driver;
    private static boolean cleanedProblemsForContestFeature = false;

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
    public void cleanProblemsOnceForContestFeature() {
        if (cleanedProblemsForContestFeature) {
            return;
        }
    
        AdminProblemCleaner.deleteAllProblemsAsAdmin(driver);
        cleanedProblemsForContestFeature = true;
    }
    @After
    public void tearDown(Scenario scenario) {
        try {
            if (driver == null || !scenario.isFailed()) {
                return;
            }

            // 1. Save standard screenshot file in the filesystem
            ScreenshotUtils.takeScreenshot(driver, scenario.getName());

            // 2. Embed screenshot directly inside the Cucumber HTML Report
            try {
                byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshotBytes, "image/png", scenario.getName());
            } catch (Exception e) {
                System.err.println("Could not attach screenshot to Cucumber report: " + e.getMessage());
            }
        } finally {
            DriverFactory.quitDriver();
            driver = null;
        }
    }
}
