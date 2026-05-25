package us.nebula.client.util.math;

import net.minecraft.client.Minecraft;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public final class AngleUtil
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    public static MovingObjectPosition raytrace(final double reach, final float yaw, final float pitch)
    {
        Vec3 var4 = MC.thePlayer.getPosition(1.0f);
        Vec3 var5 = getLookVec(yaw, pitch);
        Vec3 var6 = var4.addVector(var5.xCoord * reach, var5.yCoord * reach, var5.zCoord * reach);
        return MC.theWorld.func_147447_a(var4, var6, false, false, true);
    }

    public static Vec3 getLookVec(final float yaw, final float pitch)
    {
        float var2 = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float var3 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float var4 = -MathHelper.cos(-pitch * 0.017453292F);
        float var5 = MathHelper.sin(-pitch * 0.017453292F);
        return MC.theWorld.getWorldVec3Pool().getVecFromPool(var3 * var4, var5, var2 * var4);
    }

    public static float[] anglesToBlock(final BlockPos pos, final EnumFacing face)
    {
        final Vec3 eyes = MC.thePlayer.getPosition(1.0f);

        int offsetX = face.getFrontOffsetX();
        int offsetY = face.getFrontOffsetY();
        int offsetZ = face.getFrontOffsetZ();

        double deltaX = (Math.floor(eyes.xCoord) + 0.5)
                - ((double) pos.getX() + 0.5 - (offsetX * 0.5));
        double deltaZ = (Math.floor(eyes.zCoord) + 0.5)
                - ((double) pos.getZ() + 0.5 - (offsetZ * 0.5));

        double dist = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) (Math.toDegrees(Math.atan2(deltaZ, deltaX)) + 90.0f);
        float pitch = (float) Math.toDegrees(Math.atan2(
                eyes.yCoord - (pos.getY() - 0.5 - (offsetY * 0.5)), dist));

        return new float[]{ yaw, pitch };
    }
}
