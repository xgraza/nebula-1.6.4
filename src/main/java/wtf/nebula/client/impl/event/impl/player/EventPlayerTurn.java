package wtf.nebula.client.impl.event.impl.player;

import me.bush.eventbus.event.Event;
import net.minecraft.entity.Entity;

public class EventPlayerTurn extends Event {
    private final Entity entity;
    private float yaw, pitch;

    public EventPlayerTurn(Entity entity, float yaw, float pitch) {
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

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
