package us.nebula.impl.event.input;

import us.nebula.api.listener.Event;

/**
 * @author xgraza
 * @since 02/14/25
 */
public final class EventKey extends Event
{
    private final int keyCode;

    public EventKey(final int keyCode)
    {
        this.keyCode = keyCode;
    }

    public int getKeyCode()
    {
        return keyCode;
    }
}
