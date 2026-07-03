package bo.juezvirtual.automation.pages.client;

import bo.juezvirtual.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

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
        try {
            select.selectByVisibleText(languageName);
            return;
        } catch (Exception ignored) {
            selectByPartialText(select, languageName);
        }
    }

    private void selectByPartialText(Select select, String languageName) {
        List<WebElement> options = select.getOptions();
        for (WebElement option : options) {
            if (option.getText().toLowerCase().contains(languageName.toLowerCase())) {
                select.selectByVisibleText(option.getText());
                return;
            }
        }
        throw new IllegalArgumentException("No se encontró el lenguaje: " + languageName);
    }

    /**
     * Writes code into Monaco Editor using JavaScript injection.
     * This is the recommended approach for Monaco Editor since it doesn't support regular textarea sendKeys.
     */
    public void enterCode(String code) {
        // Inject code value directly into Monaco Editor instance
        executeJavaScript("window.editor.setValue(arguments[0]);", code);
    }

    /**
     * Submits the current Monaco Editor code.
     */
    public void clickSubmit() {
        executeJavaScript("do_submit();");
    }

    /**
     * Writes code into Monaco Editor and submits it.
     */
    public void submitCode(String code) {
        enterCode(code);
        clickSubmit();
    }
}
