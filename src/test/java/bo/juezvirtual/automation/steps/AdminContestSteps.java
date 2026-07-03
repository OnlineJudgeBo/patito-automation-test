package bo.juezvirtual.automation.steps;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.driver.DriverFactory;
import bo.juezvirtual.automation.pages.admin.CreateContestPage;
import bo.juezvirtual.automation.pages.admin.ProblemsPage;
import bo.juezvirtual.automation.utils.SharedState;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Steps for administrative contest management.
 */
public final class AdminContestSteps {
    private final WebDriver driver = DriverFactory.getDriver();
    private final CreateContestPage adminCreateContestPage = new CreateContestPage(driver);
    private final ProblemsPage adminProblemsPage = new ProblemsPage(driver);

    @Given("el administrador esta en la pagina de creacion de concursos")
    public void elAdministradorEstaEnLaPaginaDeCreacionDeConcursos() {
        driver.get(BrowserConfig.getAdminUrl() + "/contests/add");
    }

    @When("crea un concurso privado con titulo {string}, fecha {string}, hora {string}, problemas {string} y usuarios habilitados {string}")
    public void creaUnConcursoPrivadoConTituloFechaHoraProblemasYUsuariosHabilitados(
            String title, String date, String time, String problemsList, String usersList) {
        createContest(title, "privado", date, time, problemsList, usersList);
    }

    @When("crea un concurso {string} con titulo {string}, fecha {string}, hora {string}, problemas {string} y los siguientes usuarios habilitados")
    public void creaUnConcursoConUsuariosHabilitados(
            String contestType, String title, String date, String time, String problemsList, DataTable usersTable) {
        List<String> users = usersTable.asList();
        if (!users.isEmpty() && "Usuarios".equalsIgnoreCase(users.get(0))) {
            users = users.subList(1, users.size());
        }
        createContest(title, contestType, date, time, problemsList, String.join("\n", users));
    }

    @When("crea un concurso {string} con titulo {string}, fecha {string}, hora {string}, problemas {string}")
    public void creaUnConcursoSinUsuarios(
            String contestType, String title, String date, String time, String problemsList) {
        createContest(title, contestType, date, time, problemsList, "");
    }

    @Then("el concurso deberia ser guardado con exito")
    public void elConcursoDeberiaSerGuardadoConExito() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.urlContains("/admin/contests"));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/add")));

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("/admin/contests") && !currentUrl.contains("/add"),
                "El concurso no fue guardado exitosamente: " + currentUrl);
    }

    @Then("el concurso creado debe estar en la lista de concursos con los siguientes datos:")
    public void elConcursoCreadoDebeEstarEnLaListaDeConcursosConLosSiguientesDatos(DataTable table) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        for (Map<String, String> expected : table.asMaps(String.class, String.class)) {
            WebElement row = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                    "//table/tbody/tr[td[2]//a[contains(normalize-space(.), '" + expected.get("Nombre") + "')]]")));
            String actualType = row.findElement(By.xpath("./td[5]")).getText().trim();
            Assertions.assertEquals(expected.get("Tipo"), actualType, "El tipo de concurso no coincide.");
        }
    }

    private void createContest(
            String title, String contestType, String date, String time, String problemsList, String usersList) {
        String resolvedDate = resolveDate(date);
        String resolvedTime = resolveStartTime(time);
        String resolvedEndTime = resolveEndTime(time);
        String resolvedUsersList = "default_client".equalsIgnoreCase(usersList)
                ? BrowserConfig.getClientUsername()
                : usersList;
        boolean isPrivate = contestType.toLowerCase().contains("priv");

        String resolvedProblemsList = resolveProblemList(problemsList);
        driver.get(BrowserConfig.getAdminUrl() + "/contests/add");
        adminCreateContestPage.fillContestDetails(
                title, isPrivate, resolvedDate, resolvedTime, resolvedDate, resolvedEndTime, resolvedProblemsList,
                resolvedUsersList);
        adminCreateContestPage.clickSave();
        rememberContestTitle(title, isPrivate);
    }

    private String resolveProblemList(String problemsList) {
        StringBuilder resolved = new StringBuilder();
        for (String problem : problemsList.split("[\r\n,]+")) {
            String value = problem.trim();
            if (value.isEmpty()) {
                continue;
            }
            if (value.matches("\\d+")) {
                resolved.append(value);
            } else {
                driver.get(BrowserConfig.getAdminUrl() + "/problems");
                resolved.append(adminProblemsPage.getProblemIdByTitle(value));
            }
            resolved.append(System.lineSeparator());
        }
        return resolved.toString().trim();
    }

    private void rememberContestTitle(String title, boolean isPrivate) {
        if (isPrivate) {
            SharedState.setLatestPrivateContestTitle(title);
        } else {
            SharedState.setLatestPublicContestTitle(title);
        }
    }

    private String resolveDate(String date) {
        if ("actual".equalsIgnoreCase(date)) {
            return LocalDate.now().toString();
        }
        return date;
    }

    private String resolveStartTime(String time) {
        if ("actual".equalsIgnoreCase(time)) {
            return LocalTime.now()
                    .minusMinutes(1)
                    .format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        return time;
    }

    private String resolveEndTime(String time) {
        if ("actual".equalsIgnoreCase(time)) {
            return LocalTime.now()
                    .plusHours(2)
                    .format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        return time;
    }
}
