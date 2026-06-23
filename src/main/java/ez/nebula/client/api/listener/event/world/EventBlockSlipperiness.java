package ez.nebula.client.api.listener.event.world;

import ez.nebula.client.api.listener.Event;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;

public final class EventBlockSlipperiness extends Event
{
    private final Entity entity;
    private final Block block;

    private float slipperiness;

    public EventBlockSlipperiness(Entity entity, Block block, float slipperiness)
    {
        this.entity = entity;
        this.block = block;
        this.slipperiness = slipperiness;
    }

    public Entity getEntity()
    {
        return entity;
    }

    public Block getBlock()
    {
        return block;
    }

    public float getSlipperiness()
    {
        return slipperiness;
    }

    public void setSlipperiness(float slipperiness)
    {
        this.slipperiness = slipperiness;
    }
}
