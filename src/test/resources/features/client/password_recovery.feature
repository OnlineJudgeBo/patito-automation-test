@client @password_recovery @regression
Feature: Recuperación de Clave en Patito Client
  Como participante que olvidó su clave
  Quiero solicitar un código de recuperación
  Para volver a entrar a mi cuenta

  Scenario: La solicitud muestra el mismo aviso aunque el correo no esté registrado
    Given el participante navega a la pagina de recuperacion de clave
    When solicita recuperar la clave del correo "nadie.registrado@juez.bo"
    Then deberia ver el aviso de recuperacion "Si el correo electrónico proporcionado es correcto, se ha enviado un código de confirmación a su cuenta de correo (cierre esta pestaña para continuar)."
    When confirma el aviso de recuperacion
    Then deberia volver a la pagina de inicio de sesion
