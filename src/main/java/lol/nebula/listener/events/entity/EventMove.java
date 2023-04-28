package lol.nebula.listener.events.entity;

import lol.nebula.listener.bus.CancelableEvent;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class EventMove extends CancelableEvent {
    private double x, y, z;

    public EventMove(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
