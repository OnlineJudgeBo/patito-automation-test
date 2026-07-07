package bo.juezvirtual.automation.pages.admin;

import bo.juezvirtual.automation.pages.BasePage;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import bo.juezvirtual.automation.config.BrowserConfig;

/**
 * Page Object representing the administrative test case file manager page.
 * Mapped to selectors in juezvirtualbo-admin-front src/pages/FileManagerPage/FileManagerPage.jsx.
 */
public final class FileManagerPage extends BasePage {
    private final By fileInput = By.xpath("//div[p[contains(text(),'Arrastra')]]/input[@type='file']");
    
    public FileManagerPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Uploads a file by sending its absolute path directly to the hidden dropzone file input.
     */
    public void uploadFile(String absoluteFilePath) {
        waitUtils.waitForElementToBePresent(fileInput).sendKeys(absoluteFilePath);
    }

    /**
     * Checks if a file is successfully loaded and rendered in the list.
     */
    public boolean isFileUploaded(String fileName) {
        return isVisible(fileNameSpan(fileName));
    }

    /**
     * Deletes the file from the current problem file manager when it is present.
     */
    public void deleteFileIfPresent(String fileName) {
        By fileSpan = fileNameSpan(fileName);
        if (!isVisible(fileSpan)) {
            return;
        }

        WebElement fileElement = waitUtils.waitForElementToBeVisible(fileSpan);
        WebElement deleteButton = fileElement.findElement(By.xpath(
                "./ancestor::div[contains(@class, 'border')][1]//button[normalize-space(.) = 'Borrar']"));
        deleteButton.click();

        waitUtils.waitForPageToLoad();
        new WebDriverWait(driver, Duration.ofSeconds(BrowserConfig.getExplicitWait()))
                .until(webDriver -> !isVisible(fileSpan));
    }

    private By fileNameSpan(String fileName) {
        return By.xpath("//span[normalize-space(.) = " + xpathText(fileName) + "]");
    }

    private String xpathText(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        return "\"" + value + "\"";
    }
}
