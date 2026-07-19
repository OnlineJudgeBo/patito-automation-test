package bo.juezvirtual.automation.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility helper class to capture page screenshots on test failures.
 */
public final class ScreenshotUtils {
    private ScreenshotUtils() {
    }

    /**
     * Captures a screenshot of the current browser view and saves it to build/reports/screenshots.
     */
    public static void takeScreenshot(WebDriver driver, String testName) {
        if (!(driver instanceof TakesScreenshot)) {
            return;
        }

        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filename = testName.replaceAll("[^a-zA-Z0-9_]", "_") + "_" + timestamp + ".png";
            Path destPath = Paths.get("build", "reports", "screenshots", filename);

            Files.createDirectories(destPath.getParent());
            Files.copy(srcFile.toPath(), destPath);
            System.out.println("Screenshot saved to: " + destPath.toAbsolutePath());
        } catch (IOException | RuntimeException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
        }
    }
}
