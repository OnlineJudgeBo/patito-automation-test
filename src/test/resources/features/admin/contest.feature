# language: es
@admin @contest @regression
Característica: Gestión de Concursos en Panel Administrativo
  Como administrador del Juez Virtual
  Quiero crear nuevos concursos privados y públicos
  Para organizar competencias con usuarios y problemas seleccionados

  Escenario: Crear un concurso privado exitosamente
    Dado el administrador navega a la pagina de login del panel
    Cuando el administrador introduce el usuario "administrador_qa" y clave "administrador_qa"
    Entonces el administrador deberia ver el dashboard administrativo
    Dado el administrador esta en la pagina de creacion de concursos
    Cuando crea un concurso privado con titulo "Concurso Privado QA", fecha "actual", hora "actual", problemas "1000" y usuarios habilitados "participante_qa"
    Entonces el concurso deberia ser guardado con exito
