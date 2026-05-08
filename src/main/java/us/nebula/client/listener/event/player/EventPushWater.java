package us.nebula.client.listener.event.player;

import net.minecraft.entity.Entity;
import us.nebula.client.listener.Event;

/**
 * @author xgraza
 * @since 03/02/25
 */
public final class EventPushWater extends Event
{
    private final Entity entity;

    public EventPushWater(final Entity entity)
    {
        this.entity = entity;
    }

    public Entity getEntity()
    {
        return entity;
    }
}
