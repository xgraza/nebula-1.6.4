package ez.nebula.client.util.math;

import net.minecraft.client.Minecraft;
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
    private static final Minecraft MC = Minecraft.getMinecraft();
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
        return Math.sqrt(getDistanceSq(p, p2));
    }

    public static double getDistanceFromPlayerSq(final BlockPos pos)
    {
        double dX = MC.thePlayer.posX - pos.getX();
        double dY = MC.thePlayer.boundingBox.minY - pos.getY();
        double dZ = MC.thePlayer.posZ - pos.getZ();
        return dX * dX + dY * dY + dZ * dZ;
    }

    public static double getDistanceFromPlayer(final BlockPos pos)
    {
        return Math.sqrt(getDistanceFromPlayerSq(pos));
    }

    public static double getDistanceFromPlayer(final BlockPos pos, final boolean offset)
    {
        if (offset)
        {
            return getDistanceFromPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        }
        return getDistanceFromPlayer(pos);
    }

    public static double getDistanceFromPlayer(final double x, final double y, final double z)
    {
        double dX = MC.thePlayer.posX - x;
        double dY = MC.thePlayer.boundingBox.minY - y;
        double dZ = MC.thePlayer.posZ - z;
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
