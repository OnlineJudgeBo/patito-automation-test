@admin @contest @regression
Feature: Gestión de Concursos en Panel Administrativo
  Como administrador del Juez Virtual
  Quiero crear nuevos concursos privados y públicos
  Para organizar competencias con usuarios y problemas seleccionados

  Scenario: Crear un concurso privado exitosamente
    Given el administrador navega a la pagina de login del panel
    When el administrador introduce el usuario "administrador_qa" y clave "administrador_qa"
    Then el administrador deberia ver el dashboard administrativo
    Given el administrador esta en la pagina de creacion de concursos
    When crea un concurso privado con titulo "Concurso Privado QA", fecha "actual", hora "actual", problemas "1000" y usuarios habilitados "participante_qa"
    Then el concurso deberia ser guardado con exito
