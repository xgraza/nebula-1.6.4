package lol.nebula.listener.events.net;

import lol.nebula.listener.bus.CancelableEvent;
import net.minecraft.network.Packet;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class EventPacket extends CancelableEvent {

    /**
     * The packet outbound/inbound
     */
    private final Packet packet;

    public EventPacket(Packet packet) {
        this.packet = packet;
    }

    /**
     * Gets the packet
     * @return the packet
     * @param <T> the packet class
     */
    public <T extends Packet> T getPacket() {
        return (T) packet;
    }

    public static class Inbound extends EventPacket {

        public Inbound(Packet packet) {
            super(packet);
        }
    }

    public static class Outbound extends EventPacket {

        public Outbound(Packet packet) {
            super(packet);
        }
    }
}
