package ez.nebula.client.impl.module.player;

import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.manager.module.type.InteractionModule;
import ez.nebula.client.util.minecraft.player.InventoryUtil;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.util.MovingObjectPosition;

/**
 * @author xgraza
 * @since 03/05/25
 */
@ModuleManifest(name = "KeyPearl",
        description = "Throws an ender pearl from your hotbar on a key press",
        category = ModuleCategory.PLAYER)
public final class KeyPearlModule extends InteractionModule
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
        use(pearlSlot);
    }
}
