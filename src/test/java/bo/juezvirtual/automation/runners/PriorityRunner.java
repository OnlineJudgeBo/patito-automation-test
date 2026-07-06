package bo.juezvirtual.automation.runners;

import io.cucumber.core.cli.Main;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PriorityRunner {
    private static final Path CUCUMBER_REPORTS_DIR = Paths.get("build", "reports", "cucumber");
    private static final Path CONSOLIDATED_REPORT = CUCUMBER_REPORTS_DIR.resolve("consolidated.html");
    private static final Path FAILED_SCENARIOS_REPORT = CUCUMBER_REPORTS_DIR.resolve("failed-scenarios.txt");
    private static final String RETRY_REPORT_NAME = "retry-failed-scenarios";

    private PriorityRunner() {
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("No feature files provided to PriorityRunner!");
            System.exit(1);
        }

        prepareReportsDirectory();

        List<String> baseArgs = createBaseArguments();

        // Pass filter tags if they were supplied via gradle system property
        String tags = System.getProperty("tags");
        if (tags != null && !tags.trim().isEmpty()) {
            baseArgs.add("--tags");
            baseArgs.add(tags);
        }

        List<FeatureExecution> executions = new ArrayList<>();
        int initialExitCode = runFeatures(args, baseArgs, executions);
        List<String> failedScenarios = readFailedScenarioPaths();
        int retryExitCode = runFailedScenariosRetry(baseArgs, failedScenarios);
        int finalExitCode = determineFinalExitCode(initialExitCode, failedScenarios, retryExitCode);

        writeConsolidatedReport(executions, failedScenarios, retryExitCode, finalExitCode);

        System.out.println("\n--- BDD Execution Completed (Exit Code: " + finalExitCode + ") ---");

        if (finalExitCode != 0) {
            System.exit(finalExitCode);
        }
    }

    private static void prepareReportsDirectory() {
        try {
            Files.createDirectories(CUCUMBER_REPORTS_DIR);
            try (Stream<Path> files = Files.list(CUCUMBER_REPORTS_DIR)) {
                files.filter(path -> path.getFileName().toString().matches("rerun-.*\\.txt"))
                        .forEach(PriorityRunner::deleteIfExists);
            }
            deleteIfExists(FAILED_SCENARIOS_REPORT);
            deleteIfExists(CONSOLIDATED_REPORT);
        } catch (IOException e) {
            throw new IllegalStateException("Could not prepare Cucumber reports directory", e);
        }
    }

    private static int runFeatures(String[] featurePaths, List<String> baseArgs,
            List<FeatureExecution> executions) {
        int exitCode = 0;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        System.out.println("--- Starting Priority BDD Execution (Preserved JVM State) ---");
        for (String featurePath : featurePaths) {
            String reportName = toReportName(featurePath);
            List<String> runArgs = new ArrayList<>(baseArgs);
            addReportPlugins(runArgs, reportName);
            runArgs.add(featurePath);

            System.out.println("\nExecuting Feature: " + featurePath);
            System.out.println("--------------------------------------------------");

            byte result = Main.run(runArgs.toArray(new String[0]), classLoader);
            executions.add(new FeatureExecution(featurePath, reportName, result));
            if (result != 0) {
                exitCode = result;
            }
        }
        return exitCode;
    }

    private static int runFailedScenariosRetry(List<String> baseArgs, List<String> failedScenarios) {
        if (failedScenarios.isEmpty()) {
            System.out.println("\nNo failed scenarios found for automatic retry.");
            return 0;
        }

        System.out.println("\n--- Retrying failed scenarios automatically ---");
        failedScenarios.forEach(scenario -> System.out.println("Retry target: " + scenario));

        List<String> retryArgs = new ArrayList<>(baseArgs);
        addReportPlugins(retryArgs, RETRY_REPORT_NAME);
        retryArgs.addAll(failedScenarios);

        return Main.run(retryArgs.toArray(new String[0]),
                Thread.currentThread().getContextClassLoader());
    }

    private static int determineFinalExitCode(int initialExitCode, List<String> failedScenarios,
            int retryExitCode) {
        if (initialExitCode == 0) {
            return 0;
        }
        if (failedScenarios.isEmpty()) {
            return initialExitCode;
        }
        return retryExitCode;
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

    private static void addReportPlugins(List<String> runArgs, String reportName) {
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

    private static List<String> readFailedScenarioPaths() {
        if (!Files.exists(CUCUMBER_REPORTS_DIR)) {
            return List.of();
        }

        try (Stream<Path> files = Files.list(CUCUMBER_REPORTS_DIR)) {
            List<String> failedScenarios = new ArrayList<>();
            files.filter(path -> path.getFileName().toString().matches("rerun-.*\\.txt"))
                    .sorted(Comparator.comparing(Path::toString))
                    .forEach(path -> failedScenarios.addAll(readNonBlankLines(path)));

            List<String> uniqueScenarios = failedScenarios.stream()
                    .distinct()
                    .collect(Collectors.toList());
            if (!uniqueScenarios.isEmpty()) {
                Files.write(FAILED_SCENARIOS_REPORT, uniqueScenarios, StandardCharsets.UTF_8);
            }
            return uniqueScenarios;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read failed scenario rerun files", e);
        }
    }

    private static List<String> readNonBlankLines(Path path) {
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8).stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("Could not read rerun file " + path, e);
        }
    }

    private static void writeConsolidatedReport(List<FeatureExecution> executions,
            List<String> failedScenarios, int retryExitCode, int finalExitCode) {
        try {
            Files.createDirectories(CUCUMBER_REPORTS_DIR);
            StringBuilder html = new StringBuilder();
            html.append("<!doctype html>\n<html lang=\"es\">\n<head>\n")
                    .append("<meta charset=\"utf-8\">\n")
                    .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n")
                    .append("<title>Reporte Cucumber consolidado</title>\n")
                    .append("<style>body{font-family:sans-serif;max-width:960px;margin:2rem auto;}")
                    .append("table{border-collapse:collapse;width:100%;}td,th{border:1px solid #ddd;")
                    .append("padding:.5rem;text-align:left}.ok{color:#137333}.fail{color:#a50e0e}")
                    .append("</style>\n</head>\n<body>\n")
                    .append("<h1>Reporte Cucumber consolidado</h1>\n")
                    .append("<p>Resultado final: ").append(statusLabel(finalExitCode)).append("</p>\n")
                    .append("<h2>Corrida principal</h2>\n<table>\n")
                    .append("<thead><tr><th>Feature</th><th>Estado inicial</th><th>Reporte</th></tr></thead>\n")
                    .append("<tbody>\n");

            for (FeatureExecution execution : executions) {
                html.append("<tr><td>").append(escapeHtml(execution.getFeaturePath())).append("</td><td>")
                        .append(statusLabel(execution.getExitCode())).append("</td><td><a href=\"")
                        .append(execution.getReportName()).append(".html\">HTML</a></td></tr>\n");
            }

            html.append("</tbody>\n</table>\n")
                    .append("<h2>Reintento automático</h2>\n");
            if (failedScenarios.isEmpty()) {
                html.append("<p>No hubo escenarios fallidos para reintentar.</p>\n");
            } else {
                html.append("<p>Resultado del reintento: ").append(statusLabel(retryExitCode))
                        .append("</p>\n")
                        .append("<p><a href=\"").append(RETRY_REPORT_NAME)
                        .append(".html\">Ver reporte HTML del reintento</a></p>\n")
                        .append("<p><a href=\"failed-scenarios.txt\">Ver lista TXT de escenarios reintentados</a></p>\n")
                        .append("<ul>\n");
                for (String failedScenario : failedScenarios) {
                    html.append("<li><code>").append(escapeHtml(failedScenario)).append("</code></li>\n");
                }
                html.append("</ul>\n");
            }

            html.append("</body>\n</html>\n");
            Files.writeString(CONSOLIDATED_REPORT, html.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not write consolidated Cucumber report", e);
        }
    }

    private static String toReportName(String featurePath) {
        return featurePath
                .replace("src/test/resources/features/", "")
                .replaceAll("[^A-Za-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    private static String statusLabel(int exitCode) {
        if (exitCode == 0) {
            return "<span class=\"ok\">PASÓ</span>";
        }
        return "<span class=\"fail\">FALLÓ (exit code " + exitCode + ")</span>";
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private static void deleteIfExists(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new IllegalStateException("Could not delete " + path, e);
        }
    }

    private static final class FeatureExecution {
        private final String featurePath;
        private final String reportName;
        private final int exitCode;

        private FeatureExecution(String featurePath, String reportName, int exitCode) {
            this.featurePath = featurePath;
            this.reportName = reportName;
            this.exitCode = exitCode;
        }

        private String getFeaturePath() {
            return featurePath;
        }

        private String getReportName() {
            return reportName;
        }

        private int getExitCode() {
            return exitCode;
        }
    }
}
