# language: es
@client @login @smoke
Característica: Inicio de Sesión de Participantes en Patito Client
  Como participante del Juez Virtual
  Quiero iniciar sesión con mi usuario y contraseña
  Para acceder a resolver problemas y ver mis envíos

  Esquema del escenario: Inicio de sesión exitoso con credenciales válidas
    Dado el participante navega a la pagina de inicio de sesion
    Cuando el participante introduce el usuario "<usuario_alias>" y clave "<clave_alias>"
    Entonces el participante deberia ver el portal de inicio

    Ejemplos:
      | usuario_alias     | clave_alias      |
      | participante_qa   | participante_qa  |
      | userqa1           | userqa1          |

  Esquema del escenario: Intento de inicio de sesión con credenciales inválidas
    Dado el participante navega a la pagina de inicio de sesion
    Cuando el participante introduce el usuario "<usuario_alias>" y clave "<clave_alias>"
    Entonces el participante deberia ver el error de inicio de sesion "Error en el nombre de usuario o la contraseña"

    Ejemplos:
      | usuario_alias     | clave_alias  |
      | participante_qa   | wrong_pass   |
      | userqa1           | wrong_pass   |
