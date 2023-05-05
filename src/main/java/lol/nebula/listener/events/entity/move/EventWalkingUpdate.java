package lol.nebula.listener.events.entity.move;

import lol.nebula.listener.bus.CancelableEvent;
import lol.nebula.listener.events.EventStage;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class EventWalkingUpdate extends CancelableEvent {

    private final EventStage stage;
    private double x, y, stance, z;
    private float yaw, pitch;
    private boolean onGround;

    public EventWalkingUpdate(EventStage stage, double x, double y, double stance, double z, float yaw, float pitch, boolean onGround) {
        this.stage = stage;
        this.x = x;
        this.y = y;
        this.stance = stance;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public EventStage getStage() {
        return stage;
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
}
