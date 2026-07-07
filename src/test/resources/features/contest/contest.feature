@contest @regression  @clean_contest @clean_problems
Feature: Gestión y uso de contests
  Como administrador del Juez Virtual
  Quiero crear contests públicos y privados desde el panel administrativo
  Para que los participantes vean problemas, envíen soluciones y aparezcan en las clasificaciones

  Background:
    Given el administrador navega a la pagina de login del panel
    When el administrador introduce el usuario "administrador_qa" y clave "administrador_qa"
    Then el administrador deberia ver el dashboard administrativo

  Scenario: Crear los problemas base para las pruebas de contests
    Given el docente esta en la pagina de creacion de problemas
    When crea los siguientes problemas:
      | Titulo               | Tiempo | Memoria | Autor     |
      | Suma de dos números  | 2      | 128     | Patito QA |
      | Número par           | 1      | 128     | Patito QA |
    Then los problemas deberian ser guardados con exito
    And los siguientes problemas deberian aparecer listados:
      | Titulo               |
      | Suma de dos números  |
      | Número par           |

  Scenario Outline: Crear un contest público y validar el flujo del participante
    Given el administrador esta en la pagina de creacion de concursos
    When crea un concurso "publico" con titulo "Concurso Publico QA", fecha "actual", hora "actual", problemas "Suma de dos números"
    Then el concurso deberia ser guardado con exito
    And el concurso creado debe estar en la lista de concursos con los siguientes datos:
      | Nombre              | Tipo    |
      | Concurso Publico QA | Publico |
    Given existe un contest público activo
    And el participante introduce el usuario "<usuario_alias>" y clave "<clave_alias>"
    When el participante ingresa al contest público
    Then debe ver la lista de problemas disponibles
    And cada problema debe mostrar su titulo
    And cada problema debe mostrar su identificador
    And debe mostrar los siguientes datos del contest:
      | Problema | Nombre              | Setter | Aceptados | Envios |
      | 1A       | Suma de dos números |        |           |        |
    When el participante abre los siguientes problemas del contest y verifica sus nombres:
      | Identificador | Nombre               |
      | A              | Suma de dos números  |
    And el participante envía las siguientes soluciones:
      | Problema | Nombre problema      | Lenguaje | Codigo                       |
      | A        | Suma de dos números  | C++      | suma_dos_numeros_valido.cpp |
    Then el sistema debe registrar el envío
    And los envios deben tener los siguientes estados en la pagina de status:
      | Problema | Estado |
      | A        | Accepted |
    And debe mostrar los siguientes datos del contest:
      | Problema | Nombre              | Setter | Aceptados | Envios |
      | 1A       | Suma de dos números |        |           | 1      |
    And debe verse el envío en la clasificación diaria:
      | usuario_alias   | Resueltos |
      | <usuario_alias> | 1         |
    When selecciona el ranking
    Then se debe registrar la cantidad de problemas correctos y la cantidad de fallos:
      | # | NOMBRE | usuario_alias   | RESUELTOS | A    |
      |   |        | <usuario_alias> | 1         | true |

    Examples:
      | usuario_alias   | clave_alias     |
      | participante_qa | participante_qa |

  Scenario Outline: Crear un contest privado y validar el acceso del participante registrado
    Given el administrador esta en la pagina de creacion de concursos
    When crea un concurso "privado" con titulo "Concurso Privado QA", fecha "actual", hora "actual", problemas "Suma de dos números" y los siguientes usuarios habilitados
      | usuario_alias |
      | <usuario_alias> |
    Then el concurso deberia ser guardado con exito
    And el concurso creado debe estar en la lista de concursos con los siguientes datos:
      | Nombre              | Tipo    |
      | Concurso Privado QA | Privado |
    Given existe un participante registrado en el contest
    When el participante introduce el usuario "<usuario_alias>" y clave "<clave_alias>"
    And el participante ingresa al contest privado
    Then debe ver la lista de problemas disponibles
    And cada problema debe mostrar su titulo
    And cada problema debe mostrar su identificador
    And debe mostrar los siguientes datos del contest:
      | Problema | Nombre              | Setter | Aceptados | Envios |
      | 1A       | Suma de dos números |        |           |        |
    When el participante abre los siguientes problemas del contest y verifica sus nombres:
      | Identificador | Nombre               |
      | A              | Suma de dos números  |
    And el participante envía las siguientes soluciones:
      | Problema | Nombre problema      | Lenguaje | Codigo                       |
      | A        | Suma de dos números  | C++      | suma_dos_numeros_valido.cpp |
    Then el sistema debe registrar el envío
    And los envios deben tener los siguientes estados en la pagina de status:
      | Problema | Estado |
      | A        | Accepted     |
    And debe mostrar los siguientes datos del contest:
      | Problema | Nombre              | Setter | Aceptados | Envios |
      | 1A       | Suma de dos números |        |           | 1      |
    And debe verse el envío en la clasificación diaria:
      | usuario_alias   | Resueltos |
      | <usuario_alias>   | 1         |
    When selecciona el ranking
    Then se debe registrar la cantidad de problemas correctos y la cantidad de fallos:
      | # | NOMBRE | usuario_alias   | RESUELTOS | A    |
      |   |        | <usuario_alias> | 1         | true |

    Examples:
      | usuario_alias    | clave_alias      |
      | participante_qa  | participante_qa  |

  Scenario Outline: Validar distintos veredictos del juez en un contest público
    Given el administrador esta en la pagina de creacion de concursos
    When crea un concurso "publico" con titulo "Concurso Veredictos QA", fecha "actual", hora "actual", problemas "Suma de dos números, Número par"
    Then el concurso deberia ser guardado con exito
    Given existe un contest público activo
    And el participante introduce el usuario "<usuario_alias>" y clave "<clave_alias>"
    When el participante ingresa al contest público
    And el participante abre los siguientes problemas del contest y verifica sus nombres:
      | Identificador | Nombre               |
      | A             | Suma de dos números  |
      | B             | Número par            |
    And el participante envía las siguientes soluciones:
      | Problema | Nombre problema      | Lenguaje | Codigo                    |
      | A        | Suma de dos números  | C++      | suma_dos_numeros_wa.cpp  |
      | A        | Suma de dos números  | C++      | suma_dos_numeros_ce.cpp  |
      | A        | Suma de dos números  | C++      | suma_dos_numeros_tle.cpp |
      | B        | Número par           | C++      | numero_par_ac.cpp        |
      | B        | Número par           | C++      | numero_par_wa.cpp        |
    Then el sistema debe registrar el envío
    And los envios deben tener los siguientes estados en la pagina de status:
      | Problema | Estado |
      | A        | Wrong Answer |
      | A        | Compile Error |
      | A        | Time Limit Exceed |
      | B        | Accepted |
      | B        | Wrong Answer |

    Examples:
      | usuario_alias    | clave_alias      |
      | participante_qa  | participante_qa  |

  Scenario Outline: Validar distintos veredictos del juez en un contest privado
    Given el administrador esta en la pagina de creacion de concursos
    When crea un concurso "privado" con titulo "Concurso Veredictos Privado QA", fecha "actual", hora "actual", problemas "Suma de dos números, Número par" y los siguientes usuarios habilitados
      | usuario_alias |
      | <usuario_alias> |
    Then el concurso deberia ser guardado con exito
    Given existe un participante registrado en el contest
    And el participante introduce el usuario "<usuario_alias>" y clave "<clave_alias>"
    When el participante ingresa al contest privado
    And el participante abre los siguientes problemas del contest y verifica sus nombres:
      | Identificador | Nombre               |
      | A             | Suma de dos números  |
      | B             | Número par            |
    And el participante envía las siguientes soluciones:
      | Problema | Nombre problema      | Lenguaje | Codigo                    |
      | A        | Suma de dos números  | C++      | suma_dos_numeros_wa.cpp  |
      | A        | Suma de dos números  | C++      | suma_dos_numeros_ce.cpp  |
      | A        | Suma de dos números  | C++      | suma_dos_numeros_tle.cpp |
      | B        | Número par            | C++      | numero_par_ac.cpp        |
      | B        | Número par            | C++      | numero_par_wa.cpp        |
    Then el sistema debe registrar el envío
    And los envios deben tener los siguientes estados en la pagina de status:
      | Problema | Estado |
      | A        | Wrong Answer |
      | A        | Compile Error |
      | A        | Time Limit Exceed |
      | B        | Accepted |
      | B        | Wrong Answer |

    Examples:
      | usuario_alias    | clave_alias      |
      | participante_qa  | participante_qa  |
