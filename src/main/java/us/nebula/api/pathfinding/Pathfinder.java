package us.nebula.api.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.src.BlockPos;
import us.nebula.util.math.MathUtil;
import us.nebula.util.player.ChatUtil;
import us.nebula.util.player.PlayerUtil;
import us.nebula.util.world.BlockUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author xgraza
 * @since 04/08/25
 */
public final class Pathfinder
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    private static final BlockPos[] NEIGHBOR_POS = {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
            new BlockPos(1, 0, 1),
            new BlockPos(-1, 0, 1),
            new BlockPos(-1, 0, -1),
            new BlockPos(1, 0, -1),
            new BlockPos(0, 1, 0),
            new BlockPos(0, -1, 0)
    };

    private BlockPos goalBlockPos;
    private Node previousNode;
    private final List<Node> nodeList = new CopyOnWriteArrayList<>();
    private boolean resolved;

    public void pathfind(final BlockPos goalBlockPos)
    {
        resolved = false;
        if (this.goalBlockPos != null)
        {
            previousNode = null;
            nodeList.clear();
        }
        this.goalBlockPos = goalBlockPos;

        final BlockPos origin = PlayerUtil.getOrigin();

        previousNode = new Node(origin, MathUtil.distanceSq(origin, origin), MathUtil.distanceSq(origin, goalBlockPos));

        int straightNullHits = 0;
        while (!resolved && straightNullHits < 15)
        {
            boolean changed = false;
            // check for surrounding blocks
            for (final BlockPos offsetPos : NEIGHBOR_POS)
            {
                final BlockPos n = previousNode.getPos().add(offsetPos);

                // if we've reached our goal, finish
                if (n.equals(goalBlockPos))
                {
                    nodeList.add(previousNode);
                    nodeList.add(new Node(n, 0, 0));
                    resolved = true;
                    break;
                }

                // do not choose this block if its bad for us
                if (!isValidBlock(n, false))
                {
                    continue;
                }

                // calc dist
                double h = MathUtil.distanceSq(n, goalBlockPos);
                double g = MathUtil.distanceSq(n, origin);

                // create node
                final Node node = new Node(n, g, h);

                if (node.getF() < previousNode.getF())
                {
                    nodeList.add(previousNode);
                    previousNode = node;
                    straightNullHits = 0;
                    changed = true;
                    break;
                }
            }

            if (!changed)
            {
                ++straightNullHits;
            }
        }
        ChatUtil.send("Null hits: " + straightNullHits + ", node size: " + nodeList.size());
    }

    private boolean isValidBlock(final BlockPos pos, boolean b)
    {
        final Block block = MC.theWorld.getBlock(pos.getX(), pos.getY(), pos.getZ());
        if (block == Blocks.flowing_lava || block == Blocks.lava)
        {
            return false;
        }

        return BlockUtil.isReplaceable(pos);
    }

    public boolean isResolved()
    {
        return resolved;
    }

    public List<Node> getNodes()
    {
        return nodeList;
    }
}
