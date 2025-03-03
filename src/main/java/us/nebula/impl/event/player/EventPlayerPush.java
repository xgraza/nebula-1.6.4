package us.nebula.impl.event.player;

import net.minecraft.entity.Entity;
import us.nebula.api.listener.Event;

/**
 * @author xgraza
 * @since 03/02/25
 */
public final class EventPlayerPush extends Event
{
    private final Entity entity;

    public EventPlayerPush(final Entity entity)
    {
        this.entity = entity;
    }

    public Entity getEntity()
    {
        return entity;
    }
}
