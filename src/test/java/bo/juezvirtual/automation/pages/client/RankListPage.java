package bo.juezvirtual.automation.pages.client;

import bo.juezvirtual.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object representing the public client ranking list view.
 * Mapped to selectors in patito-client-web views/patito/ranklist.php.
 */
public final class RankListPage extends BasePage {
    private final By searchField = By.cssSelector("#ranklist_filter input");

    public RankListPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Filters the ranklist by typing a username/userId.
     */
    public void searchUser(String userId) {
        type(searchField, userId);
    }

    /**
     * Retrieves the number of problems solved by a user in the filtered list.
     */
    public String getUserSolvedCount(String userId) {
        By solvedCell = By.xpath("//table[@id='ranklist']/tbody/tr[td/a[contains(text(), '" + userId + "')]]/td[4]");
        return getText(solvedCell);
    }
}
