@client @registration @regression @priority_1
Feature: Registro de Participantes en Patito Client
  Como usuario visitante del Juez Virtual
  Quiero registrar un nuevo perfil de participante
  Para poder iniciar sesión y resolver problemas

  Scenario: Registro de participante exitoso
    Given el participante navega a la pagina de registro
    When completa el registro con nombre "Pedro", apellido "Perez", nickname "pedro_qa", email "pedro@juez.bo", clave "PedroPass123"
    Then deberia ver la alerta SweetAlert de exito "Registro Exitoso"

  Scenario: El participante recién registrado puede iniciar sesión
    Given el participante navega a la pagina de registro
    When completa el registro con nombre "Lucia", apellido "Rojas", nickname "lucia_qa", email "lucia@juez.bo", clave "LuciaPass123"
    Then deberia ver la alerta SweetAlert de exito "Registro Exitoso"
    When el participante inicia sesion con el usuario recien registrado y clave "LuciaPass123"
    Then el participante deberia ver el portal de inicio

  Scenario: Registro rechazado cuando el nombre de usuario ya existe
    Given el participante navega a la pagina de registro
    When intenta registrarse con nombre "Pedro", apellido "Perez", nickname "participante_qa", email "repetido@juez.bo", confirmacion de email "repetido@juez.bo", clave "PedroPass123" y confirmacion de clave "PedroPass123"
    Then deberia ver la alerta SweetAlert de error "Error" con el mensaje "Error: El nombre de usuario patito ya está en uso."

  Scenario Outline: Registro rechazado cuando <caso>
    Given el participante navega a la pagina de registro
    When intenta registrarse con nombre "Pedro", apellido "Perez", nickname "pedroinvalido", email "<email>", confirmacion de email "<email2>", clave "<clave>" y confirmacion de clave "<clave2>"
    Then deberia ver la alerta SweetAlert de error "Oops..." con el mensaje "<mensaje>"

    Examples:
      | caso                        | email          | email2         | clave        | clave2       | mensaje                                                                           |
      | los correos no coinciden    | pedro@juez.bo  | otro@juez.bo   | PedroPass123 | PedroPass123 | Los correos electrónicos no coinciden. Por favor, verifica e intenta nuevamente. |
      | las claves no coinciden     | pedro@juez.bo  | pedro@juez.bo  | PedroPass123 | OtraClave123 | Las contraseñas no coinciden. Por favor, verifica e intenta nuevamente.          |
