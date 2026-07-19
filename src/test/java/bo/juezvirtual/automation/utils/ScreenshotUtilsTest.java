package bo.juezvirtual.automation.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.lang.reflect.Proxy;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

final class ScreenshotUtilsTest {
    @Test
    void ignoresScreenshotFailureWhenBrowserWindowIsAlreadyClosed() {
        WebDriver closedDriver = (WebDriver) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[] {WebDriver.class, TakesScreenshot.class},
                (proxy, method, arguments) -> {
                    if ("getScreenshotAs".equals(method.getName())) {
                        throw new NoSuchWindowException("window already closed");
                    }
                    return null;
                });

        assertDoesNotThrow(() -> ScreenshotUtils.takeScreenshot(closedDriver, "failed scenario"));
    }
}
