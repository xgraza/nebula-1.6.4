package wtf.nebula.client.utils.world;

import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.nebula.client.utils.client.Wrapper;

public class RayTraceUtils implements Wrapper {

    public static MovingObjectPosition rayTrace(float yaw, float pitch) {
        double par1 = mc.playerController.getBlockReachDistance();
        Vec3 var4 = mc.thePlayer.getPosition(1.0f);
        Vec3 var5 = getLook(yaw, pitch);
        Vec3 var6 = var4.addVector(var5.xCoord * par1, var5.yCoord * par1, var5.zCoord * par1);
        return mc.theWorld.func_147447_a(var4, var6, false, false, true);
    }

    public static Vec3 getLook(float rotationYaw, float rotationPitch) {
        float var2 = MathHelper.cos(-rotationYaw * 0.017453292F - (float) Math.PI);
        float var3 = MathHelper.sin(-rotationYaw * 0.017453292F - (float) Math.PI);
        float var4 = -MathHelper.cos(-rotationPitch * 0.017453292F);
        float var5 = MathHelper.sin(-rotationPitch * 0.017453292F);
        return new Vec3(Vec3.fakePool, var3 * var4, var5, var2 * var4);
    }

}
