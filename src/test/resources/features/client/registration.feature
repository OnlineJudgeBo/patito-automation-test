# language: es
@client @registration @regression
Característica: Registro de Participantes en Patito Client
  Como usuario visitante del Juez Virtual
  Quiero registrar un nuevo perfil de participante
  Para poder iniciar sesión y resolver problemas

  Escenario: Registro de participante exitoso
    Dado el participante navega a la pagina de registro
    Cuando completa el registro con nombre "Pedro", apellido "Perez", nickname "pedro_qa", email "pedro@juez.bo", clave "PedroPass123"
    Entonces deberia ver la alerta SweetAlert de exito "Registro Exitoso"
