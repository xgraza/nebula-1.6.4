package ez.nebula.client.api.listener.event.network;

import ez.nebula.client.api.listener.Event;
import net.minecraft.util.IChatComponent;

/**
 * @author xgraza
 * @since 5/16/26
 */
public final class EventDisconnect extends Event
{
    private final boolean remote;
    private final IChatComponent reason;

    public EventDisconnect(boolean remote, IChatComponent reason)
    {
        this.remote = remote;
        this.reason = reason;
    }

    public boolean isRemote()
    {
        return remote;
    }

    public IChatComponent getReason()
    {
        return reason;
    }
}
