package lol.nebula.module.movement;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.listener.events.entity.EventWalkingUpdate;
import lol.nebula.listener.events.world.EventCollisionBox;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

/**
 * @author aesthetical
 * @since 04/29/23
 */
public class Jesus extends Module {
    private static final AxisAlignedBB FULL_AABB = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    private final Setting<Mode> mode = new Setting<>(Mode.SOLID, "Mode");

    public Jesus() {
        super("Jesus", "Allows you to walk on water", ModuleCategory.MOVEMENT);
    }

    @Override
    public String getMetadata() {
        return Setting.formatEnumName(mode.getValue());
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        if (mode.getValue() == Mode.DOLPHIN) {

            // do not activate outside of water or we have not passed 2 ticks
            if (!mc.thePlayer.isInWater()) return;

            // set the motionY to the upwards swim motion (~0.03)
            mc.thePlayer.motionY = 0.029279999790191618;

            // every 2 ticks slow down a bit (i love ncp)
            if (mc.thePlayer.ticksExisted % 2 == 0) {
                mc.thePlayer.motionX *= 0.77;
                mc.thePlayer.motionZ *= 0.77;
            }
        }
    }

    @Listener
    public void onWalkingUpdate(EventWalkingUpdate event) {

        // if we are not on mode SOLID - or we are not above water, do not spoof positions
        if (mode.getValue() != Mode.SOLID || !isAboveWater()) return;

        // every 2 ticks, move downwards in water
        if (mc.thePlayer.ticksExisted % 2 == 0) {
            event.setY(event.getY() - 0.01);
            event.setStance(event.getStance() - 0.01);
            event.setOnGround(false);
        }
    }

    @Listener
    public void onCollisionBox(EventCollisionBox event) {

        // if the local player is null or the world is null, do not continue - prevents NPEs
        if (mc.thePlayer == null || mc.theWorld == null) return;

        // if we are not on mode SOLID, don't add a bounding box
        if (mode.getValue() != Mode.SOLID) return;

        // if the block is not water or lava or the player is not ourselves, don't add a bounding box
        if (!(event.getBlock() instanceof BlockLiquid) || !mc.thePlayer.equals(event.getEntity())) return;

        // if we are falling more than 3 blocks or we're trapped in water, don't trap ourselves with a bb
        if (mc.thePlayer.fallDistance > 3.0f || mc.thePlayer.isInWater()) return;

        // set our new bounding box for this block
        event.setAabb(FULL_AABB.copy().offset(event.getX(), event.getY(), event.getZ()));
    }

    /**
     * Checks if the local player is above water
     * @return if the local player is above water
     */
    public static boolean isAboveWater() {

        // if we are in water, we'll say we're not above water
        if (mc.thePlayer.isInWater()) return false;

        for (double y = 0.0; y <= 1.0; y += 0.1) {

            // our new position offset by the "y"
            Vec3 pos = new Vec3(Vec3.fakePool,
                    mc.thePlayer.posX,
                    mc.thePlayer.boundingBox.minY - y,
                    mc.thePlayer.posZ);

            // get the block at this position
            Block block = mc.theWorld.getBlock(
                    (int) Math.floor(pos.xCoord),
                    (int) Math.floor(pos.yCoord),
                    (int) Math.floor(pos.zCoord));

            // if the block at this y offset is a liquid block, we are above water
            if (block instanceof BlockLiquid) return true;
        }

        // we are not above water
        return false;
    }

    public enum Mode {
        SOLID, DOLPHIN
    }
}
