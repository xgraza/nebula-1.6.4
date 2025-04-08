package us.nebula.api.pathfinding;

import net.minecraft.src.BlockPos;

/**
 * @author xgraza
 * @since 04/08/25
 */
public final class Node
{
    private final BlockPos pos;
    private final double g, h, f;

    public Node(final BlockPos pos, final double g, final double h)
    {
        this.pos = pos;
        this.g = g;
        this.h = h;
        this.f = g + h;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public double getG()
    {
        return g;
    }

    public double getH()
    {
        return h;
    }

    public double getF()
    {
        return f;
    }
}
