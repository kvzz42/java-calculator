import java.util.*;
import java.util.function.*;

/**
 * The {@code PostfixCalculator} class provides a full string-based
 * implementation of the {@code Calculator} interface. It supports evaluation of
 * postfix expressions using default or client-provided mappings from operators
 * to their associated functions.
 * <p>
 * A {@code PostfixCalculator} object tracks the current operator mappings and
 * the last answer returned through the {@code evaluate} method, which can be
 * accessed in expressions with the string {@code "ans"}. For this reason,
 * clients should not use operator mappings with {@code "ans"} as a key, as such
 * a key would never be used.
 *
 * @author Kevin Zhu
 */
public class PostfixCalculator extends AbstractCalculator
{
    /** The last answer returned by this calculator. */
    private double lastAnswer;
    
    /**
     * Constructs a {@code PostfixCalculator} object with the default operator
     * mappings from the {@code AbstractCalculator} class.
     */
    public PostfixCalculator()
    {
        super();
        lastAnswer = Double.NaN;
    }
    
    /**
     * Constructs a {@code PostfixCalculator} object with the specified operator
     * mappings. Once set, these mappings cannot be modified except through the
     * {@code setUnaryOps} and {@code setBinaryOps} methods.
     *
     * @param unaryOps  the map from unary operators to their associated
     *                  functions to use in this calculator
     * @param binaryOps the map from binary operators to their associated
     *                  functions to use in this calculator
     */
    public PostfixCalculator(Map<String, DoubleUnaryOperator> unaryOps,
                             Map<String, DoubleBinaryOperator> binaryOps)
    {
        setUnaryOps(unaryOps);
        setBinaryOps(binaryOps);
        lastAnswer = Double.NaN;
    }
    
    /**
     * {@inheritDoc}
     * <p>
     * This implementation reads expressions in postfix notation. A postfix
     * expression is recursively defined as either a number, a postfix
     * expression followed by a unary operator, or two postfix expressions
     * followed by a binary operator.
     */
    @Override
    public double evaluate(String expression)
    {
        Deque<Double> operands = new ArrayDeque<>();
        return lastAnswer = parseExpression(expression, operands) ?
                operands.peek() : Double.NaN;
    }
    
    /**
     * Processes the specified postfix expression using the specified
     * {@code Deque} as auxiliary storage, returning a success value. If the
     * parsing is successful, the {@code Deque} will hold exactly one element
     * (the value of the whole expression).
     *
     * @param  expression the input expression to evaluate
     * @param  operands   the operand stack
     * @return            {@code true} if the expression was successfully
     *                    parsed; {@code false} otherwise
     */
    private boolean parseExpression(String expression, Deque<Double> operands)
    {
        Scanner tokens = new Scanner(expression);
        while (tokens.hasNext()) {
            String operator; // declaration simplifies if-else branch structure
            if (tokens.hasNextDouble()) {
                operands.push(tokens.nextDouble());
            } else if ((operator = tokens.next()).equalsIgnoreCase("ans")) {
                operands.push(lastAnswer);
            } else if (getUnaryOps().containsKey(operator) &&
                    !operands.isEmpty()) { // unary needs one operand
                operands.push(evalUnary(operator, operands.pop()));
            } else if (getBinaryOps().containsKey(operator) &&
                    operands.size() > 1) { // binary needs two operands
                double operand2 = operands.pop();
                double operand1 = operands.pop();
                operands.push(evalBinary(operator, operand1, operand2));
            } else { // bad operator or not enough operands
                return false;
            }
        }
        return operands.size() == 1;
    }
    
    /**
     * Sets the current map from unary operators to their associated functions
     * to the specified map.
     *
     * @param unaryOps the map to set the current map to
     */
    @Override
    public void setUnaryOps(Map<String, DoubleUnaryOperator> unaryOps)
    {
        super.setUnaryOps(unaryOps);
    }
    
    /**
     * Sets the current map from binary operators to their associated functions
     * to the specified map.
     *
     * @param binaryOps the map to set the current map to
     */
    @Override
    public void setBinaryOps(Map<String, DoubleBinaryOperator> binaryOps)
    {
        super.setBinaryOps(binaryOps);
    }
}