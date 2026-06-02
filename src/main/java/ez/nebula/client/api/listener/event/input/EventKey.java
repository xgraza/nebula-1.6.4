package ez.nebula.client.api.listener.event.input;

import ez.nebula.client.api.listener.Event;

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
