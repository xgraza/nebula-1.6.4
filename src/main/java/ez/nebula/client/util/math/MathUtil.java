package ez.nebula.client.util.math;

import net.minecraft.entity.Entity;
import net.minecraft.src.BlockPos;
import net.minecraft.util.Vec3;

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

    public static double lerp(final double value, final double prevValue, final float partialTicks)
    {
        return prevValue + (value - prevValue) * partialTicks;
    }

    public static Vec3 lerpEntity(final Entity entity, final float partialTicks)
    {
        return Vec3.createVectorHelper(lerp(entity.posX, entity.prevPosX, partialTicks),
                lerp(entity.posY, entity.prevPosY, partialTicks),
                lerp(entity.posZ, entity.prevPosZ, partialTicks));
    }

    public static double getDistanceSq(final BlockPos p, final BlockPos p2)
    {
        double dX = p.getX() - p2.getX();
        double dY = p.getY() - p2.getY();
        double dZ = p.getZ() - p2.getZ();
        return dX * dX + dY * dY + dZ * dZ;
    }

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
