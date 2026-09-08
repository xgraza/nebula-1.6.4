package ez.nebula.client.api.listener.event.input;

import ez.nebula.client.api.listener.Event;
import net.minecraft.util.MovementInput;

/**
 * @author xgraza
 * @since 03/24/25
 */
public class EventUpdateInput extends Event
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

    public static final class Post extends EventUpdateInput
    {
        private boolean modifySneaking = true;

        public Post(MovementInput input)
        {
            super(input);
        }

        public void setModifySneaking(boolean modifySneaking)
        {
            this.modifySneaking = modifySneaking;
        }

        public boolean isModifySneaking()
        {
            return modifySneaking;
        }
    }
}
