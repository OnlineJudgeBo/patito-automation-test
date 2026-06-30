package bo.juezvirtual.automation.pages.client;

import bo.juezvirtual.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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
     * Checks if a problem is present in the table rows.
     */
    public boolean isProblemListed(String problemName) {
        By row = By.xpath("//table[@id='problemList']/tbody/tr[td[a[contains(text(), '" + problemName + "')]]]");
        return isVisible(row);
    }
}
