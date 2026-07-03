package bo.juezvirtual.automation.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.utils.WaitUtils;

/**
 * Abstract class representing the base for all Page Objects.
 * Centralizes locator interactions, javascript executions, and wait behaviors.
 */
public abstract class BasePage {
    protected final WebDriver driver;
    protected final WaitUtils waitUtils;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver, BrowserConfig.getExplicitWait());
        PageFactory.initElements(driver, this);
    }

    protected void click(By locator) {
        waitUtils.waitForElementToBeClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement element = waitUtils.waitForElementToBeVisible(locator);
        element.sendKeys("");
        element.clear();
        element.sendKeys(text);
    }

    protected void typeNumeric(By locator, String text) {
        WebElement element = waitUtils.waitForElementToBeVisible(locator);
        element.click();
        element.sendKeys(Keys.CONTROL + "a");
        element.sendKeys(Keys.BACK_SPACE);
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(BrowserConfig.getExplicitWait()));
        wait.ignoring(StaleElementReferenceException.class);
        return wait.until(webDriver -> {
            try {
                List<WebElement> elements = webDriver.findElements(locator);
                for (WebElement element : elements) {
                    if (element.isDisplayed()) {
                        return element.getText().trim();
                    }
                }
                return null;
            } catch (StaleElementReferenceException e) {
                return null;
            }
        });
    }

    protected boolean isVisible(By locator) {
        try {
            return waitUtils.waitForElementToBeVisible(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected void scrollTo(WebElement element) {
        executeJavaScript("arguments[0].scrollIntoView(true);", element);
    }

    protected Object executeJavaScript(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }
}
