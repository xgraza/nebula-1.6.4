package wtf.nebula.event;

import me.bush.eventbus.event.Event;

public class MotionEvent extends Event {
    private double x, y, z;

    public MotionEvent(double x, double y, double z) {
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

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
