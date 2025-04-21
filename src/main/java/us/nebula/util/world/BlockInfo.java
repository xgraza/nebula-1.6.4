package us.nebula.util.world;

import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * @author xgraza
 * @since 04/18/25
 */
public final class BlockInfo
{
    private final BlockPos pos;
    private final EnumFacing facing;

    public BlockInfo(BlockPos pos, EnumFacing facing)
    {
        this.pos = pos;
        this.facing = facing;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public EnumFacing getFacing()
    {
        return facing;
    }
}
