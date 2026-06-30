package bo.juezvirtual.automation.steps;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.driver.DriverFactory;
import bo.juezvirtual.automation.pages.client.LoginPage;
import bo.juezvirtual.automation.utils.UserDataManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Step definitions for public client authentication.
 */
public final class ClientLoginSteps {
    private final WebDriver driver = DriverFactory.getDriver();
    private final LoginPage clientLoginPage = new LoginPage(driver);

    @Given("el participante navega a la pagina de inicio de sesion")
    public void elParticipanteNavegaALaPaginaDeInicioDeSesion() {
        driver.get(BrowserConfig.getClientUrl() + "/login.php");
    }

    @When("el participante introduce el usuario {string} y clave {string}")
    public void elParticipanteIntroduceElUsuarioYClave(String user, String pass) {
        UserDataManager.UserCredentials credentials = UserDataManager.getLogin(user, pass);
        clientLoginPage.login(credentials.getUsername(), credentials.getPassword());
    }

    @When("el participante inicia sesion con sus credenciales por defecto")
    public void elParticipanteIniciaSesionConSusCredencialesPorDefecto() {
        clientLoginPage.login(BrowserConfig.getClientUsername(), BrowserConfig.getClientPassword());
    }

    @Then("el participante deberia ver el portal de inicio")
    public void elParticipanteDeberiaVerElPortalDeInicio() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login.php")));

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("index.php") || currentUrl.equals(BrowserConfig.getClientUrl() + "/"),
                "Redireccion incorrecta para el participante logueado: " + currentUrl);
    }

    @Then("el participante deberia ver el error de inicio de sesion {string}")
    public void elParticipanteDeberiaVerElErrorDeInicioDeSesion(String expectedMessage) {
        Assertions.assertTrue(clientLoginPage.isErrorMessageVisible(), "El mensaje de error no es visible.");
        Assertions.assertEquals(expectedMessage, clientLoginPage.getErrorMessageText());
    }
}
