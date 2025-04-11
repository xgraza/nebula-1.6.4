package us.nebula.api.pathfinding;

import net.minecraft.src.BlockPos;

/**
 * @author xgraza
 * @since 04/08/25
 */
public final class Node implements Comparable<Node>
{
    public final BlockPos pos;
    public Node parent;
    public double g, h, f, w;

    public Node(final BlockPos pos)
    {
        this.pos = pos;
    }

    @Override
    public int compareTo(Node o)
    {
        return Double.compare(f, o.f);
    }
}
