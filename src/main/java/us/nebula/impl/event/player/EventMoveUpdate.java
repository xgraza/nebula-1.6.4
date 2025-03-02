package us.nebula.impl.event.player;

import us.nebula.api.listener.Event;

/**
 * @author xgraza
 * @since 03/01/25
 */
public final class EventMoveUpdate extends Event
{
    private double x, y, stance, z;
    private float yaw, pitch;
    private boolean onGround;

    public EventMoveUpdate(double x, double y, double stance, double z, float yaw, float pitch, boolean onGround)
    {
        this.x = x;
        this.y = y;
        this.stance = stance;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getStance()
    {
        return stance;
    }

    public void setStance(double stance)
    {
        this.stance = stance;
    }

    public double getZ()
    {
        return z;
    }

    public void setZ(double z)
    {
        this.z = z;
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

    public boolean isOnGround()
    {
        return onGround;
    }

    public void setOnGround(boolean onGround)
    {
        this.onGround = onGround;
    }
}
