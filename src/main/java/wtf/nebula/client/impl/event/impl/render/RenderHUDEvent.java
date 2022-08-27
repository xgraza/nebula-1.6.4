package wtf.nebula.client.impl.event.impl.render;

import me.bush.eventbus.event.Event;
import net.minecraft.client.gui.ScaledResolution;

public class RenderHUDEvent extends Event {
    private final ScaledResolution resolution;

    public RenderHUDEvent(ScaledResolution resolution) {
        this.resolution = resolution;
    }

    public ScaledResolution getResolution() {
        return resolution;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
