package bo.juezvirtual.automation.pages.admin;

import bo.juezvirtual.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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
        By uploadedFileSpan = By.xpath("//span[contains(text(), '" + fileName + "')]");
        return isVisible(uploadedFileSpan);
    }
}
