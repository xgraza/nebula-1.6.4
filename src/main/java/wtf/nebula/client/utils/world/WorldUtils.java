package wtf.nebula.client.utils.world;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.Vec3;
import wtf.nebula.client.utils.client.Wrapper;

import java.util.List;

public class WorldUtils implements Wrapper {
    public static final List<Block> SNEAK_BLOCKS = Lists.newArrayList(
            // containers
            Blocks.chest,
            Blocks.trapped_chest,
            Blocks.ender_chest,

            // utilities
            Blocks.crafting_table,
            Blocks.furnace,
            Blocks.lit_furnace,
            Blocks.anvil,
            Blocks.beacon,
            Blocks.enchanting_table,
            Blocks.bed,
            Blocks.brewing_stand,

            // redstone
            Blocks.dispenser,
            Blocks.dropper,
            Blocks.unpowered_repeater,
            Blocks.powered_repeater,
            Blocks.unpowered_comparator,
            Blocks.powered_comparator,
            Blocks.jukebox,
            Blocks.noteblock,
            Blocks.stone_button,
            Blocks.wooden_button,
            Blocks.lever,
            Blocks.daylight_detector,
            Blocks.iron_door,
            Blocks.trapdoor,
            Blocks.hopper,

            // other shit
            Blocks.dragon_egg,
            Blocks.fence_gate,
            Blocks.cake
    );

    public static Block getBlock(Vec3 vec) {
        return mc.theWorld.getBlock((int) vec.xCoord, (int) vec.yCoord, (int) vec.zCoord);
    }

    public static Block getBlock(int x, int y, int z) {
        return mc.theWorld.getBlock(x, y, z);
    }

    public static boolean isReplaceable(Vec3 vec) {
        return getBlock(vec).getMaterial().isReplaceable();
    }

    public static boolean isReplaceable(int x, int y, int z) {
        return getBlock(x, y, z).getMaterial().isReplaceable();
    }

    public static boolean isReplaceable(double x, double y, double z) {
        return getBlock((int) x, (int) y, (int) z).getMaterial().isReplaceable();
    }

    public static boolean isAir(Vec3 vec) {
        return getBlock(vec).equals(Blocks.air);
    }
}
