package wtf.nebula.client.impl.event.impl.render;

import me.bush.eventbus.event.Event;
import net.minecraft.world.EnumSkyBlock;

public class EventUpdateLight extends Event {
    private final EnumSkyBlock skyBlock;

    public EventUpdateLight(EnumSkyBlock skyBlock) {
        this.skyBlock = skyBlock;
    }

    public EnumSkyBlock getSkyBlock() {
        return skyBlock;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
