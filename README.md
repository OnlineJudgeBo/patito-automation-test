# Patito Online Judge - Selenium BDD Automation Suite

Este proyecto es una suite de pruebas automatizadas de extremo a extremo (E2E) y de integración para **El Juez Virtual Patito**. Utiliza Selenium WebDriver y Cucumber BDD con Java para estructurar pruebas, usa el patrón de diseño **Page Object Model (POM)**.

---

## Estructura del Proyecto

El proyecto sigue una estructura en Gradle:

```text
patito-selenium-tests/
  ├── build.gradle               # Dependencias
  ├── settings.gradle
  ├── gradle.properties
  ├── config/
  │   └── checkstyle/
  │       └── checkstyle.xml     # Reglas de Checkstyle
  └── src/
      └── test/
          ├── java/
          │   └── bo/juezvirtual/automation/
          │       ├── config/    # Carga de propiedades (BrowserConfig.java)
          │       ├── driver/    # Lifecycle de WebDriver (DriverFactory.java)
          │       ├── pages/     # Page Objects (BasePage, LoginPage, ProblemsPage, etc.)
          │       ├── steps/     # Step definitions modulares por dominio (ClientLoginSteps, AdminContestSteps, etc.) y Hooks
          │       ├── utils/     # Utilitarios (WaitUtils, UserDataManager, ScreenshotUtils)
          │       └── runners/   # Runner Suite (CucumberTestRunner.java)
          └── resources/
              ├── features/
              ├── testcases/     # Archivos de prueba (.in) para el FileManager
              ├── testdata/      # JSONs de datos de prueba (users.json)
              └── application.propertiess
```

---

## Cómo Ejecutar

### Prerrequisitos
* Java 11 o superior instalado (preferiblemente JDK 17 o 21).
* Gradle

### Comandos de Ejecución

1. **Ejecutar todos los Tests de BDD:**
   ```bash
   ./gradlew executeBDDTests
   ```
2. **Ejecutar un subgrupo usando tags:**
   ```bash
   ./gradlew executeBDDTests -Dtags="@smoke"
   ```
3. **Ejecutar en un entorno específico:**
   ```bash
   ./gradlew executeBDDTests -Denv=develop
   ```
4. **Re-ejecutar escenarios fallados:**
   ```bash
   ./gradlew reExecuteBDDTests
   ```
5. **Validación de Calidad de Código (Checkstyle):**
   ```bash
   ./gradlew checkstyleTest
   ```

---

## Reportes y Capturas
* `build/reports/screenshots/`.
* `build/reports/cucumber/cucumber.html`.
