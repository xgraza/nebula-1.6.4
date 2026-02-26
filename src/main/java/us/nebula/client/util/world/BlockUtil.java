package us.nebula.client.util.world;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
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

    public static BlockPos getFacingVec(final EnumFacing facing)
    {
        // FUCK THIS GAME!!!!
        switch (facing)
        {
            case UP:
                return new BlockPos(0, 1, 0);
            case DOWN:
                return new BlockPos(0, -1, 0);
            case NORTH:
                return new BlockPos(0, 0, -1);
            case SOUTH:
                return new BlockPos(0, 0, 1);
            case EAST:
                return new BlockPos(1, 0, 0);
            case WEST:
                return new BlockPos(-1, 0, 0);
            default:
                return new BlockPos(0, 0, 0);
        }
    }

    public static BlockPos[] getAdjacent(final EnumFacing facing)
    {
        switch (facing)
        {
            case SOUTH:
            case NORTH:
            {
                return new BlockPos[]{ new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0) };
            }
            case EAST:
            case WEST:
            {
                return new BlockPos[]{ new BlockPos(0, 0, 1), new BlockPos(0, 0, -1) };
            }
            default:
                return null;
        }
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

    public static boolean isFDBreakableBlock(final BlockPos pos)
    {
        final Block block = MC.theWorld.getBlock(pos.getX(), pos.getY(), pos.getZ());
        return block == Blocks.water
                || block == Blocks.flowing_water
                || block == Blocks.web;
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

    public static boolean isFire(final BlockPos pos)
    {
        return isFire(pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean isFire(final int x, final int y, final int z)
    {
        return MC.theWorld.getBlock(x, y, z) instanceof BlockFire;
    }
}
