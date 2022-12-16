package wtf.nebula.client.impl.event.impl.render;

import me.bush.eventbus.event.Event;

public class EventGluPerspective extends Event {
    private float fovy, aspect, zNear, zFar;

    public EventGluPerspective(float fovy, float aspect, float zNear, float zFar) {
        this.fovy = fovy;
        this.aspect = aspect;
        this.zNear = zNear;
        this.zFar = zFar;
    }

    public float getFovy() {
        return fovy;
    }

    public void setFovy(float fovy) {
        this.fovy = fovy;
    }

    public float getAspect() {
        return aspect;
    }

    public void setAspect(float aspect) {
        this.aspect = aspect;
    }

    public float getzNear() {
        return zNear;
    }

    public void setzNear(float zNear) {
        this.zNear = zNear;
    }

    public float getzFar() {
        return zFar;
    }

    public void setzFar(float zFar) {
        this.zFar = zFar;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
