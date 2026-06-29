package bo.juezvirtual.automation.pages.admin;

import bo.juezvirtual.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

/**
 * Page Object representing administrative contest creation.
 * Mapped to selectors in juezvirtualbo-admin-front src/pages/ContestPage/CreateContestPage.jsx.
 */
public final class CreateContestPage extends BasePage {
    private final By titleField = By.id("title");
    private final By isPrivateCheckbox = By.id("isPrivate");
    private final By startDateField = By.id("startDate");
    private final By startTimeField = By.id("startTime");
    private final By endDateField = By.id("endDate");
    private final By endTimeField = By.id("endTime");
    private final By problemListArea = By.id("manualProblemList");
    private final By userListArea = By.id("manualUserList");
    private final By submitButton = By.xpath("//button[@type='submit']");
    private final By languageInput = By.xpath("//div[label[contains(text(), 'Select Language')]]//input");

    public CreateContestPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Selects programming languages using the react-select multi-selector.
     */
    public void selectLanguages(String... languages) {
        for (String lang : languages) {
            type(languageInput, lang);
            try {
                Thread.sleep(400); // Give react-select a tiny bit of time to filter options
            } catch (InterruptedException ignored) {
            }
            driver.findElement(languageInput).sendKeys(Keys.ENTER);
        }
    }

    /**
     * Fills out the contest creation form.
     * Triggers appropriate field blurs to update React Jotai state bindings.
     */
    public void fillContestDetails(String title, boolean isPrivate, String startDate, String startTime, 
                                   String endDate, String endTime, String problemIds, String userIds) {
        type(titleField, title);
        
        boolean currentlyChecked = driver.findElement(isPrivateCheckbox).isSelected();
        if (isPrivate != currentlyChecked) {
            org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", driver.findElement(isPrivateCheckbox));
        }

        type(startDateField, startDate);
        type(startTimeField, startTime);
        type(endDateField, endDate);
        type(endTimeField, endTime);

        // Select languages C, Java, Python3.12
        selectLanguages("C", "Java", "Python3.12");

        // Add problem IDs list
        type(problemListArea, problemIds);
        // Click title to force blur on the textarea and trigger jotai synchronization
        click(titleField);

        if (isPrivate) {
            type(userListArea, userIds);
            click(titleField); // Force blur
        }
    }

    /**
     * Submits the contest form.
     */
    public void clickSave() {
        click(submitButton);
    }
}
