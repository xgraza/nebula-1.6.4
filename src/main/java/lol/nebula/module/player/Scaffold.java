package lol.nebula.module.player;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.EventStage;
import lol.nebula.listener.events.entity.EventWalkingUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.math.Pair;
import lol.nebula.util.math.RotationUtils;
import lol.nebula.util.math.timing.Timer;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

/**
 * @author aesthetical
 * @since 04/28/23
 */
public class Scaffold extends Module {

    private final Setting<Boolean> tower = new Setting<>(true, "Tower");
    private final Setting<Boolean> rotate = new Setting<>(true, "Rotate");

    private final Timer towerTimer = new Timer();

    // the previous rotations
    private float[] rotations;

    public Scaffold() {
        super("Scaffold", "Rapidly places blocks under your feet", ModuleCategory.PLAYER);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        rotations = null;

        if (mc.thePlayer != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
    }

    @Listener
    public void onWalkingUpdate(EventWalkingUpdate event) {

        Pair<Vec3, EnumFacing> next = nextPlacePos();

        if (rotate.getValue()) {
            if (next != null) {
                rotations = RotationUtils.toBlock(next.getKey(), next.getValue());
            }

            if (rotations != null) RotationUtils.setRotations(event, rotations);
        }

        // cannot place a null block
        if (next == null) return;

        int slot = getBlockSlot();
        if (slot == -1) return;

        if (event.getStage() == EventStage.POST) {

            // swap to the block slot
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));

            boolean result = mc.playerController.onPlayerRightClick(mc.thePlayer,
                    mc.theWorld,
                    mc.thePlayer.inventory.getStackInSlot(slot),
                    (int) next.getKey().xCoord,
                    (int) next.getKey().yCoord,
                    (int) next.getKey().zCoord,
                    next.getValue().getOrder_a(),
                    next.getKey().addVector(0.5, 0.5, 0.5));

            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));

            // don't continue if we failed to place
            if (!result) return;

            // silently swing the player's arm server-sided
            mc.thePlayer.swingItemSilent();

            if (tower.getValue() && mc.gameSettings.keyBindJump.pressed) {

                if (towerTimer.ms(1200L, true)) {
                    mc.thePlayer.motionY = -1;
                    return;
                }

                if (mc.thePlayer.onGround && mc.thePlayer.motionY < 0.1) {
                    mc.thePlayer.motionY = 0.41999998688697815;
                } else if (mc.thePlayer.motionY <= 0.16477328182606651) {
                    mc.thePlayer.motionY = 0.41999998688697815;
                }
            }
        }
    }

    /**
     * Gets the next block slot
     * @return the slot 0-8 or -1 if no blocks applied
     */
    private int getBlockSlot() {
        int slot = -1, count = 0;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack == null || !(stack.getItem() instanceof ItemBlock) || stack.stackSize == 0) continue;

            // automatically pick infinite items
            if (stack.stackSize < 0) return i;

            if (stack.stackSize > count) {
                count = stack.stackSize;
                slot = i;
            }
        }

        return slot;
    }

    /**
     * Gets the next placement position
     * @return the next placement Vec3 or null
     */
    private Pair<Vec3, EnumFacing> nextPlacePos() {
        Vec3 pos = mc.thePlayer.getGroundPosition().addVector(0, -1, 0);

        for (EnumFacing facing : EnumFacing.values()) {
            Vec3 neighbor = pos.addVector(
                    facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
            if (!isReplaceable(neighbor)) return new Pair<>(neighbor, getOpposite(facing));
        }

        for (EnumFacing facing : EnumFacing.values()) {
            Vec3 neighbor = pos.addVector(
                    facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
            if (isReplaceable(neighbor)) {
                for (EnumFacing dir : EnumFacing.values()) {
                    Vec3 next = neighbor.addVector(dir.getFrontOffsetX(), dir.getFrontOffsetY(), dir.getFrontOffsetZ());
                    if (!isReplaceable(next)) return new Pair<>(next, getOpposite(dir));
                }
            }
        }

        return null;
    }

    /**
     * Gets the opposite facing enum for the inputted face
     * @param facing the face
     * @return the opposite face enum constant
     */
    private EnumFacing getOpposite(EnumFacing facing) {
        return EnumFacing.faceList[facing.getOrder_b()];
    }

    /**
     * Checks if the block at this position vector is replaceable
     * @param position the position
     * @return if the block can be replaced or not
     */
    private boolean isReplaceable(Vec3 position) {
        Block blockAtPosition = mc.theWorld.getBlock(
                (int) position.xCoord, (int) position.yCoord, (int) position.zCoord);
        return blockAtPosition.getMaterial().isReplaceable();
    }
}
