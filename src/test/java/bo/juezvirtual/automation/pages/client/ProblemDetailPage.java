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

    public ProblemDetailPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Clicks the "Enviar" (Submit) button to navigate to the code editor.
     */
    public void clickSubmit() {
        click(submitButton);
    }

}
