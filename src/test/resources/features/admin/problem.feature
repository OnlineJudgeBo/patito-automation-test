# language: es
@admin @problem @regression
Característica: Gestión de Problemas en Panel Administrativo
  Como docente o administrador del Juez Virtual
  Quiero registrar nuevos problemas
  Para que los participantes puedan resolverlos

  Escenario: Crear un nuevo problema con restricciones básicas exitosamente
    Dado el administrador navega a la pagina de login del panel
    Cuando el administrador introduce el usuario "administrador_qa" y clave "administrador_qa"
    Entonces el administrador deberia ver el dashboard administrativo
    Dado el docente esta en la pagina de creacion de problemas
    Cuando crea un problema con titulo "Suma Simple QA Test", tiempo "1.5", memoria "256", autor "QA Team" y descripciones basicas
    Entonces el problema deberia ser guardado con exito
