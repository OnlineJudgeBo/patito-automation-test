package bo.juezvirtual.automation.pages.admin;

import bo.juezvirtual.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page Object representing administrative problem management (List, Create, and Edit).
 * Mapped to selectors in juezvirtualbo-admin-front pages/ProblemsPage/ListProblemPage.jsx and CreateProblemPage.jsx.
 */
public final class ProblemsPage extends BasePage {
    // Problem List Page Selectors
    private final By addProblemButton = By.xpath("//a[contains(@href, '/admin/problems/add')]");
    
    // Create Problem Form Selectors
    private final By titleField = By.id("problem-title");
    private final By timeLimitField = By.id("problem-limitations");
    private final By memoryLimitField = By.id("problem-execution-time");
    
    // CKEditors (Description, Input, Output) using label associations
    private final By descriptionEditor = By.xpath("//div[label[contains(text(), 'Descripción del Problema')]]/following-sibling::div//div[contains(@class, 'ck-editor__editable')]");
    private final By inputEditor = By.xpath("//div[label[contains(text(), 'entrada')]]/following-sibling::div//div[contains(@class, 'ck-editor__editable')]");
    private final By outputEditor = By.xpath("//div[label[contains(text(), 'salida')]]/following-sibling::div//div[contains(@class, 'ck-editor__editable')]");
    
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
        type(memoryLimitField, memoryLimit);

        // Fill CKEditors
        fillCKEditor(descriptionEditor, description);
        fillCKEditor(inputEditor, inputDesc);
        fillCKEditor(outputEditor, outputDesc);

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
     * Helper method to write text into CKEditor 5 dynamic rich-text containers.
     */
    private void fillCKEditor(By locator, String text) {
        WebElement editor = waitUtils.waitForElementToBeVisible(locator);
        editor.click();
        // Since clear() can sometimes fail on contenteditable divs, we select all and delete
        editor.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"), org.openqa.selenium.Keys.DELETE);
        editor.sendKeys(text);
    }
}
