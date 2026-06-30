@admin @filemanager @integration
Feature: Administrador de Archivos de Problemas
  Como docente del Juez Virtual
  Quiero cargar archivos de casos de prueba (.in y .out)
  Para que el Juez Virtual evalúe las soluciones de los participantes

  Scenario: Carga exitosa de archivo de entrada de casos de prueba (.in)
    Given el administrador navega a la pagina de login del panel
    When el administrador introduce el usuario "administrador_qa" y clave "administrador_qa"
    Then el administrador deberia ver el dashboard administrativo
    Given el docente esta en el administrador de archivos del problema con ID "1000"
    When sube el archivo de casos de prueba con ruta absoluta "src/test/resources/testcases/sum_1.in"
    Then el archivo "sum_1.in" deberia aparecer listado en la pagina

  Scenario: Carga exitosa de archivo de salida de casos de prueba (.out)
    Given el administrador navega a la pagina de login del panel
    When el administrador introduce el usuario "administrador_qa" y clave "administrador_qa"
    Then el administrador deberia ver el dashboard administrativo
    Given el docente esta en el administrador de archivos del problema con ID "1000"
    When sube el archivo de casos de prueba con ruta absoluta "src/test/resources/testcases/sum_1.out"
    Then el archivo "sum_1.out" deberia aparecer listado en la pagina
