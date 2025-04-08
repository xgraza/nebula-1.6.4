package us.nebula.util.math;

import net.minecraft.src.BlockPos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * @author xgraza
 * @since 03/02/25
 */
public final class MathUtil
{
    public static final Random RNG = new Random();

    public static double distanceSq(final BlockPos p, final BlockPos p2)
    {
        double var7 = p.getX() - p2.getX();
        double var9 = p.getY() - p2.getY();
        double var11 = p.getZ() - p2.getZ();
        return var7 * var7 + var9 * var9 + var11 * var11;
    }

    public static int random(final int min, final int max)
    {
        return RNG.nextInt((max + 1) - min) + min;
    }

    public static double round(final double value, final int scale)
    {
        return new BigDecimal(value).setScale(scale, RoundingMode.HALF_DOWN).doubleValue();
    }
}
