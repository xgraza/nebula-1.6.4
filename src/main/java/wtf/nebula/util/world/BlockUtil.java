package wtf.nebula.util.world;

import com.google.common.collect.Lists;
import net.minecraft.src.*;
import wtf.nebula.util.Globals;
import wtf.nebula.util.MathUtil;

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

    public static Block getBlockFrom(int x, int y, int z) {
        return Block.blocksList[mc.theWorld.getBlockId(x, y, z)];
    }

    public static boolean isReplaceable(Vec3 vec) {
        return mc.theWorld.getBlockMaterial((int) vec.xCoord, (int) vec.yCoord, (int) vec.zCoord).isReplaceable();
    }

    public static boolean placeBlock(Vec3 pos, boolean swing, int slot) {
        for (EnumFacing facing : EnumFacing.values()) {

            Vec3 neighbor = pos.offset(facing);

            if (isReplaceable(neighbor)) {
                continue;
            }

            boolean sneak = SNEAK_BLOCKS.contains(getBlockFromVec(neighbor)) && !mc.thePlayer.isSneaking();
            if (sneak) {
                mc.thePlayer.sendQueue.addToSendQueue(new Packet19EntityAction(mc.thePlayer, 1));
            }

            int x = (int) neighbor.xCoord;
            int y = (int) neighbor.yCoord;
            int z = (int) neighbor.zCoord;

            if (mc.playerController.onPlayerRightClick(
                    mc.thePlayer,
                    mc.theWorld,
                    mc.thePlayer.inventory.getStackInSlot(slot),
                    x, y, z,
                    facing.order_b,
                    new Vec3(Vec3.fakePool, x, y, z))) {

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

        return true;
    }
}
