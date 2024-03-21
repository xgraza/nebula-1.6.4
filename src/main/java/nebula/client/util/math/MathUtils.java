package nebula.client.util.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Random;

import static java.lang.String.format;

/**
 * @author Gavin
 * @since 08/17/23
 */
public class MathUtils {

  public static final Random RNG = new SecureRandom();

  /**
   * Round a double to an amount of decimal places
   * @param value the value
   * @param scale the scale
   * @return the rounded double
   */
  public static double round(double value, int scale) {
    return new BigDecimal(value)
      .setScale(scale, RoundingMode.HALF_DOWN)
      .doubleValue();
  }

  public static String formatSize(int byteSize) {

    double mb = (double) byteSize / (1_000_000);
    double gb = mb / 1000;

    return format("%.3fGB %.3fMB", gb, mb);
  }
}
