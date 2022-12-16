package wtf.nebula.client.impl.event.impl.player;

import me.bush.eventbus.event.Event;
import net.minecraft.entity.Entity;

public class EventAttack extends Event {
    private final Entity entity;

    public EventAttack(Entity entity) {
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
