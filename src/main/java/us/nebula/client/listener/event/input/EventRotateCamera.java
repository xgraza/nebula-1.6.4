package us.nebula.client.listener.event.input;

import net.minecraft.entity.Entity;
import us.nebula.client.listener.Event;

/**
 * @author xgraza
 * @since 03/24/25
 */
public final class EventRotateCamera extends Event
{
    private final Entity entity;
    private final float diffYaw;
    private final float diffPitch;
    private float yaw, pitch;

    public EventRotateCamera(Entity entity, float yaw, float pitch, float diffYaw, float diffPitch)
    {
        this.entity = entity;
        this.yaw = yaw;
        this.pitch = pitch;
        this.diffYaw = diffYaw;
        this.diffPitch = diffPitch;
    }

    public Entity getEntity()
    {
        return entity;
    }

    public float getYaw()
    {
        return yaw;
    }

    public void setYaw(float yaw)
    {
        this.yaw = yaw;
    }

    public float getPitch()
    {
        return pitch;
    }

    public void setPitch(float pitch)
    {
        this.pitch = pitch;
    }

    public float getDiffYaw()
    {
        return diffYaw;
    }

    public float getDiffPitch()
    {
        return diffPitch;
    }
}
