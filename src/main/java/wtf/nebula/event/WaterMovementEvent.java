package wtf.nebula.event;

import me.bush.eventbus.event.Event;
import net.minecraft.src.Entity;

public class WaterMovementEvent extends Event {
    private final Entity entity;

    public WaterMovementEvent(Entity entity) {
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
