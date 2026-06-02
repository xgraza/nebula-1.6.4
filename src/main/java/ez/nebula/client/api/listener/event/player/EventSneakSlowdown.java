package ez.nebula.client.api.listener.event.player;

import net.minecraft.util.MovementInput;
import ez.nebula.client.api.listener.Event;

/**
 * @author xgraza
 * @since 03/24/25
 */
public final class EventSneakSlowdown extends Event
{
    private final MovementInput input;

    public EventSneakSlowdown(MovementInput input)
    {
        this.input = input;
    }

    public MovementInput getInput()
    {
        return input;
    }
}
