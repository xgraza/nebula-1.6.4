package wtf.nebula.client.impl.event.impl.move;

import me.bush.eventbus.event.Event;
import net.minecraft.entity.Entity;

public class EventEntityCollision extends Event {
    private final Entity target, entity;

    public EventEntityCollision(Entity target, Entity entity) {
        this.target = target;
        this.entity = entity;
    }

    public Entity getTarget() {
        return target;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
