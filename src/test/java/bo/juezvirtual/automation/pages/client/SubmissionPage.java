package bo.juezvirtual.automation.pages.client;

import bo.juezvirtual.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object representing the public client submission page.
 * Mapped to selectors in patito-client-web views/patito/submitpage.php.
 */
public final class SubmissionPage extends BasePage {
    private final By languageSelect = By.id("language");
    private final By submitButton = By.id("Submit");

    public SubmissionPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Selects the programming language from the dropdown.
     */
    public void selectLanguage(String languageName) {
        Select select = new Select(waitUtils.waitForElementToBeVisible(languageSelect));
        select.selectByVisibleText(languageName);
    }

    /**
     * Automates the Monaco Editor using JavaScript injection and submits the code.
     * This is the recommended approach for Monaco Editor since it doesn't support regular textarea sendKeys.
     */
    public void submitCode(String code) {
        // Inject code value directly into Monaco Editor instance
        executeJavaScript("window.editor.setValue(arguments[0]);", code);
        // Click the native submit button which triggers the do_submit() serialization and form POST
        click(submitButton);
    }
}
