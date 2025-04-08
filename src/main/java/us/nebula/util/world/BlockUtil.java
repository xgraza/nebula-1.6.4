package us.nebula.util.world;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.List;

/**
 * @author xgraza
 * @since 03/24/25
 */
public final class BlockUtil
{
    private static final Minecraft MC = Minecraft.getMinecraft();
    public static final EnumFacing[] HORIZONTALS = {
            EnumFacing.NORTH,
            EnumFacing.SOUTH,
            EnumFacing.EAST,
            EnumFacing.WEST };

    public static final List<Block> INTERACTABLE_BLOCK_LIST = Lists.newArrayList(
            Blocks.chest,
            Blocks.ender_chest,
            Blocks.trapped_chest,
            Blocks.trapdoor,
            Blocks.wooden_door,
            Blocks.stone_button,
            Blocks.wooden_button,
            Blocks.furnace,
            Blocks.lit_furnace,
            Blocks.crafting_table,
            Blocks.brewing_stand,
            Blocks.bed,
            Blocks.anvil,
            Blocks.beacon,
            Blocks.command_block,
            Blocks.unpowered_comparator,
            Blocks.powered_comparator,
            Blocks.unpowered_repeater,
            Blocks.powered_repeater,
            Blocks.lever);

    public static int getHorizontalFacing(final EnumFacing facing)
    {
        switch (facing)
        {
            case SOUTH:
            {
                return 0;
            }
            case EAST:
            {
                return 1;
            }
            case NORTH:
            {
                return 2;
            }
            case WEST:
            {
                return 3;
            }
        }
        return -1;
    }

    public static EnumFacing getOpposite(final EnumFacing facing)
    {
        return EnumFacing.values()[facing.order_b];
    }

    public static BlockPos offset(final BlockPos pos, final EnumFacing facing)
    {
        return new BlockPos(pos.getX() + facing.getFrontOffsetX(),
                pos.getY() + facing.getFrontOffsetY(),
                pos.getZ() + facing.getFrontOffsetZ());
    }

    public static boolean isAir(final BlockPos pos)
    {
        return MC.theWorld.isAirBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean isUnbreakable(final BlockPos pos)
    {
        return MC.theWorld.getBlock(pos.getX(), pos.getY(), pos.getZ()).blockHardness == -1;
    }

    public static boolean isReplaceable(final BlockPos pos)
    {
        return isReplaceable(pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean isReplaceable(final int x, final int y, final int z)
    {
        return MC.theWorld.getBlock(x, y, z).getMaterial().isReplaceable();
    }
}
