package wtf.nebula.util;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.Vec3;

import java.security.SecureRandom;
import java.util.Random;

public class MathUtil implements Globals {
    public static final Random RNG = new SecureRandom();

    public static double interpolate(double start, double end, float partialTicks) {
        return end + partialTicks * (start - end);
    }

    public static float calcYawTo(EntityLivingBase target) {
        Vec3 diff = diff(getEyes(target), getEyes(mc.thePlayer));
        float yaw = (float) (Math.atan2(diff.zCoord, diff.xCoord) * 180.0 / Math.PI - 90.0);

        if (yaw < 0.0f) {
            yaw += 360.0f;
        }

        return yaw;
    }

    public static Vec3 diff(Vec3 from, Vec3 to) {
        return Vec3.createVectorHelper(from.xCoord - to.xCoord, from.yCoord - to.yCoord, from.zCoord - to.zCoord);
    }

    public static Vec3 getEyes(Entity entity) {
        float partialTicks = mc.timer.elapsedPartialTicks;

        double x = interpolate(entity.posX, entity.lastTickPosX, partialTicks);
        double y = interpolate(entity.posY, entity.lastTickPosY, partialTicks);
        double z = interpolate(entity.posZ, entity.lastTickPosZ, partialTicks);

        return Vec3.createVectorHelper(x, y + entity.getEyeHeight(), z);
    }
}
