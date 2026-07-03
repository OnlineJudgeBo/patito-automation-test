package bo.juezvirtual.automation.steps;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.driver.DriverFactory;
import bo.juezvirtual.automation.pages.client.ProblemDetailPage;
import bo.juezvirtual.automation.pages.client.ProblemSetPage;
import bo.juezvirtual.automation.pages.client.SubmissionPage;
import bo.juezvirtual.automation.pages.client.StatusPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;

/**
 * Step definitions for public client code submission and evaluation monitoring.
 */
public final class SubmissionSteps {
    private final WebDriver driver = DriverFactory.getDriver();
    private final ProblemDetailPage problemDetailPage = new ProblemDetailPage(driver);
    private final ProblemSetPage problemSetPage = new ProblemSetPage(driver);
    private final SubmissionPage submissionPage = new SubmissionPage(driver);
    private final StatusPage statusPage = new StatusPage(driver);

    @Given("el participante esta en la pagina del problema con ID {string}")
    public void elParticipanteEstaEnLaPaginaDelProblemaConID(String id) {
        driver.get(BrowserConfig.getClientUrl() + "/problem.php?id=" + id);
    }

    @Given("el participante esta en la pagina del problema {string}")
    public void elParticipanteEstaEnLaPaginaDelProblema(String problemName) {
        driver.get(BrowserConfig.getClientUrl() + "/problemset.php");
        problemSetPage.clickProblemByName(problemName);
        Assertions.assertTrue(problemDetailPage.isStatementVisible(),
                "No se abrió el problema por nombre: " + problemName);
    }

    @When("hace clic en el boton Enviar")
    public void haceClicEnElBotonEnviar() {
        problemDetailPage.clickSubmit();
    }

    @When("selecciona el lenguaje {string} e ingresa el codigo:")
    public void seleccionaElLenguajeEIngresaElCodigo(String language, String code) {
        submissionPage.selectLanguage(language);
        submissionPage.submitCode(code);
    }

    @Then("deberia esperar la evaluacion y ver el veredicto {string}")
    public void deberiaEsperarLaEvaluacionYVerElVeredicto(String expectedVerdict) {
        statusPage.waitForSubmissionToFinishEvaluation();
        String actualVerdict = statusPage.getLastSolutionVerdict();
        Assertions.assertEquals(expectedVerdict, actualVerdict, "El veredicto final no coincide.");
    }
}
