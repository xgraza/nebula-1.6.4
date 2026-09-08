package ez.nebula.client.impl.hud;

import ez.nebula.client.api.manager.hud.HUDElement;
import ez.nebula.client.api.manager.hud.trait.HUDManifest;
import ez.nebula.client.util.render.gui.Render2D;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;

/**
 * @author xgraza
 * @since 5/6/26
 */
@HUDManifest(name = "ArmorStatus",
        description = "Displays current armor status",
        x = 20, y = 20, height = 18)
public final class ArmorStatusHUDElement extends HUDElement
{
    private static final int WIDTH_PER_COMPONENT = 16;

    @Override
    public void init()
    {
        setWidth((4 * WIDTH_PER_COMPONENT) + 4);
    }

    @Override
    public void render(final ScaledResolution res)
    {
        final ItemStack[] armorStacks = MC.thePlayer.inventory.armorInventory;
        for (int i = armorStacks.length - 1; i >= 0; --i)
        {
            final ItemStack stack = armorStacks[3 - i];
            if (stack == null)
            {
                continue;
            }
            final double posX = x + 1 + (i * WIDTH_PER_COMPONENT);
            final double posY = y + 1;
            Render2D.itemWithEffects(stack, (int) posX, (int) posY);
        }
    }
}
