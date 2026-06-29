package bo.juezvirtual.automation.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * JUnit 5 Platform Suite test runner for Cucumber BDD execution.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
    key = "cucumber.plugin", 
    value = "pretty, html:build/reports/cucumber/cucumber.html, json:build/reports/cucumber/cucumber.json, junit:build/reports/cucumber/cucumber.xml, message:build/reports/cucumber/cucumber.ndjson"
)
@ConfigurationParameter(key = "cucumber.glue", value = "bo.juezvirtual.automation.steps")
@ConfigurationParameter(key = "cucumber.publish.enabled", value = "true")
@ConfigurationParameter(key = "cucumber.publish.quiet", value = "false")
public final class CucumberTestRunner {
}
