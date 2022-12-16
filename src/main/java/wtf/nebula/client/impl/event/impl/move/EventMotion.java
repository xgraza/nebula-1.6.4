package wtf.nebula.client.impl.event.impl.move;

import me.bush.eventbus.event.Event;

public class EventMotion extends Event {
    public double x, y, z;

    public EventMotion(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
