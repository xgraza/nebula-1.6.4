package wtf.nebula.client.impl.event.impl.render;

import me.bush.eventbus.event.Event;

public class EventRotateCamera extends Event {
    private float yaw, pitch;

    public EventRotateCamera(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
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

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
