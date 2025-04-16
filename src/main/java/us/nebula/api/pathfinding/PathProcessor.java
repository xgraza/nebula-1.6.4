package us.nebula.api.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.src.BlockPos;
import us.nebula.util.math.MathUtil;
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

    private final PathfindingThread pathfindingThread = new PathfindingThread(this);
    private final List<BlockPos> pathList = new LinkedList<>();
    private BlockPos goalBlockPos;

    /**
     * Begin to process a new path for a goal position
     * @param goalXYZ the {@link BlockPos}
     */
    public void process(final BlockPos goalXYZ)
    {
        pathList.clear();
        this.goalBlockPos = goalXYZ;
    }

    public void reset()
    {
        pathList.clear();
        goalBlockPos = null;
        pathfindingThread.reset();
    }

    public List<BlockPos> getPath()
    {
        return pathList;
    }

    public boolean isProcessed()
    {
        return pathfindingThread.isProcessed();
    }

    private static final class PathfindingThread extends Thread
    {
        private final PathProcessor processor;
        private boolean processed;

        public PathfindingThread(final PathProcessor processor)
        {
            this.processor = processor;
            setName("Pathfinding Thread");
            setDaemon(true);
            start();
        }

        @Override
        public void run()
        {
            while (!isInterrupted())
            {
                if (processor.goalBlockPos == null)
                {
                    processed = true;
                    continue;
                }
                pathfind();
            }
        }

        public void reset()
        {
            processed = true;
        }

        private void pathfind()
        {
            if (!processor.pathList.isEmpty())
            {
                return;
            }
            processed = false;

            final PriorityQueue<Node> nodeQueue = new PriorityQueue<>();

            // create the starting node to build off of
            final Node startNode = new Node(PlayerUtil.getOrigin());
            startNode.setH(getHeuristicValue(startNode.getPos()));
            nodeQueue.add(startNode);

            while (!nodeQueue.isEmpty())
            {
                final Node current = nodeQueue.poll();
                if (current.getPos().equals(processor.goalBlockPos))
                {
                    reconstructPath(current);
                    processed = true;
                    return;
                }

                for (final BlockPos neighboringPos : getNeighboringPositions(current.getPos()))
                {
                    final Node next = new Node(neighboringPos);
                    next.setG(current.getG());
                    next.setH(getHeuristicValue(neighboringPos));
                    if (current.getF() < next.getF() || !nodeQueue.contains(next))
                    {
                        next.setG(next.getG() + 1);
                        next.setParent(current);
                        // add this new node to search its path
                        nodeQueue.add(next);
                    }
                }
            }
        }

        private void reconstructPath(Node node)
        {
            processor.pathList.clear();
            while (node != null)
            {
                processor.pathList.add(node.getPos());
                node = node.getParent();
            }
            Collections.reverse(processor.pathList);
        }

        private double getHeuristicValue(final BlockPos pos)
        {
            double h = MathUtil.getDistance(pos, processor.goalBlockPos);
            // TODO
            return h;
        }

        private List<BlockPos> getNeighboringPositions(final BlockPos pos)
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


        public boolean isProcessed()
        {
            return processed;
        }
    }
}
