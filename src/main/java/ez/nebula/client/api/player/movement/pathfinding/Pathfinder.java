package ez.nebula.client.api.player.movement.pathfinding;

import ez.nebula.client.impl.module.movement.JesusModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.src.BlockPos;
import ez.nebula.client.util.math.MathUtil;

import java.util.*;

/**
 * @author xgraza
 * @since 4/30/26
 */
public final class Pathfinder
{
    private static final int[][] BLOCK_POSITION_OFFSETS = new int[26][];
    private static final int[][] BASIC_SURROUNDING_OFFSETS = {
            new int[]{ -1, 0, 0 },
            new int[]{ 1, 0, 0 },
            new int[]{ 0, 0, -1 },
            new int[]{ 0, 0, 1 }
    };

    private static final float MINOR_INCONVENIENCE = 0.5f;
    private static final float NOT_IDEAL_COST = 1.0f;
    private static final float GENERAL_DANGER_COST = 2.0f;
    private static final float DANGER_COST = 5.0f;
    private static final float IMPOSSIBLE = 100.0f;

    static
    {
        int i = 0;
        for (int x = -1; x <= 1; ++x)
        {
            for (int y = -1; y <= 1; ++y)
            {
                for (int z = -1; z <= 1; ++z)
                {
                    if (x == 0 && y == 0 && z == 0)
                    {
                        continue;
                    }
                    BLOCK_POSITION_OFFSETS[i] = new int[]{ x, y, z };
                    ++i;
                }
            }
        }
    }

    private static final Minecraft MC = Minecraft.getMinecraft();

    public List<Node> getPathList(final BlockPos origin, final BlockPos goal)
    {
        final Node node = getGoalNode(origin, goal);
        if (node == null)
        {
            return Collections.emptyList();
        }
        return reverseNodeTree(node);
    }

    public Node getGoalNode(final BlockPos origin, final BlockPos goal)
    {
        if (origin.equals(goal))
        {
            return null;
        }

        final PriorityQueue<Node> openNodeQueue = new PriorityQueue<>(
                Comparator.comparing(Node::getCost).thenComparing((node) -> node.h));
        final Map<BlockPos, Float> costMap = new HashMap<>();

        // create base node
        Node currentNode = new Node(origin, null);
        currentNode.h = getHeuristic(origin, goal);
        openNodeQueue.add(currentNode);

        Node closestNode = currentNode;

        while (!openNodeQueue.isEmpty())
        {
            currentNode = openNodeQueue.poll();
            if (currentNode == null)
            {
                break;
            }
            final BlockPos pos = currentNode.getPos();
            if (pos.equals(goal))
            {
                return currentNode;
            }

            if (currentNode.g > costMap.getOrDefault(pos, Float.MAX_VALUE))
            {
                continue;
            }

            if (currentNode.h < closestNode.h)
            {
                closestNode = currentNode;
            }

            for (final int[] offset : BLOCK_POSITION_OFFSETS)
            {
                final BlockPos neighborPos = pos.add(offset[0], offset[1], offset[2]);
                if (!MC.theWorld.blockExists(neighborPos.getX(), neighborPos.getY(), neighborPos.getZ()))
                {
                    continue;
                }

                final Node node = new Node(neighborPos, currentNode);
                float g = getGCost(node, neighborPos, pos);
                if (g >= IMPOSSIBLE)
                {
                    continue;
                }

                float tentativeGCost = currentNode.g + g;
                if (tentativeGCost >= costMap.getOrDefault(neighborPos, Float.MAX_VALUE))
                {
                    continue;
                }

                node.h = getHeuristic(neighborPos, goal);
                node.g = tentativeGCost;

                costMap.put(neighborPos, tentativeGCost);
                openNodeQueue.add(node);
            }
        }

        if (!closestNode.getPos().equals(origin))
        {
            return closestNode;
        }

        return null;
    }

    public List<Node> reverseNodeTree(final Node node)
    {
        final List<Node> pathList = new LinkedList<>();

        Node n = node;
        while (n != null)
        {
            pathList.add(n);
            n = n.getParent();
        }

        Collections.reverse(pathList);
        return pathList;
    }

    private boolean isOffsetDiagonal(int deltaX, int deltaZ)
    {
        return Math.abs(deltaX) == 1 && Math.abs(deltaZ) == 1;
    }

    private float getHeuristic(final BlockPos pos, final BlockPos goal)
    {
        return (float) MathUtil.getDistance(pos, goal);
    }

    private Block getBlock(final BlockPos pos)
    {
        return MC.theWorld.getBlock(pos);
    }

    private float getGCost(final Node node, final BlockPos pos, final BlockPos prevPos)
    {
        Block block = getBlock(pos);
        Block blockAbove = getBlock(pos.up());
        Block blockUnder = getBlock(pos.down());

        if (isBlockImpassible(block, pos))
        {
            return IMPOSSIBLE;
        }

        float gCost = 1.0f;

        {
            int fallDistance = 0;
            for (int y = 1; y < 256; ++y)
            {
                final BlockPos fallPos = pos.add(0, -y, 0);
                final Block fallBlock = MC.theWorld.getBlock(fallPos);
                if (fallBlock.getMaterial().blocksMovement() || fallBlock.getMaterial() == Material.water)
                {
                    break;
                }
                ++fallDistance;
            }

            int maxFallDistance = (int) ((MC.thePlayer.getHealth() + MC.thePlayer.getAbsorptionAmount()) / 2.0f);
            if (fallDistance >= maxFallDistance + 0.5)
            {
                return IMPOSSIBLE;
            }
            gCost += fallDistance * 2.0f;
        }

        final int deltaX = pos.getX() - prevPos.getX();
        final int deltaY = pos.getY() - prevPos.getY();
        final int deltaZ = pos.getZ() - prevPos.getZ();
        final boolean diag = isOffsetDiagonal(deltaX, deltaZ);

        if (deltaY == 0 && !isBlockImpassible(blockUnder, pos.down()))
        {
            return IMPOSSIBLE;
        }

        // if we have moved up on the y-axis
        if (deltaY != 0)
        {
            if (deltaY > 1)
            {
                return IMPOSSIBLE;
            } else if (deltaY == 1)
            {
                if (isBlockImpassible(blockAbove, pos.up()) || !isBlockImpassible(blockUnder, pos.down()))
                {
                    return IMPOSSIBLE;
                }
            }

            if (deltaY < 0)
            {
                // can we even fall down?
                final BlockPos headPos = pos.up().up();
                if (isBlockImpassible(getBlock(headPos), headPos))
                {
                    return IMPOSSIBLE;
                }
            }
        }

        if (diag)
        {
            if (deltaY < 0)
            {
                gCost = getDiagonalGCost(node, pos.down(), deltaX, deltaZ, gCost);
                if (gCost >= IMPOSSIBLE)
                {
                    return IMPOSSIBLE;
                }
            }

            gCost = getDiagonalGCost(node, pos, deltaX, deltaZ, gCost);
            if (gCost >= IMPOSSIBLE)
            {
                return IMPOSSIBLE;
            }
            gCost = getDiagonalGCost(node, pos.up(), deltaX, deltaZ, gCost);
            if (gCost >= IMPOSSIBLE)
            {
                return IMPOSSIBLE;
            }
        }

        // if we are trying to pass through water, give it a not-ideal cost
        if (block == Blocks.water || block == Blocks.flowing_water)
        {
            gCost += NOT_IDEAL_COST;
        }

        // we do not want to accidentally trip redstone
        if (block == Blocks.tripwire
                || block == Blocks.tripwire_hook
                || block instanceof BlockBasePressurePlate
                || blockUnder == Blocks.tripwire
                || blockUnder == Blocks.tripwire_hook
                || blockUnder instanceof BlockBasePressurePlate)
        {
            gCost += DANGER_COST;
        }

        // by walking over a redstone ore block, we can accidentally activate it by accident
        if (blockUnder == Blocks.redstone_ore || blockUnder == Blocks.lit_redstone_ore)
        {
            gCost += DANGER_COST;
            // we do not want to accidentally activate it by going across it
        }

        return gCost;
    }

    private boolean isBlockImpassible(final Block block, final BlockPos pos)
    {
        final Block blockAbove = getBlock(pos.up());
        final Block blockUnder = getBlock(pos.down());

        // cannot pass through solid blocks
        if (block.getMaterial().blocksMovement() || blockAbove.getMaterial().blocksMovement())
        {
            return true;
        }

        // if a block does not block movement but we do not want to pass through it, return IMPOSSIBLE
        if (block == Blocks.web || block == Blocks.fire || block == Blocks.lava || block == Blocks.flowing_lava)
        {
            return true;
        }

        // let's specify blocks we don't want to walk over
        if (blockUnder == Blocks.web
                || blockUnder == Blocks.lava
                || blockUnder == Blocks.flowing_lava
                || blockUnder == Blocks.fire
                || blockUnder == Blocks.cactus)
        {
            return true;
        }

        // if we are under sand/gravel (a falling block)
        if (blockUnder == Blocks.sand || blockUnder == Blocks.gravel)
        {
            // check the block under the block at our feet -
            // we're checking to see if there are blocks under that could possibly create an update and let us fall
            final Block blockUnder2 = getBlock(pos.down().down());
            if (blockUnder2 == Blocks.standing_sign
                    || blockUnder2 == Blocks.wall_sign
                    || blockUnder2 == Blocks.tripwire
                    || blockUnder2 == Blocks.carpet)
            {
                return true;
            }
        }

        return false;
    }

    private float getDiagonalGCost(final Node node, final BlockPos pos, final int deltaX, final int deltaZ, float g)
    {
        final BlockPos adj1 = pos.add(-deltaX, 0, 0);
        final BlockPos adj2 = pos.add(0, 0, -deltaZ);

        if (node.diagnoalList.isEmpty())
        {
            node.diagnoalList.add(adj1);
            node.diagnoalList.add(adj2);
        }

        final Block block1 = MC.theWorld.getBlock(adj1);
        final Block block2 = MC.theWorld.getBlock(adj2);

        final boolean adjB1 = block1.getMaterial().blocksMovement();
        final boolean adjB2 = block2.getMaterial().blocksMovement();

        if (adjB1 || adjB2)
        {
            return IMPOSSIBLE;
        }

        g = getGCostForBlock(block1, MC.theWorld.getBlock(adj1.down()), MC.theWorld.getBlock(adj1.up()), adj1, g);
        g = getGCostForBlock(block2, MC.theWorld.getBlock(adj2.down()), MC.theWorld.getBlock(adj2.up()), adj2, g);

        return g;
    }

    private float getGCostForBlock(final Block block, final Block blockUnder, final Block blockAbove, final BlockPos pos, float g)
    {
        // check to see if it could be a trap, we don't want to accidentally set off redstone
        if (block == Blocks.tripwire || block == Blocks.tripwire_hook || block instanceof BlockBasePressurePlate)
        {
            g += GENERAL_DANGER_COST;
        }
        if (block == Blocks.carpet
                && (blockUnder == Blocks.standing_sign
                || blockUnder == Blocks.wall_sign
                || blockUnder == Blocks.tripwire))
        {
            g += GENERAL_DANGER_COST;
        }

        // potentially deadly
        if ((block == Blocks.fire || block == Blocks.lava || block == Blocks.flowing_lava)
                && !MC.thePlayer.isPotionActive(Potion.fireResistance))
        {
            g += DANGER_COST;
        }

        if (blockAbove == Blocks.lava || blockAbove == Blocks.flowing_lava)
        {
            g += DANGER_COST;
        }

        if (block == Blocks.water || block == Blocks.flowing_water && blockAbove.getMaterial() != Material.air)
        {
            return IMPOSSIBLE;
        }

        if ((blockUnder == Blocks.water || blockUnder == Blocks.flowing_water) && !JesusModule.INSTANCE.isToggled())
        {
            g += DANGER_COST;
        }

        for (final int[] offsets : BASIC_SURROUNDING_OFFSETS)
        {
            final BlockPos neighbor = pos.add(offsets[0], offsets[1], offsets[2]);
            final Block neighboringBlock = MC.theWorld.getBlock(neighbor);

            if (neighboringBlock == Blocks.cactus)
            {
                g += NOT_IDEAL_COST;
            } else if (neighboringBlock == Blocks.lava || neighboringBlock == Blocks.flowing_lava)
            {
                g += DANGER_COST;
            } else if (neighboringBlock == Blocks.ladder || neighboringBlock == Blocks.vine)
            {
                g += NOT_IDEAL_COST;
            }
        }

        return g;
    }
}
