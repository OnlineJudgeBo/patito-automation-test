package bo.juezvirtual.automation.steps;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.driver.DriverFactory;
import bo.juezvirtual.automation.pages.client.LostPasswordPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for the password recovery request of Patito Client.
 */
public final class PasswordRecoverySteps {
    private final WebDriver driver = DriverFactory.getDriver();
    private final LostPasswordPage lostPasswordPage = new LostPasswordPage(driver);

    @Given("el participante navega a la pagina de recuperacion de clave")
    public void elParticipanteNavegaALaPaginaDeRecuperacionDeClave() {
        driver.get(BrowserConfig.getClientUrl() + "/lostpassword.php");
    }

    @When("solicita recuperar la clave del correo {string}")
    public void solicitaRecuperarLaClaveDelCorreo(String email) {
        lostPasswordPage.requestRecovery(email);
    }

    @Then("deberia ver el aviso de recuperacion {string}")
    public void deberiaVerElAvisoDeRecuperacion(String expectedNotice) {
        Assertions.assertEquals(expectedNotice, lostPasswordPage.getNoticeText());
    }

    @When("confirma el aviso de recuperacion")
    public void confirmaElAvisoDeRecuperacion() {
        lostPasswordPage.confirmNotice();
    }

    @Then("deberia volver a la pagina de inicio de sesion")
    public void deberiaVolverALaPaginaDeInicioDeSesion() {
        new WebDriverWait(driver, Duration.ofSeconds(BrowserConfig.getExplicitWait()))
                .until(ExpectedConditions.urlContains("login.php"));
    }
}
