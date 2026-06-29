# language: es
@client @submission @regression
Característica: Envío y Evaluación de Soluciones
  Como participante registrado en el Juez Virtual
  Quiero enviar código de solución para un problema
  Para verificar si es correcto (Accepted) o tiene errores

  Escenario: Envío de solución correcta con veredicto Aceptado (AC)
    Dado el participante navega a la pagina de inicio de sesion
    Cuando el participante introduce el usuario "participante_qa" y clave "participante_qa"
    Entonces el participante deberia ver el portal de inicio
    Dado el participante esta en la pagina del problema con ID "1000"
    Cuando hace clic en el boton Enviar
    Y selecciona el lenguaje "Java" e ingresa el codigo:
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
    Entonces deberia esperar la evaluacion y ver el veredicto "Accepted"

  Escenario: Envío de solución con error de compilación (CE)
    Dado el participante navega a la pagina de inicio de sesion
    Cuando el participante introduce el usuario "participante_qa" y clave "participante_qa"
    Entonces el participante deberia ver el portal de inicio
    Dado el participante esta en la pagina del problema con ID "1000"
    Cuando hace clic en el boton Enviar
    Y selecciona el lenguaje "Java" e ingresa el codigo:
      """
      import java.util.Scanner;
       
      public class Main {
          public static void main(String[] args) throws Exception {
              Scanner sc = new Scanner(System.in)
              // Falta punto y coma
          }
      }
      """
    Entonces deberia esperar la evaluacion y ver el veredicto "Compile Error"
