package ez.nebula.client.api.listener.event.player;

import ez.nebula.client.api.listener.Event;
import net.minecraft.entity.Entity;

public final class EventAttackSprint extends Event
{
    private final Entity entity;
    private boolean slowdown = true;

    public EventAttackSprint(Entity entity)
    {
        this.entity = entity;
    }

    public Entity getEntity()
    {
        return entity;
    }

    public void setSlowdown(boolean slowdown)
    {
        this.slowdown = slowdown;
    }

    public boolean isSlowdown()
    {
        return slowdown;
    }
}
