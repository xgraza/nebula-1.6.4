package wtf.nebula.client.impl.event.impl.input;

import me.bush.eventbus.event.Event;
import net.minecraft.util.MovingObjectPosition;

public class EventMiddleClick extends Event {
    private final MovingObjectPosition result;

    public EventMiddleClick(MovingObjectPosition result) {
        this.result = result;
    }

    public MovingObjectPosition getResult() {
        return result;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
