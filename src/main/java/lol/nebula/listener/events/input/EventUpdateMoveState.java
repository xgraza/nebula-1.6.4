package lol.nebula.listener.events.input;

import lol.nebula.listener.bus.CancelableEvent;
import net.minecraft.util.MovementInput;

public class EventUpdateMoveState extends CancelableEvent {
    private final MovementInput input;

    public EventUpdateMoveState(MovementInput input) {
        this.input = input;
    }

    public MovementInput getInput() {
        return input;
    }
}
