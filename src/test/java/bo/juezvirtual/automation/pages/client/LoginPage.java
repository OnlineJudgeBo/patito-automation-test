package bo.juezvirtual.automation.pages.client;

import java.time.Duration;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page Object representing the public client login view.
 * Mapped to selectors in patito-client-web views/patito/login.php.
 */
public final class LoginPage extends BasePage {
    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By submitButton = By.cssSelector("form button[type='submit']");
    private final By errorMessage = By.cssSelector("p.text-red-500");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Fills credentials and clicks the submit button.
     */
    public void login(String username, String password) {
        type(usernameField, username);
        type(passwordField, password);
        click(submitButton);
        waitForLoginRequestToFinish();
    }

    /**
     * Checks if the validation error banner is visible.
     */
    public boolean isErrorMessageVisible() {
        return isVisible(errorMessage);
    }

    /**
     * Reads the validation error message text.
     */
    public String getErrorMessageText() {
        return getText(errorMessage);
    }

    /**
     * Checks whether the login form is currently available.
     */
    public boolean isLoginFormVisible() {
        return isVisible(usernameField);
    }

    private void waitForLoginRequestToFinish() {
        waitUtils.waitUntil(
                Duration.ofSeconds(BrowserConfig.getExplicitWait()),
                Duration.ofMillis(200),
                webDriver -> isLoggedIn(webDriver)
                        || webDriver.findElements(errorMessage).stream().anyMatch(WebElement::isDisplayed));
    }

    private boolean isLoggedIn(WebDriver webDriver) {
        return !webDriver.getCurrentUrl().contains("login.php");
    }
}
