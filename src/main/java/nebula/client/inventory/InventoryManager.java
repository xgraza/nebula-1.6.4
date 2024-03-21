package nebula.client.inventory;

import nebula.client.Nebula;
import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.net.EventPacket;
import nebula.client.listener.event.render.EventRenderHotbarSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S09PacketHeldItemChange;

/**
 * @author Gavin
 * @since 08/18/23
 */
public class InventoryManager {

  /**
   * The minecraft game instance
   */
  private final Minecraft mc = Minecraft.getMinecraft();

  private int slot;

  public InventoryManager() {
    Nebula.BUS.subscribe(this);
  }

  @Subscribe
  private final Listener<EventPacket.Out> packetOut = event -> {
    if (event.packet() instanceof C09PacketHeldItemChange packet) {
      int clientSlot = packet.func_149614_c();
      if (clientSlot < 0 || clientSlot > 8) event.setCanceled(true);

      slot = clientSlot;
    }
  };

  @Subscribe
  private final Listener<EventPacket.In> packetIn = event -> {
    if (event.packet() instanceof S09PacketHeldItemChange packet) {
      slot = packet.func_149385_c();
    }
  };

  @Subscribe
  private final Listener<EventRenderHotbarSlot> renderHotbarSlot = event -> {
    event.setSlot(slot);
  };

  public int slot() {
    return slot;
  }

  public void sync() {
    if (mc.thePlayer == null) return;

    if (mc.thePlayer.inventory.currentItem != slot) {
      mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(
        mc.thePlayer.inventory.currentItem));
    }
  }
}
