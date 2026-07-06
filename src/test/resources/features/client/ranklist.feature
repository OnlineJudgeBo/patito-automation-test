@client @ranklist @regression
Feature: Tabla de Clasificación Global
  Como participante o visitante del Juez Virtual
  Quiero ver el ranking general de competidores
  Para conocer el desempeño y efectividad de los usuarios

  Scenario Outline: Buscar usuario en la tabla de clasificación
    Given el participante navega a la tabla de clasificacion global
    When busca el usuario "<usuario_alias>" en el ranking
    Then el usuario "<usuario_alias>" deberia estar listado en la clasificacion

    Examples:
      | usuario_alias   |
      | participante_qa |
