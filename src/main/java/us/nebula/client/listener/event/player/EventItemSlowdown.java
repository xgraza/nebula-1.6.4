package us.nebula.client.listener.event.player;

import net.minecraft.util.MovementInput;
import us.nebula.client.listener.Event;

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
