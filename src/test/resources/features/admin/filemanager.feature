# language: es
@admin @filemanager @integration
Característica: Administrador de Archivos de Problemas
  Como docente del Juez Virtual
  Quiero cargar archivos de casos de prueba (.in y .out)
  Para que el Juez Virtual evalúe las soluciones de los participantes

  Escenario: Carga exitosa de archivo de entrada de casos de prueba (.in)
    Dado el administrador navega a la pagina de login del panel
    Cuando el administrador introduce el usuario "administrador_qa" y clave "administrador_qa"
    Entonces el administrador deberia ver el dashboard administrativo
    Dado el docente esta en el administrador de archivos del problema con ID "1000"
    Cuando sube el archivo de casos de prueba con ruta absoluta "src/test/resources/testcases/sum_1.in"
    Entonces el archivo "sum_1.in" deberia aparecer listado en la pagina

  Escenario: Carga exitosa de archivo de salida de casos de prueba (.out)
    Dado el administrador navega a la pagina de login del panel
    Cuando el administrador introduce el usuario "administrador_qa" y clave "administrador_qa"
    Entonces el administrador deberia ver el dashboard administrativo
    Dado el docente esta en el administrador de archivos del problema con ID "1000"
    Cuando sube el archivo de casos de prueba con ruta absoluta "src/test/resources/testcases/sum_1.out"
    Entonces el archivo "sum_1.out" deberia aparecer listado en la pagina
