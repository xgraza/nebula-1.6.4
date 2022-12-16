package wtf.nebula.client.impl.event.impl.render;

import me.bush.eventbus.event.Event;
import net.minecraft.entity.Entity;

public class EventRenderVanillaNameTag extends Event {
    private final Entity entity;

    public EventRenderVanillaNameTag(Entity entity) {
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
