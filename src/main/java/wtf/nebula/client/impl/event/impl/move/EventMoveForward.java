package wtf.nebula.client.impl.event.impl.move;

import me.bush.eventbus.event.Event;
import net.minecraft.util.MovementInputFromOptions;

public class EventMoveForward extends Event {
    private final MovementInputFromOptions input;

    public EventMoveForward(MovementInputFromOptions input) {
        this.input = input;
    }

    public MovementInputFromOptions getInput() {
        return input;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
