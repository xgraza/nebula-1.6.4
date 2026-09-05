package ez.nebula.client.api.player.server;

import ez.nebula.client.util.minecraft.network.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import ez.nebula.client.ClientConfig;
import ez.nebula.client.Nebula;
import ez.nebula.client.api.listener.EventBus;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.IManager;
import ez.nebula.client.api.listener.event.network.EventPacket;

/**
 * @author xgraza
 * @since 04/02/25
 */
public final class InventoryManager implements IManager
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    private int slot = -1;

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (event.getPacket() instanceof S09PacketHeldItemChange)
        {
            slot = ((S09PacketHeldItemChange) event.getPacket()).getSlotIndex();
        }
    };

    @Subscribe
    private final EventListener<EventPacket.Outbound> outboundEventListener = event ->
    {
        if (event.getPacket() instanceof C09PacketHeldItemChange)
        {
            final C09PacketHeldItemChange packet = event.getPacket();
            final int slotIndex = packet.getSlotIndex();
            if (slotIndex > 8 || slotIndex < 0)
            {
                if (ClientConfig.DEBUG)
                {
                    Nebula.INSTANCE.getLogger().warn(
                            "Something tried to set slot to {}", slotIndex);
                }
                event.cancel();
                return;
            }
            slot = slotIndex;
        }
    };

    @Override
    public void init()
    {
        EventBus.subscribe(this);
    }

    public void setSlot(final int index)
    {
        setSlot(index, false);
    }

    public void setSlot(final int index, final boolean forced)
    {
        if (slot == index && !forced)
        {
            return;
        }
        PacketUtil.send(new C09PacketHeldItemChange(slot = index));
    }

    public void setSlotClient(final int index)
    {
        if (MC.thePlayer == null)
        {
            return;
        }
        if (MC.thePlayer.inventory.currentItem != index)
        {
            setSlot(index, true);
        }
        MC.thePlayer.inventory.currentItem = MC.playerController.currentPlayerItem = index;
    }

    public void syncSlot()
    {
        if (slot != MC.thePlayer.inventory.currentItem)
        {
            PacketUtil.send(new C09PacketHeldItemChange(MC.thePlayer.inventory.currentItem));
        }
    }

    public int getSlot()
    {
        if (slot == -1 && MC.thePlayer != null)
        {
            slot = MC.thePlayer.inventory.currentItem;
        }
        return slot;
    }

    public ItemStack getStack()
    {
        if (MC.thePlayer == null)
        {
            return null;
        }
        return MC.thePlayer.inventory.getStackInSlot(getSlot());
    }
}
