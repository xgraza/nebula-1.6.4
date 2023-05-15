package lol.nebula.listener.events.render.world;

import lol.nebula.listener.bus.CancelableEvent;
import net.minecraft.entity.item.EntityFallingBlock;

/**
 * @author aesthetical
 * @since 05/15/23
 */
public class EventRenderFallingBlock extends CancelableEvent {
    private final EntityFallingBlock entity;

    public EventRenderFallingBlock(EntityFallingBlock entity) {
        this.entity = entity;
    }

    public EntityFallingBlock getEntity() {
        return entity;
    }
}
