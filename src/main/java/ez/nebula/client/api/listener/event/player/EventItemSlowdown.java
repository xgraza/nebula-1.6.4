package ez.nebula.client.api.listener.event.player;

import ez.nebula.client.api.listener.Event;
import net.minecraft.util.MovementInput;

/**
 * @author xgraza
 * @since 03/02/25
 */
public final class EventItemSlowdown extends Event
{
    private final MovementInput input;

    public EventItemSlowdown(final MovementInput input)
    {
        this.input = input;
    }

    public MovementInput getInput()
    {
        return input;
    }
}
