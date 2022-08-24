package wtf.nebula.util.world;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import wtf.nebula.util.Globals;

import java.util.List;

public class BlockUtil implements Globals {
    public static final List<Block> SNEAK_BLOCKS = Lists.newArrayList(
            Blocks.crafting_table,
            Blocks.furnace,
            Blocks.lit_furnace,
            Blocks.anvil,
            Blocks.chest,
            Blocks.ender_chest,
            Blocks.trapped_chest,
            Blocks.enchanting_table,
            Blocks.bed,
            Blocks.beacon,
            Blocks.dispenser,
            Blocks.dropper,
            Blocks.powered_comparator,
            Blocks.unpowered_comparator,
            Blocks.powered_repeater,
            Blocks.unpowered_repeater
    );

    public static Block getBlockFromVec(Vec3 vec) {
        return mc.theWorld.getBlock((int) vec.xCoord, (int) vec.yCoord, (int) vec.zCoord);
    }

    public static Block getBlockFrom(int x, int y, int z) {
        return mc.theWorld.getBlock(x, y, z);
    }

    public static boolean isReplaceable(Vec3 vec) {
        return getBlockFromVec(vec).getMaterial().isReplaceable();
    }

    public static EnumFacing hasPlaceableSide(Vec3 vec) {
        if (vec == null) {
            return null;
        }

        for (EnumFacing facing : EnumFacing.values()) {
            Vec3 n = vec.offset(facing);

            if (!isReplaceable(n)) {
                return facing;
            }
        }

        return null;
    }

    public static boolean placeBlock(Vec3 pos, boolean swing, int slot) {
        for (EnumFacing facing : EnumFacing.values()) {

            Vec3 neighbor = pos.offset(facing);

            if (isReplaceable(neighbor)) {
                continue;
            }

            boolean sneak = SNEAK_BLOCKS.contains(getBlockFromVec(neighbor)) && !mc.thePlayer.isSneaking();
            if (sneak) {
                mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 1));
            }

            int side = facing.order_a;

            int x = (int) neighbor.xCoord;
            int y = (int) neighbor.yCoord;
            int z = (int) neighbor.zCoord;

            if (mc.playerController.onPlayerRightClick(
                    mc.thePlayer,
                    mc.theWorld,
                    mc.thePlayer.inventory.getStackInSlot(slot),
                    x, y, z,
                    side,
                    pos.addVector(0.5, 0.0, 0.5))) {

                if (swing) {
                    mc.thePlayer.swingItem();
                }

                else {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                }
            }

            if (sneak) {
                mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 2));
            }

            break;
        }

        return true;
    }
}
