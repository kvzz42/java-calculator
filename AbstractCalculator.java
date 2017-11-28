import java.util.*;
import java.util.function.*;

/**
 * The {@code AbstractCalculator} abstract class provides a skeletal
 * implementation of a string-based version of the {@code Calculator} interface.
 * It contains methods for evaluating basic expressions whose tokens have
 * already been parsed into operators and operands, along with methods for
 * viewing the operators currently in use.
 *
 * @author Kevin Zhu
 */
public abstract class AbstractCalculator implements Calculator<String>
{
    /** A default map from unary operators to their associated functions. */
    public static final Map<String, DoubleUnaryOperator> DEFAULT_UNARY_OPS =
        Map.ofEntries(
            Map.entry("!", Calculator::factorial),
            Map.entry("sqrt", Math::sqrt),
            Map.entry("~", Math::round),
            Map.entry("abs", Math::abs),
            Map.entry("sin", Math::sin),
            Map.entry("cos", Math::cos),
            Map.entry("tan", Math::tan),
            Map.entry("asin", Math::asin),
            Map.entry("acos", Math::acos),
            Map.entry("atan", Math::atan),
            Map.entry("sec", a -> 1 / Math.cos(a)),
            Map.entry("csc", a -> 1 / Math.sin(a)),
            Map.entry("cot", a -> 1 / Math.tan(a)),
            Map.entry("asec", a -> Math.acos(1 / a)),
            Map.entry("acsc", a -> Math.asin(1 / a)),
            Map.entry("acot", a -> Math.atan(1 / a)),
            Map.entry("sinh", Math::sinh),
            Map.entry("cosh", Math::cosh),
            Map.entry("tanh", Math::tanh),
            Map.entry("exp", Math::exp),
            Map.entry("ln", Math::log),
            Map.entry("log10", Math::log10)
        );
    
    /** A default map from binary operators to their associated functions. */
    public static final Map<String, DoubleBinaryOperator> DEFAULT_BINARY_OPS =
        Map.ofEntries(
            Map.entry("+", Double::sum),
            Map.entry("-", (a, b) -> a - b),
            Map.entry("*", (a, b) -> a * b),
            Map.entry("/", (a, b) -> a / b),
            Map.entry("%", (a, b) -> a % b),
            Map.entry("^", Math::pow)
        );
    
    /** The default precision of this calculator. */
    public static final int DEFAULT_PRECISION = 2;
    
    /** Internal constant for allowing different angle units. */
    private static final Set<String> TRIG_OPS =
        Set.of("sin", "cos", "tan", "sec", "csc", "cot");
    
    /** Internal constant for allowing different angle units. */
    private static final Set<String> INV_TRIG_OPS =
        Set.of("asin", "acos", "atan", "asec", "acsc", "acot");
    
    /** The map from unary operators to their associated functions. */
    private Map<String, DoubleUnaryOperator> unaryOps;
    
    /** The map from binary operators to their associated functions. */
    private Map<String, DoubleBinaryOperator> binaryOps;
    
    /** The available angle units for this calculator. */
    public enum AngleUnits { RADIANS, DEGREES }
    
    /** The angle units this calculator uses. */
    private AngleUnits angleUnits;
    
    /** The floating point precision of this calculator. */
    private int precision;
    
    /** Sole constructor for use by subclasses, if necessary. */
    protected AbstractCalculator()
    {
        unaryOps = DEFAULT_UNARY_OPS;
        binaryOps = DEFAULT_BINARY_OPS;
        angleUnits = AngleUnits.RADIANS;
        precision = DEFAULT_PRECISION;
    }
    
    /**
     * Returns an unmodifiable view of the current map from unary operators to
     * their associated functions.
     *
     * @return an unmodifiable view of the current map from unary operators to
     *         their associated functions
     */
    public Map<String, DoubleUnaryOperator> getUnaryOps()
    {
        return unaryOps;
    }
    
    /**
     * Sets the current map from unary operators to their associated functions
     * to the specified map (optional operation).
     *
     * @param  unaryOps                      the map to set the current map to
     * @throws UnsupportedOperationException if this calculator does not support
     *                                       this operation
     */
    public void setUnaryOps(Map<String, DoubleUnaryOperator> unaryOps)
    {
        this.unaryOps = Collections.unmodifiableMap(new HashMap<>(unaryOps));
    }
    
    /**
     * Returns an unmodifiable view of the current map from binary operators to
     * their associated functions.
     *
     * @return an unmodifiable view of the current map from binary operators to
     *         their associated functions
     */
    public Map<String, DoubleBinaryOperator> getBinaryOps()
    {
        return binaryOps;
    }
    
    /**
     * Sets the current map from binary operators to their associated functions
     * to the specified map (optional operation).
     *
     * @param  binaryOps                     the map to set the current map to
     * @throws UnsupportedOperationException if this calculator does not support
     *                                       this operation
     */
    public void setBinaryOps(Map<String, DoubleBinaryOperator> binaryOps)
    {
        this.binaryOps = Collections.unmodifiableMap(new HashMap<>(binaryOps));
    }
    
    /**
     * Returns the angle units this calculator is using.
     *
     * @return the angle units this calculator is using.
     */
    public AngleUnits getAngleUnits()
    {
        return angleUnits;
    }
    
    /**
     * Sets the angle units of this calculator to the parameter.
     *
     * @param angleUnits the angle units to set this calculator to
     */
    public void setAngleUnits(AngleUnits angleUnits)
    {
        this.angleUnits = angleUnits;
    }
    
    /**
     * Returns the floating point precision of this calculator.
     *
     * @return the floating point precision of this calculator
     */
    public int getPrecision()
    {
        return precision;
    }
    
    /**
     * Sets the floating point precision of this calculator to the parameter.
     *
     * @param precision the desired floating point precision
     */
    public void setPrecision(int precision)
    {
        this.precision = precision;
    }
    
    /** {@inheritDoc} */
    @Override
    public String settings()
    {
        return "angles: " + (angleUnits == AngleUnits.RADIANS ?
               "radians" : "degrees") + ", precision: " + precision;
    }
    
    /**
     * Evaluates and returns the value of applying the specified unary operator
     * to the specified operand, or {@code Double.NaN} if no such operator is
     * supported.
     *
     * @param  operator the unary operator to apply
     * @param  operand  the operand to apply the operator to
     * @return          the value of applying the specified unary operator to
     *                  the specified operand, or {@code Double.NaN} if no such
     *                  operator is supported
     */
    protected double evalUnary(String operator, double operand)
    {
        if (unaryOps.containsKey(operator)) {
            if (TRIG_OPS.contains(operator)) {
                operand = ensureAngleUnits(operand, angleUnits,
                                           AngleUnits.RADIANS);
            }
            double result = unaryOps.get(operator).applyAsDouble(operand);
            if (INV_TRIG_OPS.contains(operator)) {
                result = ensureAngleUnits(result, AngleUnits.RADIANS,
                                          angleUnits);
            }
            return result;
        }
        return Double.NaN;
    }
    
    /**
     * Returns the correct value of the passed angle according to the units of
     * the passed angle and the units required.
     *
     * @param  angle   the angle to ensure
     * @param  current the units of {@code angle}
     * @param  needed  the units needed
     * @return         the correct value of the passed angle according to the
     *                 units of the passed angle and the units required
     */
    private double ensureAngleUnits(double angle, AngleUnits current,
                                    AngleUnits needed)
    {
        if (current == needed) { // nothing to do
            return angle;
        } else if (current == AngleUnits.RADIANS) {
            // angle is in radians, needs to be degrees
            return Math.toDegrees(angle);
        } else { // angle is in degrees, needs to be radians
            return Math.toRadians(angle);
        }
    }
    
    /**
     * Evaluates and returns the value of applying the specified binary operator
     * to the specified operands (left to right), or {@code Double.NaN} if no
     * such operator is supported.
     *
     * @param  operator the binary operator to apply
     * @param  operand1 the first operand
     * @param  operand2 the second operand
     * @return          the value of applying the specified binary operator to
     *                  the specified operands, or {@code Double.NaN} if no such
     *                  operator is supported
     */
    protected double evalBinary(String operator, double operand1,
                                double operand2)
    {
        return binaryOps.containsKey(operator) ?
               binaryOps.get(operator).applyAsDouble(operand1, operand2) :
               Double.NaN;
    }
}