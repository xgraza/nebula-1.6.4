package us.nebula.client.impl.cheat.player;

import net.minecraft.item.ItemEnderPearl;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import us.nebula.client.ClientSettings;
import us.nebula.client.Nebula;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.util.player.ChatUtil;
import us.nebula.client.util.player.InventoryUtil;

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

        final int pearlSlot = InventoryUtil.getHotbarItem(ItemEnderPearl.class);
        if (pearlSlot == -1)
        {
            if (ClientSettings.VERBOSE_LOGGING)
            {
                ChatUtil.send("No pearls found in slots 0-8!");
            }
            return;
        }

        Nebula.INSTANCE.getInventoryManager().setSlot(pearlSlot);
        MC.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                Nebula.INSTANCE.getInventoryManager().getStack()));
        Nebula.INSTANCE.getInventoryManager().syncSlot();
    }
}
