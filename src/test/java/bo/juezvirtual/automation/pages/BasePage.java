package bo.juezvirtual.automation.pages;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

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
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitUtils.waitForElementToBeVisible(locator).getText().trim();
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
