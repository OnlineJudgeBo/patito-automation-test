package bo.juezvirtual.automation.steps;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.driver.DriverFactory;
import bo.juezvirtual.automation.pages.admin.AdminLoginPage;
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
 * Step definitions for administrative portal authentication.
 */
public final class AdminLoginSteps {
    private final WebDriver driver = DriverFactory.getDriver();
    private final AdminLoginPage adminLoginPage = new AdminLoginPage(driver);

    @Given("el administrador navega a la pagina de login del panel")
    public void elAdministradorNavegaALaPaginaDeLoginDelPanel() {
        driver.get(BrowserConfig.getAdminUrl() + "/login");
    }

    @When("el administrador introduce el usuario {string} y clave {string}")
    public void elAdministradorIntroduceElUsuarioYClave(String user, String pass) {
        UserDataManager.UserCredentials credentials = UserDataManager.getLogin(user, pass);
        adminLoginPage.login(credentials.getUsername(), credentials.getPassword());
    }

    @When("el administrador inicia sesion con sus credenciales por defecto")
    public void elAdministradorIniciaSesionConSusCredencialesPorDefecto() {
        adminLoginPage.login(BrowserConfig.getAdminUsername(), BrowserConfig.getAdminPassword());
    }

    @Then("el administrador deberia ver el dashboard administrativo")
    public void elAdministradorDeberiaVerElDashboardAdministrativo() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("/admin") && !currentUrl.contains("login"),
                "Redireccion incorrecta para el administrador logueado: " + currentUrl);
    }

    @Then("el administrador deberia ver el error de inicio de sesion {string}")
    public void elAdministradorDeberiaVerElErrorDeInicioDeSesion(String expectedMessage) {
        Assertions.assertTrue(adminLoginPage.isErrorMessageVisible(), "El mensaje de error no es visible.");
        Assertions.assertEquals(expectedMessage, adminLoginPage.getErrorMessageText());
    }
}
