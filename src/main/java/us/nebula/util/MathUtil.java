package us.nebula.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author xgraza
 * @since 03/02/25
 */
public final class MathUtil
{
    public static double round(final double value, final int scale)
    {
        return new BigDecimal(value).setScale(scale, RoundingMode.HALF_DOWN).doubleValue();
    }
}
