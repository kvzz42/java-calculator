import java.util.*;

/**
 * The {@code CalculatorMain} class is a console application that
 * demonstrates the functionality of the {@code Calculator<String>}
 * implementations.
 *
 * @author Kevin Zhu
 */
public class CalculatorMain
{
    /**
     * The entry point of the console application. Provides user interaction
     * through the console, allowing users to enter calculator expressions to
     * evaluate and change their settings until there is a request to quit.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        System.out.println("Welcome to Kevin's Java calculator! Enter " +
                "\"help\" to view calculator commands and operations.\n");
        System.out.print(">> ");
        Calculator<String> calc =
                new PostfixCalculator(); // change to test different calculators
        Scanner console = new Scanner(System.in);
        String input = console.nextLine().trim().replaceAll("[ \t]+", " ");
        while (!input.equalsIgnoreCase("quit")) {
            double ans = calc.evaluate(input);
            if (Double.isNaN(ans)) {
                System.out.println("ERROR\n");
            } else {
                System.out.printf("%s = %.2f%n%n", input, ans);
            }
            System.out.print(">> ");
            input = console.nextLine().trim().replaceAll("[ \t]+", " ");
        }
    }
}