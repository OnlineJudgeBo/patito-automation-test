package bo.juezvirtual.automation.runners;

import io.cucumber.core.cli.Main;
import java.util.ArrayList;
import java.util.List;

public final class PriorityRunner {
    private PriorityRunner() {
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("No feature files provided to PriorityRunner!");
            System.exit(1);
        }

        List<String> baseArgs = createBaseArguments();

        // Pass filter tags if they were supplied via gradle system property
        String tags = System.getProperty("tags");
        if (tags != null && !tags.trim().isEmpty()) {
            baseArgs.add("--tags");
            baseArgs.add(tags);
        }

        int exitCode = 0;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        System.out.println("--- Starting Priority BDD Execution (Preserved JVM State) ---");
        for (String featurePath : args) {
            List<String> runArgs = new ArrayList<>(baseArgs);
            addReportPlugins(runArgs, featurePath);
            runArgs.add(featurePath);

            System.out.println("\nExecuting Feature: " + featurePath);
            System.out.println("--------------------------------------------------");
            
            byte result = Main.run(runArgs.toArray(new String[0]), classLoader);
            if (result != 0) {
                exitCode = result;
            }
        }
        System.out.println("\n--- BDD Execution Completed (Exit Code: " + exitCode + ") ---");
        
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    private static List<String> createBaseArguments() {
        List<String> baseArgs = new ArrayList<>();
        baseArgs.add("--plugin");
        baseArgs.add("pretty");
        baseArgs.add("--plugin");
        baseArgs.add("io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm");
        baseArgs.add("--glue");
        baseArgs.add("bo.juezvirtual.automation.steps");
        return baseArgs;
    }

    private static void addReportPlugins(List<String> runArgs, String featurePath) {
        String reportName = featurePath
                .replace("src/test/resources/features/", "")
                .replaceAll("[^A-Za-z0-9]+", "-")
                .replaceAll("^-|-$", "");

        runArgs.add("--plugin");
        runArgs.add("html:build/reports/cucumber/" + reportName + ".html");
        runArgs.add("--plugin");
        runArgs.add("json:build/reports/cucumber/" + reportName + ".json");
        runArgs.add("--plugin");
        runArgs.add("junit:build/reports/cucumber/" + reportName + ".xml");
        runArgs.add("--plugin");
        runArgs.add("message:build/reports/cucumber/" + reportName + ".ndjson");
        runArgs.add("--plugin");
        runArgs.add("rerun:build/reports/cucumber/rerun-" + reportName + ".txt");
    }
}
