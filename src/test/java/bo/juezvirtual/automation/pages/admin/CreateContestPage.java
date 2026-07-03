package bo.juezvirtual.automation.pages.admin;

import bo.juezvirtual.automation.pages.BasePage;
import bo.juezvirtual.automation.utils.SharedState;
import bo.juezvirtual.automation.utils.UserDataManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;

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
            WebElement input = waitUtils.waitForElementToBeVisible(languageInput);
            input.sendKeys(lang);
            waitForReactSelectFilter();
            input.sendKeys(Keys.ENTER);
        }
    }

    /**
     * Fills out the contest creation form.
     */
    public void fillContestDetails(String title, boolean isPrivate, String startDate, String startTime,
                                   String endDate, String endTime, String problemIds, String userIds) {
        setPrivateFlag(isPrivate);
        fillTextArea(problemListArea, problemIds);

        if (isPrivate) {
            String privateUserList = buildPrivateUserList(userIds);
            fillTextArea(userListArea, privateUserList);
            waitForPrivateUsersToBeCommitted(privateUserList);
        }

        selectLanguages("c", "java", "python");
        setFieldValueViaJs(titleField, title);
        setPrivateFlag(isPrivate);
        setFieldValueViaJs(startDateField, startDate);
        setFieldValueViaJs(startTimeField, startTime);
        setFieldValueViaJs(endDateField, endDate);
        setFieldValueViaJs(endTimeField, endTime);
    }

    /**
     * Submits the contest form.
     */
    public void clickSave() {
        click(submitButton);
    }

    private void setPrivateFlag(boolean isPrivate) {
        boolean currentlyChecked = driver.findElement(isPrivateCheckbox).isSelected();
        if (isPrivate != currentlyChecked) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(isPrivateCheckbox));
        }
    }

    private void fillTextArea(By locator, String value) {
        WebElement textArea = waitUtils.waitForElementToBeVisible(locator);
        textArea.clear();
        textArea.sendKeys(value);
        textArea.sendKeys(Keys.TAB);
    }

    private String buildPrivateUserList(String userIds) {
        Set<String> users = new LinkedHashSet<>();
        for (String user : userIds.split("\\R")) {
            addResolvedUser(users, user);
        }
        addResolvedUser(users, SharedState.getLatestRegisteredNickname());
        return String.join("\n", users);
    }

    private void addResolvedUser(Set<String> users, String aliasOrUsername) {
        if (aliasOrUsername == null || aliasOrUsername.trim().isEmpty()) {
            return;
        }
        UserDataManager.UserCredentials credentials = UserDataManager.getUser(aliasOrUsername.trim());
        users.add(credentials == null ? aliasOrUsername.trim() : credentials.getUsername());
    }

    private void waitForPrivateUsersToBeCommitted(String privateUserList) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        for (String userId : privateUserList.split("\\R")) {
            String normalizedUserId = userId.trim();
            if (normalizedUserId.isEmpty()) {
                continue;
            }
            wait.until(webDriver -> !webDriver.findElements(By.xpath(
                    "//label[@for='selectedUser']/following-sibling::div[contains(normalize-space(.), "
                            + xpathText(normalizedUserId) + ")]"
            )).isEmpty());
        }
    }

    private void setFieldValueViaJs(By locator, String value) {
        WebElement element = waitUtils.waitForElementToBeVisible(locator);
        element.click();
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        element.sendKeys(Keys.DELETE);
        element.sendKeys(value);
        element.sendKeys(Keys.TAB);
        executeJavaScript(
                "var el = arguments[0];"
                        + "var setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;"
                        + "setter.call(el, arguments[1]);"
                        + "el.dispatchEvent(new Event('input', { bubbles: true }));"
                        + "el.dispatchEvent(new Event('change', { bubbles: true }));"
                        + "el.dispatchEvent(new Event('blur', { bubbles: true }));",
                element, value
        );
        if (!value.equals(element.getAttribute("value"))) {
            element.click();
            element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            element.sendKeys(Keys.DELETE);
            element.sendKeys(value);
            element.sendKeys(Keys.TAB);
        }
    }

    private String xpathText(String value) {
        return "'" + value.replace("'", "\\'") + "'";
    }

    private void waitForReactSelectFilter() {
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
