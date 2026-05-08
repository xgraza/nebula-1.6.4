package us.nebula.client.listener.event.input;

import us.nebula.client.listener.Event;

/**
 * @author xgraza
 * @since 02/14/25
 */
public final class EventMouse extends Event
{
    private final int mouseButton;

    public EventMouse(final int mouseButton)
    {
        this.mouseButton = mouseButton;
    }

    public int getMouseButton()
    {
        return mouseButton;
    }
}
