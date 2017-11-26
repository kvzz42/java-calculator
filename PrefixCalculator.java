import java.util.*;
import java.util.function.*;

/**
 * The {@code PrefixCalculator} class provides a full string-based
 * implementation of the {@code Calculator} interface. It supports evaluation
 * of prefix expressions using default or client-provided mappings from
 * operators to their associated functions.
 * <p>
 * A {@code PrefixCalculator} object tracks the current operator mappings and
 * the last answer returned through the {@code evaluate} method, which can be
 * accessed in expressions with the string {@code "ans"}. For this reason,
 * clients should not use operator mappings with {@code "ans"} as a key, as such
 * a key would never be used.
 *
 * @author Kevin Zhu
 */
public class PrefixCalculator extends AbstractCalculator
{
    /** The last answer returned by this calculator. */
    private double lastAnswer;
    
    /**
     * Constructs a {@code PrefixCalculator} object with the default operator
     * mappings from the {@code AbstractCalculator} class.
     */
    public PrefixCalculator()
    {
        super();
        lastAnswer = Double.NaN;
    }
    
    /**
     * Constructs a {@code PrefixCalculator} object with the specified
     * operator mappings. Once set, these mappings cannot be modified except
     * through the {@code setUnaryOps} and {@code setBinaryOps} methods.
     *
     * @param unaryOps  the map from unary operators to their associated
     *                  functions to use in this calculator
     * @param binaryOps the map from binary operators to their associated
     *                  functions to use in this calculator
     */
    public PrefixCalculator(Map<String, DoubleUnaryOperator> unaryOps,
                            Map<String, DoubleBinaryOperator> binaryOps)
    {
        setUnaryOps(unaryOps);
        setBinaryOps(binaryOps);
        lastAnswer = Double.NaN;
    }
    
    /**
     * {@inheritDoc}
     * <p>
     * This implementation reads expressions in prefix notation. A prefix
     * expression is recursively defined as either a number, a unary operator
     * followed by a prefix expression, or a binary operator followed by two
     * prefix expressions.
     */
    @Override
    public double evaluate(String expression)
    {
        Scanner tokens = new Scanner(expression);
        lastAnswer = evaluate(tokens);
        if (tokens.hasNext()) { // input not completely parsed
            lastAnswer = Double.NaN;
        }
        return lastAnswer;
    }
    
    /**
     * Evaluates and returns the value of the next full prefix expression in the
     * specified {@code Scanner}, or {@code Double.NaN} in the case of invalid
     * input.
     *
     * @param  tokens the {@code Scanner} containing the tokens of the
     *                expression
     * @return        the value of the next full prefix expression in the
     *                specified {@code Scanner}, or {@code Double.NaN} in the
     *                case of invalid input
     */
    private double evaluate(Scanner tokens)
    {
        if (tokens.hasNextDouble()) {
            return tokens.nextDouble();
        } else if (tokens.hasNext()) {
            // next token not a number, so assumed to be
            // an operator or "ans" (for last answer)
            String operator = tokens.next();
            if (operator.equals("ans")) {
                return lastAnswer;
            } else if (getUnaryOps().containsKey(operator)) {
                return evalUnary(operator, evaluate(tokens));
            } else if (getBinaryOps().containsKey(operator)) {
                double operand1 = evaluate(tokens);
                double operand2 = evaluate(tokens);
                return evalBinary(operator, operand1, operand2);
            } // else, invalid operator
        } // else, premature end to input encountered
        return Double.NaN;
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