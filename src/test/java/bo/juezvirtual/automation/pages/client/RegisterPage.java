package bo.juezvirtual.automation.pages.client;

import bo.juezvirtual.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object representing the public client registration page.
 * Mapped to selectors in patito-client-web views/patito/register.php.
 */
public final class RegisterPage extends BasePage {
    private final By nameField = By.id("name");
    private final By lastnameField = By.id("lastname");
    private final By nicknameField = By.id("nickname");
    private final By emailField = By.id("email");
    private final By email2Field = By.id("email2");
    private final By passwordField = By.id("password");
    private final By password2Field = By.id("password2");
    private final By submitButton = By.cssSelector("form#registrationForm button[type='submit']");
    
    // SweetAlert2 elements
    private final By swalContainer = By.cssSelector("div.swal2-container");
    private final By swalTitle = By.id("swal2-title");

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Fills the participant registration form.
     */
    public void fillRegistrationForm(String name, String lastname, String nickname, 
                                     String email, String emailConfirm, String password, String passwordConfirm) {
        type(nameField, name);
        type(lastnameField, lastname);
        type(nicknameField, nickname);
        type(emailField, email);
        type(email2Field, emailConfirm);
        type(passwordField, password);
        type(password2Field, passwordConfirm);
    }

    /**
     * Submits the registration form.
     */
    public void clickRegister() {
        click(submitButton);
    }

    /**
     * Checks if the SweetAlert2 popup is visible.
     */
    public boolean isSwalContainerVisible() {
        return isVisible(swalContainer);
    }

    /**
     * Reads the title of the SweetAlert2 popup.
     */
    public String getSwalTitleText() {
        return getText(swalTitle);
    }

    /**
     * Waits for the SweetAlert2 title to change to the expected text.
     */
    public void waitForSwalTitleTextToBe(String text) {
        waitUtils.waitForTextToBePresentInElement(swalTitle, text);
    }

    /**
     * Waits for the SweetAlert2 dialog to disappear.
     */
    public void waitForSwalContainerToDisappear() {
        waitUtils.waitForElementToBeInvisible(swalContainer);
    }
}
