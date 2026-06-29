package ez.nebula.client.api.listener.event.player;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import ez.nebula.client.api.listener.Event;

/**
 * @author xgraza
 * @since 02/20/25
 */
public final class EventStep extends Event
{
    private final Entity entity;
    private final AxisAlignedBB aabb;
    private float stepHeight;

    public EventStep(final Entity entity, final AxisAlignedBB aabb, final float stepHeight)
    {
        this.entity = entity;
        this.aabb = aabb;
        this.stepHeight = stepHeight;
    }

    public Entity getEntity()
    {
        return entity;
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
