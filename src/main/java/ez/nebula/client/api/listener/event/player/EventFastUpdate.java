package ez.nebula.client.api.listener.event.player;

import ez.nebula.client.api.listener.Event;

/**
 * @author xgraza
 * @since 6/1/26
 */
public final class EventFastUpdate extends Event
{
    private int updates = 0;

    public void setUpdates(int updates)
    {
        this.updates = updates;
    }

    public int getUpdates()
    {
        return updates;
    }
}
