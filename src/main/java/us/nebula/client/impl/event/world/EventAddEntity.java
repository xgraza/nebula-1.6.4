package us.nebula.client.impl.event.world;

import net.minecraft.entity.Entity;
import us.nebula.client.api.listener.Event;

/**
 * @author xgraza
 * @since 3/17/26
 */
public final class EventAddEntity extends Event
{
    private final int id;
    private final Entity entity;
    private final boolean previousExisting;

    public EventAddEntity(int id, Entity entity, boolean previousExisting)
    {
        this.id = id;
        this.entity = entity;
        this.previousExisting = previousExisting;
    }

    public int getId()
    {
        return id;
    }

    public Entity getEntity()
    {
        return entity;
    }

    public boolean isPreviousExisting()
    {
        return previousExisting;
    }
}
