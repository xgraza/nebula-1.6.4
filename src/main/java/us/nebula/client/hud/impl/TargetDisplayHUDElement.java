package us.nebula.client.hud.impl;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import us.nebula.client.cheat.impl.combat.AutoBedCheat;
import us.nebula.client.cheat.impl.combat.KillAuraCheat;
import us.nebula.client.cheat.impl.render.HUDCheat;
import us.nebula.client.hud.HUDElement;
import us.nebula.client.hud.HUDManifest;
import us.nebula.client.hud.gui.HUDEditorScreen;
import us.nebula.client.util.render.HeadDownloader;
import us.nebula.client.util.render.RenderUtil;
import us.nebula.client.util.render.gui.font.Fonts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoord2d;
import static org.lwjgl.opengl.GL11.glVertex2d;

/**
 * @author xgraza
 * @since 5/13/26
 */
@HUDManifest(name = "TargetDisplay",
        description = "Displays information about the target you're attacking",
        padding = 5.0,
        x = 200, y = 200, width = 200, height = 80)
public final class TargetDisplayHUDElement extends HUDElement
{
    private static final RenderItem RENDER_ITEM = (RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class);

    private static final int BACKGROUND_COLOR = new Color(25, 25, 25, 230).getRGB();
    private static final int HEALTH_BAR_BACKGROUND_COLOR = new Color(25, 25, 25).getRGB();

    private static final double HEALTH_BAR_HEIGHT = 15.0;

    @Override
    public void render(ScaledResolution res)
    {
        final EntityPlayer target = getTargetedEntity();
        if (target == null)
        {
            return;
        }

        RenderUtil.roundedRectangle2D(getX(), getY(), getWidth(), getHeight(), 5f, BACKGROUND_COLOR);
        final boolean drewPlayerHead = drawPlayerHead(target);

        final double startX = drewPlayerHead ? x + (getPadding() * 2) + getTextureSize() : x + getPadding();
        final double remainingWidth = ((x + getWidth() - getPadding()) - startX);

        drawPlayerName(startX, target);
        drawHealthBar(startX, remainingWidth, target);
        drawArmor(startX, remainingWidth, target);
    }

    private boolean drawPlayerHead(final EntityPlayer target)
    {
        final int size = getTextureSize();
        final DynamicTexture texture = HeadDownloader.getOrDownloadTexture(target.getCommandSenderName(), size);
        if (texture == null)
        {
            return false;
        }
        glColor4f(1, 1, 1, 1);
        glBindTexture(GL_TEXTURE_2D, texture.getGlTextureId());
        glPushMatrix();
        glBegin(GL_QUADS);
        {
            glTexCoord2d(0, 0);
            glVertex2d(getX() + getPadding(), getY() + getPadding());

            glTexCoord2d(0, 1);
            glVertex2d(getX() + getPadding(), getY() + size + getPadding());

            glTexCoord2d(1, 1);
            glVertex2d(getX() + size + getPadding(), getY() + size + getPadding());

            glTexCoord2d(1, 0);
            glVertex2d(getX() + size + getPadding(), getY() + getPadding());
        }
        glEnd();
        glPopMatrix();
        return true;
    }

    private void drawPlayerName(final double x, final EntityPlayer target)
    {
        Fonts.POPPINS.drawStringShadow(target.getCommandSenderName(), x, getY() + getPadding() + 1, -1);
        final int textWidth = (int) Fonts.POPPINS.getStringWidth(target.getCommandSenderName()) + 4;
    }

    private void drawHealthBar(final double x, final double remainingWidth, final EntityPlayer target)
    {
        final float health = target.getHealth() + target.getAbsorptionAmount();
        final double percent = health / Math.min(24.0, (target.getMaxHealth() + 4.0));
        RenderUtil.roundedRectangle2D(x, getY() + getTextureSize() - HEALTH_BAR_HEIGHT, remainingWidth, HEALTH_BAR_HEIGHT, 2.5f, HEALTH_BAR_BACKGROUND_COLOR);
        RenderUtil.roundedRectangle2D(x, getY() + getTextureSize() - HEALTH_BAR_HEIGHT, remainingWidth * percent, HEALTH_BAR_HEIGHT, 2.5f, HUDCheat.INSTANCE.getBaseColor(0));
    }

    private void drawArmor(final double x, final double remainingWidth, final EntityPlayer target)
    {
        glPushMatrix();
        RenderHelper.enableGUIStandardItemLighting();

        final double y = getY() + getPadding() + Fonts.POPPINS.getFontHeight() + getPadding() * 2;

        RenderUtil.roundedRectangle2D(x, y - 2.0, remainingWidth, 20 + Fonts.POPPINS_SMALL.getFontHeight() - 2, 3.0f, BACKGROUND_COLOR);

        final List<ItemStack> stacks = new ArrayList<>(Arrays.asList(target.inventory.armorInventory));
        stacks.add(target.getHeldItem());

        final double spacePerItem = remainingWidth / stacks.size();
        for (int i = stacks.size() - 1; i >= 0; --i)
        {
            final ItemStack stack = stacks.get(4 - i);
            if (stack == null)
            {
                continue;
            }

            final double posX = x + 2 + (i * spacePerItem);

            RENDER_ITEM.renderItemAndEffectIntoGUI(MC.fontRenderer, MC.getTextureManager(), stack, (int) posX, (int) y);
            RENDER_ITEM.renderItemOverlayIntoGUI(MC.fontRenderer, MC.getTextureManager(), stack, (int) posX, (int) y);

            if (stack.isItemStackDamageable())
            {
                float damagePercent = (float) stack.getItemDamage() / stack.getMaxDamage();
                final int textColor = new Color(damagePercent, 1.0f - damagePercent, 0.0f).getRGB();
                Fonts.POPPINS_SMALL.drawStringShadow(String.format("%.1f", (1.0f - damagePercent) * 100.0f) + "%",
                        posX + 1, y + 15, textColor);
            } else
            {
                Fonts.POPPINS_SMALL.drawStringShadow(String.valueOf(stack.stackSize), posX + 7, y + 15, -1);
            }
        }
        RenderHelper.disableStandardItemLighting();

        glPopMatrix();
    }

    private int getTextureSize()
    {
        return (int) (getHeight() - (getPadding() * 2));
    }

    private EntityPlayer getTargetedEntity()
    {
        EntityLivingBase target = null;
        if (MC.currentScreen instanceof HUDEditorScreen)
        {
            target = MC.thePlayer;
        }
        if (AutoBedCheat.INSTANCE.isActive())
        {
            target = AutoBedCheat.INSTANCE.getTarget();
        }
        if (KillAuraCheat.INSTANCE.isAttacking())
        {
            target = KillAuraCheat.INSTANCE.getTarget();
        }
        if (!(target instanceof EntityPlayer))
        {
            return null;
        }
        return (EntityPlayer) target;
    }
}
