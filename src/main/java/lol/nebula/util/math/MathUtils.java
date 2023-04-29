package lol.nebula.util.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class MathUtils {

    /**
     * The random number generator instance
     */
    private static final Random RNG = new Random();

    /**
     * Round a double to an amount of decimal places
     * @param value the value
     * @param scale the scale
     * @return the rounded double
     */
    public static double round(double value, int scale) {
        return new BigDecimal(value).setScale(scale, RoundingMode.HALF_DOWN).doubleValue();
    }

    /**
     * Gets the random number generator instance
     * @return the instance of the RNG
     */
    public static Random getRNG() {
        return RNG;
    }
}
