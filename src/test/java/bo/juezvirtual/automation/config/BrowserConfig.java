package bo.juezvirtual.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Handles the application configuration from properties and environment variables.
 * Enables overriding configurations via system parameters (e.g. -Denv=staging) or environment variables.
 */
public final class BrowserConfig {
    private static final Properties PROPERTIES = new Properties();
    private static final String DEFAULT_ENV = "dev";
    private static String env;

    static {
        try (InputStream input = BrowserConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                PROPERTIES.load(input);
            } else {
                throw new RuntimeException("Could not find application.properties file in resources.");
            }
            env = System.getProperty("env", PROPERTIES.getProperty("env", DEFAULT_ENV)).trim().toLowerCase();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load application.properties file.", e);
        }
    }

    private BrowserConfig() {
    }

    public static String getBrowser() {
        return System.getProperty("browser", PROPERTIES.getProperty("browser", "chrome")).trim().toLowerCase();
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(System.getProperty("headless", PROPERTIES.getProperty("headless", "true")));
    }

    public static int getImplicitWait() {
        return Integer.parseInt(PROPERTIES.getProperty("implicit.wait", "5"));
    }

    public static int getExplicitWait() {
        return Integer.parseInt(PROPERTIES.getProperty("explicit.wait", "15"));
    }

    public static String getAdminUrl() {
        String url = PROPERTIES.getProperty(env + ".admin.url");
        if (url != null && url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    public static String getClientUrl() {
        String url = PROPERTIES.getProperty(env + ".client.url");
        if (url != null && url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    public static int getEvaluationMaxAttempts() {
        return Integer.parseInt(PROPERTIES.getProperty("evaluation.max.attempts", "15"));
    }

    public static int getEvaluationDelayMs() {
        return Integer.parseInt(PROPERTIES.getProperty("evaluation.delay.ms", "1000"));
    }
}
