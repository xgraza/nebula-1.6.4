package lol.nebula.management;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.net.EventPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S09PacketHeldItemChange;

/**
 * @author aesthetical
 * @since 05/01/23
 */
public class InventoryManager {

    /**
     * The minecraft game instance
     */
    private final Minecraft mc = Minecraft.getMinecraft();

    /**
     * The server slot
     */
    private int serverSlot = -1;

    @Listener
    public void onPacketOutbound(EventPacket.Outbound event) {
        if (event.getPacket() instanceof C09PacketHeldItemChange) {
            C09PacketHeldItemChange packet = event.getPacket();
            serverSlot = packet.func_149614_c();
        }
    }

    @Listener
    public void onPacketInbound(EventPacket.Inbound event) {
        if (event.getPacket() instanceof S09PacketHeldItemChange) {
            S09PacketHeldItemChange packet = event.getPacket();
            serverSlot = packet.func_149385_c();
        }
    }

    /**
     * Sets the server sided slot
     * @param slot the slot
     */
    public void setSlot(int slot) {
        serverSlot = slot;
        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
    }

    /**
     * Syncs the server slot with the client slot
     */
    public void sync() {
        if (serverSlot != mc.thePlayer.inventory.currentItem) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
    }

    /**
     * Gets the server slot
     * @return the server slot or the client slot if the server slot is -1
     */
    public int getServerSlot() {
        return serverSlot == -1
                ? mc.thePlayer.inventory.currentItem
                : serverSlot;
    }
}
