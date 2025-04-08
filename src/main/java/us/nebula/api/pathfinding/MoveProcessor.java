package us.nebula.api.pathfinding;

import net.minecraft.client.Minecraft;
import net.minecraft.src.BlockPos;
import us.nebula.util.player.PlayerUtil;

import java.util.List;

/**
 * @author xgraza
 * @since 04/08/25
 */
public final class MoveProcessor
{
    private final Pathfinder pathfinder;
    private BlockPos goalBlockPos;

    public MoveProcessor(final Pathfinder pathfinder)
    {
        this.pathfinder = pathfinder;
    }

    public void resetMovement()
    {
        goalBlockPos = null;
    }

    public void updateMovement()
    {
        if (hasMetGoal())
        {
            setNavigationPos();
        }

    }

    private boolean hasMetGoal()
    {
        if (goalBlockPos == null)
        {
            return true;
        }
        return PlayerUtil.getOrigin().equals(goalBlockPos);
    }

    private void setNavigationPos()
    {
        final List<Node> nodeList = pathfinder.getNodes();
        if (nodeList.isEmpty())
        {
            return;
        }
        goalBlockPos = nodeList.remove(0).getPos();
    }

    public BlockPos getGoalBlockPos()
    {
        return goalBlockPos;
    }
}
