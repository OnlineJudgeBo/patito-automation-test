package bo.juezvirtual.automation.pages.admin;

import java.time.Duration;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page Object representing the administrative portal login view.
 * Mapped to selectors in juezvirtualbo-admin-front src/pages/LoginPage/LoginPage.jsx.
 */
public final class AdminLoginPage extends BasePage {
    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By submitButton = By.cssSelector("form button[type='submit']");
    private final By errorMessage = By.cssSelector("p.text-red-800");
    private final By authenticatedDashboardMarker = By.xpath(
            "//*[normalize-space(.) = 'Panel de Control' or normalize-space(.) = 'Cerrar Sesión']");

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
        waitForLoginRequestToFinish();
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

    private void waitForLoginRequestToFinish() {
        waitUtils.waitUntil(
                Duration.ofSeconds(Math.max(BrowserConfig.getExplicitWait(), 20)),
                Duration.ofMillis(200),
                webDriver -> isLoggedIn(webDriver)
                        || webDriver.findElements(errorMessage).stream().anyMatch(WebElement::isDisplayed));
    }

    private boolean isLoggedIn(WebDriver webDriver) {
        return webDriver.manage().getCookieNamed("accessToken") != null
                || hasStoredAdminSession(webDriver)
                || !webDriver.getCurrentUrl().contains("/login")
                || webDriver.findElements(authenticatedDashboardMarker).stream().anyMatch(WebElement::isDisplayed);
    }

    private boolean hasStoredAdminSession(WebDriver webDriver) {
        Object token = ((JavascriptExecutor) webDriver).executeScript(
                "return window.localStorage.getItem('accessToken')"
                        + " || window.localStorage.getItem('token')"
                        + " || window.localStorage.getItem('user_id')"
                        + " || window.sessionStorage.getItem('accessToken')"
                        + " || window.sessionStorage.getItem('token');");
        return token != null && !String.valueOf(token).isBlank();
    }
}
