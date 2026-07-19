package bo.juezvirtual.automation.steps;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private static final DateTimeFormatter FORM_TIME = DateTimeFormatter.ofPattern("HH:mm");
    private final WebDriver driver = DriverFactory.getDriver();
    private final CreateContestPage adminCreateContestPage = new CreateContestPage(driver);
    private final ProblemsPage adminProblemsPage = new ProblemsPage(driver);

    @Given("el administrador esta en la pagina de creacion de concursos")
    public void elAdministradorEstaEnLaPaginaDeCreacionDeConcursos() {
        driver.get(BrowserConfig.getAdminUrl() + "/contests/add");
    }

    @When("crea un concurso {string} con titulo {string}, fecha {string}, hora {string}, problemas {string} y los siguientes usuarios habilitados")
    public void creaUnConcursoConUsuariosHabilitados(
            String contestType, String title, String date, String time, String problemsList, DataTable usersTable) {
        List<String> users = usersTable.asMaps().stream()
                .map(row -> row.get("usuario_alias"))
                .collect(Collectors.toList());
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
        for (Map<String, String> expectedContest : table.asMaps(String.class, String.class)) {
            WebElement contestRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(
                    "//table/tbody/tr[td[2]//a[contains(normalize-space(.), '"
                            + expectedContest.get("Nombre") + "')]]")));
            String contestType = contestRow.findElement(By.xpath("./td[5]")).getText().trim();
            Assertions.assertEquals(expectedContest.get("Tipo"), contestType, "El tipo de concurso no coincide.");
        }
    }

    private void createContest(
            String title, String contestType, String date, String time, String problemsList, String usersList) {
        ContestSchedule schedule = calculateTime(date, time, LocalDateTime.now());
        String contestUsernames = usersList;
        boolean isPrivate = contestType.toLowerCase().contains("priv");
        if (isPrivate) {
            String firstUser = usersList.split("\\R")[0].trim();
            if (!firstUser.isEmpty()) {
                SharedState.setContestParticipantAlias(firstUser);
            }
        }

        String contestProblemIds = resolveProblemList(problemsList);
        driver.get(BrowserConfig.getAdminUrl() + "/contests/add");
        adminCreateContestPage.fillContestDetails(
                title, isPrivate, schedule.startDate(), schedule.startTime(), schedule.endDate(), schedule.endTime(),
                contestProblemIds, contestUsernames);
        adminCreateContestPage.clickSave();
        rememberContestTitle(title, isPrivate);
    }

    private String resolveProblemList(String problemsList) {
        StringBuilder contestProblemIds = new StringBuilder();
        for (String problem : problemsList.split("[\r\n,]+")) {
            String problemTitleOrId = problem.trim();
            if (problemTitleOrId.isEmpty()) {
                continue;
            }
            if (problemTitleOrId.matches("\\d+")) {
                contestProblemIds.append(problemTitleOrId);
            } else {
                driver.get(BrowserConfig.getAdminUrl() + "/problems");
                contestProblemIds.append(adminProblemsPage.getProblemIdByTitle(problemTitleOrId));
            }
            contestProblemIds.append(System.lineSeparator());
        }
        return contestProblemIds.toString().trim();
    }

    private void rememberContestTitle(String title, boolean isPrivate) {
        if (isPrivate) {
            SharedState.setLatestPrivateContestTitle(title);
        } else {
            SharedState.setLatestPublicContestTitle(title);
        }
    }

    static ContestSchedule calculateTime(String date, String time, LocalDateTime now) {
        LocalDate contestDate = "actual".equalsIgnoreCase(date) ? now.toLocalDate() : LocalDate.parse(date);
        if (!"actual".equalsIgnoreCase(time)) {
            return new ContestSchedule(contestDate.toString(), time, contestDate.toString(), time);
        }

        LocalDateTime reference = LocalDateTime.of(contestDate, now.toLocalTime());
        LocalDateTime startsAt = reference.minusMinutes(1);
        LocalDateTime endsAt = reference.plusHours(2);
        return new ContestSchedule(
                startsAt.toLocalDate().toString(),
                startsAt.toLocalTime().format(FORM_TIME),
                endsAt.toLocalDate().toString(),
                endsAt.toLocalTime().format(FORM_TIME));
    }

    record ContestSchedule(String startDate, String startTime, String endDate, String endTime) {
    }
}
