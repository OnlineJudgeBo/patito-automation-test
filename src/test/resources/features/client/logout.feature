@client @logout @smoke
Feature: Cierre de Sesión de Participantes en Patito Client
  Como participante del Juez Virtual
  Quiero cerrar mi sesión
  Para que nadie más use mi cuenta en este equipo

  Scenario: Cierre de sesión exitoso
    Given el participante navega a la pagina de inicio de sesion
    When el participante introduce el usuario "participante_qa" y clave "participante_qa"
    Then el participante deberia ver el portal de inicio
    When el participante cierra sesion
    Then el participante deberia ver la opcion de iniciar sesion
