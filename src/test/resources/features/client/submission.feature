@client @submission @regression @clean_contest @clean_problems
Feature: Envío y Evaluación de Soluciones
  Como participante registrado en el Juez Virtual
  Quiero enviar código de solución para un problema
  Para verificar si es correcto (Accepted) o tiene errores

  Background:
    Given el administrador navega a la pagina de login del panel
    When el administrador introduce el usuario "administrador_qa" y clave "administrador_qa"
    Then el administrador deberia ver el dashboard administrativo
    Given el docente esta en la pagina de creacion de problemas
    When crea un problema con titulo "Suma de dos números", tiempo "2", memoria "128", autor "Patito QA" y descripciones basicas
    Then el problema deberia ser guardado con exito
    And el problema "Suma de dos números" deberia aparecer listado
    Given el docente esta en el administrador de archivos del problema "Suma de dos números"
    When sube el archivo de casos de prueba "src/test/resources/testcases/sum_1.in"
    Then el archivo "sum_1.in" deberia aparecer listado en la pagina
    Given el docente esta en el administrador de archivos del problema "Suma de dos números"
    When sube el archivo de casos de prueba "src/test/resources/testcases/sum_1.out"
    Then el archivo "sum_1.out" deberia aparecer listado en la pagina

  Scenario Outline: Envío de solución correcta con veredicto Aceptado (AC)
    Given el participante navega a la pagina de inicio de sesion
    When el participante introduce el usuario "<usuario_alias>" y clave "<clave_alias>"
    Then el participante deberia ver el portal de inicio
    Given el participante esta en la pagina del problema "Suma de dos números"
    When hace clic en el boton Enviar
    And selecciona el lenguaje "Java" e ingresa el codigo:
      """
      import java.util.Scanner;

      public class Main {
          public static void main(String[] args) throws Exception {
              Scanner sc = new Scanner(System.in);
              int a,b;
              a = sc.nextInt();
              b = sc.nextInt();
              System.out.println(a + b);
          }
      }
      """
    Then deberia esperar la evaluacion y ver el veredicto "Accepted"

    Examples:
      | usuario_alias   | clave_alias     |
      | participante_qa | participante_qa |

  Scenario Outline: Envío de solución con error de compilación (CE)
    Given el participante navega a la pagina de inicio de sesion
    When el participante introduce el usuario "<usuario_alias>" y clave "<clave_alias>"
    Then el participante deberia ver el portal de inicio
    Given el participante esta en la pagina del problema "Suma de dos números"
    When hace clic en el boton Enviar
    And selecciona el lenguaje "Java" e ingresa el codigo:
      """
      import java.util.Scanner;

      public class Main {
          public static void main(String[] args) throws Exception {
              Scanner sc = new Scanner(System.in)
              // Falta punto y coma
          }
      }
      """
    Then deberia esperar la evaluacion y ver el veredicto "Compile Error"

    Examples:
      | usuario_alias   | clave_alias     |
      | participante_qa | participante_qa |
