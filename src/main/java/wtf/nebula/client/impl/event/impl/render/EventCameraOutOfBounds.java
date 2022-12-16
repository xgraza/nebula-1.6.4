package wtf.nebula.client.impl.event.impl.render;

import me.bush.eventbus.event.Event;

public class EventCameraOutOfBounds extends Event {
    private double distance;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
