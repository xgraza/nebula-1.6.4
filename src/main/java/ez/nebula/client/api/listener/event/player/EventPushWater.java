package ez.nebula.client.api.listener.event.player;

import ez.nebula.client.api.listener.Event;
import net.minecraft.entity.Entity;

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
