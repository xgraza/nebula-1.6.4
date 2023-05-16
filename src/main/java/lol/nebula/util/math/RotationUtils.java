package lol.nebula.util.math;

import lol.nebula.Nebula;
import lol.nebula.listener.events.entity.move.EventWalkingUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class RotationUtils {

    /**
     * The minecraft instance
     */
    protected static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Calculates rotations to an entity
     * @param target the target entity
     * @return the resulting rotations
     */
    public static float[] rotateTo(EntityLivingBase target) {
        double diffX = target.posX - mc.thePlayer.posX;
        double diffY = (target.boundingBox.minY + 1.620) - mc.thePlayer.posY;
        double diffZ = target.posZ - mc.thePlayer.posZ;

        float yaw = (float) -(Math.toDegrees(Math.atan2(diffX, diffZ)));
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, Math.hypot(diffX, diffZ))));

        return new float[] { yaw, MathHelper.clamp_float(pitch, -90.0f, 90.0f) };
    }

    /**
     * Gets rotations to a block and its face
     * @param position the position of the block
     * @param facing the face of the block
     * @return the resulting rotations
     */
    public static float[] toBlock(Vec3 position, EnumFacing facing) {
        Vec3 vec = new Vec3(Vec3.fakePool,
                position.xCoord + 0.5 + (facing.getFrontOffsetX() / 2.0),
                position.yCoord - 0.5 + (facing.getFrontOffsetY() / 2.0),
                position.zCoord + 0.5 + (facing.getFrontOffsetZ() / 2.0));

        double diffX = vec.xCoord - mc.thePlayer.posX;
        double diffY = vec.yCoord - mc.thePlayer.posY;
        double diffZ = vec.zCoord - mc.thePlayer.posZ;

        float yaw = (float) -(Math.toDegrees(Math.atan2(diffX, diffZ)));
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, Math.hypot(diffX, diffZ))));

        return new float[] { yaw, MathHelper.clamp_float(pitch, -90.0f, 90.0f) };
    }

    /**
     * Gets rotations to a block and its face
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param facing the face of the block
     * @return the resulting rotations
     */
    public static float[] toBlock(int x, int y, int z, EnumFacing facing) {
        Vec3 vec = new Vec3(Vec3.fakePool,
                x + 0.5 + (facing.getFrontOffsetX() / 2.0),
                y - 0.5 + (facing.getFrontOffsetY() / 2.0),
                z + 0.5 + (facing.getFrontOffsetZ() / 2.0));

        double diffX = vec.xCoord - mc.thePlayer.posX;
        double diffY = vec.yCoord - mc.thePlayer.posY;
        double diffZ = vec.zCoord - mc.thePlayer.posZ;

        float yaw = (float) -(Math.toDegrees(Math.atan2(diffX, diffZ)));
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, Math.hypot(diffX, diffZ))));

        return new float[] { yaw, MathHelper.clamp_float(pitch, -90.0f, 90.0f) };
    }

    /**
     * Sets server-side rotations
     * @param event the event to modify
     * @param rotations the spoofed rotations
     */
    public static void setRotations(EventWalkingUpdate event, float[] rotations) {
        Nebula.getInstance().getRotations().spoof(rotations);
        event.setYaw(rotations[0]);
        event.setPitch(rotations[1]);
    }
}
