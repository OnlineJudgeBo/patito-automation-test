package bo.juezvirtual.automation.steps;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.driver.DriverFactory;
import bo.juezvirtual.automation.pages.admin.CreateContestPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;

/**
 * Step definitions for administrative contest management.
 */
public final class AdminContestSteps {
    private final WebDriver driver = DriverFactory.getDriver();
    private final CreateContestPage adminCreateContestPage = new CreateContestPage(driver);

    @Given("el administrador esta en la pagina de creacion de concursos")
    public void elAdministradorEstaEnLaPaginaDeCreacionDeConcursos() {
        driver.get(BrowserConfig.getAdminUrl() + "/contests/add");
    }

    @When("crea un concurso privado con titulo {string}, fecha {string}, hora {string}, problemas {string} y usuarios habilitados {string}")
    public void creaUnConcursoPrivadoConTituloFechaHoraProblemasYUsuariosHabilitados(
            String title, String date, String time, String problemsList, String usersList) {
        String resolvedDate = date;
        String resolvedTime = time;
        String resolvedEndDate = date;
        String resolvedEndTime = time;

        if ("actual".equalsIgnoreCase(date)) {
            resolvedDate = java.time.LocalDate.now().toString(); // Formato yyyy-MM-dd
            resolvedEndDate = resolvedDate;
        }
        if ("actual".equalsIgnoreCase(time)) {
            resolvedTime = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")); // Formato HH:mm
            resolvedEndTime = java.time.LocalTime.now().plusHours(2).format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        }

        String resolvedUsersList = "default_client".equalsIgnoreCase(usersList) ? BrowserConfig.getClientUsername() : usersList;

        adminCreateContestPage.fillContestDetails(
                title, true, resolvedDate, resolvedTime, resolvedEndDate, resolvedEndTime, problemsList, resolvedUsersList
        );
        adminCreateContestPage.clickSave();
    }

    @Then("el concurso deberia ser guardado con exito")
    public void elConcursoDeberiaSerGuardadoConExito() {
        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("/admin/contests"),
                "El concurso no fue guardado exitosamente: " + currentUrl);
    }
}
