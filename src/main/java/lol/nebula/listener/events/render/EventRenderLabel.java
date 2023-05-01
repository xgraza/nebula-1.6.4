package lol.nebula.listener.events.render;

import lol.nebula.listener.bus.CancelableEvent;
import net.minecraft.entity.Entity;

/**
 * @author aesthetical
 * @since 05/01/23
 */
public class EventRenderLabel extends CancelableEvent {
    private final Entity entity;
    private final String text;

    public EventRenderLabel(Entity entity, String text) {
        this.entity = entity;
        this.text = text;
    }

    public Entity getEntity() {
        return entity;
    }

    public String getText() {
        return text;
    }
}

