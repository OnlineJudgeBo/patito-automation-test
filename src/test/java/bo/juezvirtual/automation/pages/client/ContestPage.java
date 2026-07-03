package bo.juezvirtual.automation.pages.client;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.pages.BasePage;
import bo.juezvirtual.automation.utils.SharedState;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Page Object representing the public client contests list view.
 * Mapped to selectors in patito-client-web views/patito/contest.php.
 */
public final class ContestPage extends BasePage {
    private static final Pattern CID_PATTERN = Pattern.compile("[?&]cid=([0-9]+)");
    private static final Pattern PID_PATTERN = Pattern.compile("[?&]pid=([0-9]+)");
    private static final int PROBLEM_CODE_COLUMN = 2;
    private static final int PROBLEM_NAME_COLUMN = 3;
    private static final int PROBLEM_SETTER_COLUMN = 4;
    private static final int PROBLEM_ACCEPTED_COLUMN = 5;
    private static final int PROBLEM_SUBMIT_COLUMN = 6;

    public ContestPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Checks if a contest is listed.
     */
    public boolean isContestVisible(String contestId) {
        By contestLink = By.xpath("//a[contains(@href, 'contest.php?cid=" + contestId + "')]");
        return isVisible(contestLink);
    }

    /**
     * Retrieves the access type string (e.g. Public/Private) for a contest.
     */
    public String getContestAccessType(String contestId) {
        By accessBadge = By.xpath("//tr[td/span[contains(text(), '#" + contestId + "')]]/td[4]/span");
        return getText(accessBadge);
    }

    /**
     * Returns the first visible contest id for the requested access label (Público/Privado).
     */
    public String getFirstContestIdByAccess(String accessType) {
        WebElement link = getFirstContestLinkByAccess(accessType);
        String href = link.getAttribute("href");
        Matcher matcher = CID_PATTERN.matcher(href);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalStateException("No se pudo extraer cid desde href: " + href);
    }

    /**
     * Opens the first listed contest matching the requested access label.
     */
    public String openFirstContestByAccess(String accessType) {
        WebElement link = getFirstContestLinkByAccess(accessType);
        String href = link.getAttribute("href");
        Matcher matcher = CID_PATTERN.matcher(href);
        String contestId = matcher.find() ? matcher.group(1) : "";
        link.click();
        return contestId;
    }

    /**
     * Verifies that the current contest page contains at least one problem row.
     */
    public boolean hasProblemRows() {
        return !driver.findElements(problemLinks()).isEmpty();
    }

    /**
     * Checks that every listed problem has a non-empty title.
     */
    public boolean allProblemsHaveTitle() {
        List<WebElement> links = driver.findElements(problemLinks());
        return !links.isEmpty() && links.stream().allMatch(link -> !link.getText().trim().isEmpty());
    }

    /**
     * Checks that every listed problem has a visible identifier/letter and a pid in its URL.
     */
    public boolean allProblemsHaveIdentifier() {
        List<WebElement> rows = driver.findElements(By.xpath("//table/tbody/tr[.//a[contains(@href, 'problem.php?cid=')]]"));
        if (rows.isEmpty()) {
            return false;
        }
        return rows.stream().allMatch(row -> {
            String identifier = row.findElement(By.xpath("./td[2]")).getText().trim();
            String href = row.findElement(By.xpath(".//a[contains(@href, 'problem.php?cid=')]")).getAttribute("href");
            return !identifier.isEmpty() && PID_PATTERN.matcher(href).find();
        });
    }

    /**
     * Opens a contest problem by its contest letter/code (for example A, B, C).
     */
    public String openProblemByCode(String code) {
        String normalizedCode = code.trim();
        WebElement link = waitUtils.waitForElementToBeClickable(By.xpath(
                "//table/tbody/tr[td[2][normalize-space(.) = " + xpathText(normalizedCode)
                        + " or substring(normalize-space(.), string-length(normalize-space(.))) = "
                        + xpathText(normalizedCode) + "]]"
                        + "//a[contains(@href, 'problem.php?cid=')]"
        ));
        String href = link.getAttribute("href");
        Matcher matcher = PID_PATTERN.matcher(href);
        String problemId = matcher.find() ? matcher.group(1) : "";
        driver.get(href);
        return problemId;
    }

    /**
     * Opens the first problem listed in the current contest and returns its pid.
     */
    public String openFirstProblem() {
        WebElement link = waitUtils.waitForElementToBeClickable(problemLinks());
        String href = link.getAttribute("href");
        Matcher matcher = PID_PATTERN.matcher(href);
        String problemId = matcher.find() ? matcher.group(1) : "";
        driver.get(href);
        return problemId;
    }

    /**
     * Opens the ranking for the current contest.
     */
    public void openContestRank(String contestId) {
        driver.get(BrowserConfig.getClientUrl() + "/contestrank.php?cid=" + contestId);
    }

    /**
     * Checks if the contest ranking table exposes solved/fail information.
     */
    public boolean hasRankSolvedAndFailureColumns() {
        boolean hasSolved = isVisible(By.xpath("//th[contains(normalize-space(.), 'RESUELTOS')]"));
        boolean hasFailureOrProblemColumns = isVisible(By.xpath("//td[contains(., '(-') or contains(., '*')]"))
                || !driver.findElements(By.xpath("//thead/tr/th[position() > 4]")).isEmpty();
        return hasSolved && hasFailureOrProblemColumns;
    }

    /**
     * Reads the problem row for the given title in the contest problem list.
     */
    public ContestProblemRow getProblemRowByName(String problemName) {
        WebElement row = waitUtils.waitForElementToBeVisible(By.xpath(
                "//table/tbody/tr[td//*[contains(normalize-space(.), " + xpathText(problemName) + ")]]"
        ));

        return new ContestProblemRow(
                cellText(row, PROBLEM_CODE_COLUMN).replace(" ", ""),
                cellText(row, PROBLEM_NAME_COLUMN),
                cellText(row, PROBLEM_SETTER_COLUMN),
                cellText(row, PROBLEM_ACCEPTED_COLUMN),
                cellText(row, PROBLEM_SUBMIT_COLUMN)
        );
    }

    /**
     * Reads the ranking row for the given user id.
     */
    public ContestRankRow getRankRowByUser(String userId) {
        WebElement row = waitUtils.waitForElementToBeVisible(By.xpath(
                "//table/tbody/tr[td[3][contains(normalize-space(.), " + xpathText(userId) + ")]]"
        ));
        Map<String, String> problemResults = new LinkedHashMap<>();
        List<WebElement> headers = driver.findElements(By.xpath("//thead/tr/th"));
        for (int columnIndex = 5; columnIndex <= headers.size(); columnIndex++) {
            String problemCode = headers.get(columnIndex - 1).getText().trim();
            if (!problemCode.isEmpty()) {
                problemResults.put(problemCode, cellText(row, columnIndex));
            }
        }

        return new ContestRankRow(
                cellText(row, 1),
                cellText(row, 2),
                cellText(row, 3),
                cellText(row, 4),
                problemResults
        );
    }

    private WebElement getFirstContestLinkByAccess(String accessType) {
        String normalizedAccess = normalizeAccess(accessType);
        String latestTitle = null;
        if ("Privado".equalsIgnoreCase(normalizedAccess)) {
            latestTitle = SharedState.getLatestPrivateContestTitle();
        } else if ("Público".equalsIgnoreCase(normalizedAccess)) {
            latestTitle = SharedState.getLatestPublicContestTitle();
        }

        if (latestTitle != null && !latestTitle.isEmpty()) {
            try {
                By specificLink = By.xpath("//table/tbody/tr[td[2]//a[contains(normalize-space(.), '"
                        + latestTitle + "')]]/td[2]//a[contains(@href, 'contest.php?cid=')]");
                return waitUtils.waitForElementToBeClickable(specificLink);
            } catch (Exception e) {
                // If another test did not create the contest in this JVM, use the first contest of that type.
            }
        }

        By contestLink = By.xpath("//table/tbody/tr[td[4]//*[contains(normalize-space(.), '"
                + normalizedAccess + "')]]/td[2]//a[contains(@href, 'contest.php?cid=')]");
        return waitUtils.waitForElementToBeClickable(contestLink);
    }

    private By problemLinks() {
        return By.xpath("//table/tbody/tr//a[contains(@href, 'problem.php?cid=') and not(contains(normalize-space(.), 'IDE'))]");
    }

    private String normalizeAccess(String accessType) {
        String value = accessType == null ? "" : accessType.trim().toLowerCase();
        if (value.contains("priv")) {
            return "Privado";
        }
        return "Público";
    }

    private String cellText(WebElement row, int columnIndex) {
        return row.findElement(By.xpath("./td[" + columnIndex + "]")).getText().trim();
    }

    private String xpathText(String value) {
        return "'" + value.replace("'", "\\'") + "'";
    }

    public record ContestProblemRow(
            String code,
            String name,
            String setter,
            String accepted,
            String submissions
    ) {
    }

    public record ContestRankRow(
            String rank,
            String name,
            String user,
            String solved,
            Map<String, String> problemResults
    ) {
        public String problemResult(String problemCode) {
            return problemResults.getOrDefault(problemCode, "");
        }
    }
}
