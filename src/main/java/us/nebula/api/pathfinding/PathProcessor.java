package us.nebula.api.pathfinding;

import io.netty.util.internal.ConcurrentSet;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.src.BlockPos;
import us.nebula.util.math.MathUtil;
import us.nebula.util.player.ChatUtil;
import us.nebula.util.player.PlayerUtil;

import java.util.*;

/**
 * @author xgraza
 * @since 04/08/25
 */
public final class PathProcessor
{
    private static final BlockPos[] NEIGHBOR_POS = {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
            new BlockPos(1, 0, 1),
            new BlockPos(-1, 0, 1),
            new BlockPos(-1, 0, -1),
            new BlockPos(1, 0, -1),

            new BlockPos(1, 1, 0),
            new BlockPos(1, -1, 0),
            new BlockPos(-1, 1, 0),
            new BlockPos(-1, -1, 0),
            new BlockPos(0, 1, 1),
            new BlockPos(0, -1, 1),
            new BlockPos(0, 1, -1),
            new BlockPos(0, -1, -1),

            new BlockPos(0, 1, 0),
            new BlockPos(0, -1, 0),

            new BlockPos(1, 1, 1),
            new BlockPos(1, -1, 1),
            new BlockPos(-1, 1, 1),
            new BlockPos(-1, -1, 1),
            new BlockPos(-1, 1, -1),
            new BlockPos(-1, -1, -1),
            new BlockPos(1, 1, -1),
            new BlockPos(1, -1, -1)
    };
    private static final Minecraft MC = Minecraft.getMinecraft();

    private final List<BlockPos> pathQueue = new LinkedList<>();
    private BlockPos goalBlockPos;

    public void process(final BlockPos goalXYZ)
    {
        this.goalBlockPos = goalXYZ;
        pathQueue.clear();

        final Node startNode = new Node(PlayerUtil.getOrigin());
        startNode.g = 0;
        startNode.h = getHeuristicValue(startNode.pos);
        startNode.f = startNode.g + startNode.h;

        final PriorityQueue<Node> searchedNodes = new PriorityQueue<>();
        final Set<BlockPos> searchedBlocks = new ConcurrentSet<>();
        final Set<BlockPos> closedList = new ConcurrentSet<>();

        searchedNodes.add(startNode);
        searchedBlocks.add(startNode.pos);

        while (!searchedNodes.isEmpty())
        {
            final Node node = searchedNodes.poll();
            if (node.pos.equals(goalBlockPos))
            {
                reconstructPath(node);
                break;
            }

            closedList.add(node.pos);

            for (final BlockPos n : getNeighboring(node.pos))
            {
                if (closedList.contains(n))
                {
                    continue;
                }
                final Node neighborNode = new Node(n);
                final double tentativeG  = node.g + 1;
                if (tentativeG < neighborNode.g || !searchedBlocks.contains(n))
                {
                    neighborNode.g = tentativeG;
                    neighborNode.h = getHeuristicValue(n);
                    neighborNode.f = neighborNode.g + neighborNode.h;
                    neighborNode.parent = node;
                    // ensure these are not re-searched
                    searchedNodes.add(neighborNode);
                    searchedBlocks.add(neighborNode.pos);
                }
            }
        }

        ChatUtil.send("size: " + pathQueue.size());
    }

    private void reconstructPath(Node node)
    {
        pathQueue.clear();
        while (node != null)
        {
            pathQueue.add(node.pos);
            node = node.parent;
        }
        Collections.reverse(pathQueue);
    }

    private double getHeuristicValue(final BlockPos pos)
    {
        double h = MathUtil.getDistance(pos, goalBlockPos);

        if (MC.theWorld.getBlock(pos) == Blocks.lava)
        {
            h *= 2;
        }

        final Block under = MC.theWorld.getBlock(pos.down());
        if (under == Blocks.lava)
        {
            h *= 2;
        }

        // TODO

        return h;
    }

    private List<BlockPos> getNeighboring(final BlockPos pos)
    {
        final List<BlockPos> viableNeighbors = new LinkedList<>();
        for (final BlockPos neighboring : NEIGHBOR_POS)
        {
            final BlockPos neighbor = pos.add(neighboring);
            Block block = MC.theWorld.getBlock(neighbor);

            // if we cannot walk through this block or if the block under it is air
            if (!block.getMaterial().isReplaceable()
                    || MC.theWorld.getBlock(neighbor.down()) == Blocks.air)
            {
                continue;
            }

            viableNeighbors.add(neighbor);
        }
        return viableNeighbors;
    }

    public List<BlockPos> getPathQueue()
    {
        return pathQueue;
    }
}
