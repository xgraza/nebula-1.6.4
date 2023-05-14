//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.client.gui;

import com.github.lunatrius.schematica.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.*;

public class GuiSchematicLoadSlot extends GuiSlot
{
    private final Settings settings;
    private final FontRenderer fontRenderer;
    private final TextureManager renderEngine;
    private final GuiSchematicLoad guiSchematicLoad;
    protected int selectedIndex;
    
    public GuiSchematicLoadSlot(final GuiSchematicLoad guiSchematicLoad) {
        super(Settings.instance.minecraft, guiSchematicLoad.width, guiSchematicLoad.height, 16, guiSchematicLoad.height - 40, 24);
        this.settings = Settings.instance;
        this.fontRenderer = this.settings.minecraft.fontRenderer;
        this.renderEngine = this.settings.minecraft.getTextureManager();
        this.selectedIndex = -1;
        this.guiSchematicLoad = guiSchematicLoad;
    }
    
    protected int getSize() {
        return this.guiSchematicLoad.schematicFiles.size();
    }
    
    protected void elementClicked(final int index, final boolean par2, final int par3, final int par4) {
        final GuiSchematicEntry schematic = this.guiSchematicLoad.schematicFiles.get(index);
        if (schematic.isDirectory()) {
            this.guiSchematicLoad.changeDirectory(schematic.getName());
            this.selectedIndex = -1;
        }
        else {
            this.selectedIndex = index;
        }
    }
    
    protected boolean isSelected(final int index) {
        return index == this.selectedIndex;
    }
    
    protected void drawBackground() {
    }
    
    protected void drawContainerBackground(final Tessellator tessellator) {
    }
    
    protected void drawSlot(final int index, final int x, final int y, final int par4, final Tessellator tessellator, final int par6, final int par7) {
        if (index < 0 || index >= this.guiSchematicLoad.schematicFiles.size()) {
            return;
        }
        final GuiSchematicEntry schematic = this.guiSchematicLoad.schematicFiles.get(index);
        String schematicName = schematic.getName();
        if (schematic.isDirectory()) {
            schematicName += "/";
        }
        else {
            schematicName = schematicName.replaceAll("(?i)\\.schematic$", "");
        }
        GuiHelper.drawItemStack(this.renderEngine, this.fontRenderer, x, y, schematic.getItemStack());
        this.guiSchematicLoad.drawString(this.settings.minecraft.fontRenderer, schematicName, x + 24, y + 6, 16777215);
    }
}
