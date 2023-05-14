//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.client.gui;

import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.*;
import com.github.lunatrius.schematica.*;
import net.minecraft.client.renderer.*;
import net.minecraft.item.*;

class GuiSchematicMaterialsSlot extends GuiSlot
{
    private final FontRenderer fontRenderer;
    private final TextureManager renderEngine;
    private final GuiSchematicMaterials guiSchematicMaterials;
    protected int selectedIndex;
    
    public GuiSchematicMaterialsSlot(final GuiSchematicMaterials par1) {
        super(Minecraft.getMinecraft(), par1.width, par1.height, 16, par1.height - 34, 24);
        this.fontRenderer = Settings.instance.minecraft.fontRenderer;
        this.renderEngine = Settings.instance.minecraft.getTextureManager();
        this.selectedIndex = -1;
        this.guiSchematicMaterials = par1;
        this.selectedIndex = -1;
    }
    
    protected int getSize() {
        return this.guiSchematicMaterials.blockList.size();
    }
    
    protected void elementClicked(final int index, final boolean par2, final int par3, final int par4) {
        this.selectedIndex = index;
    }
    
    protected boolean isSelected(final int index) {
        return index == this.selectedIndex;
    }
    
    protected void drawBackground() {
    }
    
    protected void drawContainerBackground(final Tessellator tessellator) {
    }
    
    protected void drawSlot(final int index, final int x, final int y, final int par4, final Tessellator tessellator, final int par6, final int par7) {
        final ItemStack itemStack = this.guiSchematicMaterials.blockList.get(index);
        final String amount = Integer.toString(itemStack.stackSize);
        String itemName;
        if (itemStack != null && itemStack.getItem() != null) {
            itemName = itemStack.getItem().getItemStackDisplayName(itemStack);
        }
        else {
            itemName = "Unknown";
        }
        GuiHelper.drawItemStack(this.renderEngine, this.fontRenderer, x, y, itemStack);
        this.guiSchematicMaterials.drawString(this.fontRenderer, itemName, x + 24, y + 6, 16777215);
        this.guiSchematicMaterials.drawString(this.fontRenderer, amount, x + 215 - this.fontRenderer.getStringWidth(amount), y + 6, 16777215);
    }
}
