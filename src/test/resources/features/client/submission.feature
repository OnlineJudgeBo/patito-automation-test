@client @submission @regression
Feature: Envío y Evaluación de Soluciones
  Como participante registrado en el Juez Virtual
  Quiero enviar código de solución para un problema
  Para verificar si es correcto (Accepted) o tiene errores

  Scenario: Envío de solución correcta con veredicto Aceptado (AC)
    Given el participante navega a la pagina de inicio de sesion
    When el participante introduce el usuario "participante_qa" y clave "participante_qa"
    Then el participante deberia ver el portal de inicio
    Given el participante esta en la pagina del problema con ID "1000"
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

  Scenario: Envío de solución con error de compilación (CE)
    Given el participante navega a la pagina de inicio de sesion
    When el participante introduce el usuario "participante_qa" y clave "participante_qa"
    Then el participante deberia ver el portal de inicio
    Given el participante esta en la pagina del problema con ID "1000"
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
