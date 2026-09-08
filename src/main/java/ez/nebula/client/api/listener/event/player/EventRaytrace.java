package ez.nebula.client.api.listener.event.player;

import ez.nebula.client.api.listener.Event;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;

public final class EventRaytrace extends Event
{
    private final Entity entity;
    private final float partialTicks;
    private MovingObjectPosition result;

    public EventRaytrace(Entity entity, MovingObjectPosition result, float partialTicks)
    {
        this.entity = entity;
        this.result = result;
        this.partialTicks = partialTicks;
    }

    public Entity getEntity()
    {
        return entity;
    }

    public MovingObjectPosition getResult()
    {
        return result;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }

    public void setResult(MovingObjectPosition result)
    {
        this.result = result;
    }
}
