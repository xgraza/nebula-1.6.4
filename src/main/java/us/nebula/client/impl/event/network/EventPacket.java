package us.nebula.client.impl.event.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import us.nebula.client.api.listener.Event;

/**
 * @author xgraza
 * @since 02/14/25
 */
@SuppressWarnings("unchecked")
public class EventPacket extends Event
{
    private final Packet packet;

    public EventPacket(final Packet packet)
    {
        this.packet = packet;
    }

    public <T extends Packet> T getPacket()
    {
        return (T) packet;
    }

    public static final class Inbound extends EventPacket
    {
        private final INetHandler handler;

        public Inbound(final INetHandler handler, final Packet packet)
        {
            super(packet);
            this.handler = handler;
        }

        public INetHandler getHandler()
        {
            return handler;
        }
    }

    public static final class Outbound extends EventPacket
    {
        public Outbound(final Packet packet)
        {
            super(packet);
        }
    }
}
