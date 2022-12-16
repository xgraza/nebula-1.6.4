package wtf.nebula.client.impl.event.impl.move;

import me.bush.eventbus.event.Event;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

public class EventStep extends Event {
    private final Entity entity;
    private final AxisAlignedBB bb;

    public EventStep(Entity entity, AxisAlignedBB bb) {
        this.entity = entity;
        this.bb = bb;
    }

    public Entity getEntity() {
        return entity;
    }

    public AxisAlignedBB getBb() {
        return bb;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
