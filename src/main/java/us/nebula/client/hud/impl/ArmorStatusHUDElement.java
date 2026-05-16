package us.nebula.client.hud.impl;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import us.nebula.client.hud.HUDElement;
import us.nebula.client.hud.HUDManifest;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

/**
 * @author xgraza
 * @since 5/6/26
 */
@HUDManifest(name = "ArmorStatus",
        description = "Displays current armor status",
        x = 20, y = 20, height = 18)
public final class ArmorStatusHUDElement extends HUDElement
{
    private static final RenderItem RENDER_ITEM = (RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class);
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
            glPushMatrix();
            RenderHelper.enableGUIStandardItemLighting();

            final double posX = x + 1 + (i * 16);
            final double posY = y + 1;

            RENDER_ITEM.renderItemAndEffectIntoGUI(MC.fontRenderer, MC.getTextureManager(), stack, (int) posX, (int) posY);
            RENDER_ITEM.renderItemOverlayIntoGUI(MC.fontRenderer, MC.getTextureManager(), stack, (int) posX, (int) posY);

            RenderHelper.disableStandardItemLighting();

            glPopMatrix();
        }
    }
}
