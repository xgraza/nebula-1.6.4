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
}
