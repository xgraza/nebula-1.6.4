package lol.nebula.listener.events.render.world;

import lol.nebula.listener.bus.CancelableEvent;

/**
 * @author aesthetical
 * @since 05/15/23
 */
public class EventCameraDistance extends CancelableEvent {
    private double distance;

    public EventCameraDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
