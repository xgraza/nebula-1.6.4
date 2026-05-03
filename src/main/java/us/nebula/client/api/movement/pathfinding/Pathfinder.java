package us.nebula.client.api.movement.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.src.BlockPos;
import us.nebula.client.impl.cheat.movement.JesusCheat;
import us.nebula.client.util.math.MathUtil;

import java.util.*;

/**
 * @author xgraza
 * @since 4/30/26
 */
public final class Pathfinder
{
    private static final int[][] BLOCK_POSITION_OFFSETS = new int[26][];
    private static final int[][] BASIC_SURROUNDING_OFFSETS = {
        new int[] { -1, 0, 0 },
        new int[] { 1, 0, 0 },
        new int[] { 0, 0, -1 },
        new int[] { 0, 0, 1 }
    };

    private static final float MINOR_INCONVENIENCE = 0.5f;
    private static final float NOT_IDEAL_COST = 1.0f;
    private static final float GENERAL_DANGER_COST = 2.0f;
    private static final float DANGER_COST = 5.0f;
    private static final float NO_WAY = 100.0f;

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
                    BLOCK_POSITION_OFFSETS[i] = new int[] { x, y, z };
                    ++i;
                }
            }
        }
    }

    private static final Minecraft MC = Minecraft.getMinecraft();

    public List<BlockPos> pathfindTo(final BlockPos origin, final BlockPos goal)
    {
        final PriorityQueue<Node> openNodeQueue = new PriorityQueue<>(
                Comparator.comparing(Node::getCost).thenComparing((node) -> node.h));
        final Map<BlockPos, Float> costMap = new HashMap<>();

        // create base node
        Node currentNode = new Node(origin, null);
        currentNode.h = getHeuristic(origin, goal);
        openNodeQueue.add(currentNode);

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
                return reverseNodeTree(currentNode);
            }

            if (currentNode.g > costMap.getOrDefault(pos, Float.MAX_VALUE))
            {
                continue;
            }

            for (final int[] offset : BLOCK_POSITION_OFFSETS)
            {
                final BlockPos neighborPos = pos.add(offset[0], offset[1], offset[2]);
                float gCost = getGCost(neighborPos, pos);
                if (gCost >= NO_WAY)
                {
                    continue;
                }

                float tentativeGCost = currentNode.g + gCost;
                if (tentativeGCost >= costMap.getOrDefault(neighborPos, Float.MAX_VALUE))
                {
                    continue;
                }

                final Node node = new Node(neighborPos, currentNode);
                node.h = getHeuristic(neighborPos, goal);
                node.g = tentativeGCost;

                costMap.put(neighborPos, tentativeGCost);
                openNodeQueue.add(node);
            }
        }

        return Collections.emptyList();
    }

    private List<BlockPos> reverseNodeTree(final Node node)
    {
        final List<BlockPos> pathList = new LinkedList<>();

        Node n = node;
        while (n != null)
        {
            pathList.add(n.getPos());
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
        return (float) MathUtil.getDistance(pos, goal) * 0.5f;
    }

    private float getGCost(final BlockPos pos, final BlockPos prevPos)
    {
        final Block block = MC.theWorld.getBlock(pos);
        final Block blockUnder = MC.theWorld.getBlock(pos.down());
        final Block blockAbove = MC.theWorld.getBlock(pos.up());

        // if the block in front is a solid block, we can't walk through it
        if (block.getMaterial().blocksMovement() || blockAbove.getMaterial().blocksMovement())
        {
            return NO_WAY;
        }

        float g = 1.0f;

        final int deltaX = pos.getX() - prevPos.getX();
        final int deltaY = pos.getY() - prevPos.getY();
        final int deltaZ = pos.getZ() - prevPos.getZ();

        final boolean isDiagonal = isOffsetDiagonal(deltaX, deltaZ);
        // check if we are going to clip into a block, and avoid if possible
        if (isDiagonal)
        {
            g = 1.5f;

            // we need to make sure we have clearance on both sides

        }

        if (deltaY != 0)
        {
            int maxStepHeight = 1;
            if (MC.thePlayer.isPotionActive(Potion.jump))
            {
                maxStepHeight = MC.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1;
            }

            // can't jump higher than one block
            if (deltaY > maxStepHeight)
            {
                return NO_WAY;
            }

            // one block jump, only what's supported by step anyway
            if (deltaY == 1)
            {
                if (!blockUnder.getMaterial().blocksMovement())
                {
                    return NO_WAY;
                }

                if (block.getMaterial().blocksMovement() || blockAbove.getMaterial().blocksMovement())
                {
                    return NO_WAY;
                }

                g += isDiagonal ? 1.8f : 1.2f;
            }
        }

        int fallDistance = 0;
        for (int y = 1; y < 256; ++y)
        {
            final BlockPos fallPos = pos.add(0, -y, 0);
            if (MC.theWorld.getBlock(fallPos).getMaterial().blocksMovement())
            {
                break;
            }
            ++fallDistance;
        }

        int maxFallDistance = (int) ((MC.thePlayer.getHealth() + MC.thePlayer.getAbsorptionAmount()) / 2.0f);
        if (fallDistance >= maxFallDistance + 0.5)
        {
            return NO_WAY;
        }
        // for every block fallen, add to the cost as a more "risky" move
        g += fallDistance * 2.0f;

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

        // if we are going to walk on water and jesus is not on, we should avoid it if we can
        // in some cases (such as large bodies of water) it's unavoidable
        if ((blockUnder == Blocks.water || blockUnder == Blocks.flowing_water) && !JesusCheat.INSTANCE.isToggled())
        {
            g += NOT_IDEAL_COST;
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
