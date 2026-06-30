@client @ranklist @regression
Feature: Tabla de Clasificación Global
  Como participante o visitante del Juez Virtual
  Quiero ver el ranking general de competidores
  Para conocer el desempeño y efectividad de los usuarios

  Scenario: Buscar usuario en la tabla de clasificación
    Given el participante navega a la tabla de clasificacion global
    When busca el usuario "participante_qa" en el ranking
    Then el usuario "participante_qa" deberia estar listado en la clasificacion
