package us.nebula.impl.gui.overlay;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import us.nebula.api.manager.overlay.Overlay;
import us.nebula.api.manager.overlay.OverlayManifest;
import us.nebula.api.manager.overlay.StaticPosition;
import us.nebula.util.render.RenderUtil;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 03/19/25
 */
@StaticPosition
@OverlayManifest("Armor")
public final class ArmorOverlay extends Overlay
{
    private static final double ITEM_ICON_WIDTH = 16.0;

    @Override
    public void render(final ScaledResolution resolution, final float partialTicks)
    {
        final double posY = resolution.getScaledHeight_double() - 54;
        double posX = (resolution.getScaledWidth_double() / 2.0) + 10;

        for (int i = 0; i < 4; ++i)
        {
            final ItemStack itemStack = MC.thePlayer.inventory.armorInventory[3 - i];
            RenderUtil.renderItemWithEffects(itemStack, (int) posX, (int) posY);
            posX += ITEM_ICON_WIDTH;
        }
        RenderUtil.renderItemWithEffects(MC.thePlayer.getHeldItem(), (int) posX, (int) posY);
    }
}
