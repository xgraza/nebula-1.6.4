package ez.nebula.client.impl.hud2;

import ez.nebula.client.Nebula;
import ez.nebula.client.api.manager.hud2.HUDElement;
import ez.nebula.client.api.manager.hud2.trait.HUDManifest;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.render.gui.Render2D;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;

/**
 * @author xgraza
 * @since 9/6/26
 */
@HUDManifest(value = "Armor", description = "Displays the currently worn armor")
public final class ArmorHUDElement extends HUDElement
{
    private static final int STACK_ICON_SIZE = 16;

    private final Setting<Boolean> heldItem = builder("Held Item", false)
            .setDescription("If to also show the held item")
            .build();

    @Override
    public void render(final ScaledResolution res)
    {
        final int items = heldItem.getValue() ? 5 : 4;
        setWidth(items * STACK_ICON_SIZE);
        setHeight(STACK_ICON_SIZE);

        final ItemStack[] armor = MC.thePlayer.inventory.armorInventory;

        double posX = x;

        if (heldItem.getValue())
        {
            final ItemStack stack = Nebula.INVENTORY.stack();
            if (stack != null)
            {
                Render2D.itemWithEffects(stack, (int) posX, (int) y);
                posX += STACK_ICON_SIZE;
            } else
            {
                posX += STACK_ICON_SIZE / 2.0;
            }
        }

        for (int i = armor.length - 1; i >= 0; --i)
        {
            final ItemStack stack = armor[i];
            if (stack == null)
            {
                continue;
            }
            Render2D.itemWithEffects(stack, (int) posX, (int) y);
            posX += STACK_ICON_SIZE;
        }
    }
}
