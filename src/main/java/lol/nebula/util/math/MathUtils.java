package lol.nebula.util.math;

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
     * Gets the random number generator instance
     * @return the instance of the RNG
     */
    public static Random getRNG() {
        return RNG;
    }
}
