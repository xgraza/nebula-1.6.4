package us.nebula.client.listener.event.input;

import net.minecraft.util.MovementInput;
import us.nebula.client.listener.Event;

/**
 * @author xgraza
 * @since 03/24/25
 */
public final class EventUpdateInput extends Event
{
    private final MovementInput input;

    public EventUpdateInput(final MovementInput input)
    {
        this.input = input;
    }

    public MovementInput getInput()
    {
        return input;
    }
}
