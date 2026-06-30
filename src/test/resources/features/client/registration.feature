@client @registration @regression @priority_1
Feature: Registro de Participantes en Patito Client
  Como usuario visitante del Juez Virtual
  Quiero registrar un nuevo perfil de participante
  Para poder iniciar sesión y resolver problemas

  Scenario: Registro de participante exitoso
    Given el participante navega a la pagina de registro
    When completa el registro con nombre "Pedro", apellido "Perez", nickname "pedro_qa", email "pedro@juez.bo", clave "PedroPass123"
    Then deberia ver la alerta SweetAlert de exito "Registro Exitoso"
