package bo.juezvirtual.automation.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.pages.admin.AdminLoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Administrative cleanup helper for ending contests that are currently running.
 */
public final class AdminContestCleaner {
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(15);
    private static final DateTimeFormatter FORM_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter FORM_TIME = DateTimeFormatter.ofPattern("HH:mm");
    private static final List<DateTimeFormatter> LIST_DATE_FORMATS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ROOT),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
    );

    private AdminContestCleaner() {
    }

    /**
     * Logs into the admin portal, opens each active contest, sets its end date/time to now, and clears the session.
     */
    public static int finishRunningContestsAsAdmin(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        LocalDateTime finishedAt = LocalDateTime.now();

        clearBrowserSession(driver);
        loginAsAdmin(driver, wait);

        List<String> activeContestEditUrls = getActiveContestEditUrls(driver, wait, finishedAt);
        for (String editUrl : activeContestEditUrls) {
            finishContestFromEditPage(driver, wait, editUrl, finishedAt);
        }

        clearBrowserSession(driver);
        return activeContestEditUrls.size();
    }

    private static void loginAsAdmin(WebDriver driver, WebDriverWait wait) {
        driver.get(BrowserConfig.getAdminUrl() + "/login");
        UserDataManager.UserCredentials adminCredentials = UserDataManager.getUser("administrador_qa");
        if (adminCredentials == null) {
            adminCredentials = new UserDataManager.UserCredentials(
                    BrowserConfig.getAdminUsername(), BrowserConfig.getAdminPassword());
        }
        new AdminLoginPage(driver).login(adminCredentials.getUsername(), adminCredentials.getPassword());
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
    }

    private static List<String> getActiveContestEditUrls(WebDriver driver, WebDriverWait wait, LocalDateTime now) {
        driver.get(BrowserConfig.getAdminUrl() + "/contests");
        waitForPageReady(driver, wait);
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("table tbody tr")),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(., 'No se encontraron')]")
                )
        ));

        List<String> editUrls = new ArrayList<>();
        for (WebElement row : driver.findElements(By.cssSelector("table tbody tr"))) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() < 7) {
                continue;
            }
            LocalDateTime startTime = parseListDateTime(cells.get(2).getText());
            LocalDateTime endTime = parseListDateTime(cells.get(3).getText());
            if (startTime == null || endTime == null || startTime.isAfter(now) || endTime.isBefore(now)) {
                continue;
            }
            List<WebElement> editLinks = row.findElements(By.xpath("./td[7]//a[contains(@href, '/admin/contests/edit/')"
                    + "] | ./td[2]//a[contains(@href, '/admin/contests/edit/')]") );
            if (!editLinks.isEmpty()) {
                editUrls.add(editLinks.get(0).getAttribute("href"));
            }
        }
        return editUrls;
    }

    private static void finishContestFromEditPage(
            WebDriver driver, WebDriverWait wait, String editUrl, LocalDateTime finishedAt) {
        driver.get(editUrl);
        waitForPageReady(driver, wait);
        WebElement endDateField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("endDate")));
        WebElement endTimeField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("endTime")));

        setReactFieldValue(driver, endDateField, finishedAt.format(FORM_DATE));
        setReactFieldValue(driver, endTimeField, finishedAt.format(FORM_TIME));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[type='submit']"))).click();
        wait.until(ExpectedConditions.urlContains("/admin/contests"));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/edit/")));
    }

    private static void setReactFieldValue(WebDriver driver, WebElement element, String value) {
        ((JavascriptExecutor) driver).executeScript(
                "const el = arguments[0];"
                        + "const value = arguments[1];"
                        + "const setter = Object.getOwnPropertyDescriptor(el.constructor.prototype, 'value').set;"
                        + "setter.call(el, value);"
                        + "el.dispatchEvent(new Event('input', { bubbles: true }));"
                        + "el.dispatchEvent(new Event('change', { bubbles: true }));"
                        + "el.dispatchEvent(new Event('blur', { bubbles: true }));",
                element, value);
    }

    private static void waitForPageReady(WebDriver driver, WebDriverWait wait) {
        wait.until((ExpectedCondition<Boolean>) currentDriver -> "complete".equals(
                ((JavascriptExecutor) currentDriver).executeScript("return document.readyState")));
    }

    private static LocalDateTime parseListDateTime(String value) {
        String normalized = value == null ? "" : value.trim().replace('T', ' ');
        if (normalized.isEmpty()) {
            return null;
        }
        for (DateTimeFormatter formatter : LIST_DATE_FORMATS) {
            try {
                return LocalDateTime.parse(normalized, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported list date format.
            }
        }
        return null;
    }

    private static void clearBrowserSession(WebDriver driver) {
        try {
            driver.manage().deleteAllCookies();
            ((JavascriptExecutor) driver).executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
        } catch (Exception ignored) {
            // The browser may not have an active document yet; cookie cleanup is best effort before login.
        }
    }
}
