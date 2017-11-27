import java.util.*;
import java.util.function.*;

/**
 * The {@code InfixCalculator} class provides a full string-based implementation
 * of the {@code Calculator} interface. It supports evaluation of infix
 * expressions using default or client-provided mappings from operators to their
 * associated functions.
 * <p>
 * An {@code InfixCalculator} object tracks the current operator mappings and
 * the last answer returned through the {@code evaluate} method, which can be
 * accessed in expressions with the string {@code "ans"}. For this reason,
 * clients should not use operator mappings with {@code "ans"} as a key, as such
 * a key would never be used.
 *
 * @author Kevin Zhu
 */
public class InfixCalculator extends AbstractCalculator
{
    /** A default map from operators to their precedence values. */
    public static final Map<String, Integer> DEFAULT_OP_PRECEDENCES =
        Map.ofEntries(
            Map.entry("!", 4), Map.entry("sqrt", 4), Map.entry("~", 4),
            Map.entry("abs", 4), Map.entry("sin", 4), Map.entry("cos", 4),
            Map.entry("tan", 4), Map.entry("asin", 4), Map.entry("acos", 4),
            Map.entry("atan", 4), Map.entry("sec", 4), Map.entry("csc", 4),
            Map.entry("cot", 4), Map.entry("asec", 4), Map.entry("acsc", 4),
            Map.entry("acot", 4), Map.entry("sinh", 4), Map.entry("cosh", 4),
            Map.entry("tanh", 4), Map.entry("exp", 4), Map.entry("ln", 4),
            Map.entry("log10", 4), Map.entry("^", 3), Map.entry("*", 2),
            Map.entry("/", 2), Map.entry("%", 2), Map.entry("+", 1),
            Map.entry("-", 1)
        );

    /**
     * A default map from operators to their associativity values ({@code true}
     * for left associativity, {@code false} for right).
     */
    public static final Map<String, Boolean> DEFAULT_OP_ASSOCIATIVITY =
        Map.ofEntries(
            Map.entry("!", false), Map.entry("sqrt", false),
            Map.entry("~", false), Map.entry("abs", false),
            Map.entry("sin", false), Map.entry("cos", false),
            Map.entry("tan", false), Map.entry("asin", false),
            Map.entry("acos", false), Map.entry("atan", false),
            Map.entry("sec", false), Map.entry("csc", false),
            Map.entry("cot", false), Map.entry("asec", false),
            Map.entry("acsc", false), Map.entry("acot", false),
            Map.entry("sinh", false), Map.entry("cosh", false),
            Map.entry("tanh", false), Map.entry("exp", false),
            Map.entry("ln", false), Map.entry("log10", false),
            Map.entry("^", false), Map.entry("*", true), Map.entry("/", true),
            Map.entry("%", true), Map.entry("+", true), Map.entry("-", true)
        );
    
    /** The calculator this calculator delegates calculations to. */
    private Calculator<String> postfixCalc;
    
    /**
     * Constructs an {@code InfixCalculator} object with the default operator
     * mappings from the {@code AbstractCalculator} class.
     */
    public InfixCalculator()
    {
        postfixCalc = new PostfixCalculator();
    }
    
    /**
     * {@inheritDoc}
     * <p>
     * This implementation reads expressions in infix notation, the typical
     * notation in which operators are placed between operands.
     */
    @Override
    public double evaluate(String expression)
    {
        expression = expression.replaceAll("[(]", " ( ")
                               .replaceAll("[)]", " ) ");
        StringBuilder output = new StringBuilder(expression.length() + 1);
        Deque<String> operators = new ArrayDeque<>();
        return postfixCalc.evaluate(
                parseExpression(expression, output, operators) ?
                output.toString() : ""); // "" sets last answer to Double.NaN
    }
    
    /**
     * Processes the specified infix expression into its corresponding postfix
     * expression using the specified {@code StringBuilder} as output and {@code
     * Deque} as auxiliary storage. Returns the success value of the processing.
     * If parsing is successful, {@code output} will hold the resulting postfix
     * expression.
     *
     * @param  expression the input expression to evaluate
     * @param  output     the output to build the postfix expression with
     * @param  operators  the operator stack
     * @return            {@code true} if the expression was successfully
     *                    parsed; {@code false} otherwise
     */
    private boolean parseExpression(String expression, StringBuilder output,
                                    Deque<String> operators)
    {
        Scanner tokens = new Scanner(expression);
        boolean numOkay = true; // flag for when it is legal to find a number
        while (tokens.hasNext()) {
            String operator; // declaration simplifies if-else branch structure
            if (tokens.hasNextDouble() && numOkay) {
                output.append(tokens.nextDouble()).append(' ');
                numOkay = false;
            } else if ((operator = tokens.next()).equals("ans") && numOkay) {
                output.append(operator).append(' ');
                numOkay = false;
            } else if (DEFAULT_OP_PRECEDENCES.containsKey(operator)) {
                while (!canPush(operator, operators)) {
                    output.append(operators.pop()).append(' ');
                }
                operators.push(operator);
                numOkay = numOkay || getBinaryOps().containsKey(operator);
            } else if (!isLegalParenthesis(operator, operators,
                                           output, numOkay)) {
                return false; // invalid token
            }
        }
        while (!operators.isEmpty()) {
            output.append(operators.pop()).append(' ');
        }
        return output.indexOf("(") < 0; // no mismatched "("s allowed
    }
    
    /**
     * Returns {@code true} if the specified operator can be pushed into the
     * operator stack according to precedence and associativity rules; {@code
     * false} otherwise.
     *
     * @param  operator  the operator to be pushed
     * @param  operators the operator stack to push the operator onto
     * @return           {@code true} if the specified operator can be pushed
     *                   into the operator stack according to precedence and
     *                   associativity rules; {@code false} otherwise
     */
    private boolean canPush(String operator, Deque<String> operators)
    {
        if (operators.isEmpty() || operators.peek().equals("(")) {
            return true;
        }
        int precedence = DEFAULT_OP_PRECEDENCES.get(operator);
        int otherPrecedence = DEFAULT_OP_PRECEDENCES.get(operators.peek());
        return precedence > otherPrecedence || (precedence == otherPrecedence &&
                !DEFAULT_OP_ASSOCIATIVITY.get(operator));
    }
    
    /**
     * Processes the specified string appropriately if it is a legal
     * parenthesis, returning {@code true} if the parenthesis was legal and
     * {@code false} if the string was not a parenthesis or the parenthesis was
     * illegal.
     * <p>
     * Left parentheses are considered legal if they could be replaced by a
     * number (as legal left parentheses are the beginning of a full numeric
     * expression), whereas right parentheses pop the operator stack until a
     * matching left parenthesis is found, making it legal.
     *
     * @param  operator  the string to process
     * @param  operators the operator stack
     * @param  output    the output where the postfix expression is being built
     * @param  numOkay   flag for if it is currently legal to encounter a number
     * @return           {@code true} if the parenthesis was legal and
     *                   {@code false} if the string was not a parenthesis or
     *                   the parenthesis was illegal
     */
    private boolean isLegalParenthesis(String operator, Deque<String> operators,
                                       StringBuilder output, boolean numOkay)
    {
        if (operator.equals("(")) {
            operators.push(operator);
            return numOkay;
        } else if (operator.equals(")")) {
            while (!operators.isEmpty() && !operators.peek().equals("(")) {
                output.append(operators.pop()).append(' ');
            }
            if (!operators.isEmpty()) {
                operators.pop();
                return true;
            } // else, "(" not found
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException unsupported operation
     */
    @Override
    public void setUnaryOps(Map<String, DoubleUnaryOperator> unaryOps)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * {@inheritDoc}
     * @throws UnsupportedOperationException unsupported operation
     */
    @Override
    public void setBinaryOps(Map<String, DoubleBinaryOperator> binaryOps)
    {
        throw new UnsupportedOperationException();
    }
}