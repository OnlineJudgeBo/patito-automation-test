package bo.juezvirtual.automation.pages.admin;

import bo.juezvirtual.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object representing the administrative portal login view.
 * Mapped to selectors in juezvirtualbo-admin-front src/pages/LoginPage/LoginPage.jsx.
 */
public final class AdminLoginPage extends BasePage {
    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By submitButton = By.cssSelector("form button[type='submit']");
    private final By errorMessage = By.cssSelector("p.text-red-800");

    public AdminLoginPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Enters administrative credentials and submits.
     */
    public void login(String username, String password) {
        type(usernameField, username);
        type(passwordField, password);
        click(submitButton);
    }

    /**
     * Checks if the administrative validation error message is visible.
     */
    public boolean isErrorMessageVisible() {
        return isVisible(errorMessage);
    }

    /**
     * Reads the administrative validation error text.
     */
    public String getErrorMessageText() {
        return getText(errorMessage);
    }
}
