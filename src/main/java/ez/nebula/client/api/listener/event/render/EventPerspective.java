package ez.nebula.client.api.listener.event.render;

import ez.nebula.client.api.listener.Event;

public final class EventPerspective extends Event
{
    private float fov, aspect, zNear, zFar;

    public EventPerspective(float fov, float aspect, float zNear, float zFar)
    {
        this.fov = fov;
        this.aspect = aspect;
        this.zNear = zNear;
        this.zFar = zFar;
    }

    public float getFov()
    {
        return fov;
    }

    public void setFov(float fov)
    {
        this.fov = fov;
    }

    public float getAspect()
    {
        return aspect;
    }

    public void setAspect(float aspect)
    {
        this.aspect = aspect;
    }

    public float getzNear()
    {
        return zNear;
    }

    public void setzNear(float zNear)
    {
        this.zNear = zNear;
    }

    public float getzFar()
    {
        return zFar;
    }

    public void setzFar(float zFar)
    {
        this.zFar = zFar;
    }
}
