package wtf.nebula.client.impl.event.impl.network;

import me.bush.eventbus.event.Event;
import net.minecraft.network.Packet;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.base.EraBasedEvent;

public class PacketEvent extends EraBasedEvent {
    private final Packet packet;

    public PacketEvent(Era era, Packet packet) {
        super(era);
        this.packet = packet;
    }

    public <T extends Packet> T getPacket() {
        return (T) packet;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
