package bo.juezvirtual.automation.pages.client;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object representing the public client submission status list view.
 * Mapped to selectors in patito-client-web views/patito/status.php.
 */
public final class StatusPage extends BasePage {
    private final By statusTable = By.id("status-table");
    private final By lastRowVerdict = By.xpath("//table[@id='status-table']/tbody/tr[1]/td[5]//div[contains(@class,'status-result')]");
    private final By lastRowRunId = By.xpath("//table[@id='status-table']/tbody/tr[1]/td[1]");

    public StatusPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Reads the RunID/SolutionID of the latest submission (first row).
     */
    public String getLastSolutionRunId() {
        waitUtils.waitForElementToBePresent(statusTable);
        return getText(lastRowRunId);
    }

    /**
     * Reads the verdict string (e.g. Accepted, Compile Error) of the latest submission.
     */
    public String getLastSolutionVerdict() {
        waitUtils.waitForElementToBePresent(statusTable);
        return getText(lastRowVerdict);
    }

    /**
     * Reads the verdict string for a specific RunID/SolutionID.
     */
    public String getVerdictByRunId(String runId) {
        waitUtils.waitForElementToBePresent(statusTable);
        return getText(verdictByRunId(runId));
    }

    /**
     * Waits for the evaluation of solutions to finish by refreshing the page and checking the latest verdict
     * until it transitions away from pending/compiling states.
     */
    public void waitForSubmissionToFinishEvaluation() {
        waitForSubmissionToFinishEvaluation(null);
    }

    /**
     * Waits for a specific solution to finish evaluation.
     */
    public void waitForSubmissionToFinishEvaluation(String runId) {
        int maxAttempts = BrowserConfig.getEvaluationMaxAttempts();
        for (int i = 0; i < maxAttempts; i++) {
            driver.navigate().refresh();
            waitUtils.waitForPageToLoad();
            waitUtils.waitForElementToBePresent(statusTable);
            
            boolean isPending = false;
            try {
                String text = runId == null ? getText(lastRowVerdict).trim() : getVerdictByRunId(runId).trim();
                if (text.equalsIgnoreCase("Pending") 
                        || text.equalsIgnoreCase("Compiling") 
                        || text.equalsIgnoreCase("Running & Judging") 
                        || text.equalsIgnoreCase("Pending Rejudging")
                        || text.equalsIgnoreCase("Queuing")
                        || text.contains("Pendiente") 
                        || text.contains("Compilando") 
                        || text.contains("Ejecutando")
                        || text.isEmpty()) {
                    isPending = true;
                }
            } catch (Exception e) {
                isPending = true; // Retry if element is stale or not present yet
            }
            
            if (!isPending) {
                return; // Finished evaluation!
            }
            
            try {
                Thread.sleep(BrowserConfig.getEvaluationDelayMs());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private By verdictByRunId(String runId) {
        return By.xpath("//table[@id='status-table']/tbody/tr[td[1][normalize-space(.) = '" + runId + "']]/td[5]"
                + "//*[contains(@class,'status-result') or self::td]");
    }
}
