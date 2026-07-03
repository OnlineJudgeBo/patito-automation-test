@admin @problem @regression @clean_problems
Feature: Gestión de Problemas en Panel Administrativo
  Como docente o administrador del Juez Virtual
  Quiero registrar nuevos problemas
  Para que los participantes puedan resolverlos

  Background:
    Given el administrador navega a la pagina de login del panel
    When el administrador introduce el usuario "administrador_qa" y clave "administrador_qa"
    Then el administrador deberia ver el dashboard administrativo

  Scenario Outline: Crear problema y validar que se lista - <titulo>
    Given el docente esta en la pagina de creacion de problemas
    When crea un problema con titulo "<titulo>", tiempo "<tiempo>", memoria "<memoria>", autor "<autor>" y descripciones basicas
    Then el problema deberia ser guardado con exito
    And el problema "<titulo>" deberia aparecer listado

    Examples:
      | titulo                   | tiempo | memoria | autor     |
      | Suma de dos números      | 2      | 128     | Patito QA |
      | Número par               | 1      | 128     | Patito QA |
      | Mayor de tres            | 1      | 128     | Patito QA |
      | Tabla de multiplicar     | 1      | 128     | Patito QA |
      | Factorial                | 1      | 128     | Patito QA |
      | Contar vocales           | 1      | 128     | Patito QA |
      | Invertir una cadena      | 1      | 128     | Patito QA |
      | Máximo de un arreglo     | 1      | 128     | Patito QA |
      | ¿Es primo?               | 1      | 128     | Patito QA |
      | Serie Fibonacci          | 1      | 128     | Patito QA |
