package us.nebula.client.cheat.impl.player;

import net.minecraft.inventory.Container;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.value.Setting;
import us.nebula.client.listener.event.network.EventPacket;

/**
 * @author xgraza
 * @since 03/26/25
 */
@CheatManifest(name = "InventorySync",
        description = "Syncs inventory transactions with the server to prevent de-sync",
        category = CheatCategory.PLAYER)
public final class InventorySyncCheat extends Cheat
{
    private final Setting<Boolean> packetSetting = new Setting<>(
            "Packet", false);

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (event.getPacket() instanceof S32PacketConfirmTransaction)
        {
            final S32PacketConfirmTransaction packet = event.getPacket();
            final Container container = getContainer(packet.getID());
            if (container == null)
            {
                return;
            }
            final short transaction = packet.getUID();
            final short currentTransaction = container.transactionID;
            if (transaction + 1 <= currentTransaction || transaction > currentTransaction)
            {
                container.transactionID = (short) (transaction + 1);
            }
            if (packetSetting.getValue())
            {
                MC.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement());
            }
        }
    };

    private Container getContainer(final int windowId)
    {
        if (windowId == 0)
        {
            return MC.thePlayer.inventoryContainer;
        } else if (windowId == MC.thePlayer.openContainer.windowId)
        {
            return MC.thePlayer.openContainer;
        }
        return null;
    }
}
