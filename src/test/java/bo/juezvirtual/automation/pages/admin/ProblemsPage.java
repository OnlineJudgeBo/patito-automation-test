package bo.juezvirtual.automation.pages.admin;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import bo.juezvirtual.automation.config.BrowserConfig;
import bo.juezvirtual.automation.pages.BasePage;

/**
 * Page Object representing administrative problem management (List, Create, and Edit).
 * Mapped to selectors in juezvirtualbo-admin-front pages/ProblemsPage/ListProblemPage.jsx and CreateProblemPage.jsx.
 */
public final class ProblemsPage extends BasePage {
    // Problem List Page Selectors
    private final By addProblemButton = By.xpath("//a[contains(@href, '/admin/problems/add')]");
    private final By searchField = By.xpath("//input[contains(@placeholder, 'Buscar')]");
    
    // Create Problem Form Selectors
    private final By titleField = By.id("problem-title");
    private final By timeLimitField = By.id("problem-limitations");
    private final By memoryLimitField = By.id("problem-execution-time");
    
    // MathStatementEditor sections. Use source HTML mode to avoid CKEditor contenteditable flakiness.
    private final By descriptionEditor = By.xpath("//label[normalize-space(.) = 'Descripción del Problema']"
            + "/following-sibling::section[contains(@class, 'rich-editor')][1]");
    private final By inputEditor = By.xpath("//label[normalize-space(.) = 'Descripción de la entrada del Problema']"
            + "/following-sibling::section[contains(@class, 'rich-editor')][1]");
    private final By outputEditor = By.xpath("//label[normalize-space(.) = 'Descripción de la salida del Problema']"
            + "/following-sibling::section[contains(@class, 'rich-editor')][1]");
    
    // Textareas for samples
    private final By sampleInputField = By.id("sample-input");
    private final By sampleOutputField = By.id("sample-output");
    private final By authorField = By.id("problem-author");
    
    // Form buttons
    private final By saveButton = By.xpath("//button[text()='Guardar']");

    public ProblemsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Clicks the "Adicionar Problema" button on the problem listing page.
     */
    public void clickAddProblem() {
        click(addProblemButton);
    }

    /**
     * Fills out the entire problem creation form.
     */
    public void fillProblemDetails(String title, String timeLimit, String memoryLimit, 
                                   String description, String inputDesc, String outputDesc, 
                                   String sampleInput, String sampleOutput, String author) {
        type(titleField, title);
        type(timeLimitField, timeLimit);
        typeNumeric(memoryLimitField, memoryLimit);

        // Fill MathStatementEditor fields through their explicit HTML editing mode.
        fillHtmlEditor(descriptionEditor, description);
        fillHtmlEditor(inputEditor, inputDesc);
        fillHtmlEditor(outputEditor, outputDesc);

        // Fill sample inputs and outputs
        type(sampleInputField, sampleInput);
        type(sampleOutputField, sampleOutput);
        type(authorField, author);
    }

    /**
     * Clicks the submit/save button.
     */
    public void clickSave() {
        click(saveButton);
    }



    /**
     * Returns the current admin-list problem ID for the given visible title.
     */
    public String getProblemIdByTitle(String title) {
        filterProblemListByTitle(title);
        By problemIdCell = problemIdCellByTitle(title);
        return waitUtils.waitForElementToBeVisible(problemIdCell).getText().trim();
    }

    /**
     * Checks if a problem title appears in the admin problem listing.
     */
    public boolean isProblemListed(String title) {
        filterProblemListByTitle(title);
        By problemRow = problemRowByTitle(title);
        try {
            return waitUtils.waitForElementToBeVisible(problemRow).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Checks if a problem id and title appear together in the admin problem listing.
     */
    public boolean isProblemListed(String problemId, String title) {
        filterProblemListByTitle(title);
        By problemRow = By.xpath("//table/tbody/tr[td[1][normalize-space(.) = " + xpathText(problemId)
                + "] and td[2]//a[contains(normalize-space(.), " + xpathText(title) + ")]]");
        try {
            return waitUtils.waitForElementToBeVisible(problemRow).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    private void filterProblemListByTitle(String title) {
        By problemRow = problemRowByTitle(title);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(BrowserConfig.getExplicitWait())).until(webDriver -> {
                if (!webDriver.findElements(problemRow).isEmpty()) {
                    return true;
                }
                return typeIntoVisibleSearchField(title);
            });
        } catch (TimeoutException e) {
            typeIntoVisibleSearchField(title);
        }
    }

    private boolean typeIntoVisibleSearchField(String title) {
        List<WebElement> searchFields = driver.findElements(searchField);
        for (WebElement field : searchFields) {
            if (field.isDisplayed() && field.isEnabled()) {
                field.click();
                field.clear();
                field.sendKeys(title);
                return true;
            }
        }
        return false;
    }

    private By problemIdCellByTitle(String title) {
        return By.xpath(problemRowXpath(title) + "/td[1]");
    }

    private By problemRowByTitle(String title) {
        return By.xpath(problemRowXpath(title));
    }

    private String problemRowXpath(String title) {
        return "//table/tbody/tr[td[2]//a[contains(normalize-space(.), " + xpathText(title) + ")]]";
    }

    private String xpathText(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        return "\"" + value + "\"";
    }

    /**
     * Writes HTML into the custom problem editor by opening its source HTML mode.
     */
    private void fillHtmlEditor(By editorSectionLocator, String html) {
        WebElement editorSection = waitUtils.waitForElementToBeVisible(editorSectionLocator);
        WebElement htmlButton = editorSection.findElement(By.cssSelector("button[data-problem-editor-action='html']"));
        htmlButton.click();

        WebElement sourceTextArea = editorSection.findElement(By.cssSelector("textarea[aria-label='Código HTML']"));
        sourceTextArea.click();
        sourceTextArea.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"));
        sourceTextArea.sendKeys(org.openqa.selenium.Keys.DELETE);
        sourceTextArea.sendKeys(html);
    }
}
