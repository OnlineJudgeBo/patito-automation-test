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
    private final By pendingStatus = By.className("pending");
    private final By lastRowVerdict = By.xpath("//table[@id='status-table']/tbody/tr[1]/td[5]//div[contains(@class,'status-result')]");
    private final By lastRowRunId = By.xpath("//table[@id='status-table']/tbody/tr[1]/td[1]");

    public StatusPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Reads the RunID/SolutionID of the latest submission (first row).
     */
    public String getLastSolutionRunId() {
        return getText(lastRowRunId);
    }

    /**
     * Reads the verdict string (e.g. Accepted, Compile Error) of the latest submission.
     */
    public String getLastSolutionVerdict() {
        return getText(lastRowVerdict);
    }

    /**
     * Waits for the evaluation of solutions to finish by refreshing the page and checking the latest verdict
     * until it transitions away from pending/compiling states.
     */
    public void waitForSubmissionToFinishEvaluation() {
        int maxAttempts = BrowserConfig.getEvaluationMaxAttempts();
        for (int i = 0; i < maxAttempts; i++) {
            driver.navigate().refresh();
            waitUtils.waitForPageToLoad();
            
            boolean isPending = false;
            try {
                String text = getText(lastRowVerdict).trim();
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
}
