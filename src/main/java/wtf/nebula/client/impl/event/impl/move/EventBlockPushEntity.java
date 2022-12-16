package wtf.nebula.client.impl.event.impl.move;

import me.bush.eventbus.event.Event;
import net.minecraft.entity.Entity;

public class EventBlockPushEntity extends Event {
    private final Entity entity;

    public EventBlockPushEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
