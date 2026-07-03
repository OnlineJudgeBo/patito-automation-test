package bo.juezvirtual.automation.steps;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.driver.DriverFactory;
import bo.juezvirtual.automation.pages.client.ContestPage;
import bo.juezvirtual.automation.pages.client.LoginPage;
import bo.juezvirtual.automation.pages.client.ProblemDetailPage;
import bo.juezvirtual.automation.pages.client.RankListPage;
import bo.juezvirtual.automation.pages.client.StatusPage;
import bo.juezvirtual.automation.pages.client.SubmissionPage;
import bo.juezvirtual.automation.utils.UserDataManager;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Steps for participant contest flows.
 */
public final class ContestSteps {
    private final WebDriver driver = DriverFactory.getDriver();
    private final ContestPage contestPage = new ContestPage(driver);
    private final LoginPage loginPage = new LoginPage(driver);
    private final ProblemDetailPage problemDetailPage = new ProblemDetailPage(driver);
    private final RankListPage rankListPage = new RankListPage(driver);
    private final SubmissionPage submissionPage = new SubmissionPage(driver);
    private final StatusPage statusPage = new StatusPage(driver);

    private static final String REGISTERED_CONTEST_PARTICIPANT_ALIAS = "participante_qa";

    private String contestAccessType = "publico";
    private String currentContestId;
    private String currentParticipantAlias = REGISTERED_CONTEST_PARTICIPANT_ALIAS;
    private boolean participantSessionStarted;
    private final List<SubmissionResult> latestSubmissionResults = new ArrayList<>();

    @Given("existe un contest privado activo")
    public void existeUnContestPrivadoActivo() {
        contestAccessType = "privado";
        driver.get(BrowserConfig.getClientUrl() + "/contest.php");
        currentContestId = contestPage.getFirstContestIdByAccess(contestAccessType);
        Assertions.assertFalse(currentContestId.isBlank(), "No se encontró un contest privado activo/listado.");
    }

    @Given("existe un contest público activo")
    public void existeUnContestPublicoActivo() {
        contestAccessType = "publico";
        driver.get(BrowserConfig.getClientUrl() + "/contest.php");
        currentContestId = contestPage.getFirstContestIdByAccess(contestAccessType);
        Assertions.assertFalse(currentContestId.isBlank(), "No se encontró un contest público activo/listado.");
    }

    @Given("existe un participante registrado en el contest")
    public void existeUnParticipanteRegistradoEnElContest() {
        contestAccessType = "privado";
        currentContestId = null;
        ensureContestSelected();
        Assertions.assertNotNull(currentContestId,
                "Debe existir un contest seleccionado antes de validar el participante registrado.");
        UserDataManager.UserCredentials credentials = UserDataManager.getUser(currentParticipantAlias);
        Assertions.assertNotNull(credentials,
                "No se encontraron credenciales para el participante registrado en el contest: "
                        + currentParticipantAlias);
        Assertions.assertFalse(credentials.getUsername().isBlank(),
                "El participante registrado en el contest no tiene usuario configurado.");
    }

    @Given("el participante inició sesion correctamente")
    @Given("el participante inició sesión correctamente")
    @Given("el participante inició sesión con la cuenta registrada en el contest")
    public void elParticipanteInicioSesionCorrectamente() {
        if (isParticipantAlreadyAuthenticated() || waitForParticipantAuthentication()) {
            participantSessionStarted = true;
            return;
        }
        loginAsCurrentContestParticipant();
    }

    @When("el participante ingresa al contest privado")
    public void elParticipanteIngresaAlContestPrivado() {
        contestAccessType = "privado";
        openSelectedContest();
    }

    @When("el participante ingresa al contest público")
    public void elParticipanteIngresaAlContestPublico() {
        contestAccessType = "publico";
        openSelectedContest();
    }

    @Given("el participante está en la lista de problemas del contest")
    public void elParticipanteEstaEnLaListaDeProblemasDelContest() {
        openSelectedContest();
        Assertions.assertTrue(contestPage.hasProblemRows(), "El contest no muestra problemas disponibles.");
    }

    @Given("el participante abrió un problema del contest")
    public void elParticipanteAbrioUnProblemaDelContest() {
        elParticipanteEstaEnLaListaDeProblemasDelContest();
        contestPage.openFirstProblem();
        Assertions.assertTrue(problemDetailPage.isStatementVisible(), "No se abrió el enunciado del problema.");
    }

    @Given("el participante abrió el problema {string} del contest y verifica el nombre {string}")
    public void elParticipanteAbrioElProblemaDelContestYVerificaElNombre(String code, String expectedTitle) {
        elParticipanteEstaEnLaListaDeProblemasDelContest();
        contestPage.openProblemByCode(code);
        Assertions.assertTrue(problemDetailPage.isStatementVisible(), "No se abrió el enunciado del problema " + code);
        Assertions.assertTrue(problemDetailPage.hasProblemTitle(expectedTitle),
                "El problema " + code + " no corresponde a " + expectedTitle
                        + ". Título actual: " + problemDetailPage.getProblemTitle());
    }

    @When("el participante abre los siguientes problemas del contest y verifica sus nombres:")
    public void elParticipanteAbreLosSiguientesProblemasDelContestYVerificaSusNombres(DataTable table) {
        for (Map<String, String> expected : table.asMaps(String.class, String.class)) {
            elParticipanteAbrioElProblemaDelContestYVerificaElNombre(
                    expected.get("Identificador"),
                    expected.get("Nombre")
            );
        }
        openSelectedContest();
    }

    @Given("el participante abrió el ranking")
    public void elParticipanteAbrioElRanking() {
        ensureContestSelected();
        ensurePrivateContestParticipantSession();
        contestPage.openContestRank(currentContestId);
    }

    @When("abre un problema disponible")
    public void abreUnProblemaDisponible() {
        contestPage.openFirstProblem();
    }

    @When("selecciona un lenguaje")
    public void seleccionaUnLenguaje() {
        problemDetailPage.clickSubmit();
        submissionPage.selectLanguage("Python3");
    }

    @When("carga un código válido")
    public void cargaUnCodigoValido() {
        submissionPage.enterCode("""
                a, b = map(int, input().split())
                print(a + b)
                """);
    }

    @When("envía la solución")
    public void enviaLaSolucion() {
        submissionPage.clickSubmit();
        Assertions.assertTrue(driver.getCurrentUrl().contains("status.php"),
                "La solución no quedó en la página de estado: " + driver.getCurrentUrl());
    }

    @When("el participante envía las siguientes soluciones:")
    public void elParticipanteEnviaLasSiguientesSoluciones(DataTable table) {
        latestSubmissionResults.clear();
        for (Map<String, String> solution : table.asMaps(String.class, String.class)) {
            submitContestSolution(solution);
        }
    }

    @When("selecciona el ranking")
    public void seleccionaElRanking() {
        ensureContestSelected();
        ensurePrivateContestParticipantSession();
        contestPage.openContestRank(currentContestId);
    }

    @Then("debe ver la lista de problemas disponibles")
    public void debeVerLaListaDeProblemasDisponibles() {
        Assertions.assertTrue(contestPage.hasProblemRows(), "No se muestra una lista de problemas del contest.");
    }

    @Then("cada problema debe mostrar su titulo")
    public void cadaProblemaDebeMostrarSuTitulo() {
        Assertions.assertTrue(contestPage.allProblemsHaveTitle(), "Hay problemas sin título visible.");
    }

    @Then("cada problema debe mostrar su identificador")
    public void cadaProblemaDebeMostrarSuIdentificador() {
        Assertions.assertTrue(contestPage.allProblemsHaveIdentifier(), "Hay problemas sin identificador visible.");
    }


    @Then("debe verse el envío en la clasificación diaria:")
    public void debeVerseElEnvioEnLaClasificacionDiaria(DataTable table) {
        driver.get(BrowserConfig.getClientUrl() + "/ranklist.php?scope=d");
        Assertions.assertTrue(driver.getCurrentUrl().contains("scope=d"),
                "La verificación debe realizarse desde la clasificación diaria: " + driver.getCurrentUrl());

        for (Map<String, String> expected : table.asMaps(String.class, String.class)) {
            String participantUsername = getParticipantUsername(expected.get("Usuario"));
            rankListPage.searchUser(participantUsername);
            int minimumSolved = Integer.parseInt(expected.get("Resueltos"));
            int solved = rankListPage.isUserListed(participantUsername)
                    ? Integer.parseInt(rankListPage.getUserSolvedCount(participantUsername))
                    : 0;
            Assertions.assertTrue(solved >= minimumSolved,
                    "El envío no se refleja en la clasificación diaria para " + participantUsername
                            + ". Esperado mínimo: " + minimumSolved + ", actual: " + solved);
        }
    }

    @Then("debe mostrar los siguientes datos del contest:")
    public void debeMostrarLosSiguientesDatosDelContest(DataTable table) {
        openSelectedContest();
        for (Map<String, String> expected : table.asMaps(String.class, String.class)) {
            ContestPage.ContestProblemRow actual = contestPage.getProblemRowByName(expected.get("Nombre"));
            assertIfPresent(expected, "Problema", actual.code(), "El identificador del problema no coincide.");
            assertIfPresent(expected, "Setter", actual.setter(), "El setter no coincide.");
            assertAtLeastIfPresent(expected, "Aceptados", actual.accepted(), "La cantidad de aceptados no coincide.");
            assertAtLeastIfPresent(expected, "Envios", actual.submissions(), "La cantidad de envíos no coincide.");
        }
    }

    @Then("debe ver el enunciado del problema")
    public void debeVerElEnunciadoDelProblema() {
        Assertions.assertTrue(problemDetailPage.isStatementVisible(), "El enunciado del problema no es visible.");
    }

    @Then("debe ver las opciones para enviar una solución")
    public void debeVerLasOpcionesParaEnviarUnaSolucion() {
        Assertions.assertTrue(problemDetailPage.isSubmitAvailable(), "La opción de envío no está disponible.");
    }

    @Then("el sistema debe registrar el envío")
    public void elSistemaDebeRegistrarElEnvio() {
        Assertions.assertFalse(statusPage.getLastSolutionRunId().isBlank(), "No se registró un RunID para el envío.");
        statusPage.waitForSubmissionToFinishEvaluation();
        Assertions.assertFalse(statusPage.getLastSolutionVerdict().isBlank(), "No se muestra el estado del envío.");
    }

    @Then("debe mostrar el estado del envío")
    public void debeMostrarElEstadoDelEnvio() {
        Assertions.assertFalse(statusPage.getLastSolutionVerdict().isBlank(), "No se muestra el estado del envío.");
    }

    @Then("los envios deben tener los siguientes estados en la pagina de status:")
    public void losEnviosDebenTenerLosSiguientesEstadosEnLaPaginaDeStatus(DataTable table) {
        List<SubmissionResult> pendingSubmissionResults = new ArrayList<>(latestSubmissionResults);
        for (Map<String, String> expectedSubmission : table.asMaps(String.class, String.class)) {
            String problemCode = expectedSubmission.get("Problema");
            String expectedVerdict = expectedSubmission.get("Estado");
            if (isRepeatedHeaderRow(problemCode, expectedVerdict)) {
                continue;
            }
            SubmissionResult submittedResult = findAndRemoveExpectedResult(
                    pendingSubmissionResults, expectedSubmission);
            Assertions.assertEquals(expectedVerdict.trim(), submittedResult.verdict().trim(),
                    "El veredicto del problema " + problemCode + " no coincide. Esperado: "
                            + expectedVerdict + ", actual: " + submittedResult.verdict()
                            + ", RunID: " + submittedResult.runId());
        }
    }

    @Then("se debe registrar la cantidad de problemas correctos y la cantidad de fallos:")
    public void seDebeRegistrarLaCantidadDeProblemasCorrectosYLaCantidadDeFallos(DataTable table) {
        for (Map<String, String> expected : table.asMaps(String.class, String.class)) {
            waitForRankRowToMatch(expected);
        }
    }

    private void openSelectedContest() {
        ensurePrivateContestParticipantSession();
        ensureContestSelected();
        driver.get(BrowserConfig.getClientUrl() + "/contest.php?cid=" + currentContestId);
    }

    private void ensurePrivateContestParticipantSession() {
        if ("privado".equals(contestAccessType) && !participantSessionStarted) {
            loginAsCurrentContestParticipant();
        }
    }

    private void loginAsCurrentContestParticipant() {
        driver.get(BrowserConfig.getClientUrl() + "/login.php");
        if (waitForParticipantAuthentication()) {
            participantSessionStarted = true;
            return;
        }
        Assertions.assertTrue(loginPage.isLoginFormVisible(),
                "No se encontró el formulario de login del participante en: " + driver.getCurrentUrl());
        UserDataManager.UserCredentials credentials = UserDataManager.getLogin(
                currentParticipantAlias, currentParticipantAlias);
        loginPage.login(credentials.getUsername(), credentials.getPassword());
        Assertions.assertFalse(driver.getCurrentUrl().contains("login.php"),
                "El participante registrado en el contest no pudo iniciar sesión: "
                        + credentials.getUsername());
        participantSessionStarted = true;
    }

    private boolean isParticipantAlreadyAuthenticated() {
        String currentUrl = driver.getCurrentUrl();
        return currentUrl != null
                && currentUrl.startsWith(BrowserConfig.getClientUrl())
                && loginPage.isAuthenticated();
    }

    private boolean waitForParticipantAuthentication() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(BrowserConfig.getExplicitWait())).until(webDriver ->
                    isParticipantAlreadyAuthenticated() || !webDriver.findElements(By.id("username")).isEmpty());
            return isParticipantAlreadyAuthenticated();
        } catch (Exception e) {
            return isParticipantAlreadyAuthenticated();
        }
    }

    private void ensureContestSelected() {
        if (currentContestId == null || currentContestId.isBlank()) {
            driver.get(BrowserConfig.getClientUrl() + "/contest.php");
            currentContestId = contestPage.getFirstContestIdByAccess(contestAccessType);
        }
    }

    private String getParticipantUsername(String aliasOrUsername) {
        UserDataManager.UserCredentials credentials = UserDataManager.getUser(aliasOrUsername);
        return credentials == null ? aliasOrUsername : credentials.getUsername();
    }

    private void assertIfPresent(Map<String, String> expected, String key, String actual, String message) {
        String expectedValue = expected.get(key);
        if (expectedValue != null && !expectedValue.trim().isEmpty()) {
            Assertions.assertEquals(expectedValue.replace(" ", "").trim(), actual.replace(" ", "").trim(), message);
        }
    }

    private void assertAtLeastIfPresent(Map<String, String> expected, String key, String actual, String message) {
        String expectedValue = expected.get(key);
        if (expectedValue == null || expectedValue.trim().isEmpty()) {
            return;
        }
        int minimum = Integer.parseInt(expectedValue.trim());
        int current = Integer.parseInt(actual.trim());
        Assertions.assertTrue(current >= minimum, message + " Esperado mínimo: " + minimum + ", actual: " + current);
    }

    private void assertProblemResult(String problemCode, String expectedValue, String actualValue) {
        if (expectedValue == null || expectedValue.trim().isEmpty()) {
            return;
        }
        if ("true".equalsIgnoreCase(expectedValue)) {
            Assertions.assertFalse(actualValue.isEmpty(),
                    "El problema " + problemCode + " no está marcado como resuelto.");
            Assertions.assertFalse(actualValue.contains("-"),
                    "El problema " + problemCode + " está marcado como fallo: " + actualValue);
        } else if ("false".equalsIgnoreCase(expectedValue)) {
            Assertions.assertTrue(actualValue.isEmpty() || actualValue.contains("-"),
                    "El problema " + problemCode + " está marcado como resuelto pero se esperaba fallo: "
                            + actualValue);
        } else {
            Assertions.assertEquals(expectedValue.trim(), actualValue.trim(),
                    "El resultado del problema " + problemCode + " no coincide.");
        }
    }

    private void waitForRankRowToMatch(Map<String, String> expected) {
        AssertionError lastFailure = null;
        int maxAttempts = BrowserConfig.getEvaluationMaxAttempts();
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                ContestPage.ContestRankRow rankRow =
                        contestPage.getRankRowByUser(getParticipantUsername(expected.get("USUARIO")));
                assertIfPresent(expected, "#", rankRow.rank(), "El rango en la clasificación no coincide.");
                assertIfPresent(expected, "NOMBRE", rankRow.name(), "El nombre del usuario no coincide.");
                assertAtLeastIfPresent(expected, "RESUELTOS", rankRow.solved(),
                        "La cantidad de problemas resueltos no coincide.");
                assertProblemResults(expected, rankRow);
                return;
            } catch (AssertionError error) {
                lastFailure = error;
                sleepBeforeRankRetry();
                driver.navigate().refresh();
            }
        }
        throw lastFailure == null ? new AssertionError("No se pudo leer la clasificación del contest.") : lastFailure;
    }

    private void assertProblemResults(Map<String, String> expected, ContestPage.ContestRankRow rankRow) {
        for (Map.Entry<String, String> entry : expected.entrySet()) {
            String problemCode = entry.getKey();
            if (isRankMetadataColumn(problemCode)) {
                continue;
            }
            assertProblemResult(problemCode, entry.getValue(), rankRow.problemResult(problemCode));
        }
    }

    private boolean isRankMetadataColumn(String columnName) {
        return "#".equals(columnName)
                || "NOMBRE".equalsIgnoreCase(columnName)
                || "USUARIO".equalsIgnoreCase(columnName)
                || "RESUELTOS".equalsIgnoreCase(columnName);
    }

    private void sleepBeforeRankRetry() {
        try {
            Thread.sleep(BrowserConfig.getEvaluationDelayMs());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void submitContestSolution(Map<String, String> solution) {
        String problemCode = solution.get("Problema");
        elParticipanteAbrioElProblemaDelContestYVerificaElNombre(problemCode, solution.get("Nombre problema"));
        problemDetailPage.clickSubmit();
        ensureSubmitPageHasContestId();
        submissionPage.selectLanguage(solution.get("Lenguaje"));
        submissionPage.submitCode(readSolutionFixture(solution.get("Codigo")));
        Assertions.assertTrue(driver.getCurrentUrl().contains("status.php"),
                "La solución no quedó en la página de estado: " + driver.getCurrentUrl());
        String runId = statusPage.getLastSolutionRunId();
        Assertions.assertFalse(runId.isBlank(), "No se registró RunID para el envío del problema " + problemCode);
        statusPage.waitForSubmissionToFinishEvaluation(runId);
        String verdict = statusPage.getVerdictByRunId(runId);
        latestSubmissionResults.add(new SubmissionResult(problemCode, runId, verdict));
    }

    private SubmissionResult findAndRemoveExpectedResult(
            List<SubmissionResult> pendingSubmissionResults, Map<String, String> expectedSubmission) {
        String problemCode = expectedSubmission.get("Problema");
        for (int i = 0; i < pendingSubmissionResults.size(); i++) {
            SubmissionResult submittedResult = pendingSubmissionResults.get(i);
            if (submittedResult.problemCode().equals(problemCode)) {
                return pendingSubmissionResults.remove(i);
            }
        }
        Assertions.fail("No se registró envío para el problema " + problemCode);
        throw new IllegalStateException("Unreachable assertion path");
    }

    private boolean isRepeatedHeaderRow(String problemCode, String expectedVerdict) {
        return "Problema".equalsIgnoreCase(String.valueOf(problemCode).trim())
                && "Estado".equalsIgnoreCase(String.valueOf(expectedVerdict).trim());
    }

    private void ensureSubmitPageHasContestId() {
        ensureContestSelected();
        String currentUrl = driver.getCurrentUrl();
        if (currentContestId == null || currentContestId.isBlank() || currentUrl.contains("cid=")) {
            return;
        }
        String problemId = extractQueryParam(currentUrl, "pid");
        if (problemId.isBlank()) {
            String separator = currentUrl.contains("?") ? "&" : "?";
            driver.get(currentUrl + separator + "cid=" + currentContestId);
            return;
        }
        driver.get(BrowserConfig.getClientUrl() + "/submitpage.php?cid=" + currentContestId + "&pid=" + problemId);
    }

    private String extractQueryParam(String url, String paramName) {
        String marker = paramName + "=";
        int start = url.indexOf(marker);
        if (start < 0) {
            return "";
        }
        int valueStart = start + marker.length();
        int valueEnd = url.indexOf('&', valueStart);
        return valueEnd < 0 ? url.substring(valueStart) : url.substring(valueStart, valueEnd);
    }

    private String readSolutionFixture(String fileName) {
        String resourcePath = "testdata/solutions/" + fileName;
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (stream != null) {
                return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el fixture de solución: " + resourcePath, e);
        }

        Path path = Paths.get("src/test/resources").resolve(resourcePath);
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el fixture de solución: " + path, e);
        }
    }

    private record SubmissionResult(String problemCode, String runId, String verdict) {
        private SubmissionResult {
            problemCode = problemCode == null ? "" : problemCode.trim();
            runId = runId == null ? "" : runId.trim();
            verdict = verdict == null ? "" : verdict.trim();
        }
    }
}
