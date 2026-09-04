package ez.nebula.client.api.listener.event.player;

import ez.nebula.client.api.listener.Event;

public final class EventSneak extends Event
{
    private boolean state;

    public EventSneak(boolean state)
    {
        this.state = state;
    }

    public void setState(boolean state)
    {
        this.state = state;
    }

    public boolean isState()
    {
        return state;
    }
}
