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

    public static double getDistance(final BlockPos p, final BlockPos p2)
    {
        double dX = p.getX() - p2.getX();
        double dY = p.getY() - p2.getY();
        double dZ = p.getZ() - p2.getZ();
        return Math.sqrt(dX * dX + dY * dY + dZ * dZ);
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
