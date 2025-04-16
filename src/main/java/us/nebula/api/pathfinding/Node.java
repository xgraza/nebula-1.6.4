package us.nebula.api.pathfinding;

import net.minecraft.src.BlockPos;

/**
 * @author xgraza
 * @since 04/08/25
 */
public final class Node implements Comparable<Node>
{
    private final BlockPos pos;
    private Node parent;
    private double g, h, f;

    public Node(final BlockPos pos)
    {
        this.pos = pos;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public void setParent(Node parent)
    {
        this.parent = parent;
    }

    public Node getParent()
    {
        return parent;
    }

    public void setG(double g)
    {
        this.g = g;
        calculateF();
    }

    public void setH(double h)
    {
        this.h = h;
        calculateF();
    }

    private void calculateF()
    {
        f = g + h;
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

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Node))
        {
            return false;
        }
        final Node node = (Node) obj;
        return node.pos.equals(this.pos) || (node.g == this.g && node.h == this.h);
    }

    @Override
    public int compareTo(Node o)
    {
        return Double.compare(f, o.f);
    }
}
