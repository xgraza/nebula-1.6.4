package wtf.nebula.client.impl.event.impl.move;

import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.base.EventEraed;

public class EventMotionUpdate extends EventEraed {
    public double x, y, stance, z;
    public float yaw, pitch;
    public boolean onGround;

    public EventMotionUpdate(double x, double y, double stance, double z, float yaw, float pitch, boolean onGround) {
        super(Era.PRE);
        this.x = x;
        this.y = y;
        this.stance = stance;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public EventMotionUpdate() {
        super(Era.POST);
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
