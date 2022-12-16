package wtf.nebula.client.impl.event.impl.network;

import net.minecraft.network.Packet;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.base.EventEraed;

public class EventPacket extends EventEraed {
    private final Packet packet;

    public EventPacket(Era era, Packet packet) {
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
