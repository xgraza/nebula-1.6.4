package wtf.nebula.event;

import me.bush.eventbus.event.Event;
import net.minecraft.src.Packet;

public class PacketEvent extends Event {
    private final Packet packet;
    private final Era era;

    public PacketEvent(Era era, Packet packet) {
        this.era = era;
        this.packet = packet;
    }

    public <T extends Packet> T getPacket() {
        return (T) packet;
    }

    public Era getEra() {
        return era;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }

    public static class Send extends PacketEvent {
        public Send(Era era, Packet packet) {
            super(era, packet);
        }
    }

    public static class Receive extends PacketEvent {
        public Receive(Era era, Packet packet) {
            super(era, packet);
        }
    }

    public enum Era {
        PRE, POST
    }
}
