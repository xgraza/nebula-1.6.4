package wtf.nebula.client.utils.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import wtf.nebula.client.utils.client.Wrapper;

public class RotationUtils implements Wrapper {

    public static float[] calcAngles(Entity entity) {

        double diffX = entity.posX - mc.thePlayer.posX;
        double diffY = (entity.boundingBox.minY + 1.620) - mc.thePlayer.posY;
        double diffZ = entity.posZ - mc.thePlayer.posZ;

        //Vec3 diff = getEyes(mc.thePlayer).subtract(getEyes(entity));

        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

        float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0);
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, dist)));

        yaw = MathHelper.wrapAngleTo180_float(yaw);

        return new float[] { yaw, pitch };
    }

    public static float[] calcAngles(Vec3 pos, EnumFacing facing) {
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY - 1.620;
        double z = mc.thePlayer.posZ;

        Vec3 diff = new Vec3(
                Vec3.fakePool,
                pos.xCoord + 0.5 - x + facing.getFrontOffsetX() / 2.0,
                pos.yCoord + 0.5,
                pos.zCoord + 0.5 - z + facing.getFrontOffsetZ() / 2.0
        );

        double dist = MathHelper.sqrt_double(diff.xCoord * diff.xCoord + diff.zCoord * diff.zCoord);

        float yaw = (float) (Math.toDegrees(Math.atan2(diff.zCoord, diff.xCoord)) - 90.0);
        float pitch = (float) (Math.toDegrees(Math.atan2(y + 1.62 - diff.yCoord, dist)));

        yaw = MathHelper.wrapAngleTo180_float(yaw);

        return new float[] { yaw, pitch };
    }

    public static float[] calcAngles(Vec3 vec) {
        double diffX = vec.xCoord - mc.thePlayer.posX;
        double diffY = vec.yCoord - mc.thePlayer.posY;
        double diffZ = vec.zCoord - mc.thePlayer.posZ;

        //Vec3 diff = getEyes(mc.thePlayer).subtract(getEyes(entity));

        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

        float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0);
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, dist)));

        yaw = MathHelper.wrapAngleTo180_float(yaw);

        return new float[] { yaw, pitch };
    }
}
