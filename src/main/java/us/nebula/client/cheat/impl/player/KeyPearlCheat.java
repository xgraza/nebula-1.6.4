package us.nebula.client.cheat.impl.player;

import net.minecraft.item.ItemEnderPearl;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.MovingObjectPosition;
import us.nebula.client.Nebula;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.player.InventoryUtil;

/**
 * @author xgraza
 * @since 03/05/25
 */
@CheatManifest(name = "KeyPearl",
        description = "Throws an ender pearl from your hotbar on a key press",
        category = CheatCategory.PLAYER)
public final class KeyPearlCheat extends Cheat
{
    @Override
    public void onEnable()
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
            return;
        }

        if (getKey().isMouseBind() && getKey().getKeyCode() == 2 && MCFCheat.INSTANCE.isToggled())
        {
            final MovingObjectPosition result = MC.objectMouseOver;
            if (result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
            {
                return;
            }
        }

        Nebula.INSTANCE.getInventoryManager().setSlot(pearlSlot);
        MC.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                Nebula.INSTANCE.getInventoryManager().getStack()));
        Nebula.INSTANCE.getInventoryManager().syncSlot();
    }
}
