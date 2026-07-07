package bo.juezvirtual.automation.steps;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.driver.DriverFactory;
import bo.juezvirtual.automation.pages.admin.FileManagerPage;
import bo.juezvirtual.automation.pages.admin.ProblemsPage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for administrative problem management and file uploads.
 */
public final class AdminProblemSteps {
    private final WebDriver driver = DriverFactory.getDriver();
    private final ProblemsPage adminProblemsPage = new ProblemsPage(driver);
    private final FileManagerPage adminFileManagerPage = new FileManagerPage(driver);
    private final List<String> recentlyCreatedProblemTitles = new ArrayList<>();

    @Given("el docente esta en la pagina de creacion de problemas")
    public void elDocenteEstaEnLaPaginaDeCreacionDeProblemas() {
        driver.get(BrowserConfig.getAdminUrl() + "/problems/add");
    }

    @When("crea un problema con titulo {string}, tiempo {string}, memoria {string}, autor {string} y descripciones basicas")
    public void creaUnProblemaConTituloTiempoMemoriaAutorYDescripcionesBasicas(
            String title, String time, String memory, String author) {
        createProblem(title, time, memory, author);
    }

    @When("crea los siguientes problemas:")
    public void creaLosSiguientesProblemas(DataTable table) {
        recentlyCreatedProblemTitles.clear();
        for (Map<String, String> problem : table.asMaps(String.class, String.class)) {
            createProblem(
                    problem.get("Titulo"),
                    problem.get("Tiempo"),
                    problem.get("Memoria"),
                    problem.get("Autor")
            );
            elProblemaDeberiaSerGuardadoConExito();
            recentlyCreatedProblemTitles.add(problem.get("Titulo"));
            uploadDefaultContestTestCases(problem.get("Titulo"));
            driver.get(BrowserConfig.getAdminUrl() + "/problems/add");
        }
    }

    @Then("el problema deberia ser guardado con exito")
    public void elProblemaDeberiaSerGuardadoConExito() {
        assertProblemListRedirect();
    }

    @Then("los problemas deberian ser guardados con exito")
    public void losProblemasDeberianSerGuardadosConExito() {
        Assertions.assertFalse(recentlyCreatedProblemTitles.isEmpty(),
                "No se registraron problemas creados en este escenario.");
    }

    @Then("el problema {string} deberia aparecer listado")
    public void elProblemaDeberiaAparecerListado(String title) {
        driver.get(BrowserConfig.getAdminUrl() + "/problems");
        Assertions.assertTrue(adminProblemsPage.isProblemListed(title),
                "El problema no aparece en la lista administrativa: " + title);
    }

    @Then("los siguientes problemas deberian aparecer listados:")
    public void losSiguientesProblemasDeberianAparecerListados(DataTable table) {
        for (Map<String, String> expected : table.asMaps(String.class, String.class)) {
            elProblemaDeberiaAparecerListado(expected.get("Titulo"));
        }
    }

    @Then("el problema con ID {string} y titulo {string} deberia aparecer listado")
    public void elProblemaConIdYTituloDeberiaAparecerListado(String problemId, String title) {
        driver.get(BrowserConfig.getAdminUrl() + "/problems");
        Assertions.assertTrue(adminProblemsPage.isProblemListed(problemId, title),
                "El problema no aparece en la lista administrativa: " + problemId + " - " + title);
    }


    @Given("el docente esta en el administrador de archivos del problema con ID {string}")
    public void elDocenteEstaEnElAdministradorDeArchivosDelProblemaConID(String problemId) {
        driver.get(BrowserConfig.getAdminUrl() + "/fileManager/" + problemId);
    }

    @Given("el docente esta en el administrador de archivos del problema {string}")
    public void elDocenteEstaEnElAdministradorDeArchivosDelProblema(String title) {
        driver.get(BrowserConfig.getAdminUrl() + "/problems");
        String problemId = adminProblemsPage.getProblemIdByTitle(title);
        driver.get(BrowserConfig.getAdminUrl() + "/fileManager/" + problemId);
    }

    @When("sube el archivo de casos de prueba {string}")
    public void subeElArchivoDeCasosDePruebaConRutaAbsoluta(String filePath) {
        String resolvedPath = Paths.get(filePath).toAbsolutePath().toString();
        adminFileManagerPage.uploadFile(resolvedPath);
    }

    @Then("el archivo {string} deberia aparecer listado en la pagina")
    public void elArchivoDeberiaAparecerListadoEnLaPagina(String fileName) {
        Assertions.assertTrue(adminFileManagerPage.isFileUploaded(fileName), "El archivo no se subio correctamente.");
    }

    private void createProblem(String title, String time, String memory, String author) {
        adminProblemsPage.fillProblemDetails(
                title, time, memory,
                "<p>Descripcion del problema</p>",
                "<p>Descripcion de la entrada del problema</p>",
                "<p>Descripcion de la salida del problema</p>",
                sampleInputFor(title), sampleOutputFor(title), author
        );
        adminProblemsPage.clickSave();
    }

    private void assertProblemListRedirect() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.urlContains("/admin/problems"));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/add")));

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("/admin/problems") && !currentUrl.contains("/add"),
                "El docente no fue redirigido a la lista de problemas: " + currentUrl);
    }

    private void uploadDefaultContestTestCases(String title) {
        String baseName = testCaseBaseNameFor(title);
        elDocenteEstaEnElAdministradorDeArchivosDelProblema(title);
        deleteDefaultTestCasesForOtherProblems(baseName);
        subeElArchivoDeCasosDePruebaConRutaAbsoluta("src/test/resources/testcases/" + baseName + "_1.in");
        elArchivoDeberiaAparecerListadoEnLaPagina(baseName + "_1.in");
        subeElArchivoDeCasosDePruebaConRutaAbsoluta("src/test/resources/testcases/" + baseName + "_1.out");
        elArchivoDeberiaAparecerListadoEnLaPagina(baseName + "_1.out");
        assertOnlyExpectedDefaultContestTestCasesAreListed(baseName);
    }

    private void deleteDefaultTestCasesForOtherProblems(String expectedBaseName) {
        for (String baseName : List.of("sum", "numero_par")) {
            if (baseName.equals(expectedBaseName)) {
                continue;
            }
            adminFileManagerPage.deleteFileIfPresent(baseName + "_1.in");
            adminFileManagerPage.deleteFileIfPresent(baseName + "_1.out");
        }
    }

    private void assertOnlyExpectedDefaultContestTestCasesAreListed(String expectedBaseName) {
        for (String baseName : List.of("sum", "numero_par")) {
            boolean shouldExist = baseName.equals(expectedBaseName);
            Assertions.assertEquals(shouldExist, adminFileManagerPage.isFileUploaded(baseName + "_1.in"),
                    "El archivo .in listado no corresponde al problema: " + baseName + "_1.in");
            Assertions.assertEquals(shouldExist, adminFileManagerPage.isFileUploaded(baseName + "_1.out"),
                    "El archivo .out listado no corresponde al problema: " + baseName + "_1.out");
        }
    }

    private String testCaseBaseNameFor(String title) {
        if (title != null && title.toLowerCase().contains("par")) {
            return "numero_par";
        }
        return "sum";
    }

    private String sampleInputFor(String title) {
        if (title != null && title.toLowerCase().contains("par")) {
            return "4";
        }
        return "2 3";
    }

    private String sampleOutputFor(String title) {
        if (title != null && title.toLowerCase().contains("par")) {
            return "SI";
        }
        return "5";
    }
}
