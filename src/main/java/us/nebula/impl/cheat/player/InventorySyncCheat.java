package us.nebula.impl.cheat.player;

import net.minecraft.inventory.Container;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.impl.event.network.EventPacket;

/**
 * @author xgraza
 * @since 03/26/25
 */
@CheatManifest(name = "InventorySync",
        description = "Syncs the transactionId in containers",
        category = CheatCategory.PLAYER)
public final class InventorySyncCheat extends Cheat
{
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
