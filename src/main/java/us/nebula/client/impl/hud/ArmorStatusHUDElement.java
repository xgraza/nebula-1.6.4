package us.nebula.client.impl.hud;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import us.nebula.client.api.manager.hud.HUDElement;
import us.nebula.client.api.manager.hud.HUDManifest;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 5/6/26
 */
@HUDManifest(name = "ArmorStatus", description = "Displays current armor status")
public final class ArmorStatusHUDElement extends HUDElement
{
    private static final RenderItem RENDER_ITEM = (RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class);

    @Override
    public void init()
    {
        setX(20);
        setY(20);
        setWidth((4 * 16) + 4);
        setHeight(18);
    }

    @Override
    public void render(final ScaledResolution res)
    {
        //init();
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
