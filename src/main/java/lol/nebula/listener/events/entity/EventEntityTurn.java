package lol.nebula.listener.events.entity;

import lol.nebula.listener.bus.CancelableEvent;
import net.minecraft.entity.Entity;

public class EventEntityTurn extends CancelableEvent {
    private final Entity entity;
    private float yaw, pitch;

    public EventEntityTurn(Entity entity, float yaw, float pitch) {
        this.entity = entity;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Entity getEntity() {
        return entity;
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
}
