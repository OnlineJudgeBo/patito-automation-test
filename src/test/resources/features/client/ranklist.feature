# language: es
@client @ranklist @regression
Característica: Tabla de Clasificación Global
  Como participante o visitante del Juez Virtual
  Quiero ver el ranking general de competidores
  Para conocer el desempeño y efectividad de los usuarios

  Escenario: Buscar usuario en la tabla de clasificación
    Dado el participante navega a la tabla de clasificacion global
    Cuando busca el usuario "participante_qa" en el ranking
    Entonces el usuario "participante_qa" deberia estar listado en la clasificacion
