package lol.nebula.listener.events.entity.move;

import net.minecraft.util.MovementInput;

/**
 * @author aesthetical
 * @since 04/29/23
 */
public class EventSlowdown {
    private final MovementInput input;

    public EventSlowdown(MovementInput input) {
        this.input = input;
    }

    public MovementInput getInput() {
        return input;
    }
}
