package ez.nebula.client.api.listener.event.player;

import ez.nebula.client.api.listener.Event;
import net.minecraft.entity.Entity;

public final class EventAttackSprint extends Event
{
    private final Entity entity;

    public EventAttackSprint(Entity entity)
    {
        this.entity = entity;
    }

    public Entity getEntity()
    {
        return entity;
    }
}
