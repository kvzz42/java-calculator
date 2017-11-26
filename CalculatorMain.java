import java.util.*;
import java.util.function.*;

/**
 * The {@code CalculatorMain} class is a console application that
 * demonstrates the functionality of the {@code Calculator<String>}
 * implementations. It contains methods and constants allowing users to interact
 * with the various calculators and make requests to the application to display
 * informative text or change their settings.
 *
 * @author Kevin Zhu
 */
public class CalculatorMain
{
    /** The map of available informational texts for the user. */
    public static final Map<String, Runnable> INFO_TEXT =
        Map.ofEntries(
            Map.entry("help", CalculatorMain::printHelp)
        );
    
    /** The map of available calculators. */
    public static final Map<String, Calculator<String>> CALCULATORS =
        Map.ofEntries(
            Map.entry("prefix", new PrefixCalculator()),
            Map.entry("infix", new InfixCalculator()),
            Map.entry("postfix", new PostfixCalculator())
        );
    
    /** The map of possible user requests during application runtime. */
    public static final Map<Set<String>, Consumer<String>> REQUESTS =
        Map.ofEntries(
            Map.entry(INFO_TEXT.keySet(), CalculatorMain::printInfo),
            Map.entry(CALCULATORS.keySet(), CalculatorMain::switchCalc)
        );
    
    /** The calculator used across this application. */
    private static Calculator<String> calc;
    
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
        calc = new InfixCalculator();
        Scanner console = new Scanner(System.in);
        String input = console.nextLine().trim()
                              .replaceAll("[ \t]+", " ").toLowerCase();
        while (!input.equals("quit")) {
            Set<String> requestType = requestType(input);
            if (requestType == null) { // no request made
                double ans = calc.evaluate(input);
                if (Double.isNaN(ans)) {
                    System.out.println("ERROR\n");
                } else {
                    System.out.printf("%s = %.2f%n%n", input, ans);
                }
            } else {
                REQUESTS.get(requestType).accept(input);
            }
            System.out.print(">> ");
            input = console.nextLine().trim()
                           .replaceAll("[ \t]+", " ").toLowerCase();
        }
    }
    
    /**
     * Returns the key set within {@code REQUESTS} that the specified input
     * string is contained in, or {@code null} if it is not a valid request.
     *
     * @param  input the user input
     * @return       the key set within {@code REQUESTS} that the specified
     *               input string is contained in, or {@code null} if it is not
     *               a valid request
     */
    private static Set<String> requestType(String input)
    {
        for (Set<String> set : REQUESTS.keySet()) {
            if (set.contains(input)) {
                return set;
            }
        }
        return null;
    }
    
    /**
     * Prints the requested informative text based on user input, assuming it is
     * legal.
     *
     * @param input the user input
     */
    private static void printInfo(String input)
    {
        INFO_TEXT.get(input).run();
    }
    
    /** Prints the help text for this application. */
    private static void printHelp()
    {
        System.out.println(
            "\nCalculator commands:\n" +
            "help\t\tView this help text.\n" +
//            "mode\t\tView current calculator settings.\n" +
            "quit\t\tExit the program.\n" +
            "prefix\t\tSwitch to prefix calculator (e.g. + 1 1 = 2).\n" +
            "infix\t\tSwitch to infix calculator (e.g. 1 + 1 = 2).\n" +
            "postfix\t\tSwitch to postfix calculator (e.g. 1 1 + = 2).\n" +
//            "radians\t\tSwitch to radians.\n" +
//            "degrees\t\tSwitch to degrees.\n" +
//            "precision\tSelect a different floating point precision. " +
//            "Use high values with caution.\n\n" +
            "Calculator operations:\n" +
            "+ - * / % ^\t\t\t\tAdd, subtract, multiply, divide, modulo, " +
            "power.\n" +
//            "P C\t\t\tPermutation, combination.\n" +
            "! sqrt ~ abs\t\t\tFactorial, square root, round, absolute " +
            "value.\n" +
            "(a)sin (a)cos (a)tan\tTrigonometric functions and inverses.\n" +
            "(a)sec (a)csc (a)cot\tReciprocal trigonometric functions and " +
            "inverses. \n" +
            "sinh cosh tanh\t\t\tHyperbolic trigonometric functions.\n" +
            "exp ln log10\t\t\tExponentiate, natural log, base 10 log.\n"
        );
    }
    
    /**
     * Switches the current calculator to the requested calculator based on user
     * input, assuming it is legal.
     *
     * @param input the user input
     */
    private static void switchCalc(String input)
    {
        calc = CALCULATORS.get(input);
        System.out.println("Switched to " + input + " calculator.\n");
    }
}