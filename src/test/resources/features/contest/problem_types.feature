@problem_types @regression @clean_contest @clean_problems
Feature: Tipos de problema
  Como docente del Juez Virtual
  Quiero crear problemas con checker, con grader y por puntos
  Para evaluar tareas que no se corrigen comparando una sola respuesta

  Background:
    Given el administrador navega a la pagina de login del panel
    When el administrador introduce el usuario "administrador_qa" y clave "administrador_qa"
    Then el administrador deberia ver el dashboard administrativo

  Scenario: Un problema con checker acepta cualquier respuesta válida
    Given el docente esta en la pagina de creacion de problemas
    When crea el problema "Dos sumandos" con la siguiente evaluacion:
      | Ejemplo de entrada | 10   |
      | Ejemplo de salida  | 0 10 |
      | Casos              | sumandos/sumandos_1.in, sumandos/sumandos_1.out, sumandos/sumandos_2.in, sumandos/sumandos_2.out |
      | Checker            | sumandos/checker.cpp |
    Then el problema deberia ser guardado con exito
    Given el participante navega a la pagina de inicio de sesion
    When el participante introduce el usuario "participante_qa" y clave "participante_qa"
    Then el participante deberia ver el portal de inicio
    Given el participante esta en la pagina del problema "Dos sumandos"
    When hace clic en el boton Enviar
    And envia en "C++" la solucion "sumandos_uno_y_resto.cpp"
    Then deberia esperar la evaluacion y ver el veredicto "Accepted"
    Given el participante esta en la pagina del problema "Dos sumandos"
    When hace clic en el boton Enviar
    And envia en "C++" la solucion "sumandos_todo_y_cero.cpp"
    Then deberia esperar la evaluacion y ver el veredicto "Accepted"
    Given el participante esta en la pagina del problema "Dos sumandos"
    When hace clic en el boton Enviar
    And envia en "C++" la solucion "sumandos_wa.cpp"
    Then deberia esperar la evaluacion y ver el veredicto "Wrong Answer"

  Scenario: Un problema con grader juzga la función del participante
    Given el docente esta en la pagina de creacion de problemas
    When crea el problema "Suma con grader" con la siguiente evaluacion:
      | Casos     | suma/suma_1.in, suma/suma_1.out, suma/suma_2.in, suma/suma_2.out |
      | Grader    | suma/grader.cpp |
      | Cabeceras | suma/suma.h |
    Then el problema deberia ser guardado con exito
    Given el participante navega a la pagina de inicio de sesion
    When el participante introduce el usuario "participante_qa" y clave "participante_qa"
    Then el participante deberia ver el portal de inicio
    Given el participante esta en la pagina del problema "Suma con grader"
    When hace clic en el boton Enviar
    And envia en "C++" la solucion "suma_grader_ac.cpp"
    Then deberia esperar la evaluacion y ver el veredicto "Accepted"
    Given el participante esta en la pagina del problema "Suma con grader"
    When hace clic en el boton Enviar
    And envia en "C++" la solucion "suma_grader_con_main.cpp"
    Then deberia esperar la evaluacion y ver el veredicto "Compile Error"

  Scenario: Un problema por puntos da el puntaje de sus subtareas
    Given el docente esta en la pagina de creacion de problemas
    When crea el problema "Doble por puntos" con la siguiente evaluacion:
      | Tipo               | ioi |
      | Ejemplo de entrada | 3   |
      | Ejemplo de salida  | 6   |
      | Casos              | doble/s1_1.in, doble/s1_1.out, doble/s1_2.in, doble/s1_2.out, doble/s2_1.in, doble/s2_1.out |
      | Subtareas          | 40:sum:s1_*; 60:min:s2_* |
    Then el problema deberia ser guardado con exito
    Given el administrador esta en la pagina de creacion de concursos
    When crea un concurso por puntos "publico" con titulo "Concurso Puntos QA", fecha "actual", hora "actual", problemas "Doble por puntos"
    Then el concurso deberia ser guardado con exito
    Given existe un contest público activo
    And el participante introduce el usuario "participante_qa" y clave "participante_qa"
    When el participante ingresa al contest público
    And el participante envía las siguientes soluciones:
      | Problema | Nombre problema  | Lenguaje | Codigo            |
      | A        | Doble por puntos | C++      | doble_parcial.cpp |
      | A        | Doble por puntos | C++      | doble_ac.cpp      |
    Then el sistema debe registrar el envío
    And los envios deben tener los siguientes estados en la pagina de status:
      | Problema | Estado         |
      | A        | Parcial · 40   |
      | A        | Accepted · 100 |
    When selecciona el ranking
    Then se debe registrar la cantidad de problemas correctos y la cantidad de fallos:
      | usuario_alias   | PUNTOS |
      | participante_qa | 100    |

  Scenario: Un checker da puntaje parcial a una respuesta cercana
    Given el docente esta en la pagina de creacion de problemas
    When crea el problema "Doble aproximado" con la siguiente evaluacion:
      | Tipo               | ioi |
      | Ejemplo de entrada | 3   |
      | Ejemplo de salida  | 6   |
      | Casos              | doble/s1_1.in, doble/s1_1.out, doble/s1_2.in, doble/s1_2.out, doble/s2_1.in, doble/s2_1.out |
      | Checker            | doble/checker.cpp |
      | Subtareas          | 60:mul:s1_*; 40:min:s2_* |
    Then el problema deberia ser guardado con exito
    Given el administrador esta en la pagina de creacion de concursos
    When crea un concurso por puntos "publico" con titulo "Concurso Checker Parcial QA", fecha "actual", hora "actual", problemas "Doble aproximado"
    Then el concurso deberia ser guardado con exito
    Given existe un contest público activo
    And el participante introduce el usuario "participante_qa" y clave "participante_qa"
    When el participante ingresa al contest público
    And el participante envía las siguientes soluciones:
      | Problema | Nombre problema  | Lenguaje | Codigo         |
      | A        | Doble aproximado | C++      | doble_casi.cpp |
    Then el sistema debe registrar el envío
    And los envios deben tener los siguientes estados en la pagina de status:
      | Problema | Estado       |
      | A        | Parcial · 35 |
    When selecciona el ranking
    Then se debe registrar la cantidad de problemas correctos y la cantidad de fallos:
      | usuario_alias   | PUNTOS |
      | participante_qa | 35     |
