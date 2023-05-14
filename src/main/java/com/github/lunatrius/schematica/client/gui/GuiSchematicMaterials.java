//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.client.gui;

import net.minecraft.client.gui.*;
import net.minecraft.item.*;
import net.minecraft.client.resources.*;
import com.github.lunatrius.schematica.*;
import java.util.*;
import com.github.lunatrius.schematica.world.*;

public class GuiSchematicMaterials extends GuiScreen
{
    private final GuiScreen prevGuiScreen;
    private GuiSchematicMaterialsSlot guiSchematicMaterialsSlot;
    private GuiButton btnDone;
    private final String strMaterialName;
    private final String strMaterialAmount;
    protected final List<ItemStack> blockList;
    
    public GuiSchematicMaterials(final GuiScreen guiScreen) {
        this.btnDone = null;
        this.strMaterialName = I18n.format("schematica.gui.materialname", new Object[0]);
        this.strMaterialAmount = I18n.format("schematica.gui.materialamount", new Object[0]);
        this.prevGuiScreen = guiScreen;
        final SchematicWorld schematic = Schematica.proxy.getActiveSchematic();
        if (schematic != null) {
            this.blockList = schematic.getBlockList();
        }
        else {
            this.blockList = new ArrayList<ItemStack>();
        }
    }
    
    public void initGui() {
        int id = 0;
        this.btnDone = new GuiButton(id++, this.width / 2 + 4, this.height - 30, 150, 20, I18n.format("schematica.gui.done", new Object[0]));
        this.buttonList.add(this.btnDone);
        this.guiSchematicMaterialsSlot = new GuiSchematicMaterialsSlot(this);
    }
    
    protected void actionPerformed(final GuiButton guiButton) {
        if (guiButton.enabled) {
            if (guiButton.id == this.btnDone.id) {
                this.mc.displayGuiScreen(this.prevGuiScreen);
            }
            else {
                this.guiSchematicMaterialsSlot.func_148147_a(guiButton);
            }
        }
    }
    
    public void drawScreen(final int x, final int y, final float partialTicks) {
        this.guiSchematicMaterialsSlot.func_148128_a(x, y, partialTicks);
        this.drawString(this.fontRendererObj, this.strMaterialName, this.width / 2 - 108, 4, 16777215);
        this.drawString(this.fontRendererObj, this.strMaterialAmount, this.width / 2 + 108 - this.fontRendererObj.getStringWidth(this.strMaterialAmount), 4, 16777215);
        super.drawScreen(x, y, partialTicks);
    }
}
