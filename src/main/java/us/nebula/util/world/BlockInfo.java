package us.nebula.util.world;

import net.minecraft.src.BlockPos;
import net.minecraft.util.EnumFacing;

/**
 * @author xgraza
 * @since 04/18/25
 */
public class BlockInfo
{
    private BlockPos pos;
    private EnumFacing facing;

    public BlockInfo(BlockPos pos, EnumFacing facing)
    {
        this.pos = pos;
        this.facing = facing;
    }

    public void setPos(final BlockPos pos)
    {
        this.pos = pos;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public EnumFacing getFacing()
    {
        return facing;
    }

    public void setFacing(final EnumFacing facing)
    {
        this.facing = facing;
    }
}
