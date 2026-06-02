package ez.nebula.client.impl.module.player;

import ez.nebula.client.api.manager.module.Module;
import net.minecraft.inventory.Container;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.network.EventPacket;
import ez.nebula.client.api.setting.Setting;

/**
 * @author xgraza
 * @since 03/26/25
 */
@ModuleManifest(name = "InventorySync",
        description = "Syncs inventory transactions with the server to prevent de-sync",
        category = ModuleCategory.PLAYER)
public final class InventorySyncModule extends Module
{
    private final Setting<Boolean> packetSetting = builder("Packet", false)
            .setDescription("If to send a packet to try to further sync your inventory")
            .build();

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
