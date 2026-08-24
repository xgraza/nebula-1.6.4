package ez.nebula.client.impl.module.player;

import ez.nebula.client.util.minecraft.network.PacketUtil;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.MovingObjectPosition;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.util.minecraft.player.InventoryUtil;

/**
 * @author xgraza
 * @since 03/05/25
 */
@ModuleManifest(name = "KeyPearl",
        description = "Throws an ender pearl from your hotbar on a key press",
        category = ModuleCategory.PLAYER)
public final class KeyPearlModule extends Module
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
        if (pearlSlot == InventoryUtil.INVALID_SLOT)
        {
            return;
        }

        if (getKey().isMouseBind() && getKey().getKeyCode() == 2 && MCFModule.INSTANCE.isToggled())
        {
            final MovingObjectPosition result = MC.objectMouseOver;
            if (result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
            {
                return;
            }
        }

        Nebula.INSTANCE.getInventoryManager().setSlot(pearlSlot);
        PacketUtil.send(new C08PacketPlayerBlockPlacement(
                Nebula.INSTANCE.getInventoryManager().getStack()));
        Nebula.INSTANCE.getInventoryManager().syncSlot();
    }
}
