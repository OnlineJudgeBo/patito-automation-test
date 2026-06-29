package bo.juezvirtual.automation.steps;

import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.driver.DriverFactory;
import bo.juezvirtual.automation.pages.admin.FileManagerPage;
import bo.juezvirtual.automation.pages.admin.ProblemsPage;
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

    @Given("el docente esta en la pagina de creacion de problemas")
    public void elDocenteEstaEnLaPaginaDeCreacionDeProblemas() {
        driver.get(BrowserConfig.getAdminUrl() + "/problems/add");
    }

    @When("crea un problema con titulo {string}, tiempo {string}, memoria {string}, autor {string} y descripciones basicas")
    public void creaUnProblemaConTituloTiempoMemoriaAutorYDescripcionesBasicas(
            String title, String time, String memory, String author) {
        adminProblemsPage.fillProblemDetails(
                title, time, memory,
                "<p>Descripcion del problema</p>", 
                "<p>Descripcion de la entrada</p>", 
                "<p>Descripcion de la salida</p>",
                "sample input data", "sample output data", author
        );
        adminProblemsPage.clickSave();
    }

    @Then("el problema deberia ser guardado con exito")
    public void elProblemaDeberiaSerGuardadoConExito() {
        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("/admin/problems"),
                "El docente no fue redirigido a la lista de problemas: " + currentUrl);
    }

    @Given("el docente esta en el administrador de archivos del problema con ID {string}")
    public void elDocenteEstaEnElAdministradorDeArchivosDelProblemaConID(String problemId) {
        driver.get(BrowserConfig.getAdminUrl() + "/fileManager/" + problemId);
    }

    @When("sube el archivo de casos de prueba con ruta absoluta {string}")
    public void subeElArchivoDeCasosDePruebaConRutaAbsoluta(String filePath) {
        String resolvedPath = Paths.get(filePath).toAbsolutePath().toString();
        adminFileManagerPage.uploadFile(resolvedPath);
    }

    @Then("el archivo {string} deberia aparecer listado en la pagina")
    public void elArchivoDeberiaAparecerListadoEnLaPagina(String fileName) {
        Assertions.assertTrue(adminFileManagerPage.isFileUploaded(fileName), "El archivo no se subio correctamente.");
    }
}
