package wtf.nebula.event;

import me.bush.eventbus.event.Event;
import net.minecraft.util.MovementInput;

public class PlayerSlowdownEvent extends Event {
    private final MovementInput movementInput;

    public PlayerSlowdownEvent(MovementInput movementInput) {
        this.movementInput = movementInput;
    }

    public MovementInput getMovementInput() {
        return movementInput;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
