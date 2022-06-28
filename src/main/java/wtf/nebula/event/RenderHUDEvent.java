package wtf.nebula.event;

import me.bush.eventbus.event.Event;
import net.minecraft.src.ScaledResolution;

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
