package nebula.client.listener.event.net;

import nebula.client.listener.bus.Cancelable;
import net.minecraft.network.Packet;

/**
 * @author Gavin
 * @since 08/09/23
 */
public class EventPacket extends Cancelable {

  private final Packet packet;

  public EventPacket(Packet packet) {
    this.packet = packet;
  }

  public <T extends Packet> T packet() {
    return (T) packet;
  }

  public static class In extends EventPacket {

    public In(Packet packet) {
      super(packet);
    }
  }

  public static class Out extends EventPacket {

    public Out(Packet packet) {
      super(packet);
    }
  }
}
