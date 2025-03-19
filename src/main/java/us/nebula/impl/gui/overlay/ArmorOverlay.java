package us.nebula.impl.gui.overlay;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import us.nebula.api.manager.overlay.Overlay;
import us.nebula.api.manager.overlay.OverlayManifest;
import us.nebula.api.manager.overlay.StaticPosition;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 03/19/25
 */
@StaticPosition
@OverlayManifest("Armor")
public final class ArmorOverlay extends Overlay
{
    private static final RenderItem RENDER_ITEM = new RenderItem();
    private static final double ITEM_ICON_WIDTH = 16.0;

    @Override
    public void render(final ScaledResolution resolution, final float partialTicks)
    {
        glPushMatrix();
        final double posY = resolution.getScaledHeight_double() - 54;
        double posX = (resolution.getScaledWidth_double() / 2.0) + 10;

        for (int i = 0; i < 4; ++i)
        {
            final ItemStack itemStack = MC.thePlayer.inventory.armorInventory[3- i];
            RENDER_ITEM.renderItemAndEffectIntoGUI(MC.fontRenderer,
                    MC.getTextureManager(), itemStack, (int) posX, (int) posY);
            posX += ITEM_ICON_WIDTH;
        }
        if (MC.thePlayer.getHeldItem() != null)
        {
            RENDER_ITEM.renderItemAndEffectIntoGUI(MC.fontRenderer,
                    MC.getTextureManager(), MC.thePlayer.getHeldItem(), (int) posX, (int) posY);
        }
        glPopMatrix();
    }
}
