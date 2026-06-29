package bo.juezvirtual.automation.pages.client;

import bo.juezvirtual.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object representing the public client problem details view.
 * Mapped to selectors in patito-client-web views/patito/problem.php.
 */
public final class ProblemDetailPage extends BasePage {
    private final By problemTitle = By.cssSelector("main h2.text-xl.font-bold");
    private final By submitButton = By.xpath("//a[contains(@href, 'submitpage.php')]");
    private final By statusButton = By.xpath("//a[contains(@href, 'problemstatus.php')]");

    public ProblemDetailPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Reads the title of the problem.
     */
    public String getProblemTitle() {
        return getText(problemTitle);
    }

    /**
     * Clicks the "Enviar" (Submit) button to navigate to the code editor.
     */
    public void clickSubmit() {
        click(submitButton);
    }

    /**
     * Clicks the "Estado" (Status) button to view solutions.
     */
    public void clickStatus() {
        click(statusButton);
    }
}
