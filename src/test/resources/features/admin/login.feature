# language: es
@admin @login @smoke
Característica: Inicio de Sesión Administrativo en Juez Virtual Admin
  Como administrador, docente o auxiliar
  Quiero iniciar sesión en el panel administrativo
  Para gestionar usuarios, problemas, concursos y configuraciones

  Escenario: Inicio de sesión administrativo exitoso con rol de Administrador
    Dado el administrador navega a la pagina de login del panel
    Cuando el administrador introduce el usuario "administrador_qa" y clave "administrador_qa"
    Entonces el administrador deberia ver el dashboard administrativo

  Escenario: Intento de inicio de sesión administrativo con credenciales invalidas
    Dado el administrador navega a la pagina de login del panel
    Cuando el administrador introduce el usuario "administrador_qa" y clave "wrong_pass"
    Entonces el administrador deberia ver el error de inicio de sesion "No se pudo iniciar sesión."
