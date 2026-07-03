package bo.juezvirtual.automation.pages.client;

import bo.juezvirtual.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page Object representing the public client problem set catalog.
 * Mapped to selectors in patito-client-web views/patito/problemset.php.
 */
public final class ProblemSetPage extends BasePage {
    private final By searchField = By.cssSelector("#problemList_filter input");

    public ProblemSetPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Filters the DataTable by typing into the search input.
     */
    public void searchProblem(String problemName) {
        type(searchField, problemName);
    }

    /**
     * Clicks the link of a problem given its ID.
     */
    public void clickProblemLink(String problemId) {
        By problemLink = By.xpath("//a[contains(@href, 'problem.php?id=" + problemId + "')]");
        click(problemLink);
    }

    /**
     * Finds and opens a problem by its visible title.
     */
    public void clickProblemByName(String problemName) {
        searchProblem(problemName);
        By problemLink = By.xpath("//table[@id='problemList']/tbody/tr/td/a[contains(normalize-space(.), "
                + xpathText(problemName) + ")]");
        WebElement link = waitUtils.waitForElementToBeClickable(problemLink);
        driver.get(link.getAttribute("href"));
    }

    /**
     * Checks if a problem is present in the table rows.
     */
    public boolean isProblemListed(String problemName) {
        By row = By.xpath("//table[@id='problemList']/tbody/tr[td[a[contains(text(), '" + problemName + "')]]]");
        return isVisible(row);
    }

    private String xpathText(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        return "\"" + value + "\"";
    }
}
