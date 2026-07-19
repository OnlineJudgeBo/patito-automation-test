package bo.juezvirtual.automation.driver;

import bo.juezvirtual.automation.config.BrowserConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Factory class to manage WebDriver lifecycle and thread-safety for concurrent test execution.
 */
public final class DriverFactory {
    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    private DriverFactory() {
    }

    /**
     * Initializes and returns the WebDriver instance for the current thread.
     */
    public static WebDriver getDriver() {
        if (DRIVER_THREAD_LOCAL.get() == null) {
            String browser = BrowserConfig.getBrowser();
            boolean headless = BrowserConfig.isHeadless();
            WebDriver driver;

            switch (browser) {
                case "firefox":
                    WebDriverManager.firefoxdriver().setup();
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.addArguments("--width=1920");
                    firefoxOptions.addArguments("--height=1080");
                    if (headless) {
                        firefoxOptions.addArguments("-headless");
                    }
                    driver = new FirefoxDriver(firefoxOptions);
                    break;

                case "edge":
                    WebDriverManager.edgedriver().setup();
                    EdgeOptions edgeOptions = new EdgeOptions();
                    edgeOptions.addArguments("--window-size=1920,1080");
                    if (headless) {
                        edgeOptions.addArguments("--headless");
                    }
                    driver = new EdgeDriver(edgeOptions);
                    break;

                case "chrome":
                default:
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--ignore-certificate-errors");
                    chromeOptions.addArguments("--window-size=1920,1080");
                    if (headless) {
                        chromeOptions.addArguments("--headless=new");
                    }
                    driver = new ChromeDriver(chromeOptions);
                    break;
            }

            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(BrowserConfig.getImplicitWait()));
            driver.manage().window().setSize(new Dimension(1920, 1080));
            DRIVER_THREAD_LOCAL.set(driver);
        }
        return DRIVER_THREAD_LOCAL.get();
    }

    /**
     * Shuts down the WebDriver instance for the current thread and removes it from memory.
     */
    public static void quitDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver == null) {
            return;
        }

        try {
            driver.quit();
        } catch (RuntimeException e) {
            System.err.println("Could not close WebDriver cleanly: " + e.getMessage());
        } finally {
            DRIVER_THREAD_LOCAL.remove();
        }
    }
}
