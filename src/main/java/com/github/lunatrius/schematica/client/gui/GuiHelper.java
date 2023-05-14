//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.client.gui;

import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.item.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.*;

public class GuiHelper
{
    private static final RenderItem renderItem;
    
    public static void drawItemStack(final TextureManager textureManager, final FontRenderer fontRenderer, final int x, final int y, final ItemStack itemStack) {
        drawItemStackSlot(textureManager, x, y);
        if (itemStack != null && itemStack.getItem() != null) {
            GL11.glEnable(32826);
            RenderHelper.enableGUIStandardItemLighting();
            GuiHelper.renderItem.renderItemIntoGUI(fontRenderer, textureManager, itemStack, x + 2, y + 2);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(32826);
        }
    }
    
    public static void drawItemStackSlot(final TextureManager textureManager, final int x, final int y) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        textureManager.bindTexture(Gui.statIcons);
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(x + 1 + 0), (double)(y + 1 + 18), 0.0, 0.0, 0.140625);
        tessellator.addVertexWithUV((double)(x + 1 + 18), (double)(y + 1 + 18), 0.0, 0.140625, 0.140625);
        tessellator.addVertexWithUV((double)(x + 1 + 18), (double)(y + 1 + 0), 0.0, 0.140625, 0.0);
        tessellator.addVertexWithUV((double)(x + 1 + 0), (double)(y + 1 + 0), 0.0, 0.0, 0.0);
        tessellator.draw();
    }
    
    static {
        renderItem = new RenderItem();
    }
}
