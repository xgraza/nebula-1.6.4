package wtf.nebula.event;

import me.bush.eventbus.event.Event;
import net.minecraft.src.MovingObjectPosition;

public class MiddleClickMouseEvent extends Event {
    private final MovingObjectPosition objectMouseOver;

    public MiddleClickMouseEvent(MovingObjectPosition objectMouseOver) {
        this.objectMouseOver = objectMouseOver;
    }

    public MovingObjectPosition getObjectMouseOver() {
        return objectMouseOver;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
