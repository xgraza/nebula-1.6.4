package ez.nebula.client.api.listener.event.game;

import ez.nebula.client.api.listener.Event;
import net.minecraft.client.gui.GuiScreen;

public final class EventDisplayGUI extends Event
{
    private final GuiScreen previous;
    private GuiScreen pending;

    public EventDisplayGUI(GuiScreen previous, GuiScreen pending)
    {
        this.previous = previous;
        this.pending = pending;
    }

    public GuiScreen getPrevious()
    {
        return previous;
    }

    public GuiScreen getPending()
    {
        return pending;
    }

    public void setPending(GuiScreen pending)
    {
        this.pending = pending;
    }
}
