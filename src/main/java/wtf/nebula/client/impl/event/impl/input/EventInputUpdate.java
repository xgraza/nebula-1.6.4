package wtf.nebula.client.impl.event.impl.input;

import me.bush.eventbus.event.Event;
import net.minecraft.util.MovementInput;

public class EventInputUpdate extends Event {
    private final MovementInput input;

    public EventInputUpdate(MovementInput input) {
        this.input = input;
    }

    public MovementInput getInput() {
        return input;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
