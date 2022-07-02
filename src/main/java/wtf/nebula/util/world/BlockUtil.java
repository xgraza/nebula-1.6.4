package wtf.nebula.util.world;

import com.google.common.collect.Lists;
import net.minecraft.src.*;
import wtf.nebula.util.Globals;

import java.util.List;

public class BlockUtil implements Globals {
    public static final List<Block> REPLACEABLE = Lists.newArrayList(
            Block.waterStill,
            Block.waterMoving,
            Block.lavaMoving,
            Block.lavaStill
    );

    public static final List<Block> SNEAK_BLOCKS = Lists.newArrayList(
            Block.furnaceBurning,
            Block.furnaceIdle,
            Block.workbench,
            Block.anvil,
            Block.beacon,
            Block.trapdoor,
            Block.doorWood,
            Block.brewingStand,
            Block.chest,
            Block.chestTrapped,
            Block.enderChest,
            Block.lockedChest
    );

    public static Block getBlockFromVec(Vec3 vec) {
        return Block.blocksList[mc.theWorld.getBlockId((int) vec.xCoord, (int) vec.yCoord, (int) vec.zCoord)];
    }

    public static boolean isReplaceable(Vec3 vec) {
        Block block = getBlockFromVec(vec);
        return block == null || REPLACEABLE.contains(block);
    }

    public static void placeBlock(Vec3 pos, boolean swing, int slot) {
        for (EnumFacing facing : EnumFacing.values()) {

            Vec3 neighbor = pos.addVector(facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
            EnumFacing opposite = EnumFacing.values()[facing.order_b];

            if (isReplaceable(neighbor)) {
                continue;
            }

            boolean sneak = SNEAK_BLOCKS.contains(getBlockFromVec(neighbor)) && !mc.thePlayer.isSneaking();
            if (sneak) {
                mc.thePlayer.sendQueue.addToSendQueue(new Packet19EntityAction(mc.thePlayer, 1));
            }

            Vec3 hitVec = Vec3.createVectorHelper(neighbor.xCoord + 0.5, neighbor.yCoord + 0.5, neighbor.zCoord + 0.5);
            hitVec.xCoord += opposite.getFrontOffsetX() * 0.5;
            hitVec.yCoord += opposite.getFrontOffsetY() * 0.5;
            hitVec.zCoord += opposite.getFrontOffsetZ() * 0.5;

            if (mc.playerController.onPlayerRightClick(
                    mc.thePlayer,
                    mc.theWorld,
                    mc.thePlayer.inventory.getStackInSlot(slot),
                    (int) neighbor.xCoord,
                    (int) neighbor.yCoord,
                    (int) neighbor.zCoord,
                    opposite.ordinal(),
                    hitVec)) {

                if (swing) {
                    mc.thePlayer.swingItem();
                }

                else {
                    mc.thePlayer.sendQueue.addToSendQueue(new Packet18Animation());
                }
            }

            if (sneak) {
                mc.thePlayer.sendQueue.addToSendQueue(new Packet19EntityAction(mc.thePlayer, 2));
            }

            break;
        }
    }
}
