package lol.nebula.listener.events.entity;

import lol.nebula.listener.bus.CancelableEvent;
import net.minecraft.entity.Entity;

/**
 * @author aesthetical
 * @since 05/03/23
 */
public class EventEntityRidingUpdate extends CancelableEvent {
    private final Entity ridingEntity;

    public EventEntityRidingUpdate(Entity ridingEntity) {
        this.ridingEntity = ridingEntity;
    }

    public Entity getRidingEntity() {
        return ridingEntity;
    }
}
