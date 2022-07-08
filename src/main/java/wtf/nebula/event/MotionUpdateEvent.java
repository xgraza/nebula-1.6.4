package wtf.nebula.event;

import me.bush.eventbus.event.Event;

public class MotionUpdateEvent extends Event {
    private double x, y, stance, z;
    private float yaw, pitch;
    private boolean onGround;

    private final Era era;

    public MotionUpdateEvent(Era era) {
        this.era = era;
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

    public double getStance() {
        return stance;
    }

    public void setStance(double stance) {
        this.stance = stance;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public Era getEra() {
        return era;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }

    public enum Era {
        PRE, POST
    }
}
