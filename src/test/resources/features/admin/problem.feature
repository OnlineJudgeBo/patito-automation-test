@admin @problem @regression
Feature: Gestión de Problemas en Panel Administrativo
  Como docente o administrador del Juez Virtual
  Quiero registrar nuevos problemas
  Para que los participantes puedan resolverlos

  Scenario: Crear un nuevo problema con restricciones básicas exitosamente
    Given el administrador navega a la pagina de login del panel
    When el administrador introduce el usuario "administrador_qa" y clave "administrador_qa"
    Then el administrador deberia ver el dashboard administrativo
    Given el docente esta en la pagina de creacion de problemas
    When crea un problema con titulo "Suma Simple QA Test", tiempo "1.5", memoria "256", autor "QA Team" y descripciones basicas
    Then el problema deberia ser guardado con exito
