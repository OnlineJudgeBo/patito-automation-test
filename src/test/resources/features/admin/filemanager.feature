@admin @filemanager @integration @clean_contest @clean_problems
Feature: Administrador de Archivos de Problemas
  Como docente del Juez Virtual
  Quiero cargar archivos de casos de prueba (.in y .out)
  Para que el Juez Virtual evalúe las soluciones de los participantes

  Background:
    Given el administrador navega a la pagina de login del panel
    When el administrador introduce el usuario "administrador_qa" y clave "administrador_qa"
    Then el administrador deberia ver el dashboard administrativo
    Given el docente esta en la pagina de creacion de problemas
    When crea un problema con titulo "Suma de dos números", tiempo "2", memoria "128", autor "Patito QA" y descripciones basicas
    Then el problema deberia ser guardado con exito
    And el problema "Suma de dos números" deberia aparecer listado

  Scenario: Carga exitosa de archivo de entrada de casos de prueba (.in)
    Given el docente esta en el administrador de archivos del problema "Suma de dos números"
    When sube el archivo de casos de prueba "src/test/resources/testcases/sum_1.in"
    Then el archivo "sum_1.in" deberia aparecer listado en la pagina

  Scenario: Carga exitosa de archivo de salida de casos de prueba (.out)
    Given el docente esta en el administrador de archivos del problema "Suma de dos números"
    When sube el archivo de casos de prueba "src/test/resources/testcases/sum_1.out"
    Then el archivo "sum_1.out" deberia aparecer listado en la pagina
