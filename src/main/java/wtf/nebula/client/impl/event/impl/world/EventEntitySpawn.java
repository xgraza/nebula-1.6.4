package wtf.nebula.client.impl.event.impl.world;

import me.bush.eventbus.event.Event;
import net.minecraft.entity.Entity;

public class EventEntitySpawn extends Event {
    private final Entity entity;

    public EventEntitySpawn(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
