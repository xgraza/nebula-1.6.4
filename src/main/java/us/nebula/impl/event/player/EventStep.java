package us.nebula.impl.event.player;

import net.minecraft.util.AxisAlignedBB;
import us.nebula.api.listener.Event;

/**
 * @author xgraza
 * @since 02/20/25
 */
public final class EventStep extends Event
{
    private final AxisAlignedBB aabb;
    private float stepHeight;

    public EventStep(final AxisAlignedBB aabb, final float stepHeight)
    {
        this.aabb = aabb;
        this.stepHeight = stepHeight;
    }

    public AxisAlignedBB getAABB()
    {
        return aabb;
    }

    public float getStepHeight()
    {
        return stepHeight;
    }

    public void setStepHeight(float stepHeight)
    {
        this.stepHeight = stepHeight;
    }
}
