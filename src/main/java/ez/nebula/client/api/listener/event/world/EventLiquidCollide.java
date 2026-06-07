package ez.nebula.client.api.listener.event.world;

import ez.nebula.client.api.listener.Event;

/**
 * @author xgraza
 * @since 6/7/26
 */
public final class EventLiquidCollide extends Event
{
    private boolean result;

    public EventLiquidCollide(boolean result)
    {
        this.result = result;
    }

    public void setResult(boolean result)
    {
        this.result = result;
    }

    public boolean isResult()
    {
        return result;
    }
}
