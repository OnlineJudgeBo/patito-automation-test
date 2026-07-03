package bo.juezvirtual.automation.utils;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.pages.admin.AdminLoginPage;

/**
 * Administrative cleanup helper for starting problem scenarios with an empty
 * admin problem list.
 */
public final class AdminProblemCleaner {
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(15);
    private static final int MAX_DELETE_ATTEMPTS = 500;
    private static final By PROBLEM_ROWS = By.cssSelector("table tbody tr");
    private static final By EMPTY_RESULTS = By.xpath("//*[contains(., 'No se encontraron')]");
    private static final List<String> PROBLEMS_TO_DELETE = List.of(
            "Suma de dos números",
            "Número par",
            "Mayor de tres",
            "Tabla de multiplicar",
            "Factorial",
            "Contar vocales",
            "Invertir una cadena",
            "Máximo de un arreglo",
            "¿Es primo?",
            "Serie Fibonacci");

    private AdminProblemCleaner() {
    }

    /**
     * Logs into the admin portal, deletes every listed problem through the UI, and
     * clears the session.
     */
    public static int deleteAllProblemsAsAdmin(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        clearBrowserSession(driver);
        loginAsAdmin(driver, wait);

        int deleted = 0;
        for (int attempts = 0; attempts < MAX_DELETE_ATTEMPTS; attempts++) {
            driver.get(BrowserConfig.getAdminUrl() + "/problems");
            waitForProblemList(driver, wait);

            WebElement row = firstDeletableProblemRow(driver);
            if (row == null) {
                break;
            } 

            String problemName = row.findElement(By.xpath(".//td[2]")).getText();
            if (PROBLEMS_TO_DELETE.contains(problemName)) {
                row.findElement(By.xpath(".//a[normalize-space(.) = 'Borrar']")).click();
                deleted++;
                wait.until(ExpectedConditions.stalenessOf(row));
            }
        }

        clearBrowserSession(driver);
        return deleted;
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

    private static void waitForProblemList(WebDriver driver, WebDriverWait wait) {
        waitForPageReady(driver, wait);
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(PROBLEM_ROWS),
                ExpectedConditions.presenceOfElementLocated(EMPTY_RESULTS)));
    }

    private static WebElement firstDeletableProblemRow(WebDriver driver) {
        List<WebElement> rows = driver.findElements(PROBLEM_ROWS);
        for (WebElement row : rows) {
            try {
                if (!row.findElements(By.xpath(".//a[normalize-space(.) = 'Borrar']")).isEmpty()) {
                    return row;
                }
            } catch (StaleElementReferenceException ignored) {
                return null;
            }
        }
        return null;
    }

    private static void waitForPageReady(WebDriver driver, WebDriverWait wait) {
        wait.until((ExpectedCondition<Boolean>) currentDriver -> "complete".equals(
                ((JavascriptExecutor) currentDriver).executeScript("return document.readyState")));
    }

    private static void clearBrowserSession(WebDriver driver) {
        try {
            driver.manage().deleteAllCookies();
            ((JavascriptExecutor) driver).executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
        } catch (Exception ignored) {
            // The browser may not have an active document yet; cookie cleanup is best
            // effort before login.
        }
    }
}
