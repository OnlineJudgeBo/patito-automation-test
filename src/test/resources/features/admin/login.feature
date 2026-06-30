@admin @login @smoke
Feature: Inicio de Sesión Administrativo en Juez Virtual Admin
  Como administrador, docente o auxiliar
  Quiero iniciar sesión en el panel administrativo
  Para gestionar usuarios, problemas, concursos y configuraciones

  Scenario: Inicio de sesión administrativo exitoso con rol de Administrador
    Given el administrador navega a la pagina de login del panel
    When el administrador introduce el usuario "administrador_qa" y clave "administrador_qa"
    Then el administrador deberia ver el dashboard administrativo

  Scenario: Intento de inicio de sesión administrativo con credenciales invalidas
    Given el administrador navega a la pagina de login del panel
    When el administrador introduce el usuario "administrador_qa" y clave "wrong_pass"
    Then el administrador deberia ver el error de inicio de sesion "No se pudo iniciar sesión."
