package us.nebula.util.world;

import net.minecraft.client.Minecraft;
import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;

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

    public static boolean isReplaceable(final BlockPos pos)
    {
        return isReplaceable(pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean isReplaceable(final int x, final int y, final int z)
    {
        return MC.theWorld.getBlock(x, y, z).getMaterial().isReplaceable();
    }
}
