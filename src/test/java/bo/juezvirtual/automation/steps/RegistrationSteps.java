package bo.juezvirtual.automation.steps;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.driver.DriverFactory;
import bo.juezvirtual.automation.pages.client.RegisterPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;

/**
 * Step definitions for public participant registration.
 */
public final class RegistrationSteps {
    private final WebDriver driver = DriverFactory.getDriver();
    private final RegisterPage clientRegisterPage = new RegisterPage(driver);

    @Given("el participante navega a la pagina de registro")
    public void elParticipanteNavegaALaPaginaDeRegistro() {
        driver.get(BrowserConfig.getClientUrl() + "/registerpage.php");
    }

    @When("completa el registro con nombre {string}, apellido {string}, nickname {string}, email {string}, clave {string}")
    public void completaElRegistroConNombreApellidoNicknameEmailYClave(String name, String lastname, String nickname, String email, String password) {
        // Generate a unique suffix using millisecond timestamp to prevent database duplicate key constraints
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(7);
        
        // Strip out all non-alphanumeric characters from nickname to satisfy the PHP validation (/^[a-zA-Z0-9]+$/)
        String uniqueNickname = (nickname + timestamp).replaceAll("[^a-zA-Z0-9]", "");
        String uniqueEmail = email.replace("@", timestamp + "@");

        clientRegisterPage.fillRegistrationForm(name, lastname, uniqueNickname, uniqueEmail, uniqueEmail, password, password);
        clientRegisterPage.clickRegister();
        bo.juezvirtual.automation.utils.SharedState.setLatestRegisteredNickname(uniqueNickname);
    }

    @Then("deberia ver la alerta SweetAlert de exito {string}")
    public void deberiaVerLaAlertaSweetAlertDeExito(String expectedSwalText) {
        Assertions.assertTrue(clientRegisterPage.isSwalContainerVisible(), "La alerta Swal no es visible.");
        clientRegisterPage.waitForSwalTitleTextToBe(expectedSwalText);
        Assertions.assertEquals(expectedSwalText, clientRegisterPage.getSwalTitleText());
        clientRegisterPage.waitForSwalContainerToDisappear();
    }
}
