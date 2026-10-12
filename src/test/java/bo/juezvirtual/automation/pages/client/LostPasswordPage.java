package bo.juezvirtual.automation.pages.client;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import bo.juezvirtual.automation.pages.BasePage;

/**
 * Page Object for the password recovery request form of Patito Client.
 */
public final class LostPasswordPage extends BasePage {
    private final By emailField = By.id("email");
    private final By submitButton = By.cssSelector("form#lostpassword button[type='submit']");
    private final By swalMessage = By.id("swal2-html-container");
    private final By swalConfirmButton = By.cssSelector("button.swal2-confirm");

    public LostPasswordPage(WebDriver driver) {
        super(driver);
    }

    public void requestRecovery(String email) {
        type(emailField, email);
        click(submitButton);
    }

    public String getNoticeText() {
        return getText(swalMessage);
    }

    public void confirmNotice() {
        click(swalConfirmButton);
    }
}
