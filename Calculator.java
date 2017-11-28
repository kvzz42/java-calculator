/**
 * The {@code Calculator} interface describes classes that can evaluate and
 * return the value of expressions of a particular type. It also contains
 * static methods of mathematical functions that the standard library does not
 * provide.
 *
 * @param <T> the type of expression this calculator can evaluate
 *
 * @author Kevin Zhu
 */
public interface Calculator<T>
{
    /**
     * Evaluates and returns the value of the given input expression as a {@code
     * double}, or {@code Double.NaN} if the expression is invalid or produces a
     * not-a-number value.
     *
     * @param  expression the input expression to evaluate
     * @return            the value of the specified expression, or
     *                    {@code Double.NaN} if the expression is invalid or
     *                    produces a not-a-number value
     */
    double evaluate(T expression);
    
    /**
     * Returns a string representing the relevant settings of this calculator.
     *
     * @return a string representing the relevant settings of this calculator
     */
    String settings();
    
    /**
     * Calculates and returns the mathematical factorial of the specified {@code
     * double}. If the specified {@code double} is not a whole number or is
     * negative, returns {@code Double.NaN} instead.
     *
     * @param  n the {@code double} to calculate the factorial of
     * @return   the factorial of {@code n}, or {@code Double.NaN} if {@code n}
     *           is not a whole number or is negative
     */
    static double factorial(double n)
    {
        if ((int) n != n || n < 0) {
            return Double.NaN;
        }
        long product = 1;
        for (long i = (long) n; i > 1; --i) {
            product *= i;
        }
        return product;
    }
}