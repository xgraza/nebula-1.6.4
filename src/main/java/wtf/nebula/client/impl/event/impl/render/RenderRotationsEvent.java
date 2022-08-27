package wtf.nebula.client.impl.event.impl.render;

import me.bush.eventbus.event.Event;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.base.EraBasedEvent;

public class RenderRotationsEvent extends EraBasedEvent {
    public float yaw, pitch;

    public RenderRotationsEvent(Era era, float yaw, float pitch) {
        super(era);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
