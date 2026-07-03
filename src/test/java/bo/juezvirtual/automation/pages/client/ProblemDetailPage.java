package bo.juezvirtual.automation.pages.client;

import bo.juezvirtual.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object representing the public client problem details view.
 * Mapped to selectors in patito-client-web views/patito/problem.php.
 */
public final class ProblemDetailPage extends BasePage {
    private final By submitButton = By.xpath("//a[contains(@href, 'submitpage.php')]");
    private final By problemTitle = By.cssSelector("h2");
    private final By descriptionHeading = By.xpath("//h2[contains(normalize-space(.), 'Descripción')]");

    public ProblemDetailPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Clicks the "Enviar" (Submit) button to navigate to the code editor.
     */
    public void clickSubmit() {
        String href = waitUtils.waitForElementToBeClickable(submitButton).getAttribute("href");
        driver.get(href);
    }

    /**
     * Reads the visible problem title from the statement page.
     */
    public String getProblemTitle() {
        return getText(problemTitle);
    }

    /**
     * Checks if the problem statement title contains the expected name.
     */
    public boolean hasProblemTitle(String expectedTitle) {
        return getProblemTitle().contains(expectedTitle);
    }

    /**
     * Checks if the problem statement content is rendered.
     */
    public boolean isStatementVisible() {
        return isVisible(problemTitle) && isVisible(descriptionHeading);
    }

    /**
     * Checks if the submission action is available from the problem statement.
     */
    public boolean isSubmitAvailable() {
        return isVisible(submitButton);
    }
}
