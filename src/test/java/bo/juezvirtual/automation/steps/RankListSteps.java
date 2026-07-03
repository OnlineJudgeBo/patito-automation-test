package bo.juezvirtual.automation.steps;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.driver.DriverFactory;
import bo.juezvirtual.automation.pages.client.ProblemSetPage;
import bo.juezvirtual.automation.pages.client.RankListPage;
import bo.juezvirtual.automation.pages.client.ContestPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;

/**
 * Step definitions for client catalogs, scoreboards, and contest views.
 */
public final class RankListSteps {
    private final WebDriver driver = DriverFactory.getDriver();
    private final ProblemSetPage problemSetPage = new ProblemSetPage(driver);
    private final RankListPage rankListPage = new RankListPage(driver);
    private final ContestPage clientContestPage = new ContestPage(driver);

    @Given("el participante navega al catalogo de problemas")
    public void elParticipanteNavegaAlCatalogoDeProblemas() {
        driver.get(BrowserConfig.getClientUrl() + "/problemset.php");
    }

    @When("busca el problema {string}")
    public void buscaElProblema(String problemName) {
        problemSetPage.searchProblem(problemName);
    }

    @Then("deberia ver el problema {string} en la lista")
    public void deberiaVerElProblemaEnLaLista(String problemName) {
        Assertions.assertTrue(problemSetPage.isProblemListed(problemName), "El problema no aparece en el listado.");
    }

    @Given("el participante navega a la tabla de clasificacion global")
    public void elParticipanteNavegaALaTablaDeClasificacionGlobal() {
        driver.get(BrowserConfig.getClientUrl() + "/ranklist.php");
    }

    @When("busca el usuario {string} en el ranking")
    public void buscaElUsuarioEnElRanking(String userId) {
        bo.juezvirtual.automation.utils.UserDataManager.UserCredentials creds = bo.juezvirtual.automation.utils.UserDataManager.getUser(userId);
        String resolvedUser = creds != null ? creds.getUsername() : ("default_client".equalsIgnoreCase(userId) ? BrowserConfig.getClientUsername() : userId);
        rankListPage.searchUser(resolvedUser);
    }

    @Then("el usuario {string} deberia estar listado en la clasificacion")
    public void elUsuarioDeberiaEstarListadoEnLaClasificacion(String userId) {
        bo.juezvirtual.automation.utils.UserDataManager.UserCredentials creds = bo.juezvirtual.automation.utils.UserDataManager.getUser(userId);
        String resolvedUser = creds != null ? creds.getUsername() : ("default_client".equalsIgnoreCase(userId) ? BrowserConfig.getClientUsername() : userId);
        String solved = rankListPage.getUserSolvedCount(resolvedUser);
        Assertions.assertNotNull(solved, "El usuario no aparece listado en la clasificacion.");
    }

    @Given("el participante navega a la lista de concursos")
    public void elParticipanteNavegaALaListaDeConcursos() {
        driver.get(BrowserConfig.getClientUrl() + "/contest.php");
    }

    @Then("deberia ver el concurso con ID {string} con tipo de acceso {string}")
    public void deberiaVerElConcursoConIDConTipoDeAcceso(String contestId, String expectedAccessType) {
        Assertions.assertTrue(clientContestPage.isContestVisible(contestId), "El concurso no esta visible en la lista.");
        String actualAccessType = clientContestPage.getContestAccessType(contestId);
        Assertions.assertTrue(actualAccessType.contains(expectedAccessType), "El tipo de acceso no coincide.");
    }
}
