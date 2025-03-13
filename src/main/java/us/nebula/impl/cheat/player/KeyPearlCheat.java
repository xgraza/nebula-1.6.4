package us.nebula.impl.cheat.player;

import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import us.nebula.ClientSettings;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.util.player.ChatUtil;

/**
 * @author xgraza
 * @since 03/05/25
 */
@CheatManifest(name = "KeyPearl",
        description = "Throws an ender pearl on a key press",
        category = CheatCategory.PLAYER)
public final class KeyPearlCheat extends Cheat
{
    @Override
    protected void onEnable()
    {
        super.onEnable();

        toggle();
        if (MC.thePlayer == null || MC.theWorld == null)
        {
            return;
        }

        final int pearlSlot = getPearlSlot();
        if (pearlSlot == -1)
        {
            if (ClientSettings.VERBOSE_LOGGING)
            {
                ChatUtil.send("No pearls found in slots 0-8!");
            }
            return;
        }

        MC.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(pearlSlot));
        MC.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                MC.thePlayer.inventory.getStackInSlot(pearlSlot)));
        MC.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(
                MC.thePlayer.inventory.currentItem));
    }

    private int getPearlSlot()
    {
        for (int i = 0; i < 9; ++i)
        {
            final ItemStack itemStack = MC.thePlayer.inventory.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() instanceof ItemEnderPearl)
            {
                return i;
            }
        }
        return -1;
    }
}
