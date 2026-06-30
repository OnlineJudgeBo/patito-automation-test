@client @login @smoke
Feature: Inicio de Sesión de Participantes en Patito Client
  Como participante del Juez Virtual
  Quiero iniciar sesión con mi usuario y contraseña
  Para acceder a resolver problemas y ver mis envíos

  Scenario Outline: Inicio de sesión exitoso con credenciales válidas
    Given el participante navega a la pagina de inicio de sesion
    When el participante introduce el usuario "<usuario_alias>" y clave "<clave_alias>"
    Then el participante deberia ver el portal de inicio

    Examples:
      | usuario_alias     | clave_alias      |
      | participante_qa   | participante_qa  |
      | userqa1           | userqa1          |

  Scenario Outline: Intento de inicio de sesión con credenciales inválidas
    Given el participante navega a la pagina de inicio de sesion
    When el participante introduce el usuario "<usuario_alias>" y clave "<clave_alias>"
    Then el participante deberia ver el error de inicio de sesion "Error en el nombre de usuario o la contraseña"

    Examples:
      | usuario_alias     | clave_alias  |
      | participante_qa   | wrong_pass   |
      | userqa1           | wrong_pass   |
