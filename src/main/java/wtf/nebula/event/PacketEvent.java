package wtf.nebula.event;

import me.bush.eventbus.event.Event;
import net.minecraft.src.Packet;

public class PacketEvent extends Event {
    private final Packet packet;

    public PacketEvent(Packet packet) {
        this.packet = packet;
    }

    public <T extends Packet> T getPacket() {
        return (T) packet;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }

    public static class Send extends PacketEvent {
        public Send(Packet packet) {
            super(packet);
        }
    }

    public static class Receive extends PacketEvent {
        public Receive(Packet packet) {
            super(packet);
        }
    }
}
