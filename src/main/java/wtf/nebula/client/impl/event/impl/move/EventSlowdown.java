package wtf.nebula.client.impl.event.impl.move;

import me.bush.eventbus.event.Event;
import net.minecraft.util.MovementInput;

public class EventSlowdown extends Event {
    private final MovementInput input;

    public EventSlowdown(MovementInput input) {
        this.input = input;
    }

    public MovementInput getInput() {
        return input;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
