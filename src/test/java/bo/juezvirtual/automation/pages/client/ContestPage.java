package bo.juezvirtual.automation.pages.client;

import bo.juezvirtual.automation.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object representing the public client contests list view.
 * Mapped to selectors in patito-client-web views/patito/contest.php.
 */
public final class ContestPage extends BasePage {
    private final By contestTable = By.id("contest-table");

    public ContestPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Navigates to a specific contest by clicking its title link in the table.
     */
    public void clickContestLink(String contestId) {
        By contestLink = By.xpath("//a[contains(@href, 'contest.php?cid=" + contestId + "')]");
        click(contestLink);
    }

    /**
     * Checks if a contest is listed.
     */
    public boolean isContestVisible(String contestId) {
        By contestLink = By.xpath("//a[contains(@href, 'contest.php?cid=" + contestId + "')]");
        return isVisible(contestLink);
    }

    /**
     * Retrieves the access type string (e.g. Public/Private) for a contest.
     */
    public String getContestAccessType(String contestId) {
        By accessBadge = By.xpath("//tr[td/span[contains(text(), '#" + contestId + "')]]/td[4]/span");
        return getText(accessBadge);
    }
}
