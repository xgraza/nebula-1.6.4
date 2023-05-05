package lol.nebula.listener.events.entity.move;

import lol.nebula.listener.bus.CancelableEvent;
import net.minecraft.entity.Entity;

/**
 * @author aesthetical
 * @since 05/02/23
 */
public class EventPushOutOfBlocks extends CancelableEvent {
    private final Entity entity;

    public EventPushOutOfBlocks(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
